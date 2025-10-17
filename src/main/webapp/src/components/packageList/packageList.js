// packageList.js — carrusel animado + modales (usa window.__PKG_MODAL_DATA__)

// ===== Utils =====
function normKey(s){ return (s||"").trim().replace(/\u00A0/g," ").replace(/[\u2013\u2014]/g,"-").replace(/\s+/g," ").toLowerCase(); }
function $(sel,ctx=document){ return ctx.querySelector(sel); }
function $all(sel,ctx=document){ return Array.from(ctx.querySelectorAll(sel)); }
function escapeHtml(s){ return String(s??"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#39;"); }
function br(s){ return escapeHtml(s).replace(/\n/g,"<br>"); }
const moneyFmt = new Intl.NumberFormat('es-ES', { maximumFractionDigits: 2 });
function fmtMoney(n, cur){ if(n==null||n==="") return "--"; const num = Number(n); const val = Number.isFinite(num)? moneyFmt.format(num) : String(n); return (cur? (cur+" ") : "") + val; }
function seatLabel(raw){ const s=(raw||"").toUpperCase(); if (s.includes("BUS")) return "Business"; if (s.includes("PREM")) return "Premium"; return "Económica"; }
function chip(text){ return `<span class="inline-flex items-center gap-1 text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-700">${escapeHtml(text)}</span>`; }

// ===== Modal =====
function ensureModalRoot(){ let r=document.getElementById("modal-root"); if(!r){r=document.createElement("div"); r.id="modal-root"; document.body.appendChild(r);} return r; }
function closeModal(){ const r=ensureModalRoot(); r.innerHTML=""; document.body.classList.remove("overflow-hidden"); }
function openModal(html){
    const r=ensureModalRoot();
    document.body.classList.add("overflow-hidden");
    r.innerHTML = `
    <div class="fixed inset-0 z-[70]">
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm"></div>
      <div class="absolute inset-0 flex items-end sm:items-center justify-center p-3 sm:p-6">
        <div role="dialog" aria-modal="true"
             class="w-full sm:max-w-3xl bg-white rounded-2xl shadow-2xl overflow-hidden">
          ${html}
        </div>
      </div>
    </div>`;
    r.firstElementChild.firstElementChild.addEventListener("click", closeModal, {once:true});
    const onEsc=(e)=>{ if(e.key==="Escape"){ closeModal(); document.removeEventListener("keydown", onEsc);} };
    document.addEventListener("keydown", onEsc);
}

// ===== Estado para “volver” =====
let __lastPkg = null; // { pkgName, basics }

// ===== App =====
document.addEventListener("DOMContentLoaded", () => {
    // -------- Carrusel con transición --------
    const carouselEl = document.getElementById("pkg-carousel");
    const slides = carouselEl ? carouselEl.querySelectorAll(".js-slide") : [];
    const prevBtn = document.getElementById("pkg-prev");
    const nextBtn = document.getElementById("pkg-next");
    const dotsWrap = document.getElementById("pkg-dots");
    let current = 0, timer = null;

    if (slides.length) {
        slides.forEach((s, i) => {
            s.classList.add("transition-all","duration-500","ease-out","transform");
            if (i === 0) {
                s.classList.add("opacity-100","translate-x-0","z-20","pointer-events-auto");
                s.classList.remove("opacity-0","translate-x-6","-translate-x-6","z-10","pointer-events-none","hidden");
            } else {
                s.classList.add("opacity-0","translate-x-6","z-10","pointer-events-none");
                s.classList.remove("opacity-100","translate-x-0","-translate-x-6","z-20","pointer-events-auto","hidden");
            }
        });

        if (dotsWrap && slides.length > 1) {
            dotsWrap.innerHTML = Array.from(slides).map((_, i) =>
                `<button class="w-2.5 h-2.5 rounded-full ${i===0?'bg-white':'bg-white/40 hover:bg-white/70'} ring-1 ring-black/5"
                 data-idx="${i}" aria-label="Slide ${i+1}"></button>`
            ).join("");
            dotsWrap.querySelectorAll("button").forEach(btn => {
                btn.addEventListener("click", () => go(parseInt(btn.dataset.idx, 10)));
            });
        }
        function updateDots(){
            if (!dotsWrap) return;
            dotsWrap.querySelectorAll("button").forEach((b, i) => {
                b.classList.toggle("bg-white", i === current);
                b.classList.toggle("bg-white/40", i !== current);
            });
        }

        function activate(next, dir){
            if (next === current) return;
            const curEl  = slides[current];
            const nextEl = slides[next];

            curEl.classList.remove("opacity-100","translate-x-0","z-20","pointer-events-auto");
            curEl.classList.add("opacity-0", dir>0 ? "-translate-x-6" : "translate-x-6", "z-10","pointer-events-none");

            nextEl.classList.remove("opacity-100","translate-x-0","-translate-x-6","translate-x-6","z-10","pointer-events-none");
            nextEl.classList.add("opacity-0", dir>0 ? "translate-x-6" : "-translate-x-6", "z-20");
            nextEl.getBoundingClientRect(); // reflow
            nextEl.classList.remove("opacity-0", dir>0 ? "translate-x-6" : "-translate-x-6");
            nextEl.classList.add("opacity-100","translate-x-0","pointer-events-auto");

            current = next;
            updateDots();
        }

        function go(to){
            const next = (typeof to === "number") ? ((to + slides.length) % slides.length)
                : (current + 1) % slides.length;
            const dir  = (typeof to === "number") ? (to > current ? 1 : -1) : 1;
            activate(next, dir);
            restart();
        }

        function start(){ if (!timer && slides.length > 1) timer = setInterval(() => go(), 4500); }
        function stop(){ if (timer) { clearInterval(timer); timer = null; } }
        function restart(){ stop(); start(); }

        prevBtn?.addEventListener("click", () => go(current - 1));
        nextBtn?.addEventListener("click", () => go(current + 1));
        carouselEl?.addEventListener("mouseenter", stop);
        carouselEl?.addEventListener("mouseleave", start);
        document.addEventListener("visibilitychange", () => document.hidden ? stop() : start());

        start();
    }

    // -------- Modales --------
    const dataMap = (typeof window!=="undefined" && window.__PKG_MODAL_DATA__) ? window.__PKG_MODAL_DATA__ : {};

    function renderRouteModal(route, seat, currency){
        const cats = Array.isArray(route.categories)? route.categories : [];
        const headerImg = route.image
            ? `<img src="${escapeHtml(route.image)}" alt="${escapeHtml(route.nombre||"Ruta")}" class="w-full h-40 object-cover">`
            : `<div class="w-full h-40 bg-gradient-to-r from-sky-700 to-gray-800"></div>`;
        const html = `
      <header class="relative">
        ${headerImg}
        <button type="button" class="absolute top-3 right-3 rounded-full bg-white/90 hover:bg-white p-2 shadow" id="route-close" aria-label="Cerrar">✕</button>
        <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/60 to-transparent text-white">
          <h3 class="text-lg font-bold">${escapeHtml(route.nombre || "")}</h3>
          <p class="text-sm opacity-90">${escapeHtml(route.originCity || "")} → ${escapeHtml(route.destinationCity || "")}</p>
        </div>
      </header>

      <section class="p-4 sm:p-6 space-y-4 text-sm">
        <div class="flex flex-wrap gap-2">
          ${route.status ? chip("Estado: "+route.status) : ""}
          ${route.airline ? chip("Aerolínea: "+route.airline) : ""}
          ${seat ? chip("Cabina: "+seatLabel(seat)) : ""}
        </div>

        ${cats.length ? `<div class="flex flex-wrap gap-2">${cats.map(c=>chip(c)).join("")}</div>` : ""}

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div class="p-3 rounded-lg bg-gray-50">
            <div class="text-gray-500">Precio turista</div>
            <div class="font-semibold">${fmtMoney(route.priceTourist, currency)}</div>
          </div>
          <div class="p-3 rounded-lg bg-gray-50">
            <div class="text-gray-500">Precio business</div>
            <div class="font-semibold">${fmtMoney(route.priceBusiness, currency)}</div>
          </div>
        </div>

        <div class="flex items-center justify-end gap-2">
          <button type="button" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50" id="route-back">Volver</button>
        </div>
      </section>
    `;
        openModal(html);
        $("#route-close")?.addEventListener("click", closeModal);
        // 🔁 Volver al último paquete en lugar de cerrar
        $("#route-back")?.addEventListener("click", () => {
            if (__lastPkg) renderPackageModal(__lastPkg.pkgName, __lastPkg.basics);
            else closeModal();
        });
    }

    function unitPriceForSeat(route, seat){
        const s=(seat||"").toUpperCase();
        if (s.includes("BUS")) return route.priceBusiness ?? route.priceTourist;
        return route.priceTourist ?? route.priceBusiness;
    }

    function renderPackageModal(pkgName, basics){
        // Guardamos para que "Volver" sepa a dónde ir
        __lastPkg = { pkgName, basics };

        const key = normKey(pkgName);
        const data = dataMap[key] || {};  // {description, seatType, discount, validityDays, created, priceActual, currency, routes:[...]}

        const currency = data.currency || "";
        const seat = data.seatType || "";
        const vig  = (data.validityDays!=null) ? `${data.validityDays} días` : "";
        const disc = (data.discount!=null && data.discount>0) ? `${moneyFmt.format(data.discount)}% off` : "";

        let sumaRutas = 0;
        const routes = Array.isArray(data.routes) ? data.routes : [];
        routes.forEach(r => {
            const u = unitPriceForSeat(r, seat);
            if (u!=null && !Number.isNaN(Number(u))) sumaRutas += Number(u);
        });

        const chips = [
            seat ? `Asiento: ${seatLabel(seat)}` : null,
            vig ? `Vigencia: ${vig}` : null,
            disc || null,
            data.created ? `Creado: ${escapeHtml(data.created)}` : null
        ].filter(Boolean).map(chip).join(" ");

        const priceActual = fmtMoney(data.priceActual ?? basics?.price, currency);
        const header = `
      <div class="bg-gradient-to-r from-sky-800 to-slate-800 text-white">
        <div class="p-5 sm:p-6">
          <h3 class="text-2xl font-bold">${escapeHtml(pkgName)}</h3>
          <p class="opacity-90 mt-1">${br(basics?.desc || data.description || "")}</p>
          <div class="mt-3 flex flex-wrap gap-2">${chips}</div>
        </div>
      </div>`;

        const tableRows = routes.map((r, idx) => {
            const unit = unitPriceForSeat(r, seat);
            return `
        <tr class="border-t hover:bg-gray-50 cursor-pointer" data-route-idx="${idx}">
          <td class="px-3 py-2 font-medium">${escapeHtml(r.originCity || "")} → ${escapeHtml(r.destinationCity || "")}</td>
          <td class="px-3 py-2">${escapeHtml(r.airline || "")}</td>
          <td class="px-3 py-2">${escapeHtml(seatLabel(seat))}</td>
          <td class="px-3 py-2 text-right">${fmtMoney(unit, currency)}</td>
        </tr>`;
        }).join("");

        const html = `
      ${header}
      <section class="p-4 sm:p-6 space-y-5">
        <div class="flex items-end justify-between">
          <div class="text-3xl font-extrabold text-emerald-600">${priceActual}</div>
          <div class="text-right">
            <div class="text-xs uppercase tracking-wider text-gray-500">Suma rutas (ref.)</div>
            <div class="font-semibold">${sumaRutas>0 ? fmtMoney(sumaRutas, currency) : "--"}</div>
          </div>
        </div>

        <div class="border rounded-xl overflow-hidden">
          <table class="w-full text-sm">
            <thead class="bg-gray-50 text-gray-600">
              <tr>
                <th class="text-left px-3 py-2">Origen → Destino</th>
                <th class="text-left px-3 py-2">Aerolínea</th>
                <th class="text-left px-3 py-2">Cabina</th>
                <th class="text-right px-3 py-2">Precio unit.</th>
              </tr>
            </thead>
            <tbody>
              ${tableRows || `<tr><td colspan="4" class="px-3 py-6 text-center text-gray-500">Sin rutas asociadas.</td></tr>`}
            </tbody>
          </table>
        </div>

        <div class="flex items-center justify-end gap-2">
          <button type="button" id="pkg-cancel" class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Cerrar</button>
          <a href="${(window.BASE || '') + '/packages'}" class="px-4 py-2 rounded-lg bg-[#0B4C73] text-white hover:brightness-110">Ver más paquetes</a>
        </div>
      </section>
    `;
        openModal(html);

        $("#pkg-cancel")?.addEventListener("click", closeModal);
        const modal = $("#modal-root");
        $all('tr[data-route-idx]', modal).forEach(tr => {
            tr.addEventListener('click', () => {
                const idx = Number(tr.getAttribute('data-route-idx'));
                const route = routes[idx];
                if (route) renderRouteModal(route, seat, currency);
            });
        });
    }

    // Abrir modal desde cada slide (con fallback al <h3>)
    slides.forEach(slide => {
        slide.querySelector(".js-open-pkg-modal")?.addEventListener("click", () => {
            const name  = slide.dataset.name || slide.querySelector('h3')?.textContent?.trim() || "Paquete";
            const desc  = slide.dataset.description || "";
            const price = slide.dataset.price || "--";
            renderPackageModal(name, { desc, price });
        });
    });
});
