(async function () {
  const path = location.pathname.replace(/\\/g,'/').toLowerCase();
  if (path.endsWith('/public/index.html') || path.endsWith('/index.html') || path === '/' ) {
    // No mostrar panel en la página principal
    return;
  }

  let container = document.getElementById('rightMenu');
  if (!container) {
    container = document.createElement('aside');
    container.id = 'rightMenu';
    container.className = 'hidden lg:block';
    (document.querySelector('main .grid') || document.querySelector('main') || document.body).prepend(container);
  }

  const escapeHtml = s => String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

  container.classList.add(
    'w-[240px]','sticky','top-[72px]','self-start','max-h-[80vh]','overflow-y-auto','overflow-x-hidden',
    'rounded-2xl','bg-gradient-to-br','from-brand','to-blue-500','text-white',
    'shadow-[0_6px_24px_-8px_rgba(0,0,0,.30)]','ring-1','ring-black/10','p-4','flex','flex-col'
  );

  if (!document.getElementById('__rightMenuStyle')) {
    const st = document.createElement('style');
    st.id = '__rightMenuStyle';
    st.textContent = `
      #rightMenu::-webkit-scrollbar{width:8px}
      #rightMenu::-webkit-scrollbar-track{background:transparent}
      #rightMenu::-webkit-scrollbar-thumb{background:rgba(255,255,255,.25);border-radius:12px}
      #rightMenu::-webkit-scrollbar-thumb:hover{background:rgba(255,255,255,.4)}
      .rm-link.active{position:relative;background:linear-gradient(to right,rgba(255,255,255,.18),rgba(255,255,255,.05));font-weight:600}
      .rm-link.active::before{content:"";position:absolute;left:0;top:0;bottom:0;width:4px;border-radius:0 4px 4px 0;background:#fff}
    `;
    document.head.appendChild(st);
  }

  async function loadTemplate(){
    const guesses = [
      'leftPanel.html',
      '../components/leftPanel/leftPanel.html',
      '../../views/components/leftPanel/leftPanel.html',
      '/public/src/views/components/leftPanel/leftPanel.html',
      // fallback (por compatibilidad previa):
      'rightMenu.html',
      '../components/rightMenu/rightMenu.html',
      '../../views/components/rightMenu/rightMenu.html',
      '/public/src/views/components/rightMenu/rightMenu.html'
    ];
    for (const url of guesses) {
      try { const r = await fetch(url,{cache:'no-store'}); if(r.ok) return await r.text(); } catch {}
    }
    return '';
  }

  let tpl = await loadTemplate();
  if (!tpl){
    container.innerHTML = `<div class="text-xs text-white/80">No se pudo cargar el panel.</div>`;
    return;
  }
  const match = tpl.match(/<aside[^>]*id=["']rightMenu["'][^>]*>([\s\S]*?)<\/aside>/i);
  if (match) tpl = match[1];
  container.innerHTML = tpl;

  function applyVisibility(){
    if (window.innerWidth >= 1024){
      container.classList.remove('hidden');
      container.classList.add('block');
    } else {
      container.classList.add('hidden');
    }
  }
  applyVisibility();
  window.addEventListener('resize', applyVisibility);
  requestAnimationFrame(applyVisibility);

  function readSession(){
    try{
      const auth = JSON.parse(sessionStorage.getItem('auth')||'null');
      if(auth?.role) return { role:auth.role, name:auth.name||auth.nickname };
    }catch{}
    const role = sessionStorage.getItem('role')||null;
    let name=null;
    try{
      if(role==='user' && sessionStorage.getItem('user')) name = JSON.parse(sessionStorage.getItem('user')).name;
      if(role==='airline' && sessionStorage.getItem('airline')) name = JSON.parse(sessionStorage.getItem('airline')).name;
      if(!name){
        const ud = JSON.parse(localStorage.getItem('userData')||'null');
        if(ud) name = ud.nombre || ud.nickname || ud.name;
      }
    }catch{}
    return { role, name };
  }

  const ctx = readSession();
  const nameEl = container.querySelector('#rm-name');
  const roleEl = container.querySelector('#rm-role');
  if (nameEl) nameEl.textContent = ctx.name || (ctx.role==='airline'?'Aerolínea':'Visitante');
  if (roleEl) roleEl.textContent = ctx.role ? (ctx.role==='airline'?'Aerolínea':'Cliente') : 'Sin sesión';

  const list = container.querySelector('#rm-list');
  const existingTargets = new Set(
    Array.from(list?.querySelectorAll('.rm-link[data-route]')||[])
      .map(a=>a.getAttribute('data-route'))
  );
  function addLink(label, route, icon='👉'){
    if(!list || existingTargets.has(route)) return;
    const li=document.createElement('li');
    li.innerHTML=`<a href="#" data-route="${route}" class="rm-link flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-white/10 transition">
      <span>${icon}</span><span class="flex-1 truncate">${escapeHtml(label)}</span></a>`;
    list.appendChild(li); existingTargets.add(route);
  }

  if(!ctx.role){
    addLink('Iniciar sesión','register/register.html','🔐');
  } else if (ctx.role==='user'){
    addLink('Buscar vuelos','flightf/flight.html','✈️');
    addLink('Panel Reservas','reservationPanel/reservationPanel.html','🧾');
    addLink('Paquetes','package/package.html','🎒');
  } else if (ctx.role==='airline'){
    addLink('Panel aerolínea','adminPanel/adminPanel.html','🛠️');
    addLink('Rutas de vuelo','checkflightroute/checkflightroute.html','🧭');
    addLink('Crear vuelo','createFlight/createFlight.html','✈️');
    addLink('Crear ruta de vuelo','createFlightRoute/createflightRoute.html','🧭');
    addLink('Crear paquete','createPackage/createPackage.html','🆕');
    addLink('Mis paquetes','checkPackage/checkPackage.html','📦');
    addLink('Panel Reservas','reservationPanel/reservationPanel.html','🧾');
    addLink('Paquetes','package/package.html','🎒');
  } else if (ctx.role==='admin'){
    // Eliminar Panel Reservas si existe en la plantilla base
    const pr = list.querySelector('[data-route="reservationPanel/reservationPanel.html"]')?.closest('li');
    if(pr) pr.remove();
    // Añadir sólo Aceptar rutas
    addLink('Aceptar rutas','acceptFlightRoute/acceptFlightRoute.html','🛡️');
  }

  function resolve(route){
    if (typeof window.getRelativePath === 'function') return window.getRelativePath(route);
    return '/public/src/views/' + route;
  }

  list?.addEventListener('click',(e)=>{
    const a = e.target.closest('.rm-link'); if(!a) return;
    e.preventDefault();
    const r=a.getAttribute('data-route'); if(r) location.href=resolve(r);
  });

  function highlight(){
    const current = location.pathname.replace(/\\/g,'/').replace(/\/index\.html$/,'/');
    container.querySelectorAll('.rm-link').forEach(a=>{
      a.classList.remove('active'); a.removeAttribute('aria-current');
      const route=a.getAttribute('data-route'); if(!route) return;
      const abs = resolve(route).replace(/\/index\.html$/,'/');
      if(current.endsWith(route) || current===abs){
        a.classList.add('active'); a.setAttribute('aria-current','page');
      }
    });
  }
  highlight();
  setTimeout(highlight,250);

  window.refreshRightMenu = function(){ highlight(); applyVisibility(); };
})();
