// Inyectar header y footer dinámicamente
fetch("../header/header.html")
  .then(r=>r.text())
  .then(html=>{
    const el = document.getElementById("header");
    if(el) el.innerHTML = html;
    const s = document.createElement("script");
    s.src = "../header/header.js";
    s.onload = ()=>{ if(typeof initHeader==='function') initHeader(); };
    document.body.appendChild(s);
  });

fetch("../footer/footer.html")
  .then(r=>r.text())
  .then(html=>{
    const el = document.getElementById("footer");
    if(el) el.innerHTML = html;
  });

(function(){
  const $ = (s,ctx=document)=>ctx.querySelector(s);
  const esc = s=>String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

  // Guardas se crean en createFlightRoute (no incluido). Migrar status si falta.
  function readRoutes(){
    let data=[];
    try{ data = JSON.parse(localStorage.getItem('flightRoutes')||'[]'); }catch{}
    // Normalizar: cada grupo { aerolinea, routes:[{origen,destino,tiempo,status?}] }
    data.forEach(g=>{
      if(Array.isArray(g.routes)){
        g.routes.forEach(r=>{
          if(!r.status) r.status = 'Ingresada';
        });
      }
    });
    localStorage.setItem('flightRoutes', JSON.stringify(data));
    return data;
  }
  function writeRoutes(list){
    localStorage.setItem('flightRoutes', JSON.stringify(list));
  }

  function ensureDemoIfEmpty(){
    const cur = readRoutes();
    if(cur.length) return;
    const demo = [
      { aerolinea:'CieloSur', routes:[
        { origen:'MVD', destino:'AEP', tiempo:'0h55m', status:'Ingresada' },
        { origen:'AEP', destino:'MVD', tiempo:'0h50m', status:'Ingresada' }
      ]},
      { aerolinea:'AndesAir', routes:[
        { origen:'SCL', destino:'MDZ', tiempo:'1h00m', status:'Ingresada' },
        { origen:'MDZ', destino:'SCL', tiempo:'1h05m', status:'Confirmada' }
      ]}
    ];
    writeRoutes(demo);
  }

  function isAdmin(){
    try{
      const auth = JSON.parse(sessionStorage.getItem('auth')||'null');
      return auth?.role === 'admin';
    }catch{}
    return false;
  }

  function guard(){
    if(!isAdmin()){
      const alertBox = $('#afr-alert');
      alertBox.textContent = 'Acceso restringido: solo administradores.';
      alertBox.classList.remove('hidden');
      $('#afr-airline').disabled = true;
      return false;
    }
    return true;
  }

  function loadAirlines(){
    const data = readRoutes();
    const select = $('#afr-airline');
    const airlines = Array.from(new Set(
      data
        .filter(g => (g.routes||[]).some(r=>r.status==='Ingresada'))
        .map(g=>g.aerolinea)
    ));
    select.innerHTML = airlines.length
      ? '<option value="">-- Selecciona --</option>'+airlines.map(a=>`<option value="${esc(a)}">${esc(a)}</option>`).join('')
      : '<option value="">(Sin pendientes)</option>';
  }

  function renderRoutesFor(airline){
    const tbody = $('#afr-routes');
    const empty = $('#afr-empty');
    const approve = $('#afr-approve');
    const reject = $('#afr-reject');
    approve.disabled = true; reject.disabled = true;

    if(!airline){
      tbody.innerHTML = '';
      empty.classList.remove('hidden');
      empty.textContent = 'Selecciona una aerolínea.';
      return;
    }

    const data = readRoutes();
    const grp = data.find(g=>g.aerolinea===airline);
    const pending = (grp?.routes||[]).filter(r=>r.status==='Ingresada');
    if(!pending.length){
      tbody.innerHTML='';
      empty.classList.remove('hidden');
      empty.textContent='No hay rutas ingresadas para esta aerolínea.';
      return;
    }
    empty.classList.add('hidden');
    tbody.innerHTML = pending.map((r,i)=>`
      <tr class="border-t hover:bg-gray-50">
        <td class="px-3 py-2">
          <input type="radio" name="afr-route" value="${i}" class="afr-route-radio">
        </td>
        <td class="px-3 py-2">${esc(r.origen)}</td>
        <td class="px-3 py-2">${esc(r.destino)}</td>
        <td class="px-3 py-2">${esc(r.tiempo)}</td>
        <td class="px-3 py-2">${esc(r.status)}</td>
      </tr>
    `).join('');

    tbody.addEventListener('change', e=>{
      if(e.target.matches('.afr-route-radio')){
        approve.disabled = false;
        reject.disabled = false;
      }
    }, { once:true });
  }

  function updateStatus(airline, indexPending, newStatus){
    const data = readRoutes();
    const grp = data.find(g=>g.aerolinea===airline);
    if(!grp) return;
    const pendingIndexes = [];
    grp.routes.forEach((r,idx)=>{ if(r.status==='Ingresada') pendingIndexes.push(idx); });
    const realIdx = pendingIndexes[indexPending];
    if(realIdx===undefined) return;
    grp.routes[realIdx].status = newStatus;
    writeRoutes(data);
  }

  document.addEventListener('DOMContentLoaded', ()=>{
    ensureDemoIfEmpty();
    if(!guard()) return;
    loadAirlines();

    $('#afr-airline')?.addEventListener('change', e=>{
      renderRoutesFor(e.target.value || '');
    });

    $('#afr-approve')?.addEventListener('click', ()=>{
      const airline = $('#afr-airline').value;
      if(!airline) return;
      const sel = document.querySelector('.afr-route-radio:checked');
      if(!sel) return;
      updateStatus(airline, parseInt(sel.value,10), 'Confirmada');
      renderRoutesFor(airline);
      loadAirlines();
      alert('Ruta confirmada.');
    });

    $('#afr-reject')?.addEventListener('click', ()=>{
      const airline = $('#afr-airline').value;
      if(!airline) return;
      const sel = document.querySelector('.afr-route-radio:checked');
      if(!sel) return;
      updateStatus(airline, parseInt(sel.value,10), 'Rechazada');
      renderRoutesFor(airline);
      loadAirlines();
      alert('Ruta rechazada.');
    });
  });
})();
