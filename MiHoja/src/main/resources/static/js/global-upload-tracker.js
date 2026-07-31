document.addEventListener("DOMContentLoaded", () => {
    const STORAGE_KEY = "mihoja.activeUploadJob";
    let tracker = document.getElementById("globalUploadTracker");
    let pollTimer = null;

    function ensureTracker() {
        if (tracker) {
            return tracker;
        }
        tracker = document.createElement("div");
        tracker.id = "globalUploadTracker";
        tracker.className = "global-upload-tracker is-hidden";
        tracker.innerHTML = `
            <div class="progress-orb" aria-hidden="true">
                <span></span>
                <span></span>
                <span></span>
            </div>
            <div class="progress-copy">
                <strong id="globalUploadTitle">Procesando carga</strong>
                <span id="globalUploadMessage">Importando datos...</span>
            </div>
        `;
        document.body.appendChild(tracker);
        return tracker;
    }

    function showTracker(title, message) {
        const node = ensureTracker();
        node.classList.remove("is-hidden");
        const titleNode = document.getElementById("globalUploadTitle");
        const messageNode = document.getElementById("globalUploadMessage");
        if (titleNode) titleNode.textContent = title;
        if (messageNode) messageNode.textContent = message;
    }

    function hideTracker() {
        ensureTracker().classList.add("is-hidden");
    }

    function showToast(text, ok) {
        const toast = document.createElement("div");
        toast.className = "toast " + (ok ? "success" : "error");
        toast.textContent = text;
        document.body.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 500);
        }, 3600);
    }

    async function pollJob(jobId) {
        try {
            const response = await fetch(`/api/upload-jobs/${jobId}`);
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || "No fue posible consultar la carga.");
            }
            showTracker(data.title || "Procesando carga", data.message || "Importando datos...");

            if (data.status === "completed") {
                localStorage.removeItem(STORAGE_KEY);
                hideTracker();
                showToast(data.message || "Carga completada.", true);
                window.clearInterval(pollTimer);
                return;
            }

            if (data.status === "failed") {
                localStorage.removeItem(STORAGE_KEY);
                hideTracker();
                showToast(data.message || "La carga falló.", false);
                window.clearInterval(pollTimer);
            }
        } catch (error) {
            hideTracker();
        }
    }

    window.miHojaTrackUpload = (jobId) => {
        localStorage.setItem(STORAGE_KEY, jobId);
        showTracker("Procesando carga", "Importando datos del archivo...");
        window.clearInterval(pollTimer);
        pollTimer = window.setInterval(() => pollJob(jobId), 1400);
        pollJob(jobId);
    };

    const activeJob = localStorage.getItem(STORAGE_KEY);
    if (activeJob) {
        window.miHojaTrackUpload(activeJob);
    }
});
