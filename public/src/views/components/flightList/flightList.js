// Datos de ejemplo
const vuelos = [
  { destino:"Cancún", descripcion:"El Caribe para disfrutar",
    imagen:"https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80",
    precioAnterior:881, precioActual:708, moneda:"USD" },
  { destino:"Madrid", descripcion:"Historia y cultura europea",
    imagen:"https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=400&q=80",
    precioAnterior:950, precioActual:799, moneda:"USD" },
  { destino:"Río de Janeiro", descripcion:"Playas y alegría brasileña",
    imagen:"https://images.unsplash.com/photo-1502082553048-f009c37129b9?auto=format&fit=crop&w=400&q=80",
    precioAnterior:700, precioActual:599, moneda:"USD" },
  { destino:"Barcelona", descripcion:"Arquitectura y costa mediterránea",
    imagen:"https://tse1.mm.bing.net/th/id/OIP.-OsQz4obH7-onrNwV2K8_QHaE8?rs=1&pid=ImgDetMain&o=7&rm=3",
    precioAnterior:700, precioActual:599, moneda:"USD" },
];

function renderFlightList() {
  const container = document.getElementById("flight-list-container");
  if (!container) return;

  const isCarousel = vuelos.length > 3;
  container.className = ""; // limpiar clases previas

if (isCarousel) {
  container.className = ""; // limpia
  container.classList.add(
    "flex","gap-8","w-full",
    "overflow-x-auto","scroll-smooth","no-scrollbar",
    "pr-6","-mr-6","pb-6","-mb-6",          
    "snap-x","snap-mandatory",              // <-- snap
    "overscroll-x-contain","select-none"    // UX nice
  );
  container.setAttribute("tabindex", "0");  // <-- para teclado
  container.innerHTML = vuelos.map(v => card(v, true)).join("");

  const prev = document.getElementById("flight-prev");
  const next = document.getElementById("flight-next");
  if (prev && next) {
    prev.classList.remove("hidden");
    next.classList.remove("hidden");
    const scrollAmount = 340;
    prev.onclick = () => container.scrollBy({ left: -scrollAmount, behavior: "smooth" });
    next.onclick = () => container.scrollBy({ left:  scrollAmount, behavior: "smooth" });
  }

  // teclado: ← / →
  container.onkeydown = (e) => {
    if (e.key === "ArrowLeft")  container.scrollBy({ left: -340, behavior: "smooth" });
    if (e.key === "ArrowRight") container.scrollBy({ left:  340, behavior: "smooth" });
  };
}

}

function card(vuelo, isCarousel) {
  return `
    <div class="bg-gray-50 rounded-2xl shadow-lg overflow-hidden flex flex-col ${isCarousel ? "min-w-[320px] max-w-xs w-full flex-shrink-0" : ""}">
      <div class="relative">
        <img src="${vuelo.imagen}" alt="${vuelo.destino}" class="w-full h-40 object-cover" />
        <div class="absolute bottom-0 left-0 w-full px-4 py-2 bg-gradient-to-t from-black/70 to-transparent flex flex-col">
          <div class="flex justify-between items-end">
            <span class="text-white text-xs font-light">VUELO</span>
            <div class="text-right">
              <span class="text-gray-200 text-xs line-through">${vuelo.moneda} ${vuelo.precioAnterior}</span><br>
              <span class="text-lg font-bold text-white">${vuelo.moneda} <span class="text-yellow-300">${vuelo.precioActual}</span></span>
            </div>
          </div>
        </div>
      </div>
      <div class="p-4 flex-1 flex flex-col justify-between">
        <h3 class="text-lg font-semibold text-gray-900 mb-1">${vuelo.destino}</h3>
        <p class="text-gray-500 text-sm">${vuelo.descripcion}</p>
      </div>  
    </div>
  `;
}

// Autoinyección si existe #vuelos
if (document.getElementById("vuelos")) {
  fetch("src/views/components/flightList/flightList.html")
    .then(res => res.text())
    .then(html => {
      document.getElementById("vuelos").innerHTML = html;
      renderFlightList();
    });
}
