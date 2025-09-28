// Paquetes de ejemplo (puedes reemplazar por fetch a futuro)
const paquetes = [
	{
		nombre: "Las Vegas",
		descripcion: "¡Aprovechá esta oferta!\nVUELO + HOTEL EN",
		imagen: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
		precioAnterior: 1322,
		precioActual: 1024,
		moneda: "USD"
	},
	{
		nombre: "Paquete Playa",
		descripcion: "7 días en Cancún con hotel y vuelos incluidos.",
		imagen: "https://th.bing.com/th/id/R.b8765c387d1af7495f214fa09d1dbec4?rik=7WmX%2fEPgAKFfMw&pid=ImgRaw&r=0",
		precioAnterior: 900,
		precioActual: 799,
		moneda: "USD"
	},
	{
		nombre: "Paquete Montaña",
		descripcion: "5 días en los Alpes con excursiones guiadas.",
		imagen: "https://th.bing.com/th/id/R.d374281c15e2727b0c950d1e4bc10870?rik=yjbDO0Mfj%2f7d%2bg&pid=ImgRaw&r=0",
		precioAnterior: 1100,
		precioActual: 950,
		moneda: "USD"
	},
	{
		nombre: "Paquete Ciudad",
		descripcion: "4 días en Nueva York con tours incluidos.",
		imagen: "https://a.cdn-hotels.com/gdcs/production101/d154/ee893f00-c31d-11e8-9739-0242ac110006.jpg",
		precioAnterior: 1200,
		precioActual: 999,
		moneda: "USD"
	},
	{
		nombre: "Paquete Selva",
		descripcion: "Aventura en la selva amazónica con guías expertos.",
		imagen: "https://tse1.mm.bing.net/th/id/OIP.5ZiS43i0f9eE7FROXboIngHaEK?rs=1&pid=ImgDetMain&o=7&rm=3",
		precioAnterior: 1500,
		precioActual: 1200,
		moneda: "USD"
	}
];
let current = 0;
let intervalId = null;
let carouselMounted = false;

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function renderPackage(idx) {
  const carousel = document.getElementById("package-carousel");
  if (!carousel) return; // guard
  const pkg = paquetes[idx];

  // solo la desc permite \n -> <br>
  const safeDesc = escapeHtml(pkg.descripcion).replace(/\n/g, "<br>");

  carousel.innerHTML = `
    <img src="${pkg.imagen}" alt="${escapeHtml(pkg.nombre)}"
         class="w-full aspect-[16/9] md:h-[420px] object-cover transition-all duration-700 rounded-3xl" />
    <div class="absolute inset-0 bg-black/40 rounded-3xl"></div>

    <!-- Flechas -->
    <button type="button" id="prev-package" aria-label="Anterior"
      class="absolute left-2 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-black/70
             text-white rounded-full w-12 h-12 flex items-center justify-center z-10 md:w-14 md:h-14 focus:outline-none">
      <svg class="w-6 h-6 md:w-8 md:h-8" fill="none" stroke="currentColor" stroke-width="2"
           viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
    </button>

    <button type="button" id="next-package" aria-label="Siguiente"
      class="absolute right-2 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-black/70
             text-white rounded-full w-12 h-12 flex items-center justify-center z-10 md:w-14 md:h-14 focus:outline-none">
      <svg class="w-6 h-6 md:w-8 md:h-8" fill="none" stroke="currentColor" stroke-width="2"
           viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
    </button>

    <div class="absolute bottom-0 left-0 w-full flex flex-col md:flex-row md:justify-between items-end p-4 md:p-8 gap-2 md:gap-0">
      <div class="text-white text-left w-full md:w-auto">
        <h3 class="text-2xl md:text-4xl font-bold mb-1 md:mb-2 drop-shadow">${escapeHtml(pkg.nombre)}</h3>
        <p class="text-base md:text-xl font-light leading-tight drop-shadow">${safeDesc}</p>
      </div>
      <div class="text-right flex flex-col items-end w-full md:w-auto mt-2 md:mt-0">
        <span class="text-gray-200 text-sm md:text-lg line-through">${pkg.moneda} ${pkg.precioAnterior}</span>
        <span class="text-2xl md:text-4xl font-extrabold text-white">${pkg.moneda} <span class="text-yellow-300">${pkg.precioActual}</span></span>
      </div>
    </div>
  `;

  // Re-vincular flechas del slide actual
  const prevBtn = document.getElementById("prev-package");
  const nextBtn = document.getElementById("next-package");
  if (prevBtn) prevBtn.onclick = prevPackage;
  if (nextBtn) nextBtn.onclick = nextPackage;

  // Montar swipe solo 1 vez en el contenedor
  if (!carouselMounted) {
    let startX = null;
    carousel.addEventListener("touchstart", e => {
      if (e.touches.length === 1) startX = e.touches[0].clientX;
    }, { passive: true });
    carousel.addEventListener("touchend", e => {
      if (startX !== null && e.changedTouches.length === 1) {
        const dx = e.changedTouches[0].clientX - startX;
        if (dx > 50) prevPackage();
        else if (dx < -50) nextPackage();
      }
      startX = null;
    }, { passive: true });
    carouselMounted = true;
  }
}

function prevPackage() {
  current = (current - 1 + paquetes.length) % paquetes.length;
  renderPackage(current);
  resetCarouselInterval();
}

function nextPackage() {
  current = (current + 1) % paquetes.length;
  renderPackage(current);
  resetCarouselInterval();
}

function resetCarouselInterval() {
  if (intervalId) clearInterval(intervalId);
  intervalId = setInterval(() => {
    current = (current + 1) % paquetes.length;
    renderPackage(current);
  }, 4000);
}

// Pausar cuando la pestaña no está visible
document.addEventListener("visibilitychange", () => {
  if (document.hidden) {
    if (intervalId) clearInterval(intervalId);
  } else {
    resetCarouselInterval();
  }
});

function startCarousel() {
  renderPackage(current);
  resetCarouselInterval();
}

// Inyección del componente
async function injectPackageList(containerId) {
  const host = document.getElementById(containerId);
  if (!host) return;
  try {
    const res = await fetch("src/views/components/packageList/packageList.html", { cache: "no-cache" });
    const html = await res.text();
    host.innerHTML = html;

    // Asegurate que en packageList.html exista <div id="package-carousel" class="relative">...</div>
    if (document.getElementById("package-carousel")) {
      startCarousel();
    }
  } catch (e) {
    console.error("No se pudo cargar el componente de paquetes:", e);
  }
}

// Auto-inyectar si existe el contenedor
if (document.getElementById("paquetes")) {
  injectPackageList("paquetes");
}