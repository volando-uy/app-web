// Inyectar header / footer
fetch("../header/header.html").then(r=>r.text()).then(h=>{
  const el = document.getElementById("header"); if(el) el.innerHTML = h;
  const s = document.createElement("script"); s.src="../header/header.js"; s.onload=()=>{ if(typeof initHeader==='function') initHeader(); }; document.body.appendChild(s);
});
fetch("../footer/footer.html").then(r=>r.text()).then(h=>{
  const el = document.getElementById("footer"); if(el) el.innerHTML = h;
});

// Utils
const $ = (s,ctx=document)=>ctx.querySelector(s);
const esc = s=>String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

// Leer sesión
function getAirlineName(){
  try{
    const auth = JSON.parse(sessionStorage.getItem('auth')||'null');
    if(auth?.role==='airline') return auth.name || auth.nickname;
    const a = JSON.parse(sessionStorage.getItem('airline')||'null');
    if(a?.name) return a.name;
  }catch{}
  return null;
}

// Leer paquetes
function readPackages(){
  try { return JSON.parse(localStorage.getItem('packages')||'[]'); } catch { return []; }
}

// NUEVO: sembrar paquetes demo para una aerolínea si no existen
function seedDemoPackages(airline){
  if(!airline) return;
  let list = readPackages();
  const hasAny = list.some(p => (p.airline||'').toLowerCase() === airline.toLowerCase());
  if(hasAny) return; // ya tiene
  const demo = [
    {
      id: 'PKG-'+airline.replace(/\s+/g,'').toUpperCase()+'-001',
      name: 'Escapada '+airline.split(' ')[0],
      duration: '3 días',
      origin: 'MVD',
      dest: 'AEP',
      category: 'City Break',
      price: 299,
      description: 'Paquete demo para visualizar listado. Incluye vuelo y una noche de hotel.',
      includes: ['Vuelo ida y vuelta','1 noche hotel','Traslados'],
      images: ['https://via.placeholder.com/400x220.png?text=Paquete+1'],
      airline
    },
    {
      id: 'PKG-'+airline.replace(/\s+/g,'').toUpperCase()+'-002',
      name: 'Aventura '+airline.split(' ')[0],
      duration: '5 días',
      origin: 'MVD',
      dest: 'SCL',
      category: 'Aventura',
      price: 549,
      description: 'Segundo paquete demo para pruebas. Datos ficticios.',
      includes: ['Vuelos','Hotel 4*','Excursión','Seguro básico'],
      images: ['https://via.placeholder.com/400x220.png?text=Paquete+2'],
      airline
    }
  ];
  list = list.concat(demo);
  localStorage.setItem('packages', JSON.stringify(list));
}

function render(){
  const container = $('#cp-list');
  const alertBox = $('#cp-alert');
  if(!container) return;

  const airline = getAirlineName();

  // NUEVO: sembrar antes de leer
  if(airline) seedDemoPackages(airline);

  if(!airline){
    alertBox.textContent = 'Debes iniciar sesión como aerolínea para ver tus paquetes.';
    alertBox.classList.remove('hidden');
    container.innerHTML = '';
    return;
  }
  alertBox.classList.add('hidden');

  const all = readPackages();
  // Filtro: paquetes con airline = nombre. Si no tienen airline (viejos), no se muestran.
  const mine = all.filter(p => (p.airline||'').toLowerCase() === airline.toLowerCase());

  if(!mine.length){
    container.innerHTML = `
      <div class="col-span-full p-6 rounded-xl bg-white shadow text-center text-sm text-gray-600">
        No tienes paquetes registrados todavía.
        <div class="mt-3">
          <a href="../createPackage/createPackage.html" class="inline-block px-4 py-2 bg-brand text-white rounded-lg text-sm hover:brightness-110">
            Crear primer paquete
          </a>
        </div>
      </div>
    `;
    return;
  }

  container.innerHTML = mine.map(p=>{
    const img = (p.images && p.images[0]) || 'https://via.placeholder.com/400x220.png?text=Paquete';
    const inc = (p.includes||[]).slice(0,3).map(i=>`<li class="text-xs text-gray-600">${esc(i)}</li>`).join('');
    return `
      <article class="bg-white rounded-xl shadow hover:shadow-lg transition flex flex-col overflow-hidden">
        <div class="relative">
          <img src="${img}" alt="${esc(p.name)}" class="w-full h-40 object-cover">
          <span class="absolute top-2 left-2 bg-brand/80 text-white text-[11px] px-2 py-0.5 rounded">
            ${esc(p.airline||'')}
          </span>
        </div>
        <div class="p-4 flex flex-col gap-3 flex-1">
          <div>
            <h3 class="font-semibold text-brand leading-tight">${esc(p.name)}</h3>
            <p class="text-xs text-gray-500 mt-0.5">${esc(p.origin||'-')} → ${esc(p.dest||'-')} • ${esc(p.duration||'')}</p>
          </div>
          <ul class="space-y-1">${inc || '<li class="text-xs text-gray-400">Sin items</li>'}</ul>
          <div class="mt-auto flex items-center justify-between pt-2 border-t">
            <span class="text-sm font-bold text-emerald-600">${p.price ? 'USD '+Number(p.price).toFixed(2) : '—'}</span>
            <a href="../createPackage/createPackage.html" class="text-xs px-2 py-1 rounded border text-brand hover:bg-brand hover:text-white transition">
              Editar
            </a>
          </div>
        </div>
      </article>
    `;
  }).join('');
}

document.addEventListener('DOMContentLoaded', ()=>{
  render();
  $('#btn-refresh')?.addEventListener('click', render);
});
