// index.js

(function () {
    function getBase() {
        if (window.__BASE__) return window.__BASE__;
        var m = location.pathname.match(/^\/[^/]+/);
        return m ? m[0] : "";
    }
    var BASE = getBase();

    function $(sel, ctx) { return (ctx || document).querySelector(sel); }

    function animateOnScroll(el) {
        if (!el) return;
        el.classList.add("opacity-0", "translate-y-8", "transition-all", "duration-700");
        var obs = new IntersectionObserver(function (entries, o) {
            entries.forEach(function (e) {
                if (e.isIntersecting) {
                    e.target.classList.add("opacity-100", "translate-y-0");
                    e.target.classList.remove("opacity-0", "translate-y-8");
                    o.unobserve(e.target);
                }
            });
        }, { threshold: 0.18 });
        obs.observe(el);
    }

    async function injectHeader() {
        var container = $("#header-container");
        if (!container) return;
        try {
            var res = await fetch(BASE + "/src/views/header/header.html", { cache: "no-cache" });
            if (!res.ok) throw new Error("HTTP " + res.status);
            container.innerHTML = await res.text();

            // Cargar el JS del header (si existe)
            var script = document.createElement("script");
            script.src = BASE + "/src/views/header/header.js";
            script.defer = true;
            script.onload = function () {
                try { if (typeof window.initHeader === "function") window.initHeader(); } catch (_) {}
            };
            document.body.appendChild(script);
        } catch (err) {
            console.warn("[header] No se pudo inyectar:", err);
        }
    }

    async function injectFooter() {
        var container = $("#footer-container");
        if (!container) return;
        try {
            var res = await fetch(BASE + "/src/views/footer/footer.html", { cache: "no-cache" });
            if (!res.ok) throw new Error("HTTP " + res.status);
            container.innerHTML = await res.text();

        } catch (err) {
            console.warn("[footer] No se pudo inyectar:", err);
        }
    }

    function init() {
         injectHeader();
        injectFooter();

        // Animaciones suaves
        animateOnScroll(document.getElementById("paquetes"));
        animateOnScroll(document.getElementById("vuelos"));
        animateOnScroll(document.getElementById("footer-container"));
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
