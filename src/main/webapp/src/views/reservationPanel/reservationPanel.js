/* Inyección header / footer */
fetch("../header/header.html").then(r=>r.text()).then(h=>{
  const el = document.getElementById("header"); if(el) el.innerHTML = h;
  const s = document.createElement("script"); s.src="../header/header.js"; s.onload=()=>{ if(typeof initHeader==='function') initHeader(); }; document.body.appendChild(s);
});
fetch("../footer/footer.html").then(r=>r.text()).then(h=>{ const el=document.getElementById("footer"); if(el) el.innerHTML=h; });

/* Utilidades */
const $ = (s,ctx=document)=>ctx.querySelector(s);
const $$ = (s,ctx=document)=>Array.from(ctx.querySelectorAll(s));
const esc = s => String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
function read(k){ try{return JSON.parse(localStorage.getItem(k)||'[]');}catch{return [];} }

/* Seed demo si no hay / o faltan reservas (merge) */
(function seedDemo(){
  const now = Date.now();
  const mkDate = (offsetMin)=> new Date(now - offsetMin*60000).toISOString();
  const demo = [
    /* -------------------- AEROLÍNEA: LATAM Airlines (2 vuelos misma ruta) -------------------- */
    {
      id:"R-DEMO1",
      airline:"LATAM Airlines",
      routeKey:"OKA-MVD",              // Ruta larga
      flightId:"FL-LAT-001",
      flightSummary:{ salida:{hora:"07:25",ciudad:"OKA"}, llegada:{hora:"10:37",ciudad:"MVD"}, duracion:"39h 12m", tipo:"Estándar", precio:0 },
      passengers:[
        { firstName:"Juan", lastName:"Cliente", docType:"CI", docNumber:"12345678" },
        { firstName:"Ana", lastName:"Pérez", docType:"CI", docNumber:"87654321" }
      ],
      seatClass:"TURISTA",
      createdAt: mkDate(120),
      userName:"Juan Cliente"
    },
    {
      id:"R-DEMO2",
      airline:"LATAM Airlines",
      routeKey:"OKA-MVD",
      flightId:"FL-LAT-001",
      flightSummary:{ salida:{hora:"07:25",ciudad:"OKA"}, llegada:{hora:"10:37",ciudad:"MVD"}, duracion:"39h 12m", tipo:"Estándar", precio:0 },
      passengers:[
        { firstName:"Luis", lastName:"Suarez", docType:"CI", docNumber:"55667788" }
      ],
      seatClass:"EJECUTIVO",
      createdAt: mkDate(95),
      userName:"Luis Suarez"
    },
    {
      id:"R-DEMO3",
      airline:"LATAM Airlines",
      routeKey:"OKA-MVD",
      flightId:"FL-LAT-002",          // Segundo vuelo misma ruta
      flightSummary:{ salida:{hora:"09:10",ciudad:"OKA"}, llegada:{hora:"12:25",ciudad:"MVD"}, duracion:"38h 40m", tipo:"Estándar", precio:0 },
      passengers:[
        { firstName:"Carla", lastName:"Gómez", docType:"CI", docNumber:"33445566" },
        { firstName:"Mateo", lastName:"Gómez", docType:"CI", docNumber:"99887766" }
      ],
      seatClass:"TURISTA",
      createdAt: mkDate(80),
      userName:"Carla Gómez"
    },

    /* -------------------- AEROLÍNEA: CieloSur (otra aerolínea, otra ruta) -------------------- */
    {
      id:"R-DEMO4",
      airline:"CieloSur",
      routeKey:"MVD-AEP",
      flightId:"FL-CIE-010",
      flightSummary:{ salida:{hora:"09:45",ciudad:"MVD"}, llegada:{hora:"10:40",ciudad:"AEP"}, duracion:"0h 55m", tipo:"Estándar", precio:184.69 },
      passengers:[
        { firstName:"Juan", lastName:"Cliente", docType:"CI", docNumber:"12345678" } // mismo cliente en otra aerolínea
      ],
      seatClass:"TURISTA",
      createdAt: mkDate(60),
      userName:"Juan Cliente"
    },
    {
      id:"R-DEMO5",
      airline:"CieloSur",
      routeKey:"MVD-AEP",
      flightId:"FL-CIE-010",
      flightSummary:{ salida:{hora:"09:45",ciudad:"MVD"}, llegada:{hora:"10:40",ciudad:"AEP"}, duracion:"0h 55m", tipo:"Estándar", precio:184.69 },
      passengers:[
        { firstName:"Pedro", lastName:"López", docType:"PASAPORTE", docNumber:"AB1234567" }
      ],
      seatClass:"TURISTA",
      createdAt: mkDate(55),
      userName:"Pedro López"
    },

    /* -------------------- AEROLÍNEA: AndesAir (otra ruta distinta) -------------------- */
    {
      id:"R-DEMO6",
      airline:"AndesAir",
      routeKey:"SCL-MDZ",
      flightId:"FL-AND-300",
      flightSummary:{ salida:{hora:"09:00",ciudad:"SCL"}, llegada:{hora:"10:00",ciudad:"MDZ"}, duracion:"1h 00m", tipo:"Regional", precio:95 },
      passengers:[
        { firstName:"Ana", lastName:"Pérez", docType:"CI", docNumber:"87654321" }
      ],
      seatClass:"TURISTA",
      createdAt: mkDate(45),
      userName:"Ana Pérez"
    },
    {
      id:"R-DEMO7",
      airline:"AndesAir",
      routeKey:"SCL-MDZ",
      flightId:"FL-AND-301",
      flightSummary:{ salida:{hora:"19:00",ciudad:"MDZ"}, llegada:{hora:"20:06",ciudad:"SCL"}, duracion:"1h 06m", tipo:"Regional", precio:85 },
      passengers:[
        { firstName:"Juan", lastName:"Cliente", docType:"CI", docNumber:"12345678" }
      ],
      seatClass:"EJECUTIVO",
      createdAt: mkDate(30),
      userName:"Juan Cliente"
    }
  ];

  let existing;
  try { existing = JSON.parse(localStorage.getItem('reservations')||'[]'); } catch { existing = []; }
  const ids = new Set(existing.map(r=>r.id));
  const merged = existing.concat(demo.filter(d=>!ids.has(d.id)));
  localStorage.setItem('reservations', JSON.stringify(merged));
  window.seedReservationsDemo = function(force=false){
    if(force){
      const exIds = new Set(JSON.parse(localStorage.getItem('reservations')||'[]').map(r=>r.id));
      const add = demo.filter(d=>!exIds.has(d.id));
      const cur = JSON.parse(localStorage.getItem('reservations')||'[]').concat(add);
      localStorage.setItem('reservations', JSON.stringify(cur));
      console.log('[reservations] demo merged (force).');
    } else {
      console.log('[reservations] demo already ensured.');
    }
  };
})();

/* Contexto sesión */
function getSession(){
  let role=null,name=null;
  try{
    const auth=JSON.parse(sessionStorage.getItem('auth')||'null');
    if(auth?.role){ role=auth.role; name=auth.name||auth.nickname||null; }
    else role=sessionStorage.getItem('role');
  }catch{}
  return { role, name };
}

/* Estado UI */
const state = {
  role:null,
  airline:null,
  routeKey:null,
  flightId:null,
  reservationId:null
};

/* Elementos */
const filtersEl = $('#rp-filters');
const resultsEl = $('#rp-results');
const detailEl = $('#rp-detail');
const alertEl = $('#rp-alert');

/* Render de mensajes */
function showAlert(msg){
  alertEl.textContent = msg;
  alertEl.classList.remove('hidden');
}
function hideAlert(){ alertEl.classList.add('hidden'); }

/* Flujo Airline:
   1) Lista rutas propias -> 2) Vuelos por ruta -> 3) Reservas por vuelo -> 4) Detalle */
function flowAirline(){
  const all = read('reservations').filter(r=>r.airline && r.airline.toLowerCase() === state.airline.toLowerCase());
  if(!all.length){ showAlert('No hay reservas registradas para esta aerolínea.'); filtersEl.innerHTML=''; resultsEl.innerHTML=''; return; }
  hideAlert();

  // Rutas
  const routeSet = Array.from(new Set(all.map(r=>r.routeKey).filter(Boolean)));
  filtersEl.innerHTML = `
    <div class="bg-white p-4 rounded-xl shadow flex flex-col gap-4">
      <div>
        <label class="text-xs font-semibold text-gray-500">Rutas</label>
        <select id="flt-route" class="mt-1 w-full border rounded px-3 py-2">
          <option value="">-- Selecciona ruta --</option>
          ${routeSet.map(r=>`<option value="${esc(r)}"${r===state.routeKey?' selected':''}>${esc(r)}</option>`).join('')}
        </select>
      </div>
      ${state.routeKey ? `
      <div>
        <label class="text-xs font-semibold text-gray-500">Vuelos</label>
        <select id="flt-flight" class="mt-1 w-full border rounded px-3 py-2">
          <option value="">-- Selecciona vuelo --</option>
          ${Array.from(new Set(all.filter(r=>r.routeKey===state.routeKey).map(r=>r.flightId))).map(f=>`
            <option value="${esc(f)}"${f===state.flightId?' selected':''}>${esc(f)}</option>`).join('')}
        </select>
      </div>`:''}
    </div>
  `;

  $('#flt-route')?.addEventListener('change', e=>{
    state.routeKey = e.target.value || null;
    state.flightId = null;
    state.reservationId = null;
    detailEl.classList.add('hidden');
    flowAirline();
    renderResultsAirline();
  });
  $('#flt-flight')?.addEventListener('change', e=>{
    state.flightId = e.target.value || null;
    state.reservationId = null;
    detailEl.classList.add('hidden');
    renderResultsAirline();
  });

  renderResultsAirline();
}

function renderResultsAirline(){
  const all = read('reservations').filter(r=>r.airline && r.airline.toLowerCase()===state.airline.toLowerCase());
  let filtered = all;
  if(state.routeKey) filtered = filtered.filter(r=>r.routeKey===state.routeKey);
  if(state.flightId) filtered = filtered.filter(r=>r.flightId===state.flightId);

  if(!state.routeKey){
    resultsEl.innerHTML = `<div class="text-sm text-gray-600">Selecciona una ruta para continuar.</div>`;
    return;
  }
  if(state.routeKey && !state.flightId){
    resultsEl.innerHTML = `<div class="text-sm text-gray-600">Selecciona un vuelo para ver reservas.</div>`;
    return;
  }

  if(!filtered.length){
    resultsEl.innerHTML = `<div class="text-sm text-gray-600">No hay reservas para ese vuelo.</div>`;
    return;
  }

  resultsEl.innerHTML = `
    <div class="bg-white p-4 rounded-xl shadow">
      <h2 class="font-semibold mb-3">Reservas del vuelo ${esc(state.flightId)}</h2>
      <div class="divide-y">
        ${filtered.map(r=>`
          <button data-res="${esc(r.id)}" class="w-full text-left py-3 flex items-center justify-between hover:bg-gray-50">
            <span class="text-sm font-medium">${esc(r.id)}</span>
            <span class="text-xs text-gray-500">${r.passengers.length} pasaj. • ${new Date(r.createdAt).toLocaleDateString()}</span>
          </button>
        `).join('')}
      </div>
    </div>
  `;

  resultsEl.querySelectorAll('[data-res]').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      state.reservationId = btn.getAttribute('data-res');
      const r = filtered.find(x=>x.id===state.reservationId);
      if(r) renderReservationDetail(r);
    });
  });
}

/* Flujo User:
   1) Aerolíneas -> 2) Rutas -> 3) Vuelos -> 4) Su reserva (si existe) */
function flowUser(){
  const all = read('reservations');
  if(!all.length){ showAlert('No tienes reservas registradas todavía.'); filtersEl.innerHTML=''; resultsEl.innerHTML=''; return; }
  hideAlert();

  const airlines = Array.from(new Set(all.map(r=>r.airline))).filter(Boolean);
  filtersEl.innerHTML = `
    <div class="bg-white p-4 rounded-xl shadow flex flex-col gap-4">
      <div>
        <label class="text-xs font-semibold text-gray-500">Aerolínea</label>
        <select id="usr-airline" class="mt-1 w-full border rounded px-3 py-2">
          <option value="">-- Selecciona aerolínea --</option>
          ${airlines.map(a=>`<option value="${esc(a)}"${a===state.airline?' selected':''}>${esc(a)}</option>`).join('')}
        </select>
      </div>
      ${state.airline ? `
      <div>
        <label class="text-xs font-semibold text-gray-500">Ruta</label>
        <select id="usr-route" class="mt-1 w-full border rounded px-3 py-2">
          <option value="">-- Selecciona ruta --</option>
          ${Array.from(new Set(all.filter(r=>r.airline===state.airline).map(r=>r.routeKey))).map(k=>`
            <option value="${esc(k)}"${k===state.routeKey?' selected':''}>${esc(k)}</option>`).join('')}
        </select>
      </div>`:''}
      ${state.airline && state.routeKey ? `
      <div>
        <label class="text-xs font-semibold text-gray-500">Vuelo</label>
        <select id="usr-flight" class="mt-1 w-full border rounded px-3 py-2">
          <option value="">-- Selecciona vuelo --</option>
          ${Array.from(new Set(all.filter(r=>r.airline===state.airline && r.routeKey===state.routeKey).map(r=>r.flightId))).map(f=>`
            <option value="${esc(f)}"${f===state.flightId?' selected':''}>${esc(f)}</option>`).join('')}
        </select>
      </div>`:''}
    </div>
  `;

  $('#usr-airline')?.addEventListener('change', e=>{
    state.airline = e.target.value || null;
    state.routeKey = state.flightId = state.reservationId = null;
    detailEl.classList.add('hidden');
    flowUser(); renderResultsUser();
  });
  $('#usr-route')?.addEventListener('change', e=>{
    state.routeKey = e.target.value || null;
    state.flightId = state.reservationId = null;
    detailEl.classList.add('hidden');
    flowUser(); renderResultsUser();
  });
  $('#usr-flight')?.addEventListener('change', e=>{
    state.flightId = e.target.value || null;
    state.reservationId = null;
    detailEl.classList.add('hidden');
    renderResultsUser();
  });

  renderResultsUser();
}

function renderResultsUser(){
  const all = read('reservations');
  if(!state.airline){ resultsEl.innerHTML = `<div class="text-sm text-gray-600">Selecciona una aerolínea.</div>`; return; }
  if(!state.routeKey){ resultsEl.innerHTML = `<div class="text-sm text-gray-600">Selecciona una ruta.</div>`; return; }
  if(!state.flightId){ resultsEl.innerHTML = `<div class="text-sm text-gray-600">Selecciona un vuelo.</div>`; return; }

  const mine = all.filter(r=>r.airline===state.airline && r.routeKey===state.routeKey && r.flightId===state.flightId);
  if(!mine.length){
    resultsEl.innerHTML = `<div class="bg-white p-4 rounded-xl shadow text-sm text-gray-700">No tienes reserva para ese vuelo.</div>`;
    return;
  }

  const r = mine[mine.length-1]; // última reserva encontrada
  resultsEl.innerHTML = `
    <div class="bg-white p-4 rounded-xl shadow">
      <h2 class="font-semibold mb-3">Tu reserva</h2>
      <button class="px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110" id="btn-open-my-res">Ver detalle (${esc(r.id)})</button>
    </div>
  `;
  $('#btn-open-my-res')?.addEventListener('click', ()=> renderReservationDetail(r));
}

/* Detalle común */
function renderReservationDetail(r){
  detailEl.classList.remove('hidden');
  detailEl.innerHTML = `
    <div class="flex items-start justify-between mb-4">
      <h3 class="text-lg font-semibold">Reserva ${esc(r.id)}</h3>
      <button id="rp-close-detail" class="text-gray-500 hover:text-brand">✕</button>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
      <div class="p-3 rounded-lg bg-gray-50">
        <div class="text-xs text-gray-500">Aerolínea</div>
        <div class="font-medium">${esc(r.airline)}</div>
      </div>
      <div class="p-3 rounded-lg bg-gray-50">
        <div class="text-xs text-gray-500">Ruta</div>
        <div class="font-medium">${esc(r.routeKey)}</div>
      </div>
      <div class="p-3 rounded-lg bg-gray-50">
        <div class="text-xs text-gray-500">Vuelo</div>
        <div class="font-medium">${esc(r.flightId)}</div>
      </div>
      <div class="p-3 rounded-lg bg-gray-50">
        <div class="text-xs text-gray-500">Fecha reserva</div>
        <div class="font-medium">${new Date(r.createdAt).toLocaleString()}</div>
      </div>
    </div>

    <h4 class="mt-6 mb-2 font-semibold text-sm">Pasajeros</h4>
    <div class="overflow-auto rounded border">
      <table class="w-full text-xs md:text-sm">
        <thead class="bg-gray-100 text-gray-600">
          <tr>
            <th class="px-3 py-2 text-left">Nombre</th>
            <th class="px-3 py-2 text-left">Apellido</th>
            <th class="px-3 py-2 text-left">Tipo Doc</th>
            <th class="px-3 py-2 text-left">Documento</th>
          </tr>
        </thead>
        <tbody>
          ${(r.passengers||[]).map(p=>`
            <tr class="border-t">
              <td class="px-3 py-2">${esc(p.firstName||'')}</td>
              <td class="px-3 py-2">${esc(p.lastName||'')}</td>
              <td class="px-3 py-2">${esc(p.docType||'')}</td>
              <td class="px-3 py-2">${esc(p.docNumber||'')}</td>
            </tr>`).join('')}
          ${(!r.passengers||!r.passengers.length)?`<tr><td colspan="4" class="px-3 py-4 text-center text-gray-500">Sin pasajeros.</td></tr>`:''}
        </tbody>
      </table>
    </div>
    <div class="mt-6 flex justify-end">
      <button id="rp-close-detail-2" class="px-4 py-2 border rounded hover:bg-gray-50">Cerrar</button>
    </div>
  `;
  $('#rp-close-detail')?.addEventListener('click', ()=>detailEl.classList.add('hidden'));
  $('#rp-close-detail-2')?.addEventListener('click', ()=>detailEl.classList.add('hidden'));
}

/* Init */
document.addEventListener('DOMContentLoaded', ()=>{
  const ses = getSession();
  state.role = ses.role;
  if(!state.role){
    showAlert('Debes iniciar sesión para consultar reservas.');
    filtersEl.innerHTML = '';
    resultsEl.innerHTML = '';
    return;
  }
  if(state.role === 'airline'){
    // intentar deducir nombre (ya guardado en sessionStorage.airline)
    try {
      const a = JSON.parse(sessionStorage.getItem('airline')||'null');
      state.airline = a?.name || 'Aerolínea';
    } catch { state.airline='Aerolínea'; }
    flowAirline();
  } else {
    flowUser();
  }
});
