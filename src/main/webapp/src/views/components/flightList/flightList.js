(function(){
    function init() {
        var container = document.getElementById("flight-list-container");
        if (!container) return;

        var prev = document.getElementById("flight-prev");
        var next = document.getElementById("flight-next");
        if (!prev || !next) return; // no hay carrusel

        var STEP = 340;
        prev.addEventListener("click", function(){
            container.scrollBy({ left: -STEP, behavior: "smooth" });
        });
        next.addEventListener("click", function(){
            container.scrollBy({ left:  STEP, behavior: "smooth" });
        });

        container.tabIndex = 0;
        container.addEventListener("keydown", function(e){
            if (e.key === "ArrowLeft")  container.scrollBy({ left: -STEP, behavior: "smooth" });
            if (e.key === "ArrowRight") container.scrollBy({ left:  STEP, behavior: "smooth" });
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
