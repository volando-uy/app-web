// index.js
document.addEventListener("DOMContentLoaded", () => {
    const slidesContainer = document.getElementById("package-slides");
    const slides = slidesContainer?.children || [];
    const prevBtn = document.getElementById("carousel-prev");
    const nextBtn = document.getElementById("carousel-next");
    let index = 0;

    function updateCarousel() {
        const offset = -index * 100;
        slidesContainer.style.transform = `translateX(${offset}%)`;
    }

    prevBtn?.addEventListener("click", () => {
        if (index > 0) {
            index--;
            updateCarousel();
        }
    });

    nextBtn?.addEventListener("click", () => {
        if (index < slides.length - 1) {
            index++;
            updateCarousel();
        }
    });

    // Optional: swipe support
    let startX = 0;
    slidesContainer.addEventListener("touchstart", (e) => {
        startX = e.touches[0].clientX;
    });

    slidesContainer.addEventListener("touchend", (e) => {
        const endX = e.changedTouches[0].clientX;
        const diff = startX - endX;
        if (diff > 50 && index < slides.length - 1) {
            index++;
            updateCarousel();
        } else if (diff < -50 && index > 0) {
            index--;
            updateCarousel();
        }
    });
});
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

    function init() {

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
