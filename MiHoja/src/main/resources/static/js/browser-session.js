(function () {
    "use strict";

    var KEY = "mihoja.browserSession.clientId";
    var PING_ENDPOINT = "/internal/browser-session/ping";
    var END_ENDPOINT = "/internal/browser-session/end";
    var HEARTBEAT_MS = 15000;

    function getClientId() {
        try {
            var current = localStorage.getItem(KEY);
            if (current) {
                return current;
            }
            var created = (crypto.randomUUID && crypto.randomUUID()) || ("client-" + Date.now() + "-" + Math.random());
            localStorage.setItem(KEY, created);
            return created;
        } catch (error) {
            return "client-" + Date.now();
        }
    }

    var clientId = getClientId();

    function post(endpoint, keepalive) {
        return fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ clientId: clientId }),
            keepalive: !!keepalive,
            credentials: "same-origin"
        }).catch(function () {
            // Silently ignore transient network errors in client heartbeat.
        });
    }

    function ping() {
        post(PING_ENDPOINT, false);
    }

    function end() {
        var payload = JSON.stringify({ clientId: clientId });
        if (navigator.sendBeacon) {
            var blob = new Blob([payload], { type: "application/json" });
            navigator.sendBeacon(END_ENDPOINT, blob);
            return;
        }
        post(END_ENDPOINT, true);
    }

    ping();
    setInterval(ping, HEARTBEAT_MS);
    window.addEventListener("focus", ping);
    window.addEventListener("beforeunload", end);
})();
