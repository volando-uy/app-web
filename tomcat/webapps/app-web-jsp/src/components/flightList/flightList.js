// flightList.js — carrusel horizontal + modal de detalle de vuelo
(function () {
    // ===== Modal infra =====
    function ensureModalRoot(){
        var r = document.getElementById("modal-root");
        if(!r){ r = document.createElement("div"); r.id = "modal-root"; document.body.appendChild(r); }
        return r;
    }
    function closeModal(){
        var r = ensureModalRoot();
        r.innerHTML = "";
        document.body.classList.remove("overflow-hidden");
    }
    function openModal(html){
        var r = ensureModalRoot();
        document.body.classList.add("overflow-hidden");
        r.innerHTML = `
      <div class="fixed inset-0 z-[80]">
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm"></div>
        <div class="absolute inset-0 flex items-end sm:items-center justify-center p-3 sm:p-6">
          <div role="dialog" aria-modal="true"
               class="w-full sm:max-w-2xl bg-white rounded-2xl shadow-2xl overflow-hidden">
            ${html}
          </div>
        </div>
      </div>`;
        r.firstElementChild.firstElementChild.addEventListener("click", closeModal, {once:true});
        var onEsc = function(e){ if(e.key==="Escape"){ closeModal(); document.removeEventListener("keydown", onEsc);} };
        document.addEventListener("keydown", onEsc);
    }

    function minsToHM(mins){
        var n = parseInt(mins,10);
        if (isNaN(n) || n<0) return "--";
        var h = Math.floor(n/60), m = n%60;
        return h>0 ? (h+" h "+m+" min") : (m+" min");
    }

    function renderFlightModal(d){
        var durHM = minsToHM(d.duration);
        var headerImg = d.image
            ? `<img src="${d.image}" alt="${d.name}" class="w-full h-40 object-cover">`
            : `<div class="w-full h-40 bg-gradient-to-r from-sky-700 to-gray-800"></div>`;

        var html = `
      <header class="relative">
        ${headerImg}
        <button type="button" class="absolute top-3 right-3 rounded-full bg-white/90 hover:bg-white p-2 shadow" id="flight-close" aria-label="Cerrar">✕</button>
        <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/60 to-transparent text-white">
          <h3 class="text-lg font-bold">${d.name}</h3>
        </div>
      </header>

      <section class="p-4 sm:p-6 space-y-4 text-sm">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div class="p-3 rounded-lg bg-gray-50">
            <div class="text-gray-500">Sale</div>
            <div class="font-semibold">${d.dep}</div>
          </div>
          <div class="p-3 rounded-lg bg-gray-50">
            <div class="text-gray-500">Duración</div>
            <div class="font-semibold">${durHM}</div>
          </div>
          <div class="p-3 rounded-lg bg-gray-50">
            <div class="text-gray-500">Asientos Económica (máx.)</div>
            <div class="font-semibold">${d.economy || "--"}</div>
          </div>
          <div class="p-3 rounded-lg bg-gray-50">
            <div class="text-gray-500">Asientos Business (máx.)</div>
            <div class="font-semibold">${d.business || "--"}</div>
          </div>
        </div>

        <div class="flex items-center justify-between text-xs text-gray-500">
          <div>Creado: <span class="font-medium">${d.created || "--"}</span></div>
          <div>Nombre interno: <span class="font-medium">${d.name}</span></div>
        </div>

        <div class="flex items-center justify-end gap-2">
          <button type="button" id="flight-cancel" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Cerrar</button>
          <a href="/app-web-jsp/flight/list" class="px-4 py-2 rounded-lg bg-[#0B4C73] text-white hover:brightness-110">Ver todos los vuelos</a>
        </div>
      </section>
    `;
        openModal(html);
        document.getElementById("flight-close")?.addEventListener("click", closeModal);
        document.getElementById("flight-cancel")?.addEventListener("click", closeModal);
    }

    // ===== Carrusel =====
    function initCarousel(){
        var container = document.getElementById("flight-list-container");
        if (!container) return;

        var isCarousel = container.classList.contains("snap-x");
        var prev = document.getElementById("flight-prev");
        var next = document.getElementById("flight-next");

        // Click en card -> modal
        Array.prototype.forEach.call(document.querySelectorAll(".js-flight-card"), function(card){
            card.addEventListener("click", function(){
                renderFlightModal({
                    name: card.dataset.name || "Vuelo",
                    image: card.dataset.image || "",
                    dep: card.dataset.dep || "--",
                    duration: card.dataset.duration || "--",
                    economy: card.dataset.economy || "--",
                    business: card.dataset.business || "--",
                    created: card.dataset.created || "--"
                });
            });
        });

        if (!isCarousel || !prev || !next) return;

        var firstCard = container.querySelector("article");
        var STEP = firstCard ? (firstCard.getBoundingClientRect().width + 24) : 340;

        function scrollByStep(dir){ container.scrollBy({ left: dir * STEP, behavior: "smooth" }); }
        function updateArrows(){
            var maxScroll = container.scrollWidth - container.clientWidth - 1;
            var x = container.scrollLeft;
            (x <= 0) ? prev.classList.add("hidden") : prev.classList.remove("hidden");
            (x >= maxScroll) ? next.classList.add("hidden") : next.classList.remove("hidden");
        }

        prev.addEventListener("click", function(){ scrollByStep(-1); });
        next.addEventListener("click", function(){ scrollByStep( 1); });

        container.tabIndex = 0;
        container.addEventListener("keydown", function (e) {
            if (e.key === "ArrowLeft")  scrollByStep(-1);
            if (e.key === "ArrowRight") scrollByStep( 1);
        });

        container.addEventListener("wheel", function (e) {
            if (Math.abs(e.deltaY) > Math.abs(e.deltaX)) {
                container.scrollBy({ left: e.deltaY, behavior: "auto" });
                e.preventDefault();
            }
        }, { passive: false });

        container.addEventListener("scroll", updateArrows, { passive: true });
        window.addEventListener("resize", function () {
            var c = container.querySelector("article");
            STEP = c ? (c.getBoundingClientRect().width + 24) : 340;
            updateArrows();
        });

        updateArrows();
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initCarousel);
    } else {
        initCarousel();
    }
})();
