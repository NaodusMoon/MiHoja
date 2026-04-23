"use client";

import { startTransition, useDeferredValue, useEffect, useEffectEvent, useMemo, useRef, useState } from "react";
import Image from "next/image";
import {
  Bell,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  Briefcase,
  Download,
  EllipsisVertical,
  Filter,
  Grid2x2,
  LayoutGrid,
  GraduationCap,
  MapPinned,
  Mars,
  Plus,
  Search,
  Sparkles,
  Trash2,
  Building2,
  User,
  UserRound
} from "lucide-react";

import {
  cleanupDashboardDuplicates,
  deleteDashboardPeople,
  getDashboardNotifications,
  getDashboardPeople
} from "@/lib/api";
import type {
  DashboardFilterOptions,
  DashboardFilters,
  DashboardNotification,
  DashboardOverview,
  DashboardPeopleResponse,
  DashboardPersonCard
} from "@/lib/types";

type Props = {
  overview: DashboardOverview;
};

type SortKey = "name-asc" | "name-desc" | "number-asc";
type FilterKey = keyof DashboardFilters;

const DEFAULT_PER_PAGE = 6;
const emptyFilters: DashboardFilters = {
  sexo: [],
  lugarExpedicion: [],
  formacion: [],
  dependencia: [],
  cargo: []
};

const emptyFilterOptions: DashboardFilterOptions = {
  sexo: [],
  lugarExpedicion: [],
  formacion: [],
  dependencia: [],
  cargo: []
};

const filterLabels: Record<FilterKey, string> = {
  sexo: "Sexo",
  lugarExpedicion: "Lugar de Expedicion",
  formacion: "Formacion Academica",
  dependencia: "Dependencia",
  cargo: "Tipo de Cargo"
};

const filterIcons: Record<FilterKey, typeof Mars> = {
  sexo: Mars,
  lugarExpedicion: MapPinned,
  formacion: GraduationCap,
  dependencia: Building2,
  cargo: Briefcase
};

const navItems = [
  { id: "consultar", label: "Consultar", icon: UserRound, active: true, href: "/" },
  { id: "insertar", label: "Insertar", icon: Plus, active: false, href: "/insertar" },
  { id: "campos", label: "Campos", icon: Grid2x2, active: false, href: "/configuracion-campos" }
];

const reportDownloads = [
  { id: "word", label: "Descargar Word", logo: "/word-mark.svg", href: "/api/backend/descargar/todos/word" },
  { id: "pdf", label: "Descargar PDF", logo: "/pdf-mark.svg", href: "/api/backend/descargar/todos/pdf" },
  {
    id: "excel",
    label: "Descargar Excel",
    logo: "/excel-mark.svg",
    href: "/api/backend/descargar/todos/excel"
  }
];

function formatPersonName(person: DashboardPersonCard) {
  return `${person.apellidos} ${person.nombres}`.trim();
}

function getAccentTone(index: number) {
  return ["gold", "green", "green", "green", "gold", "green"][index % 6];
}

function countSelectedFilters(filters: DashboardFilters) {
  return Object.values(filters).filter((values) => values.length > 0).length;
}

function mergeFilterOptions(
  current: DashboardFilterOptions,
  incoming: DashboardFilterOptions,
  selected: DashboardFilters
): DashboardFilterOptions {
  const next = {} as DashboardFilterOptions;

  (Object.keys(filterLabels) as FilterKey[]).forEach((key) => {
    next[key] = Array.from(new Set([...current[key], ...incoming[key], ...selected[key]])).sort((a, b) =>
      a.localeCompare(b)
    );
  });

  return next;
}

function getVisiblePages(currentPage: number, totalPages: number) {
  if (totalPages <= 7) {
    return Array.from({ length: totalPages }, (_, index) => index + 1);
  }

  const pages: Array<number | string> = [1];
  const start = Math.max(2, currentPage - 1);
  const end = Math.min(totalPages - 1, currentPage + 1);

  if (start > 2) {
    pages.push("start-ellipsis");
  }

  for (let page = start; page <= end; page += 1) {
    pages.push(page);
  }

  if (end < totalPages - 1) {
    pages.push("end-ellipsis");
  }

  pages.push(totalPages);
  return pages;
}

export function DashboardShell({ overview }: Props) {
  const requestSequenceRef = useRef(0);
  const [query, setQuery] = useState("");
  const [searchInput, setSearchInput] = useState("");
  const [filterSearchQuery, setFilterSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState<SortKey>("name-asc");
  const [compactMode, setCompactMode] = useState(false);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [selectedAllScope, setSelectedAllScope] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [filters, setFilters] = useState<DashboardFilters>(emptyFilters);
  const [isLoading, setIsLoading] = useState(false);
  const [isMutating, setIsMutating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [hasLoadedOnce, setHasLoadedOnce] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [notificationsLoading, setNotificationsLoading] = useState(false);
  const [notifications, setNotifications] = useState<DashboardNotification[]>([]);
  const [notificationsMessage, setNotificationsMessage] = useState<string | null>(null);
  const [unreadNotifications, setUnreadNotifications] = useState(1);
  const [downloadMenuOpen, setDownloadMenuOpen] = useState(false);
  const [availableFilterOptions, setAvailableFilterOptions] = useState<DashboardFilterOptions>(emptyFilterOptions);
  const [peopleResponse, setPeopleResponse] = useState<DashboardPeopleResponse>({
    people: overview.recentPeople,
    metrics: overview.metrics,
    filterOptions: emptyFilterOptions,
    total: overview.recentPeople.length,
    page: 1,
    size: DEFAULT_PER_PAGE,
    totalPages: 1,
    duplicateCount: 0,
    activeFilterCount: 0,
    query: ""
  });

  const deferredFilterSearchQuery = useDeferredValue(filterSearchQuery);
  const selectionScope = useMemo(() => JSON.stringify({ query, filters }), [filters, query]);

  const loadPeople = useEffectEvent(async () => {
    const requestId = requestSequenceRef.current + 1;
    requestSequenceRef.current = requestId;
    setIsLoading(true);
    setError(null);

    try {
      const nextResponse = await getDashboardPeople({
        query,
        filters,
        sortBy,
        page: currentPage,
        size: DEFAULT_PER_PAGE
      });

      if (requestId !== requestSequenceRef.current) {
        return;
      }

      setPeopleResponse(nextResponse);
      setAvailableFilterOptions((current) => mergeFilterOptions(current, nextResponse.filterOptions, filters));
      setHasLoadedOnce(true);
    } catch (requestError) {
      if (requestId === requestSequenceRef.current) {
        setError(requestError instanceof Error ? requestError.message : "No se pudo cargar el dashboard.");
      }
    } finally {
      if (requestId === requestSequenceRef.current) {
        setIsLoading(false);
      }
    }
  });

  useEffect(() => {
    loadPeople();
  }, [currentPage, filters, loadPeople, query, sortBy]);

  useEffect(() => {
    if (!toast) {
      return;
    }

    const timeout = window.setTimeout(() => setToast(null), 3200);
    return () => window.clearTimeout(timeout);
  }, [toast]);

  useEffect(() => {
    const nextQuery = searchInput.trim();
    const timeout = window.setTimeout(
      () =>
        startTransition(() => {
          setQuery(nextQuery);
          setCurrentPage(1);
        }),
      nextQuery ? 220 : 0
    );

    return () => window.clearTimeout(timeout);
  }, [searchInput]);

  const loadNotifications = useEffectEvent(async () => {
    setNotificationsLoading(true);
    setNotificationsMessage(null);

    try {
      const response = await getDashboardNotifications();
      setNotifications(response.notifications);
      setUnreadNotifications(response.unreadCount);
      setNotificationsMessage(response.message ?? null);
    } catch (requestError) {
      setNotifications([]);
      setUnreadNotifications(0);
      setNotificationsMessage(
        requestError instanceof Error ? requestError.message : "No se pudieron cargar las notificaciones."
      );
    } finally {
      setNotificationsLoading(false);
    }
  });

  const toggleNotifications = useEffectEvent(async () => {
    const nextOpen = !notificationsOpen;
    setNotificationsOpen(nextOpen);

    if (nextOpen) {
      setUnreadNotifications(0);
      await loadNotifications();
    }
  });

  const toggleFilterValue = (key: FilterKey, value: string) => {
    setCurrentPage(1);
    setFilters((current) => ({
      ...current,
      [key]: current[key].includes(value)
        ? current[key].filter((item) => item !== value)
        : [...current[key], value]
    }));
  };

  const clearFilters = () => {
    setFilters(emptyFilters);
    setSelectedAllScope(null);
    setCurrentPage(1);
  };

  const togglePersonSelection = (personId: number) => {
    setSelectedAllScope(null);
    setSelectedIds((current) =>
      current.includes(personId) ? current.filter((id) => id !== personId) : [...current, personId]
    );
  };

  const allVisibleSelected =
    peopleResponse.total > 0 && selectedAllScope === selectionScope && selectedIds.length === peopleResponse.total;

  const toggleVisibleSelection = async () => {
    if (allVisibleSelected) {
      setSelectedIds([]);
      setSelectedAllScope(null);
      return;
    }

    setIsMutating(true);
    setError(null);

    try {
      const fullSelectionResponse = await getDashboardPeople({
        query,
        filters,
        sortBy,
        page: 1,
        size: Math.max(peopleResponse.total, 1)
      });

      setSelectedIds(fullSelectionResponse.people.map((person) => person.id));
      setSelectedAllScope(selectionScope);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : "No se pudieron seleccionar todos los registros.");
    } finally {
      setIsMutating(false);
    }
  };

  const runDelete = async (ids: number[]) => {
    if (ids.length === 0) {
      return;
    }

    setIsMutating(true);
    setError(null);

    try {
      const response = await deleteDashboardPeople(ids);
      setToast(response.message);
      setSelectedIds([]);
      setSelectedAllScope(null);
      await loadPeople();
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : "No se pudieron eliminar los registros.");
    } finally {
      setIsMutating(false);
    }
  };

  const runCleanup = async () => {
    setIsMutating(true);
    setError(null);

    try {
      const response = await cleanupDashboardDuplicates();
      setToast(response.message);
      setSelectedIds([]);
      setSelectedAllScope(null);
      await loadPeople();
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : "No se pudo limpiar duplicados.");
    } finally {
      setIsMutating(false);
    }
  };

  const visibleRangeStart = peopleResponse.total === 0 ? 0 : (peopleResponse.page - 1) * peopleResponse.size + 1;
  const visibleRangeEnd = Math.min(peopleResponse.page * peopleResponse.size, peopleResponse.total);
  const visiblePages = useMemo(
    () => getVisiblePages(peopleResponse.page, peopleResponse.totalPages),
    [peopleResponse.page, peopleResponse.totalPages]
  );
  const normalizedFilterSearch = deferredFilterSearchQuery.trim().toLowerCase();

  const sidebarMetrics = useMemo(
    () => [
      { id: "visibles", value: String(peopleResponse.total), label: "Visibles" },
      { id: "seleccionadas", value: String(selectedIds.length), label: "Seleccionadas" },
      { id: "activos", value: String(countSelectedFilters(filters)), label: "Filtros activos" }
    ],
    [filters, peopleResponse.total, selectedIds.length]
  );

  const filteredSidebarOptions = useMemo(() => {
    return (Object.keys(filterLabels) as FilterKey[]).map((key) => {
      const groupLabel = filterLabels[key];
      const options = availableFilterOptions[key]
        .filter((value) => {
          if (!normalizedFilterSearch) {
            return true;
          }

          const queryText = normalizedFilterSearch.normalize("NFD").replace(/\p{Diacritic}/gu, "");
          const candidateText = `${groupLabel} ${value}`
            .toLowerCase()
            .normalize("NFD")
            .replace(/\p{Diacritic}/gu, "");
          return candidateText.includes(queryText);
        });

      return {
        key,
        groupLabel,
        options,
        visible: options.length > 0 || !normalizedFilterSearch
      };
    });
  }, [availableFilterOptions, normalizedFilterSearch]);

  return (
    <main className="dashboardRoot">
      <aside className="sidebar">
        <div className="brandBlock">
          <div className="brandMark">
            <Image alt="Logo MiHoja" height={62} priority src="/logo-mihoja.png" width={62} />
          </div>
          <div className="brandCopy">
            <h1>MiHoja</h1>
            <p>Sistema de Gestion de Datos de Personal</p>
          </div>
        </div>

        <nav className="sidebarNav">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <a
                key={item.id}
                href={item.href}
                className={`navAction ${item.active ? "navActionActive" : ""}`}
              >
                <Icon aria-hidden="true" />
                <span>{item.label}</span>
              </a>
            );
          })}
        </nav>

        <section className="filtersPanel">
          <div className="filterPanelHeader">
            <Filter aria-hidden="true" />
            <p className="sectionLabel">Panel de filtros</p>
          </div>
          <p className="filtersCopy">Los cambios se aplican automaticamente mientras seleccionas opciones.</p>

          <label className="filterSearch">
            <input
              onChange={(event) => setFilterSearchQuery(event.target.value)}
              placeholder="Buscar filtros o secciones"
              type="search"
              value={filterSearchQuery}
            />
            <span className="searchMiniIcon">
              <Search aria-hidden="true" />
            </span>
          </label>

          <div className="miniMetrics">
            {sidebarMetrics.map((metric) => (
              <article key={metric.id} className="miniMetric">
                <span>{metric.value}</span>
                <small>{metric.label}</small>
              </article>
            ))}
          </div>

          <div className="filterGroupList">
            {filteredSidebarOptions
              .filter((group) => group.visible)
              .map((group) => (
                <details key={group.key} className="filterGroup" open>
                  <summary className="filterGroupHeader">
                    <span className="filterGroupTitle">
                      {(() => {
                        const Icon = filterIcons[group.key];
                        return <Icon aria-hidden="true" />;
                      })()}
                      <span>{group.groupLabel}</span>
                    </span>
                    <span className="filterGroupArrow">
                      <ChevronDown aria-hidden="true" />
                    </span>
                  </summary>
                  <div className="filterOptions">
                    {group.options.map((value) => (
                      <label key={value} className="filterOption">
                        <input
                          checked={filters[group.key].includes(value)}
                          onChange={() => toggleFilterValue(group.key, value)}
                          type="checkbox"
                        />
                        <span>{value}</span>
                      </label>
                    ))}
                  </div>
                </details>
              ))}

            {normalizedFilterSearch && filteredSidebarOptions.every((group) => !group.visible) ? (
              <p className="filterSearchEmpty">No hay filtros que coincidan con esa busqueda.</p>
            ) : null}
          </div>

          <div className="filterActions">
            <button className="ghostButton" onClick={clearFilters} type="button">
              <Trash2 aria-hidden="true" />
              <span>Limpiar filtros</span>
            </button>
            <button className="solidButton" disabled={isMutating} onClick={runCleanup} type="button">
              <Sparkles aria-hidden="true" />
              <span>Limpiar duplicados</span>
            </button>
          </div>
        </section>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div className="profileWrap">
            <div className="profileAvatar">
              <UserRound aria-hidden="true" />
            </div>
            <div>
              <p className="welcomeTitle">Hola, Sandra</p>
              <span className="welcomeRole">Administradora</span>
            </div>
          </div>

          <div className="topbarActions">
            <div className="downloadWrap">
              <button
                aria-expanded={downloadMenuOpen}
                className="downloadButton"
                onClick={() => setDownloadMenuOpen((value) => !value)}
                type="button"
              >
                <Download aria-hidden="true" />
                <span>Descargar reportes</span>
                <ChevronDown aria-hidden="true" />
              </button>

              {downloadMenuOpen ? (
                <section className="downloadMenu">
                  {reportDownloads.map((item) => {
                    return (
                      <a key={item.id} className="downloadMenuItem" href={item.href} target="_blank" rel="noreferrer">
                        <Image alt="" aria-hidden="true" className="downloadMenuLogo" height={22} src={item.logo} width={22} />
                        <span>{item.label}</span>
                      </a>
                    );
                  })}
                </section>
              ) : null}
            </div>

            <div className="notificationWrap">
              <button className="iconBubble" type="button" aria-label="Notificaciones" onClick={toggleNotifications}>
                {unreadNotifications > 0 ? <span className="notificationDot" /> : null}
                <Bell aria-hidden="true" />
              </button>

              {notificationsOpen ? (
                <section className="notificationPanel">
                  <div className="notificationPanelHeader">
                    <strong>Notificaciones</strong>
                    <span>{notificationsLoading ? "Actualizando..." : `${notifications.length} items`}</span>
                  </div>

                  {notificationsMessage ? <p className="notificationPanelMessage">{notificationsMessage}</p> : null}

                  <div className="notificationList">
                    {notificationsLoading ? (
                      <p className="notificationEmpty">Cargando notificaciones...</p>
                    ) : notifications.length > 0 ? (
                      notifications.map((notification) => (
                        <article key={notification.id} className={`notificationItem notificationTone-${notification.tone}`}>
                          <strong>{notification.title}</strong>
                          <p>{notification.description}</p>
                        </article>
                      ))
                    ) : (
                      <p className="notificationEmpty">No hay notificaciones disponibles.</p>
                    )}
                  </div>
                </section>
              ) : null}
            </div>

            <button className="accountBubble" type="button">
              <span>SA</span>
            </button>

            <button className="topbarChevron" type="button" aria-label="Abrir cuenta">
              <ChevronRight aria-hidden="true" />
            </button>
          </div>
        </header>

        <section className="controlsRow">
          <button
            className={`compactToggle ${compactMode ? "compactToggleOn" : ""}`}
            onClick={() => setCompactMode((value) => !value)}
            type="button"
          >
            <span>Modo compacto: {compactMode ? "ON" : "OFF"}</span>
            <span className="toggleTrack">
              <span className="toggleThumb" />
            </span>
          </button>

          <label
            className={`searchBar ${searchInput ? "searchBarActive" : ""} ${
              isLoading && Boolean(searchInput.trim()) ? "searchBarLoading" : ""
            }`}
          >
            <span className="searchBarIcon">
              <Search aria-hidden="true" />
            </span>
            <input
              onChange={(event) => setSearchInput(event.target.value)}
              placeholder="Buscar por nombre, cedula o cargo..."
              type="search"
              value={searchInput}
            />
            <button type="button">
              <Search aria-hidden="true" />
            </button>
          </label>
        </section>

        <section className="toolbar">
          <div className="selectionRow">
            <div className="selectionCluster">
            <label className="selectionToggle">
              <input checked={allVisibleSelected} onChange={() => void toggleVisibleSelection()} type="checkbox" />
              <span>Seleccionar todos</span>
            </label>

            <button
              className={`dangerButton ${selectedIds.length === 0 ? "dangerButtonDisabled" : ""}`}
              disabled={selectedIds.length === 0 || isMutating}
              onClick={() => runDelete(selectedIds)}
              type="button"
            >
              <Trash2 aria-hidden="true" />
              <span>Eliminar seleccionados</span>
            </button>
            </div>
          </div>

          <div className="sortRow">
            <label className="sortSelect">
              <span>Ordenar por:</span>
              <select
                onChange={(event) => {
                  setSortBy(event.target.value as SortKey);
                  setCurrentPage(1);
                }}
                value={sortBy}
              >
                <option value="name-asc">Nombre A - Z</option>
                <option value="name-desc">Nombre Z - A</option>
                <option value="number-asc">Numero</option>
              </select>
            </label>

            <div className="viewToggle">
              <button className={!compactMode ? "viewToggleActive" : ""} type="button" aria-label="Vista en cuadricula">
                <LayoutGrid aria-hidden="true" />
              </button>
              <button className={compactMode ? "viewToggleActive" : ""} type="button" aria-label="Vista compacta">
                <Grid2x2 aria-hidden="true" />
              </button>
            </div>
          </div>
        </section>

        {error ? <section className="statusBanner statusBannerError">{error}</section> : null}
        {toast ? <section className="statusBanner statusBannerSuccess">{toast}</section> : null}

        <section className={`cardGrid ${compactMode ? "cardGridCompact" : ""}`} data-loading={isLoading}>
          {peopleResponse.people.map((person, index) => {
            const accentTone = getAccentTone(index);
            const isSelected = selectedIds.includes(person.id);

            return (
              <article
                key={person.id}
                className={`personCard personAccent-${accentTone} ${isSelected ? "personCardSelected" : ""}`}
              >
                <div className="cardTopRow">
                  <label className="personCheckbox">
                    <input checked={isSelected} onChange={() => togglePersonSelection(person.id)} type="checkbox" />
                  </label>
                  <button className="moreButton" type="button" aria-label="Mas opciones">
                    <EllipsisVertical aria-hidden="true" />
                  </button>
                </div>

                <div className="personMain">
                  <div className={`avatarWrap ${compactMode ? "avatarWrapCompact" : ""}`}>
                    <div className="avatarArc" />
                    <div className="avatarRing">
                      {person.imagenUrl ? (
                        <img alt={formatPersonName(person)} className="avatarImage" src={person.imagenUrl} />
                      ) : (
                        <User className="avatarPlaceholderIcon" aria-hidden="true" />
                      )}
                    </div>
                  </div>

                  <div className="personInfo">
                    <h3>
                      <a className="personLink" href={`/muestra/${person.id}`}>
                        {formatPersonName(person)}
                      </a>
                    </h3>
                    <p>
                      <strong>N:</strong> {person.numero ?? person.id}
                    </p>
                    <p>
                      <strong>Cedula:</strong> {person.cedula}
                    </p>
                    <p>
                      <strong>Cargo:</strong> {person.cargo ?? "NO DISPONIBLE"}
                    </p>
                    <p>
                      <strong>Dependencia:</strong> {person.dependencia ?? "NO DISPONIBLE"}
                    </p>
                  </div>
                </div>

                <div className="cardFooter">
                  <span className="statusPill">
                    <i />
                    {(person.estado ?? "NO DISPONIBLE").toUpperCase()}
                  </span>
                  <button
                    className="trashButton"
                    onClick={() => runDelete([person.id])}
                    type="button"
                    aria-label="Eliminar persona"
                  >
                    <Trash2 aria-hidden="true" />
                  </button>
                </div>
              </article>
            );
          })}

          {!isLoading && hasLoadedOnce && peopleResponse.people.length === 0 ? (
            <article className="emptyState">
              <strong>No hay resultados para esta consulta.</strong>
              <p>Ajusta la busqueda o limpia filtros para volver a consultar.</p>
            </article>
          ) : null}

          {isLoading && !hasLoadedOnce ? (
            <article className="emptyState">
              <strong>Cargando datos del panel...</strong>
              <p>Estamos consultando el backend para traer los registros iniciales.</p>
            </article>
          ) : null}
        </section>

        {peopleResponse.total > 0 ? (
        <footer className="bottomBar">
          <div className="pager">
            <button
              disabled={peopleResponse.page === 1 || isLoading}
              onClick={() => setCurrentPage((page) => Math.max(1, page - 1))}
              type="button"
              aria-label="Pagina anterior"
            >
              <ChevronLeft aria-hidden="true" />
            </button>
            {visiblePages.map((page) =>
              typeof page === "number" ? (
                <button
                  key={page}
                  className={page === peopleResponse.page ? "pagerActive" : ""}
                  onClick={() => setCurrentPage(page)}
                  type="button"
                >
                  {page}
                </button>
              ) : (
                <span key={page} className="pagerEllipsis">
                  ...
                </span>
              )
            )}
            <button
              disabled={peopleResponse.page === peopleResponse.totalPages || isLoading}
              onClick={() => setCurrentPage((page) => Math.min(peopleResponse.totalPages, page + 1))}
              type="button"
              aria-label="Pagina siguiente"
            >
              <ChevronRight aria-hidden="true" />
            </button>
          </div>

          <div className="pageSummary">
            <span>
              Mostrando {visibleRangeStart}-{visibleRangeEnd} de {peopleResponse.total}
            </span>
            <button className="pageSizeButton" type="button">
              Mostrar 12 por pagina
              <ChevronDown aria-hidden="true" />
            </button>
          </div>
        </footer>
        ) : null}

        <section className="footerStrip">
          <div className="footerBrand">
            <div className="footerTree">
              <Image alt="Logo MiHoja" height={26} src="/logo-mihoja.png" width={26} />
            </div>
            <strong>MiHoja</strong>
            <span>Sistema de Gestion de Datos de Personal</span>
          </div>

          <div className="footerMeta">
            <div className="cityBadge">ALCALDIA DE GARAGOA</div>
            <span>Todos los derechos reservados © 2024</span>
          </div>
        </section>
      </section>
    </main>
  );
}
