function getRelativePath(target) {
  // Mapeo explícito a rutas absolutas dentro de /public para evitar errores de rutas relativas
  if (!target) return '#';
  // índice de targets comunes: index, flightf, package, register, adminPanel, etc.
  if (target === 'index.html') return '/public/index.html';
  if (target.startsWith('flightf/')) return '/public/src/views/' + target;
  if (target.startsWith('package/')) return '/public/src/views/' + target;
  if (target.startsWith('register/')) return '/public/src/views/' + target;
  if (target.startsWith('adminPanel/')) return '/public/src/views/' + target;
  // fallback: si se pasa una ruta ya absoluta (empieza con /) la devolvemos
  if (target.startsWith('/')) return target;
  // último recurso: asumir que está dentro de src/views
  return '/public/src/views/' + target;
}

function setupHeaderLinks() {
  document.querySelectorAll('.nav-link').forEach(link => {
    const target = link.getAttribute('data-target');
    if (target) {
      link.setAttribute('href', getRelativePath(target));
      link.onclick = null;
    }
  });
  const logo = document.getElementById('logo-link');
  if (logo) logo.setAttribute('href', getRelativePath('index.html'));
}

// nuevo: detectar rol/nombre (igual que antes)
function getRoleAndName() {
  const role = sessionStorage.getItem('role') || (() => {
    try {
      const ud = JSON.parse(localStorage.getItem('userData') || 'null');
      return ud ? (ud.tipo === 'aerolinea' ? 'airline' : 'user') : null;
    } catch { return null; }
  })();
  let name = null;
  try {
    if (sessionStorage.getItem('user')) name = JSON.parse(sessionStorage.getItem('user')).name;
    if (!name) {
      const ud = JSON.parse(localStorage.getItem('userData') || 'null');
      if (ud) name = ud.nombre || ud.nickname || ud.name;
    }
    if (!name && sessionStorage.getItem('airline')) name = JSON.parse(sessionStorage.getItem('airline')).name;
  } catch (e) { /* ignore */ }
  return { role, name };
}

// nuevo: renderiza links del header según rol usando IDs claros
function renderHeaderByRole() {
  const { role, name } = getRoleAndName();
  const desktopNav = document.getElementById('nav-desktop');
  const mobileNav = document.getElementById('nav-mobile');
  const rightActions = document.getElementById('header-actions');

  if (!role) return;

  const userLinks = [
    { text: 'Vuelos', target: 'flightf/flight.html' },
    { text: 'Paquetes', target: 'package/package.html' }
  ];
  const airlineLinks = [
    { text: 'Varios', target: 'adminPanel/adminPanel.html' },
    { text: 'Vuelos', target: 'flightf/flight.html' },
    { text: 'Listados', target: 'adminPanel/listings.html' },
    { text: 'Paquetes', target: 'package/package.html' }
  ];

  const makeAnchor = (item, extra = '') => `<a href="#" class="nav-link ${extra}" data-target="${item.target}">${escapeHtml(item.text)}</a>`;

  if (desktopNav) {
    desktopNav.innerHTML = (role === 'airline' ? airlineLinks : userLinks).map(l => makeAnchor(l, 'hover:text-yellow-300')).join(' ');
  }
  if (mobileNav) {
    mobileNav.innerHTML = (role === 'airline' ? airlineLinks : userLinks)
      .map(l => `<a href="#" class="nav-link block px-2 py-3 rounded" data-target="${l.target}">${escapeHtml(l.text)}</a>`).join('') +
      `<div class="h-px bg-white/10 my-2"></div>`;
    if (role !== 'airline') mobileNav.insertAdjacentHTML('beforeend', `<a href="#" class="nav-link block px-2 py-3 rounded" data-target="register/register.html">Iniciar sesión</a>`);
  }

  if (rightActions) {
    if (role === 'airline') {
      rightActions.innerHTML = `<a href="#" class="nav-link px-3 py-2 text-white font-semibold" data-target="adminPanel/adminPanel.html">✈️ ${escapeHtml(name || 'Aerolínea')}</a>`;
    } else {
      rightActions.innerHTML = `<a href="#" class="nav-link px-3 py-2 rounded-lg bg-white/10" data-target="register/register.html">Iniciar sesión</a><div class="text-sm text-white/90 ml-3">👤 ${escapeHtml(name || '')}</div>`;
    }
  }
}

function initHeader() {
  const btn = document.getElementById('btnMenu');
  const menu = document.getElementById('mobileMenu');

  btn?.addEventListener('click', () => {
    menu.classList.toggle('hidden');
    const open = btn.getAttribute('aria-expanded') === 'true';
    btn.setAttribute('aria-expanded', String(!open));
  });

  try { renderHeaderByRole(); } catch(e) { /* ignore */ }
  setupHeaderLinks();
}

function escapeHtml(s){ return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }