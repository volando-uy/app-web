document.addEventListener("DOMContentLoaded", () => {
    const carouselEl = document.getElementById("pkg-carousel");
    if (!carouselEl) return;

    const slides = carouselEl.querySelectorAll(".js-slide");
    const prevBtn = document.getElementById("pkg-prev");
    const nextBtn = document.getElementById("pkg-next");
    let current = 0;

    if (!slides.length) return;

    function show(index) {
        slides[current].classList.add("hidden");
        current = (index + slides.length) % slides.length;
        slides[current].classList.remove("hidden");
    }

    prevBtn && prevBtn.addEventListener("click", () => show(current - 1));
    nextBtn && nextBtn.addEventListener("click", () => show(current + 1));
});
