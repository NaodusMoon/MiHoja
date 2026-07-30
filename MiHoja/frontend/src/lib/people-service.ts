import type {
  DashboardFilterOptions,
  DashboardMetric,
  DashboardOverview,
  DashboardPeopleResponse,
  DashboardPersonCard
} from "@/lib/types";
import { encodeFilter, supabaseRest } from "@/lib/supabase-rest";

export type PersonRecord = {
  n: number;
  numero: number | null;
  nombres: string | null;
  apellidos: string | null;
  cedula: string | null;
  correo_institucional: string | null;
  direccion: string | null;
  enlace_sigep: string | null;
  estado: string | null;
  fecha_nacimiento: string | null;
  imagen_url: string | null;
  lugar_expedicion: string | null;
  numero_hijos: number | null;
  sexo: string | null;
  telefono_institucional: string | null;
  formacion?: Array<{
    formacion_academica: string | null;
    grado: string | null;
    titulo: string | null;
  }>;
  persona_cargo_laboral?: Array<{
    id_pcl: number;
    fecha_ingreso: string | null;
    fecha_firma_contrato: string | null;
    meses_experiencia: number | null;
    cargo_laboral: {
      id_cargo: number;
      cargo: string | null;
      codigo: string | null;
      dependencia: string | null;
    } | null;
  }>;
  contacto_emergencia?: Array<{
    nombre_contacto_emergencia: string | null;
    parentesco: string | null;
    telefono_contacto_emergencia: string | null;
  }>;
  riesgo_procedencia?: Array<{
    medio_transporte: string | null;
    procedencia_trabajador: string | null;
    riesgo: string | null;
  }>;
  salud?: Array<{
    afp: string | null;
    arl: string | null;
    carnet_vacunacion: boolean | null;
    ccf: string | null;
    dotacion: string | null;
    eps: string | null;
    rh: string | null;
  }>;
  alergia?: Array<{ nombre: string }>;
  enfermedad?: Array<{ nombre: string }>;
  medicamento?: Array<{ nombre: string }>;
  persona_campo_valor?: Array<{
    valor: string;
    campo_id: number;
    campo_personalizado: { nombre: string } | null;
  }>;
};

const personSelect = [
  "n",
  "numero",
  "nombres",
  "apellidos",
  "cedula",
  "correo_institucional",
  "direccion",
  "enlace_sigep",
  "estado",
  "fecha_nacimiento",
  "imagen_url",
  "lugar_expedicion",
  "numero_hijos",
  "sexo",
  "telefono_institucional",
  "formacion(formacion_academica,grado,titulo)",
  "persona_cargo_laboral(id_pcl,fecha_ingreso,fecha_firma_contrato,meses_experiencia,cargo_laboral(id_cargo,cargo,codigo,dependencia))",
  "contacto_emergencia(nombre_contacto_emergencia,parentesco,telefono_contacto_emergencia)",
  "riesgo_procedencia(medio_transporte,procedencia_trabajador,riesgo)",
  "salud(afp,arl,carnet_vacunacion,ccf,dotacion,eps,rh)",
  "alergia(nombre)",
  "enfermedad(nombre)",
  "medicamento(nombre)",
  "persona_campo_valor(valor,campo_id,campo_personalizado(nombre))"
].join(",");

function first<T>(items: T[] | undefined) {
  return items?.[0] ?? null;
}

export async function getAllPeople(): Promise<PersonRecord[]> {
  return supabaseRest<PersonRecord[]>(
    `persona?select=${encodeURIComponent(personSelect)}&order=numero.asc&limit=1000`
  );
}

export async function getPersonById(id: number): Promise<PersonRecord | null> {
  const rows = await supabaseRest<PersonRecord[]>(
    `persona?select=${encodeURIComponent(personSelect)}&n=eq.${encodeFilter(id)}&limit=1`
  );
  return rows[0] ?? null;
}

function toCard(person: PersonRecord): DashboardPersonCard {
  const cargo = first(person.persona_cargo_laboral)?.cargo_laboral;
  return {
    id: person.n,
    numero: person.numero,
    nombres: person.nombres ?? "SIN NOMBRE",
    apellidos: person.apellidos ?? "",
    cedula: person.cedula ?? "NO DISPONIBLE",
    cargo: cargo?.cargo ?? "NO DISPONIBLE",
    dependencia: cargo?.dependencia ?? "NO DISPONIBLE",
    correoInstitucional: person.correo_institucional,
    telefonoInstitucional: person.telefono_institucional,
    estado: person.estado ?? "NO DISPONIBLE",
    imagenUrl: person.imagen_url
  };
}

function unique(values: Array<string | null | undefined>) {
  return Array.from(new Set(values.filter((value): value is string => Boolean(value)))).sort((a, b) =>
    a.localeCompare(b)
  );
}

function getFilterOptions(people: PersonRecord[]): DashboardFilterOptions {
  return {
    sexo: unique(people.map((person) => person.sexo)),
    lugarExpedicion: unique(people.map((person) => person.lugar_expedicion)),
    formacion: unique(people.map((person) => first(person.formacion)?.formacion_academica)),
    dependencia: unique(people.map((person) => first(person.persona_cargo_laboral)?.cargo_laboral?.dependencia)),
    cargo: unique(people.map((person) => first(person.persona_cargo_laboral)?.cargo_laboral?.cargo))
  };
}

function buildMetrics(total: number, activeFilters: number, duplicateCount: number): DashboardMetric[] {
  return [
    { id: "total", label: "Registros visibles", value: String(total), tone: "positive" },
    { id: "selected", label: "Seleccionadas", value: "0", tone: "neutral" },
    { id: "filters", label: "Filtros activos", value: String(activeFilters), tone: "accent" },
    { id: "duplicates", label: "Duplicados detectados", value: String(duplicateCount), tone: "warning" }
  ];
}

function duplicateCount(people: PersonRecord[]) {
  const seen = new Set<string>();
  let duplicates = 0;
  for (const person of people) {
    const key = person.cedula?.trim().toLowerCase();
    if (!key) continue;
    if (seen.has(key)) duplicates += 1;
    seen.add(key);
  }
  return duplicates;
}

export async function getDashboardOverviewData(): Promise<DashboardOverview> {
  const people = await getAllPeople();
  return {
    metrics: buildMetrics(people.length, 0, duplicateCount(people)),
    highlights: [
      "Datos ficticios listos para demostraciones",
      "Busqueda y filtros conectados con Supabase",
      "Importacion de Excel disponible desde Insertar"
    ],
    recentPeople: people.slice(0, 6).map(toCard)
  };
}

export async function getDashboardPeopleData(params: URLSearchParams): Promise<DashboardPeopleResponse> {
  const people = await getAllPeople();
  const query = (params.get("query") ?? "").trim().toLocaleLowerCase();
  const sortBy = params.get("sortBy") ?? "name-asc";
  const page = Math.max(1, Number(params.get("page") ?? 1));
  const size = Math.min(200, Math.max(1, Number(params.get("size") ?? 6)));
  const filters = {
    sexo: params.getAll("sexo"),
    lugarExpedicion: params.getAll("lugarExpedicion"),
    formacion: params.getAll("formacion"),
    dependencia: params.getAll("dependencia"),
    cargo: params.getAll("cargo")
  };

  const activeFilterCount = Object.values(filters).filter((values) => values.length > 0).length;
  const filtered = people.filter((person) => {
    const cargo = first(person.persona_cargo_laboral)?.cargo_laboral;
    const formation = first(person.formacion)?.formacion_academica;
    const searchable = [
      person.nombres,
      person.apellidos,
      person.cedula,
      cargo?.cargo,
      cargo?.dependencia
    ]
      .filter(Boolean)
      .join(" ")
      .toLocaleLowerCase();

    return (
      (!query || searchable.includes(query)) &&
      (!filters.sexo.length || filters.sexo.includes(person.sexo ?? "")) &&
      (!filters.lugarExpedicion.length || filters.lugarExpedicion.includes(person.lugar_expedicion ?? "")) &&
      (!filters.formacion.length || filters.formacion.includes(formation ?? "")) &&
      (!filters.dependencia.length || filters.dependencia.includes(cargo?.dependencia ?? "")) &&
      (!filters.cargo.length || filters.cargo.includes(cargo?.cargo ?? ""))
    );
  });

  filtered.sort((a, b) => {
    if (sortBy === "number-asc") return (a.numero ?? 0) - (b.numero ?? 0);
    const aName = `${a.apellidos ?? ""} ${a.nombres ?? ""}`;
    const bName = `${b.apellidos ?? ""} ${b.nombres ?? ""}`;
    return sortBy === "name-desc" ? bName.localeCompare(aName) : aName.localeCompare(bName);
  });

  const total = filtered.length;
  const totalPages = Math.max(1, Math.ceil(total / size));
  const safePage = Math.min(page, totalPages);
  const start = (safePage - 1) * size;

  return {
    people: filtered.slice(start, start + size).map(toCard),
    metrics: buildMetrics(total, activeFilterCount, duplicateCount(people)),
    filterOptions: getFilterOptions(people),
    total,
    page: safePage,
    size,
    totalPages,
    duplicateCount: duplicateCount(people),
    activeFilterCount,
    query
  };
}

export async function upsertPeople(rows: Array<Record<string, unknown>>) {
  return supabaseRest<PersonRecord[]>("persona?on_conflict=cedula", {
    method: "POST",
    body: JSON.stringify(rows),
    prefer: "resolution=merge-duplicates,return=representation"
  });
}

export async function updatePerson(id: number, values: Record<string, unknown>) {
  const rows = await supabaseRest<PersonRecord[]>(`persona?n=eq.${encodeFilter(id)}`, {
    method: "PATCH",
    body: JSON.stringify(values),
    prefer: "return=representation"
  });
  return rows[0] ?? null;
}

export async function deletePeople(ids: number[]) {
  if (ids.length === 0) return;
  const filter = ids.map((id) => Number(id)).filter(Number.isFinite).join(",");
  await supabaseRest<void>(`persona?n=in.(${filter})`, {
    method: "DELETE",
    prefer: "return=minimal"
  });
}
