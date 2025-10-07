// Inyectar header/footer (igual que otras vistas)
fetch("../header/header.html").then(r=>r.text()).then(html=>{
  const el = document.getElementById("header");
  if(el) el.innerHTML = html;
  const s = document.createElement("script"); s.src="../header/header.js"; s.onload = ()=> { if(typeof initHeader==='function') initHeader(); }; document.body.appendChild(s);
});
fetch("../footer/footer.html").then(r=>r.text()).then(html=>{ const el = document.getElementById("footer"); if(el) el.innerHTML = html; });

// Lógica de paquetes (localStorage)
document.addEventListener('DOMContentLoaded', () => {
  const KEY = 'packages';
  const KEY_CITIES = 'cities';
  const KEY_CATEGORIES = 'categories';

  // elementos
  const selOrigin = document.getElementById('pkg-origin');
  const selDest = document.getElementById('pkg-dest');
  const selCat = document.getElementById('pkg-category');
  const nameIn = document.getElementById('pkg-name');
  const durIn = document.getElementById('pkg-duration');
  const priceIn = document.getElementById('pkg-price');
  const descIn = document.getElementById('pkg-desc');
  const imagesIn = document.getElementById('pkg-images');
  const imagesPreview = document.getElementById('pkg-images-preview');
  const includesInput = document.getElementById('pkg-include-input');
  const includesWrap = document.getElementById('pkg-includes');
  const btnSave = document.getElementById('btn-save-package');
  const btnReset = document.getElementById('btn-reset-package');
  const pkgList = document.getElementById('packages-list');
  const preview = document.getElementById('pkg-preview');

  let includes = [];
  let images = []; // data URLs
  let editId = null;

  // storage helpers
  const read = (k) => { try { return JSON.parse(localStorage.getItem(k) || '[]'); } catch { return []; } };
  const write = (k, v) => localStorage.setItem(k, JSON.stringify(v));

  // cargar selects de ciudades y categorias
  function populateSelects(){
    const cities = read(KEY_CITIES);
    const cats = read(KEY_CATEGORIES);
    const renderOptions = (arr, sel) => {
      if(!sel) return;
      sel.innerHTML = `<option value="">--</option>` + arr.map(c=>`<option value="${escapeHtml(c.name)}">${escapeHtml(c.name)}${c.country?(' - '+escapeHtml(c.country)):''}</option>`).join('');
    };
    renderOptions(cities, selOrigin);
    renderOptions(cities, selDest);
    if(selCat) selCat.innerHTML = `<option value="">--</option>` + cats.map(c=>`<option value="${escapeHtml(c.name)}">${escapeHtml(c.name)}</option>`).join('');
  }

  // includes UI
  function renderIncludes(){
    includesWrap.innerHTML = includes.map((inc, i)=>`<span class="px-2 py-1 bg-gray-100 rounded-full text-sm flex items-center gap-2"><span>${escapeHtml(inc)}</span><button data-i="${i}" class="remove-include text-red-500 text-xs">✕</button></span>`).join(' ');
  }
  includesInput.addEventListener('keydown', (e)=>{
    if(e.key === 'Enter'){ e.preventDefault(); const v = includesInput.value.trim(); if(v){ includes.push(v); includesInput.value=''; renderIncludes(); } }
  });
  includesWrap.addEventListener('click', (e)=>{ const b = e.target.closest('.remove-include'); if(!b) return; const i = Number(b.getAttribute('data-i')); includes.splice(i,1); renderIncludes(); });

  // images handling
  imagesIn.addEventListener('change', (e)=>{
    const files = Array.from(e.target.files || []);
    const max = 5;
    const toAdd = files.slice(0, max - images.length);
    toAdd.forEach(f=>{
      const reader = new FileReader();
      reader.onload = (ev)=>{
        images.push(ev.target.result);
        renderImagesPreview();
      };
      reader.readAsDataURL(f);
    });
    imagesIn.value = '';
  });
  function renderImagesPreview(){
    imagesPreview.innerHTML = images.map((d,i)=>`<div class="relative"><img src="${d}" class="w-24 h-16 object-cover rounded"/><button data-i="${i}" class="remove-img absolute -top-2 -right-2 bg-white rounded-full p-1 text-red-500">✕</button></div>`).join(' ');
  }
  imagesPreview.addEventListener('click', (e)=>{ const b = e.target.closest('.remove-img'); if(!b) return; const i = Number(b.getAttribute('data-i')); images.splice(i,1); renderImagesPreview(); });

  // package render
  function renderPackages(){
    const list = read(KEY).slice().reverse();
    pkgList.innerHTML = list.length ? list.map(p=>`
      <div class="p-3 border rounded flex items-start gap-3">
        <img src="${p.images?.[0]||''}" class="w-20 h-12 object-cover rounded bg-gray-100"/>
        <div class="flex-1">
          <div class="font-semibold">${escapeHtml(p.name)}</div>
          <div class="text-xs text-gray-500">${escapeHtml(p.origin||'-')} → ${escapeHtml(p.dest||'-')} • ${escapeHtml(p.duration||'')}</div>
          <div class="text-sm text-gray-700 mt-1">${escapeHtml(p.includes?.slice(0,2).join(', ')||'')}</div>
        </div>
        <div class="flex flex-col gap-2">
          <button data-id="${p.id}" class="edit-pkg px-2 py-1 border rounded text-sm">Editar</button>
          <button data-id="${p.id}" class="del-pkg px-2 py-1 text-red-500 border rounded text-sm">Eliminar</button>
          <button data-id="${p.id}" class="add-route-pkg px-2 py-1 border rounded text-sm bg-gray-50">Añadir rutas</button>
        </div>
      </div>
    `).join('') : `<div class="text-sm text-gray-500">No hay paquetes creados.</div>`;
  }

  // preview single
  function showPreview(pkg){
    if(!pkg) { preview.innerHTML = 'Selecciona o crea un paquete para ver preview.'; return; }
    preview.innerHTML = `
      <div class="font-semibold text-lg">${escapeHtml(pkg.name)}</div>
      <div class="text-xs text-gray-500">${escapeHtml(pkg.origin||'')} → ${escapeHtml(pkg.dest||'')} • ${escapeHtml(pkg.duration||'')}</div>
      <div class="mt-2">${escapeHtml(pkg.description||'')}</div>
      <div class="flex gap-2 mt-3">${(pkg.images||[]).slice(0,3).map(i=>`<img src="${i}" class="w-20 h-12 object-cover rounded"/>`).join('')}</div>
      <div class="mt-2 text-sm text-gray-700"><b>Incluye:</b> ${(pkg.includes||[]).map(escapeHtml).join(', ')}</div>
      <div class="mt-2 font-bold text-brand">${pkg.price ? ('$ ' + Number(pkg.price).toFixed(2)) : ''}</div>
    `;
  }

  // save package
  btnSave.addEventListener('click', ()=>{
    const name = (nameIn.value||'').trim();
    if(!name){ alert('Nombre obligatorio'); return; }
    // Detectar aerolínea desde sesión
    let airlineName = '';
    try {
      const auth = JSON.parse(sessionStorage.getItem('auth')||'null');
      if(auth?.role === 'airline') airlineName = auth.name || auth.nickname || '';
      if(!airlineName && sessionStorage.getItem('airline')) {
        const a = JSON.parse(sessionStorage.getItem('airline'));
        airlineName = a?.name || airlineName;
      }
    } catch {}
    const pkg = {
      id: editId || Date.now().toString(),
      name,
      duration: durIn.value||'',
      origin: selOrigin.value||'',
      dest: selDest.value||'',
      category: selCat.value||'',
      price: priceIn.value||'',
      description: descIn.value||'',
      includes: includes.slice(),
      images: images.slice(),
      airline: airlineName // NUEVO
    };
    const list = read(KEY);
    if(editId){
      // replace existing (remove old by id)
      const idx = list.findIndex(x=>x.id===editId);
      if(idx>=0) list.splice(idx,1,pkg);
    } else {
      list.push(pkg);
    }
    write(KEY, list);
    resetForm();
    renderPackages();
    showPreview(pkg);
    alert('Paquete guardado');
  });

  // reset / clear
  btnReset.addEventListener('click', resetForm);
  function resetForm(){
    editId = null;
    nameIn.value = durIn.value = priceIn.value = descIn.value = '';
    selOrigin.selectedIndex = selDest.selectedIndex = selCat.selectedIndex = 0;
    includes = []; images = []; renderIncludes(); renderImagesPreview();
    showPreview(null);
  }

  // delegation edit/delete/add-route
  pkgList.addEventListener('click', (e)=>{
    const del = e.target.closest('.del-pkg');
    const edit = e.target.closest('.edit-pkg');
    const addr = e.target.closest('.add-route-pkg');
    if(del){
      const id = del.getAttribute('data-id');
      const list = read(KEY);
      const idx = list.findIndex(x=>x.id===id);
      if(idx<0) return;
      if(!confirm('Eliminar paquete "'+list[idx].name+'"?')) return;
      list.splice(idx,1); write(KEY,list); renderPackages(); resetForm();
    } else if(edit){
      const id = edit.getAttribute('data-id');
      const list = read(KEY);
      const p = list.find(x=>x.id===id);
      if(!p) return;
      // populate
      editId = p.id;
      nameIn.value = p.name||''; durIn.value = p.duration||''; priceIn.value = p.price||''; descIn.value = p.description||'';
      selOrigin.value = p.origin||''; selDest.value = p.dest||''; selCat.value = p.category||'';
      includes = p.includes ? p.includes.slice() : []; images = p.images ? p.images.slice() : [];
      renderIncludes(); renderImagesPreview(); showPreview(p);
      // remove old entry so save will push updated one (or we could replace)
      const idx = list.findIndex(x=>x.id===id); if(idx>=0){ list.splice(idx,1); write(KEY,list); renderPackages(); }
    } else if(addr){
      const id = addr.getAttribute('data-id');
      openFlightRouteModal(id);
    }
  });

  // ---------- Flight routes modal logic ----------
  const modal = document.getElementById('flightroute-modal');
  const frClose = document.getElementById('fr-close');
  const frCancel = document.getElementById('fr-cancel');
  const frSave = document.getElementById('fr-save');
  const frAddRow = document.getElementById('fr-add-row');
  const frRows = document.getElementById('fr-routes-rows');
  const frAirline = document.getElementById('fr-airline');
  const frQty = document.getElementById('fr-quantity');
  const frmPkgTitle = document.getElementById('frm-pkg-title');

  function openFlightRouteModal(pkgId){
    // open modal and populate if package already has routes
    const list = read(KEY);
    const pkg = list.find(p=>p.id===pkgId);
    if(!pkg) { alert('Paquete no encontrado'); return; }
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    frRows.innerHTML = '';
    frAirline.value = (pkg.flightRoutes && pkg.flightRoutes.airline) ? pkg.flightRoutes.airline : '';
    frQty.value = 1;
    frmPkgTitle.textContent = `Añadir rutas a: ${pkg.name}`;

    // Rellenar rutas disponibles para la aerolínea (si existen)
    const airlineName = (pkg.flightRoutes && pkg.flightRoutes.airline) ? pkg.flightRoutes.airline : frAirline.value;
    const avail = getAvailableRoutesForAirline(airlineName);
    populateAvailableRoutes(avail, pkg.flightRoutes && pkg.flightRoutes.routes ? pkg.flightRoutes.routes : []);

    // Si ya tenía rutas manuales, mostrarlas también
    const existing = (pkg.flightRoutes && pkg.flightRoutes.routes) ? pkg.flightRoutes.routes : [];
    if(!existing.length) {
      addFlightRouteRow('', 1); // una fila manual por defecto
    } else {
      // si quieres mostrar manual rows too, leave manual empty (we use available list above)
      // but to allow manual edits, we render no manual rows by default; user can add manual rows with button
    }

    // store editing target id on modal element
    modal.setAttribute('data-pkg-id', pkgId);
  }

  // Obtener rutas disponibles: intenta localStorage 'flightRoutes', si no existe usa window.vuelos (si está cargado)
  function getAvailableRoutesForAirline(airline) {
    const out = [];
    try {
      const stored = JSON.parse(localStorage.getItem('flightRoutes') || '[]');
      if (Array.isArray(stored) && stored.length) {
        stored.forEach((f, fi) => {
          if (!airline || String(f.aerolinea||'').toLowerCase() === String(airline||'').toLowerCase()) {
            const routes = Array.isArray(f.routes) ? f.routes : (f.detalles && f.detalles.rutas) || [];
            routes
              .filter(r=>r.status!=='Rechazada') // evitar rechazadas
              .forEach((r, ri) => {
                const label = r.label || ((r.origen && r.destino) ? `${r.origen} → ${r.destino} (${r.tiempo||''})` : JSON.stringify(r));
                out.push({ id: `stored_${fi}_${ri}`, label });
              });
          }
        });
      }
    } catch(e){ /* ignore */ }
    // fallback: usar window.vuelos si existe
    if (!out.length && window.vuelos && Array.isArray(window.vuelos)) {
      window.vuelos.forEach((f, fi) => {
        if (!airline || String(f.aerolinea||'').toLowerCase() === String(airline||'').toLowerCase()) {
          const routes = (f.detalles && f.detalles.rutas) || [];
          routes.forEach((r, ri) => {
            const label = `${f.aerolinea}: ${r.origen} → ${r.destino} (${r.tiempo||''})`;
            out.push({ id: `vuelos_${fi}_${ri}`, label });
          });
        }
      });
    }
    return out;
  }

  // Render lista de rutas disponibles con checkbox + qty
  function populateAvailableRoutes(available, selectedRoutes) {
    const container = document.getElementById('fr-available-routes');
    container.innerHTML = '';
    if (!available || !available.length) {
      container.innerHTML = `<div class="text-sm text-gray-500">No hay rutas preexistentes para la aerolínea. Usa "Añadir fila manual".</div>`;
      return;
    }
    available.forEach((a, i) => {
      const already = (selectedRoutes || []).find(r => r.label === a.label);
      const qty = already ? (already.qty || 1) : 1;
      const row = document.createElement('div');
      row.className = 'flex items-center gap-2';
      row.innerHTML = `
        <input type="checkbox" class="fr-available-checkbox" data-id="${escapeHtml(a.id)}" ${already ? 'checked' : ''} />
        <div class="flex-1 text-sm">${escapeHtml(a.label)}</div>
        <input type="number" min="1" value="${qty}" class="fr-available-qty w-20 border rounded px-2 py-1" />
      `;
      container.appendChild(row);
    });
  }

  frClose?.addEventListener('click', closeFlightRouteModal);
  frCancel?.addEventListener('click', closeFlightRouteModal);

  function addFlightRouteRow(label = '', qty = 1){
    const idx = frRows.children.length;
    const row = document.createElement('div');
    row.className = 'flex items-center gap-2';
    row.innerHTML = `
      <input class="fr-route-label flex-1 border rounded px-2 py-1" placeholder="Origen → Destino o descripción" value="${escapeHtml(label)}" />
      <input type="number" class="fr-route-qty w-20 border rounded px-2 py-1" min="1" value="${Number(qty)||1}" />
      <button class="fr-row-remove px-2 py-1 text-red-500">Eliminar</button>
    `;
    frRows.appendChild(row);
  }

  frAddRow?.addEventListener('click', (e)=>{
    e.preventDefault();
    addFlightRouteRow('', 1);
  });

  frRows?.addEventListener('click', (e)=>{
    const rem = e.target.closest('.fr-row-remove');
    if(rem){
      const row = rem.closest('div');
      row?.remove();
    }
  });

  frSave?.addEventListener('click', ()=>{
    const pkgId = modal.getAttribute('data-pkg-id');
    if(!pkgId) return;
    const airline = (frAirline.value || '').trim();
    const qtyGlobal = Math.max(1, parseInt(frQty.value || '1', 10));
    // recoger rutas seleccionadas de la lista disponible
    const selectedAvailable = Array.from(frRows.parentElement.querySelectorAll('.fr-available-checkbox'))
      .filter(ch => ch.checked)
      .map((ch, i) => {
        const id = ch.getAttribute('data-id');
        const label = ch.parentElement.querySelector('div')?.textContent?.trim() || '';
        const qtyInput = ch.parentElement.querySelector('.fr-available-qty');
        const qty = qtyInput ? (Number(qtyInput.value) || 1) : 1;
        return { label, qty };
      });
    // recoger filas manuales (si las hay)
    const manual = Array.from(frRows.querySelectorAll('.fr-route-label')).map((inp, i) => {
      const label = inp.value.trim();
      const qty = Number(frRows.querySelectorAll('.fr-route-qty')[i].value) || 1;
      return { label, qty };
    }).filter(r => r.label);
    // combinar
    const rows = selectedAvailable.concat(manual).map(r => ({ label: r.label, qty: (Number(r.qty)||1) * qtyGlobal }));
    if(rows.length === 0){ alert('Agrega al menos una ruta'); return; }
    // attach to package
    const list = read(KEY);
    const pkg = list.find(p=>p.id===pkgId);
    if(!pkg){ alert('Paquete no encontrado'); closeFlightRouteModal(); return; }
    pkg.flightRoutes = pkg.flightRoutes || { airline:'', routes: [] };
    pkg.flightRoutes.airline = airline;
    // Expand rows by global qtyGlobal: for each row push qtyGlobal times? We'll store qty per row and also multiply if needed.
    // Here we store rows with their qty multiplied by qtyGlobal (useful for stock)
    pkg.flightRoutes.routes = rows;
    write(KEY, list);
    renderPackages();
    alert('Rutas añadidas al paquete');
    closeFlightRouteModal();
  });

  // init
  populateSelects();
  renderPackages();
  resetForm();

  // helper
  function escapeHtml(s){ return String(s||'').replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }
});
