// ====== Datos de ejemplo extendidos: paquetes + rutas ======
const paquetes = [
  {
    id: "PKG-VEGAS",
    nombre: "Las Vegas",
    descripcion: "¡Aprovechá esta oferta!\nVUELO + HOTEL EN",
    imagen: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
    precioAnterior: 1322,
    precioActual: 1024,
    moneda: "USD",
    rutas: [
      { id: "FR-001", codigo: "UY123", origen: "MVD", destino: "MIA", tipo: "Internacional", cantidad: 1, duracionHs: 9.5, escalas: 0, precioUnitario: 580, aerolinea: "CieloSur", avion: "A320", salida: "2025-11-05 08:30", llegada: "2025-11-05 18:00" },
      { id: "FR-002", codigo: "US456", origen: "MIA", destino: "LAS", tipo: "Doméstica",    cantidad: 1, duracionHs: 5.2, escalas: 0, precioUnitario: 240, aerolinea: "SkyUS",    avion: "B737", salida: "2025-11-06 10:00", llegada: "2025-11-06 15:12" }
    ]
  },
  {
    id: "PKG-CANCUN",
    nombre: "Paquete Playa",
    descripcion: "7 días en Cancún con hotel y vuelos incluidos.",
    imagen: "https://th.bing.com/th/id/R.b8765c387d1af7495f214fa09d1dbec4?rik=7WmX%2fEPgAKFfMw&pid=ImgRaw&r=0",
    precioAnterior: 900,
    precioActual: 799,
    moneda: "USD",
    rutas: [
      { id: "FR-101", codigo: "UY777", origen: "MVD", destino: "PTY", tipo: "Internacional", cantidad: 1, duracionHs: 7.8, escalas: 0, precioUnitario: 420, aerolinea: "CieloSur", avion: "A321", salida: "2025-12-01 09:00", llegada: "2025-12-01 16:48" },
      { id: "FR-102", codigo: "PA888", origen: "PTY", destino: "CUN", tipo: "Internacional", cantidad: 1, duracionHs: 2.3, escalas: 0, precioUnitario: 160, aerolinea: "PanAir",   avion: "E190", salida: "2025-12-01 18:00", llegada: "2025-12-01 20:18" }
    ]
  },
  {
    id: "PKG-ALPES",
    nombre: "Paquete Montaña",
    descripcion: "5 días en los Alpes con excursiones guiadas.",
    imagen: "https://th.bing.com/th/id/R.d374281c15e2727b0c950d1e4bc10870?rik=yjbDO0Mfj%2f7d%2bg&pid=ImgRaw&r=0",
    precioAnterior: 1100,
    precioActual: 950,
    moneda: "USD",
    rutas: [
      { id: "FR-201", codigo: "UY900", origen: "MVD", destino: "MAD", tipo: "Internacional", cantidad: 1, duracionHs: 11.2, escalas: 0, precioUnitario: 650, aerolinea: "CieloSur", avion: "A330", salida: "2025-12-10 20:00", llegada: "2025-12-11 09:12" },
      { id: "FR-202", codigo: "EU333", origen: "MAD", destino: "GVA", tipo: "Doméstica EU", cantidad: 1, duracionHs: 1.8, escalas: 0, precioUnitario: 120, aerolinea: "EuroFly",  avion: "A319", salida: "2025-12-11 11:00", llegada: "2025-12-11 12:48" }
    ]
  },
  {
    id: "PKG-NY",
    nombre: "Paquete Ciudad",
    descripcion: "4 días en Nueva York con tours incluidos.",
    imagen: "https://a.cdn-hotels.com/gdcs/production101/d154/ee893f00-c31d-11e8-9739-0242ac110006.jpg",
    precioAnterior: 1200,
    precioActual: 999,
    moneda: "USD",
    rutas: [
      { id: "FR-301", codigo: "UY555", origen: "MVD", destino: "JFK", tipo: "Internacional", cantidad: 1, duracionHs: 10.7, escalas: 0, precioUnitario: 620, aerolinea: "CieloSur", avion: "B787", salida: "2025-12-20 21:00", llegada: "2025-12-21 09:42" }
    ]
  },
  {
    id: "PKG-AMAZONAS",
    nombre: "Paquete Selva",
    descripcion: "Aventura en la selva amazónica con guías expertos.",
    imagen: "https://tse1.mm.bing.net/th/id/OIP.5ZiS43i0f9eE7FROXboIngHaEK?rs=1&pid=ImgDetMain&o=7&rm=3",
    precioAnterior: 1500,
    precioActual: 1200,
    moneda: "USD",
    rutas: [
      { id: "FR-401", codigo: "UY321", origen: "MVD", destino: "GRU", tipo: "Regional", cantidad: 1, duracionHs: 2.4, escalas: 0, precioUnitario: 210, aerolinea: "CieloSur", avion: "A320", salida: "2025-12-05 07:30", llegada: "2025-12-05 09:54" },
      { id: "FR-402", codigo: "BR654", origen: "GRU", destino: "MAO", tipo: "Doméstica BR", cantidad: 1, duracionHs: 4.0, escalas: 0, precioUnitario: 200, aerolinea: "BrasilAir", avion: "B737", salida: "2025-12-05 12:00", llegada: "2025-12-05 16:00" }
    ]
  }
];

let current = 0;
let intervalId = null;
let carouselMounted = false;

// ====== Utils ======
function $(sel, ctx=document){ return ctx.querySelector(sel); }
function $all(sel, ctx=document){ return Array.from(ctx.querySelectorAll(sel)); }
function escapeHtml(str){ return String(str).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }
function br(str){ return escapeHtml(str).replace(/\n/g, "<br>"); }

// ====== Modal infra ======
function ensureModalRoot(){
  let root = document.getElementById('modal-root');
  if(!root){
    root = document.createElement('div');
    root.id = 'modal-root';
    document.body.appendChild(root);
  }
  return root;
}

function closeModal(){
  const root = ensureModalRoot();
  root.innerHTML = '';
  document.body.classList.remove('overflow-hidden');
  // devuelve el foco si existía un botón invocador
  const last = document.activeElement;
  if(last && typeof last.blur === 'function') last.blur();
}

function openModal(html){
  const root = ensureModalRoot();
  document.body.classList.add('overflow-hidden');
  root.innerHTML = `
    <div class="fixed inset-0 z-[70]">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>
      <div class="absolute inset-0 flex items-end sm:items-center justify-center p-3 sm:p-6">
        <div role="dialog" aria-modal="true"
             class="w-full sm:max-w-2xl bg-white rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in duration-150">
          ${html}
        </div>
      </div>
    </div>
  `;

  // cerrar por overlay / ESC
  const overlay = root.firstElementChild.firstElementChild;
  overlay.addEventListener('click', closeModal, { once: true });
  document.addEventListener('keydown', escHandler, { once: true });

  function escHandler(e){ if(e.key === 'Escape') closeModal(); }

  // focus primer botón dentro del modal
  const firstBtn = root.querySelector('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
  firstBtn?.focus();
}

// ====== Render modal de Paquete ======
function renderPackageModal(pkg){
  const totalRutas = pkg.rutas.reduce((acc,r)=> acc + (r.precioUnitario * r.cantidad), 0);
  const html = `
    <header class="relative">
      <img src="${pkg.imagen}" alt="${escapeHtml(pkg.nombre)}" class="w-full h-40 object-cover">
      <button type="button" class="absolute top-3 right-3 rounded-full bg-white/90 hover:bg-white p-2 shadow" id="pkg-close" aria-label="Cerrar">
        ✕
      </button>
      <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/60 to-transparent text-white">
        <h3 class="text-xl font-bold">${escapeHtml(pkg.nombre)}</h3>
        <p class="text-sm opacity-90">${br(pkg.descripcion)}</p>
      </div>
    </header>

    <section class="p-4 sm:p-6 space-y-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <div class="text-gray-700">
          <div class="line-through text-sm">${pkg.moneda} ${pkg.precioAnterior}</div>
          <div class="text-2xl font-extrabold">${pkg.moneda} <span class="text-emerald-600">${pkg.precioActual}</span></div>
        </div>
        <div class="text-right text-gray-700">
          <div class="text-xs uppercase tracking-wide">Suma rutas (ref.)</div>
          <div class="font-semibold">${pkg.moneda} ${totalRutas}</div>
        </div>
      </div>

      <div class="border rounded-xl overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-gray-50 text-gray-600">
            <tr>
              <th class="text-left px-3 py-2">Ruta</th>
              <th class="text-left px-3 py-2">Tipo</th>
              <th class="text-center px-3 py-2">Cant.</th>
              <th class="text-right px-3 py-2">Precio unit.</th>
            </tr>
          </thead>
          <tbody>
            ${pkg.rutas.map(r => `
              <tr class="border-t hover:bg-gray-50 cursor-pointer" data-route-id="${r.id}">
                <td class="px-3 py-2 font-medium">${escapeHtml(r.origen)} → ${escapeHtml(r.destino)} <span class="text-gray-400 ml-1">(${escapeHtml(r.codigo)})</span></td>
                <td class="px-3 py-2">${escapeHtml(r.tipo)}</td>
                <td class="px-3 py-2 text-center">${r.cantidad}</td>
                <td class="px-3 py-2 text-right">${pkg.moneda} ${r.precioUnitario}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-end gap-2">
        <button type="button" id="pkg-cancel" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Cerrar</button>
        <a href="#" class="px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110 nav-link" data-target="package/package.html">Ver más paquetes</a>
      </div>
    </section>
  `;

  openModal(html);

  // Listeners
  $('#pkg-close')?.addEventListener('click', closeModal);
  $('#pkg-cancel')?.addEventListener('click', closeModal);

  // Click en fila de ruta → detalle de ruta
  $all('tr[data-route-id]').forEach(tr => {
    tr.addEventListener('click', () => {
      const rid = tr.getAttribute('data-route-id');
      const route = pkg.rutas.find(r => r.id === rid);
      if(route) renderRouteModal(route, pkg);
    });
  });
}

// ====== Render modal de Ruta (Consulta de Ruta de Vuelo) ======
function renderRouteModal(route, pkg){
  const html = `
    <header class="p-4 sm:p-5 border-b flex items-start justify-between">
      <div>
        <h3 class="text-lg font-bold">Ruta ${escapeHtml(route.codigo)} — ${escapeHtml(route.origen)} → ${escapeHtml(route.destino)}</h3>
        <p class="text-sm text-gray-500">${escapeHtml(route.tipo)} • ${route.duracionHs} h • ${route.escalas} escalas</p>
      </div>
      <button type="button" class="rounded-full bg-gray-100 hover:bg-gray-200 p-2" id="route-close" aria-label="Cerrar">✕</button>
    </header>

    <section class="p-4 sm:p-6 space-y-4">
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm">
        <div class="p-3 rounded-lg bg-gray-50">
          <div class="text-gray-500">Aerolínea</div>
          <div class="font-medium">${escapeHtml(route.aerolinea)}</div>
        </div>
        <div class="p-3 rounded-lg bg-gray-50">
          <div class="text-gray-500">Avión</div>
          <div class="font-medium">${escapeHtml(route.avion)}</div>
        </div>
        <div class="p-3 rounded-lg bg-gray-50">
          <div class="text-gray-500">Salida</div>
          <div class="font-medium">${escapeHtml(route.salida)}</div>
        </div>
        <div class="p-3 rounded-lg bg-gray-50">
          <div class="text-gray-500">Llegada</div>
          <div class="font-medium">${escapeHtml(route.llegada)}</div>
        </div>
      </div>

      <div class="flex items-center justify-between">
        <div class="text-gray-600">Cantidad en paquete: <span class="font-semibold">${route.cantidad}</span></div>
        <div class="text-right">
          <div class="text-xs uppercase tracking-wide text-gray-500">Precio unitario</div>
          <div class="text-lg font-bold">${escapeHtml(pkg.moneda)} ${route.precioUnitario}</div>
        </div>
      </div>

      <div class="flex items-center justify-end gap-2">
        <button type="button" id="route-back" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Volver al paquete</button>
        <a href="#" class="nav-link px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110" data-target="flightf/flight.html">Ver rutas</a>
      </div>
    </section>
  `;
  openModal(html);
  $('#route-close')?.addEventListener('click', () => renderPackageModal(pkg)); // vuelve al paquete
  $('#route-back')?.addEventListener('click', () => renderPackageModal(pkg));
}

// ====== Carrusel (con botón "Ver paquete") ======
function renderPackage(idx) {
  const carousel = document.getElementById("package-carousel");
  if (!carousel) return;
  const pkg = paquetes[idx];

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

    <!-- Info y CTA -->
    <div class="absolute bottom-0 left-0 w-full flex flex-col md:flex-row md:justify-between items-end p-4 md:p-8 gap-2 md:gap-0">
      <div class="text-white text-left w-full md:w-auto">
        <h3 class="text-2xl md:text-4xl font-bold mb-1 md:mb-2 drop-shadow">${escapeHtml(pkg.nombre)}</h3>
        <p class="text-base md:text-xl font-light leading-tight drop-shadow">${br(pkg.descripcion)}</p>
      </div>
      <div class="text-right flex flex-col items-end w-full md:w-auto mt-2 md:mt-0">
        <span class="text-gray-200 text-sm md:text-lg line-through">${pkg.moneda} ${pkg.precioAnterior}</span>
        <span class="text-2xl md:text-4xl font-extrabold text-white">${pkg.moneda} <span class="text-yellow-300">${pkg.precioActual}</span></span>
        <button type="button" id="btn-ver-paquete" class="mt-3 px-4 py-2 rounded-lg bg-yellow-400 text-black font-semibold hover:brightness-110 shadow">
          Ver paquete
        </button>
      </div>
    </div>
  `;

  // Navegación
  const prevBtn = document.getElementById("prev-package");
  const nextBtn = document.getElementById("next-package");
  prevBtn && (prevBtn.onclick = prevPackage);
  nextBtn && (nextBtn.onclick = nextPackage);

  // CTA modal
  const btnVer = document.getElementById('btn-ver-paquete');
  btnVer?.addEventListener('click', () => renderPackageModal(pkg));

  // Swipe una vez
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

// ====== Inyección del componente (respeta tu fetch actual) ======
async function injectPackageList(containerId) {
  const host = document.getElementById(containerId);
  if (!host) return;
  try {
    const res = await fetch("views/components/packageList/packageList.jsp", { cache: "no-cache" });
    const html = await res.text();
    host.innerHTML = html;

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
