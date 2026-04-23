document.addEventListener("DOMContentLoaded", () => {
    if (document.body.dataset.page !== "insertar") {
        return;
    }

    const form = document.getElementById("formulario-insercion");
    if (!form) {
        return;
    }

    const storageKey = "mihoja.insertar.draft";
    const estado = document.getElementById("estado");
    const fechaEgreso = document.getElementById("fechaEgreso");
    const draftBanner = document.createElement("div");
    draftBanner.className = "draft-banner is-hidden";
    draftBanner.innerHTML = `
        <span id="draftBannerText">Borrador local disponible.</span>
        <div class="draft-banner-actions">
            <button type="button" class="primary" id="restoreDraftButton">Restaurar</button>
            <button type="button" class="secondary" id="clearDraftButton">Descartar</button>
        </div>
    `;
    document.body.insertBefore(draftBanner, document.body.firstChild);

    const draftBannerText = document.getElementById("draftBannerText");
    const restoreDraftButton = document.getElementById("restoreDraftButton");
    const clearDraftButton = document.getElementById("clearDraftButton");

    function serializeForm() {
        const entries = {};
        new FormData(form).forEach((value, key) => {
            entries[key] = value;
        });
        localStorage.setItem(storageKey, JSON.stringify(entries));
    }

    function restoreForm(data) {
        Object.entries(data).forEach(([key, value]) => {
            const field = form.elements.namedItem(key);
            if (!(field instanceof RadioNodeList) && field instanceof HTMLElement && "value" in field) {
                field.value = value;
            }
        });
        syncEstadoFields();
        updateCommaCounters();
    }

    function clearDraft() {
        localStorage.removeItem(storageKey);
        draftBanner.classList.add("is-hidden");
        draftBannerText.textContent = "Borrador local eliminado.";
    }

    function syncEstadoFields() {
        if (!estado || !fechaEgreso) {
            return;
        }
        const isRetirado = estado.value?.toLowerCase() === "retirado";
        fechaEgreso.disabled = !isRetirado;
        fechaEgreso.required = isRetirado;
        const group = fechaEgreso.closest(".form-group");
        group?.classList.toggle("is-disabled", !isRetirado);
        group?.classList.toggle("is-highlighted", isRetirado);
        if (!isRetirado) {
            fechaEgreso.value = "";
        }
    }

    function countCommaItems(value) {
        return value
            .split(",")
            .map((item) => item.trim())
            .filter(Boolean).length;
    }

    function ensureCounter(fieldId, label) {
        const field = document.getElementById(fieldId);
        const group = field?.closest(".form-group");
        if (!field || !group) {
            return null;
        }
        let meta = group.querySelector(".field-meta");
        if (!meta) {
            meta = document.createElement("div");
            meta.className = "field-meta";
            meta.innerHTML = `<span>${label}</span><span class="field-chip">0 items</span>`;
            group.appendChild(meta);
        }
        return meta.querySelector(".field-chip");
    }

    const counterMap = [
        { id: "enfermedades", label: "Separar por comas" },
        { id: "alergias", label: "Separar por comas" },
        { id: "medicamentos", label: "Separar por comas" }
    ].map((entry) => ({ ...entry, chip: ensureCounter(entry.id, entry.label) }));

    function updateCommaCounters() {
        counterMap.forEach(({ id, chip }) => {
            const field = document.getElementById(id);
            if (!field || !chip) {
                return;
            }
            const count = countCommaItems(field.value || "");
            chip.textContent = `${count} item${count === 1 ? "" : "s"}`;
        });
    }

    const savedDraft = localStorage.getItem(storageKey);
    if (savedDraft) {
        draftBanner.classList.remove("is-hidden");
    }

    restoreDraftButton.addEventListener("click", () => {
        const rawDraft = localStorage.getItem(storageKey);
        if (!rawDraft) {
            return;
        }
        restoreForm(JSON.parse(rawDraft));
        draftBannerText.textContent = "Borrador restaurado.";
        draftBanner.classList.remove("is-hidden");
    });

    clearDraftButton.addEventListener("click", clearDraft);

    form.addEventListener("input", () => {
        serializeForm();
        updateCommaCounters();
        draftBanner.classList.remove("is-hidden");
        draftBannerText.textContent = "Cambios guardados localmente.";
    });

    form.addEventListener("reset", () => {
        clearDraft();
        setTimeout(() => {
            syncEstadoFields();
            updateCommaCounters();
        }, 0);
    });

    form.addEventListener("submit", () => {
        const submitButton = form.querySelector(".submit-btn");
        if (submitButton instanceof HTMLButtonElement) {
            submitButton.disabled = true;
            submitButton.textContent = "Guardando...";
            setTimeout(() => {
                submitButton.disabled = false;
                submitButton.textContent = "Insertar";
            }, 5000);
        }
    });

    estado?.addEventListener("change", syncEstadoFields);
    syncEstadoFields();
    updateCommaCounters();
});
