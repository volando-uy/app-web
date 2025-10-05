// Inyección header/footer (similar a otras vistas)
fetch("../header/header.html").then(r=>r.text()).then(h=>{
  const el=document.getElementById("header"); if(el) el.innerHTML=h;
  const s=document.createElement("script"); s.src="../header/header.js"; s.onload=()=>{ if(typeof initHeader==='function') initHeader(); }; document.body.appendChild(s);
});
fetch("../footer/footer.html").then(r=>r.text()).then(h=>{
  const el=document.getElementById("footer"); if(el) el.innerHTML=h;
});

// Utils
const $ = (q,ctx=document)=>ctx.querySelector(q);
const esc = s=>String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
function read(k){ try{return JSON.parse(localStorage.getItem(k)||'[]');}catch{return[];} }
function write(k,v){ localStorage.setItem(k,JSON.stringify(v)); }
function nowISO(){ return new Date().toISOString(); }
function addDays(dateIso, days){
  const d=new Date(dateIso); d.setDate(d.getDate()+days); return d.toISOString();
}

// Obtener usuario (cliente) actual
function getCurrentUser(){
  try{
    const auth = JSON.parse(sessionStorage.getItem('auth')||'null');
    if(auth?.role==='user') return { role:'user', name: auth.name || auth.nickname || 'Cliente' };
    // fallback role simple
    const role = sessionStorage.getItem('role');
    if(role==='user'){
      let u=null; try{ u=JSON.parse(sessionStorage.getItem('user')||'null'); }catch{}
      return { role:'user', name: (u&&u.name)||'Cliente' };
    }
  }catch{}
  return null;
}

// Sembrar demo si no hay paquetes con rutas (para pruebas)
function seedDemoIfNeeded(){
  const pkgs = read('packages');
  const hasWithRoutes = pkgs.some(p=>p.flightRoutes && p.flightRoutes.routes && p.flightRoutes.routes.length);
  if(hasWithRoutes) return;
  const demo = [
    {
      id: 'PKG-DEMO-ROUTES-1',
      name: 'Escapada Express',
      origin:'MVD', dest:'AEP', duration:'2 días',
      price: 250, images: [], includes:['Vuelo ida','Vuelo vuelta'],
      flightRoutes:{
        airline:'CieloSur',
        routes:[
          { label:'MVD → AEP (0h55m)', qty:1 },
          { label:'AEP → MVD (0h50m)', qty:1 }
        ]
      }
    },
    {
      id: 'PKG-DEMO-ROUTES-2',
      name: 'Multi tramo Andes',
      origin:'SCL', dest:'MDZ', duration:'5 días',
      price: 540, images: [], includes:['Vuelos','Soporte'],
      flightRoutes:{
        airline:'AndesAir',
        routes:[
          { label:'SCL → MDZ (1h00m)', qty:1 },
          { label:'MDZ → SCL (1h05m)', qty:1 }
        ]
      }
    }
  ];
  write('packages', pkgs.concat(demo));
}

// Filtrar paquetes elegibles
function getEligiblePackages(){
  const list = read('packages');
  return list.filter(p=>{
    const fr = p.flightRoutes;
    return fr && Array.isArray(fr.routes) && fr.routes.length>0;
  });
}

// Render de compras vigentes
function renderPurchases(user){
  const box = $('#bp-purchases');
  const listEl = $('#bp-purchases-list');
  if(!user || !box || !listEl) return;
  const all = read('packagePurchases')
    .filter(pp=>pp.user===user.name)
    .sort((a,b)=> new Date(b.purchaseDate)-new Date(a.purchaseDate));
  if(!all.length){
    box.classList.add('hidden');
    listEl.innerHTML='';
    return;
  }
  box.classList.remove('hidden');
  listEl.innerHTML = all.map(p=>{
    const exp = new Date(p.expiresAt);
    const expired = exp < new Date();
    return `<div class="flex items-center justify-between p-2 rounded border ${expired?'bg-gray-50':'bg-emerald-50'}">
      <div class="text-xs">
        <div class="font-semibold">${esc(p.packageName)}</div>
        <div class="text-gray-500">${p.cost ? 'USD '+p.cost : 'Sin costo'} • Compra: ${new Date(p.purchaseDate).toLocaleDateString()}</div>
        <div class="text-gray-500">Vence: ${exp.toLocaleDateString()} ${expired ? '(Vencido)':''}</div>
      </div>
      <span class="text-[10px] px-2 py-1 rounded-full ${expired?'bg-gray-300 text-gray-700':'bg-emerald-600 text-white'}">${expired?'VENCIDO':'ACTIVO'}</span>
    </div>`;
  }).join('');
}

// Estado selección
let selection = null;

// Limpiar selección
function clearSelection(){
  selection = null;
  $('#bp-selection')?.classList.add('hidden');
  $('#bp-selection-info') && ($('#bp-selection-info').innerHTML='');
  document.querySelectorAll('[data-pkg-id]').forEach(c=>c.classList.remove('ring-2','ring-brand'));
}

// Mostrar selección
function showSelection(pkg, user){
  const panel = $('#bp-selection');
  if(!panel) return;
  panel.classList.remove('hidden');
  const routes = pkg.flightRoutes.routes.map(r=>`<li class="text-xs">${esc(r.label)} x ${r.qty}</li>`).join('');
  $('#bp-selection-info').innerHTML = `
    <div class="text-sm">
      <div><b>Paquete:</b> ${esc(pkg.name||pkg.title||pkg.id)}</div>
      <div><b>Precio:</b> ${pkg.price ? 'USD '+pkg.price : '—'}</div>
      <div><b>Rutas:</b></div>
      <ul class="mt-1 ml-4 list-disc">${routes}</ul>
      <div class="mt-2 text-xs text-gray-500">El paquete vencerá en 30 días desde hoy.</div>
      <div class="mt-1 text-xs text-gray-500">Usuario: ${esc(user.name)}</div>
    </div>
  `;
}

// Ya comprado y vigente
function hasActivePurchase(user, pkg){
  const all = read('packagePurchases')
    .filter(p=>p.user===user.name && p.packageId===pkg.id);
  const now=new Date();
  return all.some(p=> new Date(p.expiresAt) > now);
}

// Render paquetes
function renderPackages(){
  const user = getCurrentUser();
  const listEl = $('#bp-list');
  const alertEl = $('#bp-alert');
  if(!listEl||!alertEl) return;

  if(!user){
    alertEl.textContent='Debes iniciar sesión como cliente para comprar un paquete.';
    alertEl.classList.remove('hidden');
    listEl.innerHTML='';
    $('#bp-selection')?.classList.add('hidden');
    $('#bp-purchases')?.classList.add('hidden');
    return;
  }

  seedDemoIfNeeded();
  const pkgs = getEligiblePackages();

  if(!pkgs.length){
    alertEl.textContent='No hay paquetes con rutas disponibles.';
    alertEl.classList.remove('hidden');
    listEl.innerHTML='';
    $('#bp-selection')?.classList.add('hidden');
    renderPurchases(user);
    return;
  }

  alertEl.classList.add('hidden');

  listEl.innerHTML = pkgs.map(p=>{
    const active = hasActivePurchase(user,p);
    const firstImg = p.images && p.images[0];
    return `
      <article class="relative bg-white rounded-xl shadow hover:shadow-lg transition flex flex-col overflow-hidden border
        ${selection && selection.id===p.id ? 'ring-2 ring-brand' : ''}" data-pkg-id="${esc(p.id)}">
        <div class="h-36 bg-gray-100 relative overflow-hidden">
          ${firstImg ? `<img src="${esc(firstImg)}" class="w-full h-full object-cover">` : ''}
          <div class="absolute top-2 left-2 bg-brand/80 text-white text-[11px] px-2 py-0.5 rounded">${esc(p.flightRoutes.airline||'')}</div>
        </div>
        <div class="p-4 flex flex-col gap-2 flex-1">
          <h3 class="font-semibold text-brand text-sm leading-snug">${esc(p.name||p.title||p.id)}</h3>
          <p class="text-xs text-gray-500 truncate">${p.origin||'-'} → ${p.dest||'-'} • ${p.duration||''}</p>
          <ul class="text-[11px] text-gray-600 space-y-0.5">
            ${p.flightRoutes.routes.slice(0,3).map(r=>`<li>${esc(r.label)} x ${r.qty}</li>`).join('')}
            ${p.flightRoutes.routes.length>3?`<li>…</li>`:''}
          </ul>
          <div class="mt-auto flex items-center justify-between pt-2 border-t">
            <span class="text-sm font-bold text-emerald-600">${p.price?('USD '+p.price):'Sin costo'}</span>
            <button class="text-xs px-2 py-1 rounded border
              ${active?'opacity-50 cursor-not-allowed':'hover:bg-brand hover:text-white transition'}"
              data-select="${esc(p.id)}">${active?'Ya comprado':'Seleccionar'}</button>
          </div>
        </div>
      </article>
    `;
  }).join('');

  // Eventos
  listEl.querySelectorAll('[data-select]').forEach(btn=>{
    btn.addEventListener('click', e=>{
      const id = btn.getAttribute('data-select');
      const pkg = pkgs.find(x=>x.id===id);
      if(!pkg) return;
      if(hasActivePurchase(user,pkg)){
        alert('Ya tienes este paquete activo. Elige otro.');
        return;
      }
      selection = pkg;
      document.querySelectorAll('[data-pkg-id]').forEach(c=>c.classList.remove('ring-2','ring-brand'));
      btn.closest('[data-pkg-id]')?.classList.add('ring-2','ring-brand');
      showSelection(pkg,user);
    });
  });

  // PRESELECCIÓN desde package.js
  const pre = sessionStorage.getItem('preselectPackage');
  if (pre && user) {
    const prePkg = pkgs.find(p=>p.id === pre);
    if (prePkg && !hasActivePurchase(user, prePkg)) {
      selection = prePkg;
      const card = listEl.querySelector(`[data-pkg-id="${pre}"]`);
      card?.classList.add('ring-2','ring-brand');
      showSelection(prePkg, user);
    }
    sessionStorage.removeItem('preselectPackage');
  }

  renderPurchases(user);
}

// Confirmar compra
function confirmPurchase(){
  const user = getCurrentUser();
  if(!user){ alert('Sesión expirada.'); return; }
  if(!selection){ alert('Selecciona un paquete.'); return; }
  if(hasActivePurchase(user, selection)){
    alert('Ya tienes un activo de este paquete.');
    return;
  }
  const purchaseDate = nowISO();
  const expiresAt = addDays(purchaseDate, 30);
  const entry = {
    id: 'PP-'+Date.now(),
    packageId: selection.id,
    packageName: selection.name||selection.title||selection.id,
    cost: selection.price||0,
    currency: 'USD',
    user: user.name,
    purchaseDate,
    expiresAt,
    status:'ACTIVE'
  };
  const list = read('packagePurchases');
  list.push(entry);
  write('packagePurchases', list);
  alert('Compra registrada.');
  clearSelection();
  renderPackages();
}

// Eventos globales
document.addEventListener('DOMContentLoaded', ()=>{
  $('#btn-refresh')?.addEventListener('click', ()=>{ clearSelection(); renderPackages(); });
  $('#btn-cancel')?.addEventListener('click', ()=>{ window.location.href='/public/index.html'; });
  $('#btn-clear')?.addEventListener('click', ()=> clearSelection());
  $('#btn-confirm')?.addEventListener('click', confirmPurchase);
  renderPackages();
});
