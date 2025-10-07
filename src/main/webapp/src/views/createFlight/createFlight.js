fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    document.getElementById("header").innerHTML = data;

    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => {
      initHeader();
    };
    document.body.appendChild(script);
  });

// Inyectar footer dinámicamente
function importFooter() {
  fetch("../footer/footer.html")
    .then(res => res.text())
    .then(data => {
      document.getElementById("footer").innerHTML = data;
    });
}

if (document.getElementById("footer")) {
  importFooter();
}

// Placeholder para futura lógica de creación de vuelos.
// Puedes implementar guardado en localStorage o futura llamada a API aquí.
console.log('[createFlight] listo para implementar.');

// Utilidades simples
const $$ = (s,ctx=document)=>Array.from(ctx.querySelectorAll(s));
const $  = (s,ctx=document)=>ctx.querySelector(s);
const esc = s=>String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const read = (k,def=[])=>{ try{ return JSON.parse(localStorage.getItem(k)||JSON.stringify(def)); }catch{ return def; } };
const write = (k,v)=>localStorage.setItem(k,JSON.stringify(v));

// Obtener aerolínea logueada
function getAirlineSession(){
  try{
    const auth = JSON.parse(sessionStorage.getItem('auth')||'null');
    if(auth?.role==='airline') return auth.name || auth.nickname;
    const a = JSON.parse(sessionStorage.getItem('airline')||'null');
    return a?.name || null;
  }catch{}
  return null;
}

// Sembrar rutas demo si no existen (formato usado en otras partes: { aerolinea, routes:[{origen,destino,tiempo}] })
function seedRoutesIfNeeded(airline){
  if(!airline) return;
  let fr = read('flightRoutes', []);
  const has = fr.some(r => (r.aerolinea||'').toLowerCase() === airline.toLowerCase());
  if(has) return;
  fr.push({
    aerolinea: airline,
    routes: [
      { origen:"MVD", destino:"AEP", tiempo:"0h55m" },
      { origen:"AEP", destino:"MVD", tiempo:"0h50m" }
    ]
  });
  localStorage.setItem('flightRoutes', JSON.stringify(fr));
}

// Convertir flightRoutes normalizados a filas para la aerolínea
function getFlattenRoutes(airline){
  const store = read('flightRoutes', []);
  const out = [];
  store.forEach(grp=>{
    if(!airline || (grp.aerolinea||'').toLowerCase() === airline.toLowerCase()){
      (grp.routes||[]).forEach((r,idx)=>{
        out.push({
          id: `${grp.aerolinea||'AIR'}-${idx}-${r.origen}-${r.destino}`,
          airline: grp.aerolinea||'',
          origen: r.origen,
          destino: r.destino,
          duracion: r.tiempo||'',
        });
      });
    }
  });
  return out;
}

// Render de tabla de rutas
function renderRoutes(){
  const airline = getAirlineSession();
  const tbody = $('#cf-routes-tbody');
  const noRoutes = $('#cf-no-routes');
  const form = $('#cf-form');
  if(!tbody) return;
  if(!airline){
    $('#cf-alert').classList.remove('hidden');
    $('#cf-alert').textContent = 'Debes iniciar sesión como aerolínea para crear vuelos.';
    tbody.innerHTML = '';
    noRoutes.classList.remove('hidden');
    form.classList.add('opacity-50','pointer-events-none');
    return;
  }
  seedRoutesIfNeeded(airline);
  const rows = getFlattenRoutes(airline);
  if(!rows.length){
    tbody.innerHTML = '';
    noRoutes.classList.remove('hidden');
  } else {
    noRoutes.classList.add('hidden');
    tbody.innerHTML = rows.map(r=>`
      <tr class="border-t hover:bg-gray-50">
        <td class="px-3 py-2 text-sm font-medium">${esc(r.origen)} → ${esc(r.destino)}</td>
        <td class="px-3 py-2 text-sm">${esc(r.duracion)}</td>
        <td class="px-3 py-2 text-sm">${esc(r.origen)}</td>
        <td class="px-3 py-2 text-sm">${esc(r.destino)}</td>
        <td class="px-3 py-2 text-center">
          <input type="radio" name="cf-route" value="${esc(r.id)}" class="w-4 h-4">
        </td>
      </tr>
    `).join('');
  }
}

// Reset formulario
function resetForm(){
  $('#cf-name').value = '';
  $('#cf-duration').value = '';
  $('#cf-seats-exec').value = '';
  $('#cf-seats-tour').value = '';
  const now = new Date();
  const isoLocal = (d)=> new Date(d.getTime() - d.getTimezoneOffset()*60000).toISOString().slice(0,16);
  $('#cf-created').value = isoLocal(new Date());
  $('#cf-flightdate').value = '';
  $$('input[name="cf-route"]').forEach(r=>r.checked=false);
  $('#cf-success').classList.add('hidden');
}

// Guardar vuelo
function handleSubmit(e){
  e.preventDefault();
  const airline = getAirlineSession();
  const alertBox = $('#cf-alert');
  alertBox.classList.add('hidden');

  if(!airline){
    alertBox.textContent = 'Sesión de aerolínea inválida.';
    alertBox.classList.remove('hidden');
    return;
  }

  const routeRadio = $$('input[name="cf-route"]').find(r=>r.checked);
  if(!routeRadio){
    alertBox.textContent = 'Debes seleccionar una ruta.';
    alertBox.classList.remove('hidden');
    return;
  }

  const name = $('#cf-name').value.trim();
  const created = $('#cf-created').value;
  const flightDate = $('#cf-flightdate').value;

  if(!name || !created || !flightDate){
    alertBox.textContent = 'Completa los campos obligatorios (*).';
    alertBox.classList.remove('hidden');
    return;
  }

  const duration = $('#cf-duration').value.trim();
  const seatsExec = parseInt($('#cf-seats-exec').value||'0',10);
  const seatsTour = parseInt($('#cf-seats-tour').value||'0',10);

  // Recuperar datos de la ruta seleccionada
  const airlineRoutes = getFlattenRoutes(airline);
  const selectedRoute = airlineRoutes.find(r=>r.id===routeRadio.value);
  if(!selectedRoute){
    alertBox.textContent = 'La ruta seleccionada ya no existe.';
    alertBox.classList.remove('hidden');
    return;
  }

  const flights = read('flights', []);
  const flight = {
    id: 'FL-' + Date.now(),
    name,
    airline,
    duration: duration || selectedRoute.duracion,
    seats: {
      executive: seatsExec,
      tourist: seatsTour
    },
    createdAt: created,
    flightDate,
    route: {
      id: selectedRoute.id,
      origin: selectedRoute.origen,
      dest: selectedRoute.destino,
      duration: selectedRoute.duracion
    }
  };
  flights.push(flight);
  write('flights', flights);

  $('#cf-success').textContent = 'Vuelo creado correctamente.';
  $('#cf-success').classList.remove('hidden');
  resetForm();
  // Mantener rutas listadas
}

// Inicializar
document.addEventListener('DOMContentLoaded', ()=>{
  renderRoutes();
  resetForm();
  $('#btn-reload-routes')?.addEventListener('click', renderRoutes);
  $('#cf-form')?.addEventListener('submit', handleSubmit);
  $('#cf-reset')?.addEventListener('click', resetForm);
});