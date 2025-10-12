// Cargar header y ejecutar su script al terminar
fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    const headerEl = document.getElementById("header");
    if (headerEl) headerEl.innerHTML = data;

    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => {
      if (typeof initHeader === "function") initHeader();
    };
    document.body.appendChild(script);
  })
  .catch(err => {
    console.error("Error cargando header:", err);
  });

// Inyectar footer dinámicamente (queda igual)
fetch("../footer/footer.html").then(r=>r.text()).then(html=>{
  const el = document.getElementById("footer");
  if(el) el.innerHTML = html;
});

// --- Lógica de la página de reserva ---
document.addEventListener("DOMContentLoaded", () => {
  const root = document.getElementById("booking-root");
  if(!root) return;

  // Intentar cargar vuelo seleccionado desde sessionStorage
  const raw = sessionStorage.getItem("bookingFlight");
  let flight = null;
  try { flight = raw ? JSON.parse(raw) : null; } catch(e){ flight = null; }

  if(!flight){
    // Mostrar mensaje si no hay vuelo seleccionado
    root.innerHTML = `
      <div class="bg-white p-6 rounded-xl shadow-md text-center">
        <h2 class="text-xl font-bold text-brand mb-2">No hay vuelo seleccionado</h2>
        <p class="text-gray-600 mb-4">Vuelve a la búsqueda y selecciona un vuelo para reservar.</p>
        <div><a href="../flightf/bookFlight.jsp" class="px-4 py-2 bg-orange-500 text-white rounded">Volver a vuelos</a></div>
      </div>
    `;
    return;
  }

  // Renderizar layout principal 
  root.innerHTML = `
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 space-y-4">
        <div class="bg-white p-4 md:p-6 shadow-lg rounded-xl">
          <div class="flex items-center gap-4">
            <img src="${flight.logo}" alt="${flight.aerolinea}" class="w-14 h-14 object-contain rounded-md bg-white p-1" />
            <div>
              <div class="text-sm text-gray-500">Aerolinea</div>
              <div class="text-lg font-semibold">${flight.aerolinea}</div>
              <div class="text-sm text-gray-500 mt-1">${flight.salida.ciudad} → ${flight.llegada.ciudad} • ${flight.duracion}</div>
            </div>
            <div class="ml-auto text-right">
              <div class="text-xs text-gray-500">Tipo</div>
              <div class="text-sm font-semibold">${flight.tipo || 'Estándar'}</div>
              <div class="text-orange-600 text-2xl font-bold mt-2">${flight.precio === 0 ? '+US$0.00' : 'US$'+flight.precio}</div>
            </div>
          </div>
        </div>

        <div class="bg-white p-4 md:p-6 shadow-lg rounded-xl">
          <h3 class="text-sm font-semibold mb-3">Detalles y pasajeros</h3>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-3 items-end mb-4">
            <div>
              <label class="text-xs text-gray-500">Clase</label>
              <select id="seat-class" class="w-full border rounded px-3 py-2 text-sm">
                <option value="TURISTA" ${(!flight.tipo || flight.tipo==='Estándar')?'selected':''}>Turista</option>
                <option value="EJECUTIVO">Ejecutivo</option>
              </select>
            </div>
            <div>
              <label class="text-xs text-gray-500">Pasajeros</label>
              <div class="flex gap-2">
                <input id="passengers-count" type="number" min="1" value="1" class="w-24 border rounded px-3 py-2 text-sm" />
                <button id="btn-add-passenger" class="px-3 py-2 bg-brand text-white rounded text-sm">+ Añadir pasajero</button>
              </div>
            </div>
          </div>

          <div>
            <div class="text-sm font-semibold mb-2">Lista de pasajeros</div>
            <div class="overflow-auto border rounded">
              <table id="passengers-table" class="w-full text-left">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-3 py-2 text-xs">Tipo Doc</th>
                    <th class="px-3 py-2 text-xs">Documento</th>
                    <th class="px-3 py-2 text-xs">Nombre</th>
                    <th class="px-3 py-2 text-xs">Apellido</th>
                    <th class="px-3 py-2 text-xs">Tipo Equipaje Base</th>
                    <th class="px-3 py-2 text-xs">Tipo Equipaje Extra</th>
                    <th class="px-3 py-2 text-xs">Equipaje Extra</th>
                    <th class="px-3 py-2 text-xs">Acción</th>
                  </tr>
                </thead>
                <tbody>
                  <tr class="border-b">
                    <td class="px-3 py-2">
                      <select name="doc-type" class="w-full text-sm px-2 py-1 border rounded">
                        <option value="CI">CI</option>
                        <option value="RUT">RUT</option>
                        <option value="PASAPORTE">Pasaporte</option>
                      </select>
                    </td>
                    <td class="px-3 py-2"><input name="doc-number" class="w-full text-sm px-2 py-1 border rounded" placeholder="CI / Pasaporte"></td>
                    <td class="px-3 py-2"><input name="first-name" class="w-full text-sm px-2 py-1 border rounded" placeholder="Nombre"></td>
                    <td class="px-3 py-2"><input name="last-name" class="w-full text-sm px-2 py-1 border rounded" placeholder="Apellido"></td>
                    <td class="px-3 py-2">
                      <select name="bag-base" class="w-full text-sm px-2 py-1 border rounded">
                        <option value="BOLSO">Bolso</option>
                        <option value="MOCHILA">Mochila</option>
                        <option value="CARRY_ON">Carry-on</option>
                      </select>
                    </td>
                    <td class="px-3 py-2">
                      <select name="bag-extra-type" class="w-full text-sm px-2 py-1 border rounded">
                        <option value="">--</option>
                        <option value="MALETA">Maleta</option>
                        <option value="BOLSO">Bolso</option>
                      </select>
                    </td>
                    <td class="px-3 py-2"><input name="bag-extra" class="w-full text-sm px-2 py-1 border rounded" placeholder="Equipaje Extra"></td>
                    <td class="px-3 py-2"><button class="text-sm text-red-500 remove-passenger">Eliminar</button></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

      </div>

      <aside class="lg:col-span-1">
        <div class="bg-white p-4 md:p-6 shadow-lg rounded-xl lg:sticky lg:top-24">
          <div class="flex items-center gap-3 mb-3">
            <img src="${flight.logo}" class="h-10 w-10 object-contain" alt="${flight.aerolinea}" />
            <div>
              <div class="text-xs text-gray-500">Resumen</div>
              <div class="text-sm font-semibold">${flight.salida.ciudad} → ${flight.llegada.ciudad}</div>
            </div>
          </div>

          <div class="text-sm text-gray-600 mb-2">Precio base</div>
          <div id="price-breakdown" class="space-y-2 mb-4">
            <!-- rellenado por JS -->
          </div>

          <div class="flex gap-2">
            <button id="btn-back" class="flex-1 px-4 py-2 border rounded">Volver</button>
            <button id="btn-confirm" class="flex-1 px-4 py-2 bg-orange-500 text-white rounded font-semibold">Confirmar</button>
          </div>
        </div>
      </aside>
    </div>
  `;

  // Inicializar funciones
  function renderPrice(){
    const count = Math.max(1, parseInt(document.getElementById('passengers-count').value || '1',10));
    // precio base por pasajero (fallback)
    const unit = typeof flight.precio === 'number' ? flight.precio : 100;
    const subtotal = (unit * count);
    const taxes = Math.round(subtotal * 0.12 * 100)/100;
    const total = Math.round((subtotal + taxes) * 100)/100;
    const breakdown = document.getElementById('price-breakdown');
    breakdown.innerHTML = `
      <div class="flex justify-between text-sm"><div>${count} x ${unit===0?'+US$0.00':'US$'+unit}</div><div class="font-semibold">US$${subtotal}</div></div>
      <div class="flex justify-between text-sm text-gray-500"><div>Impuestos y tasas</div><div>US$${taxes}</div></div>
      <hr class="my-2" />
      <div class="flex justify-between text-base font-semibold"><div>Total</div><div>US$${total}</div></div>
    `;
  }

  // Eventos
  document.getElementById('passengers-count').addEventListener('change', e=>{
    const val = Math.max(1, parseInt(e.target.value||'1',10));
    document.getElementById('passengers-count').value = val;
    renderPrice();
  });

  document.getElementById('btn-add-passenger').addEventListener('click', e=>{
    e.preventDefault();
    addPassengerRow();
  });

  document.getElementById('btn-back').addEventListener('click', ()=> location.href = '../flightf/bookFlight.jsp');

  document.getElementById('btn-confirm').addEventListener('click', submitBooking);

  // remove delegation
  document.querySelector('#passengers-table tbody').addEventListener('click', (ev)=>{
    if(ev.target && ev.target.classList.contains('remove-passenger')){
      ev.preventDefault();
      const row = ev.target.closest('tr');
      if(row) row.remove();
      // sync count
      const rows = document.querySelectorAll('#passengers-table tbody tr').length;
      document.getElementById('passengers-count').value = Math.max(1, rows);
      renderPrice();
    }
  });

  // helpers
  function addPassengerRow(){
    const tbody = document.querySelector('#passengers-table tbody');
    const tr = document.createElement('tr');
    tr.className = 'border-b';
    tr.innerHTML = `
      <td class="px-3 py-2">
        <select name="doc-type" class="w-full text-sm px-2 py-1 border rounded">
          <option value="CI">CI</option>
          <option value="RUT">RUT</option>
          <option value="PASAPORTE">Pasaporte</option>
        </select>
      </td>
      <td class="px-3 py-2"><input name="doc-number" class="w-full text-sm px-2 py-1 border rounded" placeholder="CI / Pasaporte"></td>
      <td class="px-3 py-2"><input name="first-name" class="w-full text-sm px-2 py-1 border rounded" placeholder="Nombre"></td>
      <td class="px-3 py-2"><input name="last-name" class="w-full text-sm px-2 py-1 border rounded" placeholder="Apellido"></td>
      <td class="px-3 py-2">
        <select name="bag-base" class="w-full text-sm px-2 py-1 border rounded">
          <option value="BOLSO">Bolso</option>
          <option value="MOCHILA">Mochila</option>
          <option value="CARRY_ON">Carry-on</option>
        </select>
      </td>
      <td class="px-3 py-2">
        <select name="bag-extra-type" class="w-full text-sm px-2 py-1 border rounded">
          <option value="">--</option>
          <option value="MALETA">Maleta</option>
          <option value="BOLSO">Bolso</option>
        </select>
      </td>
      <td class="px-3 py-2"><input name="bag-extra" class="w-full text-sm px-2 py-1 border rounded" placeholder="Equipaje Extra"></td>
      <td class="px-3 py-2"><button class="text-sm text-red-500 remove-passenger">Eliminar</button></td>
    `;
    tbody.appendChild(tr);
    const rows = tbody.querySelectorAll('tr').length;
    document.getElementById('passengers-count').value = rows;
    renderPrice();
  }

  function submitBooking(){
    const seatClass = document.getElementById('seat-class').value;
    const passengers = Array.from(document.querySelectorAll('#passengers-table tbody tr')).map(tr=>{
      return {
        docType: tr.querySelector('select[name="doc-type"]')?.value||'',
        docNumber: tr.querySelector('input[name="doc-number"]')?.value||'',
        firstName: tr.querySelector('input[name="first-name"]')?.value||'',
        lastName: tr.querySelector('input[name="last-name"]')?.value||'',
        bagBase: tr.querySelector('select[name="bag-base"]')?.value||'',
        bagExtraType: tr.querySelector('select[name="bag-extra-type"]')?.value||'',
        bagExtra: tr.querySelector('input[name="bag-extra"]')?.value||''
      };
    });

    if(passengers.length === 0){ alert('Agrega al menos un pasajero'); return; }

    // Validar que la cantidad indicada coincida con las filas cargadas
    const desired = Math.max(1, parseInt(document.getElementById('passengers-count').value || '1', 10));
    if (passengers.length !== desired) {
      alert('La cantidad de pasajes indicada (' + desired + ') no coincide con la cantidad de pasajeros cargados (' + passengers.length + ').');
      return;
    }

    // Validar nombres y apellidos (obligatorios)
    const missingNameIndex = passengers.findIndex(p => !p.firstName || !p.lastName);
    if (missingNameIndex !== -1) {
      alert('Fila ' + (missingNameIndex + 1) + ': Nombre y Apellido son obligatorios.');
      return;
    }

    // Validar CI = 8 dígitos cuando corresponda
    const invalidCI = passengers.findIndex(p => p.docType && String(p.docType).toUpperCase() === 'CI' && !/^\d{8}$/.test(String(p.docNumber || '')));
    if (invalidCI !== -1) {
      alert('Fila ' + (invalidCI + 1) + ': para Tipo Doc = CI el documento debe tener exactamente 8 dígitos.');
      return;
    }

    // demo: almacenar reserva y mostrar confirmación
    const booking = { flight, seatClass, passengers, createdAt: new Date().toISOString() };
    sessionStorage.setItem('lastBooking', JSON.stringify(booking));

    // === NUEVO: persistir en localStorage (reservations) ===
    try {
      const list = JSON.parse(localStorage.getItem('reservations') || '[]');
      // generar ids razonables
      const flightId = flight.id || ('FL-' + (flight.aerolinea||'').substring(0,3).toUpperCase() + '-' + Date.now());
      const routeKey = (flight.rutas && flight.rutas.length)
        ? (flight.rutas[0].origen + '-' + flight.rutas[flight.rutas.length-1].destino)
        : (flight.salida?.ciudad + '-' + flight.llegada?.ciudad);
      const userAuth = JSON.parse(sessionStorage.getItem('auth') || 'null');
      const entry = {
        id: 'R-' + Date.now(),
        flightId,
        airline: flight.aerolinea,
        routeKey,
        flightSummary: {
          salida: flight.salida,
          llegada: flight.llegada,
          duracion: flight.duracion,
          tipo: flight.tipo,
          precio: flight.precio
        },
        passengers,
        seatClass,
        createdAt: booking.createdAt,
        userName: userAuth ? (userAuth.name || userAuth.nickname) : null
      };
      list.push(entry);
      localStorage.setItem('reservations', JSON.stringify(list));
    } catch(e){ console.warn('No se pudo guardar la reserva persistente', e); }

    alert('Reserva registrada correctamente. (Demo)');
 
  }

  // inicial render
  renderPrice();
});
