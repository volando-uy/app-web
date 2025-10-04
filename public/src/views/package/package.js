/* ======================= Header/Footer dinámicos ======================= */
// Traigo el header por fetch y después cargo su JS (si no, initHeader no existe)
fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    document.getElementById("header").innerHTML = data;
    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => { 
      // acá inicializo el menú; si no está aún el DOM, el header.js ya maneja eso
      if (typeof initHeader === "function") initHeader();
    };
    document.body.appendChild(script);
  });

// Footer a la vieja usanza, total es puro HTML
function importFooter() {
  fetch("../footer/footer.html")
    .then(res => res.text())
    .then(data => { document.getElementById("footer").innerHTML = data; });
}
if (document.getElementById("footer")) importFooter();

/* ======================= Datos de ejemplo ======================= */
/* Nota estudiante: metí rutas detalladas para poder cumplir el “tipos y cantidades”.
   La idea es que después esto salga de un endpoint (GET /packages y GET /flightroute/:id). */
const paquetes = [
  {
    id: "PKG-BA-CBA",
    nombre: "Buenos Aires - Córdoba",
    imagen: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80",
    duracion: "2 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 120,
    moneda: "USD",
    color: "bg-[#c0392b]",
    iconos: ["fa-solid fa-bus"],
    rutas: [
      { id:"FR-BA-CBA-001", codigo:"UY101", origen:"BUE", destino:"COR", tipo:"Doméstica", cantidad:1, duracionHs:1.2, escalas:0, precioUnitario:70, aerolinea:"CieloSur", avion:"E190", salida:"2025-11-01 08:00", llegada:"2025-11-01 09:12" },
      { id:"FR-BA-CBA-002", codigo:"UY102", origen:"COR", destino:"BUE", tipo:"Doméstica", cantidad:1, duracionHs:1.2, escalas:0, precioUnitario:50, aerolinea:"CieloSur", avion:"E190", salida:"2025-11-03 18:00", llegada:"2025-11-03 19:12" }
    ]
  },
  {
    id: "PKG-SCL-MDZ",
    nombre: "Santiago de Chile - Mendoza",
    imagen: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
    duracion: "3 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 180,
    moneda: "USD",
    color: "bg-[#6c3483]",
    iconos: ["fa-solid fa-bus"],
    rutas: [
      { id:"FR-SCL-MDZ-001", codigo:"CL300", origen:"SCL", destino:"MDZ", tipo:"Regional", cantidad:1, duracionHs:1.0, escalas:0, precioUnitario:95, aerolinea:"AndesAir", avion:"A320", salida:"2025-12-05 09:00", llegada:"2025-12-05 10:00" },
      { id:"FR-SCL-MDZ-002", codigo:"CL301", origen:"MDZ", destino:"SCL", tipo:"Regional", cantidad:1, duracionHs:1.1, escalas:0, precioUnitario:85, aerolinea:"AndesAir", avion:"A320", salida:"2025-12-08 19:00", llegada:"2025-12-08 20:06" }
    ]
  },
  {
    id: "PKG-MVD-PDE",
    nombre: "Montevideo - Punta del Este",
    imagen: "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=400&q=80",
    duracion: "1 día",
    incluye: ["Viaje ida y vuelta"],
    precio: 60,
    moneda: "USD",
    color: "bg-[#e67e22]",
    iconos: ["fa-solid fa-bus"],
    rutas: [
      { id:"FR-MVD-PDE-001", codigo:"UY001", origen:"MVD", destino:"PDP", tipo:"Doméstica", cantidad:1, duracionHs:0.5, escalas:0, precioUnitario:35, aerolinea:"CieloSur", avion:"ATR72", salida:"2025-11-15 07:30", llegada:"2025-11-15 08:00" },
      { id:"FR-MVD-PDE-002", codigo:"UY002", origen:"PDP", destino:"MVD", tipo:"Doméstica", cantidad:1, duracionHs:0.5, escalas:0, precioUnitario:25, aerolinea:"CieloSur", avion:"ATR72", salida:"2025-11-15 20:30", llegada:"2025-11-15 21:00" }
    ]
  },
  // … podés seguir completando el resto igual que antes si querés
];

/* ======================= Helpers de DOM ======================= */
const $ = (sel, ctx = document) => ctx.querySelector(sel);
const $all = (sel, ctx = document) => Array.from(ctx.querySelectorAll(sel));
const escapeHtml = (s) => String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const br = (s) => escapeHtml(s).replace(/\n/g, "<br>");

/* ======================= Infra de Modal ======================= */
// Nota estudiante: modal simple con overlay + blur. No uso lib para no complicar.
// Cosas piolas: cierra con ESC, click afuera, y tranca el scroll de fondo.
function ensureModalRoot(){
  let root = document.getElementById("modal-root");
  if(!root){
    root = document.createElement("div");
    root.id = "modal-root";
    document.body.appendChild(root);
  }
  return root;
}
function openModal(html){
  const root = ensureModalRoot();
  document.body.classList.add("overflow-hidden"); // así no scrollea el body de atrás
  root.innerHTML = `
    <div class="fixed inset-0 z-[70]">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" id="modal-overlay"></div>
      <div class="absolute inset-0 flex items-end sm:items-center justify-center p-3 sm:p-6">
        <div role="dialog" aria-modal="true"
             class="w-full sm:max-w-2xl bg-white rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in duration-150">
          ${html}
        </div>
      </div>
    </div>
  `;
  $("#modal-overlay")?.addEventListener("click", closeModal, { once: true });
  const esc = (e) => { if(e.key === "Escape") closeModal(); };
  document.addEventListener("keydown", esc, { once: true });
}
function closeModal(){
  const root = ensureModalRoot();
  root.innerHTML = "";
  document.body.classList.remove("overflow-hidden");
}

/* ======================= Render: lista de paquetes ======================= */
// Nota estudiante: mantuve tu card grid. Le agregué data-id para saber qué paquete abrir.
function renderPackages() {
  const container = document.getElementById("packages-list");
  if (!container) return;

  container.innerHTML = paquetes.map(pkg => `
    <article class="group bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col transition-all duration-300 hover:shadow-2xl hover:-translate-y-1">
      <div class="relative">
        <img src="${pkg.imagen}" alt="${escapeHtml(pkg.nombre)}" class="w-full h-48 object-cover" />
        <div class="absolute left-0 bottom-0 flex gap-2 px-4 py-2 ${pkg.color} rounded-tr-2xl">
          ${ (pkg.iconos || []).map(icon => `<i class="${icon} text-white text-lg"></i>`).join("") }
        </div>
      </div>
      <div class="p-5 flex-1 flex flex-col justify-between">
        <header>
          <h3 class="text-lg font-bold text-gray-900 mb-1 group-hover:text-brand">${escapeHtml(pkg.nombre)}</h3>
          <div class="text-gray-700 text-sm mb-2">Duración: ${escapeHtml(pkg.duracion)}</div>
          <div class="text-gray-700 text-sm">Incluye:
            <ul class="list-disc ml-5 mt-1">
              ${(pkg.incluye || []).map(i => `<li>${escapeHtml(i)}</li>`).join("")}
            </ul>
          </div>
        </header>
        <div class="mt-4">
          <div class="flex items-end justify-between">
            <div class="text-xs text-gray-500">DESDE</div>
            <div class="text-2xl font-bold text-[#e67e22]">${pkg.moneda} ${pkg.precio}</div>
          </div>
          <div class="text-xs text-right text-[#e67e22] font-semibold">Precio por persona<br>en base doble</div>

          <!-- CTA para ver el paquete (abre modal) -->
          <button type="button"
                  class="mt-3 w-full px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110"
                  data-open-package="${pkg.id}">
            Ver paquete
          </button>
          <a href="#" data-target="buypackage/buypackage.html"
             class="mt-2 w-full inline-block text-center px-4 py-2 rounded-lg bg-emerald-600 text-white hover:brightness-110 nav-link">
            Comprar
          </a>
        </div>
      </div>
    </article>
  `).join("");

  // Delego eventos de apertura de modal
  container.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-open-package]");
    if (!btn) return;
    const id = btn.getAttribute("data-open-package");
    const pkg = paquetes.find(p => p.id === id);
    if (pkg) openPackageModal(pkg);
  });
}

/* ======================= Modal: detalle de paquete ======================= */
// Nota estudiante: acá cumplo el “tipos y cantidades de rutas que lo integran”.
// También muestro costo del paquete (precio) y sumatoria de precios unitarios por referencia.
function openPackageModal(pkg){
  const sumaRutas = (pkg.rutas || []).reduce((acc, r) => acc + (r.precioUnitario * (r.cantidad || 0)), 0);
  const html = `
    <header class="relative">
      <img src="${pkg.imagen}" alt="${escapeHtml(pkg.nombre)}" class="w-full h-40 object-cover">
      <button type="button" class="absolute top-3 right-3 rounded-full bg-white/90 hover:bg-white p-2 shadow" id="pkg-close" aria-label="Cerrar">✕</button>
      <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/60 to-transparent text-white">
        <h3 class="text-xl font-bold">${escapeHtml(pkg.nombre)}</h3>
        <p class="text-sm opacity-90">Duración: ${escapeHtml(pkg.duracion)} • Incluye: ${(pkg.incluye||[]).map(escapeHtml).join(", ")}</p>
      </div>
    </header>

    <section class="p-4 sm:p-6 space-y-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <div class="text-gray-700">
          <div class="text-xs uppercase tracking-wide">Costo del paquete</div>
          <div class="text-2xl font-extrabold">${pkg.moneda} <span class="text-emerald-600">${pkg.precio}</span></div>
        </div>
        <div class="text-right text-gray-700">
          <div class="text-xs uppercase tracking-wide">Suma de rutas (referencia)</div>
          <div class="font-semibold">${pkg.moneda} ${sumaRutas}</div>
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
            ${(pkg.rutas || []).map(r => `
              <tr class="border-t hover:bg-gray-50 cursor-pointer" data-open-route="${r.id}" data-pkg-id="${pkg.id}">
                <td class="px-3 py-2 font-medium">${escapeHtml(r.origen)} → ${escapeHtml(r.destino)} <span class="text-gray-400 ml-1">(${escapeHtml(r.codigo)})</span></td>
                <td class="px-3 py-2">${escapeHtml(r.tipo)}</td>
                <td class="px-3 py-2 text-center">${r.cantidad || 0}</td>
                <td class="px-3 py-2 text-right">${escapeHtml(pkg.moneda)} ${r.precioUnitario}</td>
              </tr>
            `).join("")}
            ${(!pkg.rutas || pkg.rutas.length === 0) ? `
              <tr><td colspan="4" class="px-3 py-6 text-center text-gray-500">Este paquete no tiene rutas cargadas.</td></tr>` : ""}
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-end gap-2">
        <button type="button" id="pkg-cancel" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Cerrar</button>
        <a href="#" class="nav-link px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110" data-target="flightf/flight.html">Ver rutas</a>
        <a href="#" class="nav-link px-4 py-2 rounded-lg bg-emerald-600 text-white hover:brightness-110"
           data-target="buypackage/buypackage.html">Comprar</a>
      </div>
    </section>
  `;
  openModal(html);

  // Cierres “obvios”
  $("#pkg-close")?.addEventListener("click", closeModal);
  $("#pkg-cancel")?.addEventListener("click", closeModal);

  // Delego click en filas para abrir el modal de ruta
  // Delego clicks SOLO en las filas del tbody del modal activo
const tbody = document.querySelector('#modal-root tbody');
if (tbody) {
  // Limpio por si quedó algo viejo (prevención si reabrís rápido)
  tbody.onclick = null;
  tbody.addEventListener('click', (e) => {
    const tr = e.target.closest('[data-open-route]');
    if (!tr) return;
    const rid = tr.getAttribute('data-open-route');
    const pid = tr.getAttribute('data-pkg-id');
    const pack = paquetes.find(p => p.id === pid);
    const route = pack?.rutas?.find(r => r.id === rid);
    if (route) openRouteModal(route, pack);
  });
}

}

/* ======================= Modal: detalle de ruta ======================= */
// Nota estudiante: esto copia la estructura de “Consulta de Ruta de Vuelo”.
// Si más adelante hay un endpoint real (GET /flightroute/:id) lo encajo acá.
function openRouteModal(route, pkg){
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
        <div class="text-gray-600">Cantidad en paquete: <span class="font-semibold">${route.cantidad || 0}</span></div>
        <div class="text-right">
          <div class="text-xs uppercase tracking-wide text-gray-500">Precio unitario</div>
          <div class="text-lg font-bold">${escapeHtml(pkg.moneda)} ${route.precioUnitario}</div>
        </div>
      </div>

      <div class="flex items-center justify-end gap-2">
        <!-- Nota estudiante: “volver” re-renderiza el modal anterior del paquete -->
        <button type="button" id="route-back" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Volver al paquete</button>
        <a href="#" class="nav-link px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110" data-target="flightf/flight.html">Ver rutas</a>
      </div>
    </section>
  `;
  openModal(html);
  $("#route-close")?.addEventListener("click", () => openPackageModal(pkg));
  $("#route-back")?.addEventListener("click", () => openPackageModal(pkg));
}

/* ======================= Boot ======================= */
document.addEventListener("DOMContentLoaded", renderPackages);
