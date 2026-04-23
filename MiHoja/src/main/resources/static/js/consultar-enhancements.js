document.addEventListener("DOMContentLoaded", () => {
    if (document.body.dataset.page !== "consultar") {
        return;
    }

    const sidebarForm = document.querySelector(".sidebar-filtros form");
    const content = document.querySelector(".content");
    const cards = Array.from(document.querySelectorAll(".hoja-card"));
    const liveSearchInput = document.getElementById("liveSearchInput");
    const selectItems = Array.from(document.querySelectorAll(".select-item"));

    if (!sidebarForm || !content) {
        return;
    }

    document.body.classList.add("filters-enhanced");

    const toolbar = document.createElement("section");
    toolbar.className = "interactive-toolbar";
    toolbar.innerHTML = `
        <div class="interactive-toolbar-header">
            <div class="interactive-toolbar-copy">
                <h2 class="interactive-eyebrow interactive-eyebrow--title">Panel de filtros</h2>
                <p class="interactive-helper">Los cambios se aplican automaticamente mientras seleccionas opciones.</p>
            </div>
        </div>
        <input type="search" id="filterSectionSearch" placeholder="Buscar filtros o secciones">
        <div class="interactive-stats">
            <span class="interactive-pill" id="visibleCardsPill">0 visibles</span>
            <span class="interactive-pill" id="activeFiltersPill">0 filtros activos</span>
            <span class="interactive-pill" id="selectedCardsPill">0 seleccionadas</span>
        </div>
    `;
    sidebarForm.insertBefore(toolbar, sidebarForm.firstChild);

    const visibleCardsPill = document.getElementById("visibleCardsPill");
    const activeFiltersPill = document.getElementById("activeFiltersPill");
    const selectedCardsPill = document.getElementById("selectedCardsPill");
    const filterSectionSearch = document.getElementById("filterSectionSearch");
    const sections = Array.from(document.querySelectorAll(".filtro-seccion"));
    const submitButtons = Array.from(sidebarForm.querySelectorAll(".btn-filtro"));
    const filterCheckboxes = Array.from(sidebarForm.querySelectorAll("input[type='checkbox']"));
    let autoSubmitTimer = null;

    submitButtons.forEach((button) => {
        button.hidden = true;
        button.setAttribute("aria-hidden", "true");
        button.tabIndex = -1;
    });

    sections.forEach((section) => {
        section.classList.add("filter-card");

        const title = section.querySelector("h4");
        if (!title) {
            return;
        }

        const titleText = (title.textContent || "Filtro").trim();
        section.dataset.filterTitle = titleText;

        const wrapper = document.createElement("div");
        wrapper.className = "filter-section-content";

        Array.from(section.children).forEach((child) => {
            if (child !== title) {
                wrapper.appendChild(child);
            }
        });

        wrapper.querySelectorAll("br").forEach((node) => node.remove());

        const options = document.createElement("div");
        options.className = "filter-options";

        Array.from(wrapper.children).forEach((child) => {
            if (child.tagName === "LABEL") {
                options.appendChild(child);
            }
        });

        if (options.children.length > 0) {
            wrapper.prepend(options);
        }

        const heading = document.createElement("button");
        heading.type = "button";
        heading.className = "filter-heading-btn";
        heading.setAttribute("aria-expanded", "false");
        heading.innerHTML = `
            <span class="filter-heading-copy">
                <span class="filter-heading-title">${titleText}</span>
                <span class="filter-heading-meta">0 seleccionadas</span>
            </span>
        `;

        title.replaceWith(heading);
        section.appendChild(wrapper);

        wrapper.classList.add("is-collapsed");

        heading.addEventListener("click", () => {
            const expanded = heading.getAttribute("aria-expanded") === "true";
            heading.setAttribute("aria-expanded", expanded ? "false" : "true");
            wrapper.classList.toggle("is-collapsed", expanded);
        });
    });

    function normalizeText(value) {
        return (value || "")
            .toString()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase()
            .trim();
    }

    function updateSectionMeta() {
        sections.forEach((section) => {
            const checkedCount = section.querySelectorAll("input[type='checkbox']:checked").length;
            const meta = section.querySelector(".filter-heading-meta");
            if (meta) {
                meta.textContent = checkedCount === 0 ? "Sin seleccion" : `${checkedCount} seleccionada${checkedCount > 1 ? "s" : ""}`;
            }
            section.classList.toggle("has-active-filters", checkedCount > 0);
        });
    }

    function updateCardStats() {
        const visibleCount = cards.filter((card) => card.style.display !== "none").length;
        const selectedCount = selectItems.filter((checkbox) => checkbox.checked).length;
        const activeFilters = filterCheckboxes.filter((checkbox) => checkbox.checked).length;

        visibleCardsPill.textContent = `${visibleCount} visibles`;
        selectedCardsPill.textContent = `${selectedCount} seleccionadas`;
        activeFiltersPill.textContent = `${activeFilters} filtros activos`;
        updateSectionMeta();
    }

    function filterSections() {
        const query = normalizeText(filterSectionSearch.value);
        sections.forEach((section) => {
            const title = normalizeText(section.dataset.filterTitle || section.textContent);
            const matches = !query || title.includes(query);
            section.style.display = matches ? "" : "none";
        });
    }

    function focusSearchShortcut(event) {
        const target = event.target;
        const tagName = target instanceof HTMLElement ? target.tagName : "";
        const isTypingContext = ["INPUT", "TEXTAREA", "SELECT"].includes(tagName);
        if (event.key === "/" && !isTypingContext) {
            event.preventDefault();
            liveSearchInput?.focus();
            liveSearchInput?.select();
        }
    }

    function requestSidebarSubmit() {
        window.clearTimeout(autoSubmitTimer);
        autoSubmitTimer = window.setTimeout(() => {
            sidebarForm.requestSubmit();
        }, 180);
    }

    filterCheckboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", () => {
            updateCardStats();
            requestSidebarSubmit();
        });
    });

    filterSectionSearch.addEventListener("input", filterSections);
    document.addEventListener("keydown", focusSearchShortcut);
    selectItems.forEach((checkbox) => checkbox.addEventListener("change", updateCardStats));

    const observer = new MutationObserver(updateCardStats);
    cards.forEach((card) => observer.observe(card, { attributes: true, attributeFilter: ["style"] }));

    updateCardStats();
    filterSections();
});
