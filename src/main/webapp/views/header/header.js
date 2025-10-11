/* ---------- DEMO AUTH: 1 cliente y 1 aerolínea ---------- */
const DEMO_USERS = {
  user: {
    role: "user",
    tipo: "cliente",
    nickname: "juan",
    name: "Juan Cliente",
    email: "juan@example.com",
    password: "1234",
  },
  airline: {
    role: "airline",
    tipo: "aerolinea",
    nickname: "cielosur",
    name: "CieloSur",
    email: "ops@cielosur.com",
    password: "1234",
  },
};

// Guarda los demos una sola vez para tenerlos a mano en localStorage (opcional)
(function seedDemoOnce() {
  if (!localStorage.getItem("demoSeeded")) {
    localStorage.setItem("demo:user", JSON.stringify(DEMO_USERS.user));
    localStorage.setItem("demo:airline", JSON.stringify(DEMO_USERS.airline));
    localStorage.setItem("demoSeeded", "1");
  }
})();

/* Helpers rápidos para loguearte en desarrollo desde la consola:
   window.loginAsDemo("user")     // cliente
   window.loginAsDemo("airline")  // aerolínea
   window.logout()                // cerrar sesión
*/
window.loginAsDemo = function (kind = "user") {
  const demo = DEMO_USERS[kind];
  if (!demo) return;
  const auth = { role: demo.role, name: demo.name, nickname: demo.nickname };
  sessionStorage.setItem("auth", JSON.stringify(auth));
  sessionStorage.setItem("role", demo.role);
  sessionStorage.setItem(demo.role === "airline" ? "airline" : "user", JSON.stringify({ name: demo.name }));
  localStorage.setItem("userData", JSON.stringify({
    tipo: demo.tipo,
    nombre: demo.name,
    nickname: demo.nickname,
    email: demo.email,
    avatar: '/assets/images/default-avatar.png'
  }));
  refreshHeader();
  if (typeof window.refreshRightMenu === 'function') window.refreshRightMenu();
};

window.logout = function () {
  sessionStorage.removeItem("auth");
  sessionStorage.removeItem("role");
  sessionStorage.removeItem("user");
  sessionStorage.removeItem("airline");
  localStorage.removeItem("userData");
  refreshHeader();
  if (typeof window.refreshRightMenu === 'function') window.refreshRightMenu();
  // Redirigir siempre al index después de logout
  setTimeout(()=>{ window.location.href = '/public/index.html'; }, 50);
};

/* ---------- Ruteo relativo que ya usabas ---------- */
function getRelativePath(target) {
  if (!target) return "#";
  if (target === "index.html") return "/public/index.html";
  if (target.startsWith("flightf/")) return "/public/src/views/" + target;
  if (target.startsWith("package/")) return "/public/src/views/" + target;
  if (target.startsWith("register/")) return "/public/src/views/" + target;
  if (target.startsWith("createCity&Category/")) return "/public/src/views/" + target;
  if (target.startsWith("adminPanel/")) return "/public/src/views/" + target;
  if (target.startsWith("createFlightRoute/")) return "/public/src/views/" + target;
  if (target.startsWith("createFlight/")) return "/public/src/views/" + target;
  if (target.startsWith("/")) return target;
  return "/public/src/views/" + target;
}

function setupHeaderLinks() {
  document.querySelectorAll(".nav-link").forEach((link) => {
    const target = link.getAttribute("data-target");
    if (target) {
      link.setAttribute("href", getRelativePath(target));
      link.onclick = null;
    }
  });
  const logo = document.getElementById("logo-link");
  if (logo) logo.setAttribute("href", getRelativePath("index.html"));
}

// Asegura que el link de login siempre funcione
function forceLoginLink() {
  const links = document.querySelectorAll('[data-target="register/register.jsp"]');
  links.forEach(a => {
    a.addEventListener('click', (e) => {
      const target = a.getAttribute('data-target');
      if (!target) return;
      e.preventDefault();
      window.location.href = getRelativePath(target);
    }, { once: true });
  });
}

/* ---------- Detección de rol/nombre ---------- */
function getRoleAndName() {
  let role = null;
  let name = null;
  try {
    const auth = JSON.parse(sessionStorage.getItem('auth') || 'null');
    if (auth?.role) {
      role = auth.role;
      name = auth.name || auth.nickname || null;
    } else {
      role = sessionStorage.getItem('role');
    }
    if (role && !name) {
      if (role === 'user' && sessionStorage.getItem('user')) {
        name = JSON.parse(sessionStorage.getItem('user')).name;
      } else if (role === 'airline' && sessionStorage.getItem('airline')) {
        name = JSON.parse(sessionStorage.getItem('airline')).name;
      }
    }
    if (role && !name) {
      const ud = JSON.parse(localStorage.getItem('userData') || 'null');
      if (ud) name = ud.nombre || ud.nickname || ud.name;
    }
  } catch {}
  return { role, name };
}

/* ---------- Render según rol + botones de sesión ---------- */
function renderHeaderByRole() {
  const { role, name } = getRoleAndName();
  const desktopNav = document.getElementById("nav-desktop");
  const mobileNav  = document.getElementById("nav-mobile");
  const rightActions = document.getElementById("header-actions");

  // Base (desktop)
  const baseUserLinks = [
    { text: "Vuelos", target: "flightf/flight.jsp" },
    { text: "Paquetes", target: "package/package.jsp" }
  ];
  const baseAirlineLinks = [
    { text: "Varios", target: "createCity&Category/createCityAndCategory.jsp" },
    { text: "Crear vuelo", target: "createFlight/createFlight.jsp" },
    { text: "Crear ruta de vuelo", target: "createFlightRoute/createflightRoute.jsp" },
    { text: "Rutas de vuelo", target: "checkflightroute/checkflightroute.html" },
    { text: "Paquetes", target: "checkPackage/checkPackage.jsp" }
  ];
  const baseAdminLinks = [
    { text: "Aceptar rutas", target: "acceptFlightRoute/acceptFlightRoute.jsp" },
    { text: "Vuelos", target: "flightf/flight.jsp" },
    { text: "Paquetes", target: "package/package.jsp" }
  ];

  // Extras SOLO mobile (sin 'Mi perfil')
  const extraUserLinks = [
    { text: "Panel Reservas", target: "reservationPanel/reservationPanel.jsp" }
  ];
  const extraAirlineLinks = [
    { text: "Panel aerolínea", target: "adminPanel/adminPanel.html" },
    { text: "Panel Reservas", target: "reservationPanel/reservationPanel.jsp" }
  ];
  const extraAdminLinks = [{ text:"Panel Reservas", target:"reservationPanel/reservationPanel.jsp" }];

  // Estilo unificado tipo “pill”
  const makeA = (item, extra="") =>
    `<a href="#" class="nav-link px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 transition text-sm ${extra}"
       data-target="${item.target}">${escapeHtml(item.text)}</a>`;

  // Desktop
  if (desktopNav) {
    const links = role === "airline"
      ? baseAirlineLinks
      : role === "admin"
        ? baseAdminLinks
        : baseUserLinks;
    desktopNav.innerHTML = links.map(l => makeA(l)).join("");
    desktopNav.classList.add("md:flex","gap-2");
  }

  // Mobile (aplica mismo estilo a cada item)
  if (mobileNav) {
    const base = role === "airline"
      ? baseAirlineLinks
      : role === "admin"
        ? baseAdminLinks
        : baseUserLinks;
    const extras = role === "airline"
      ? extraAirlineLinks
      : role === "admin"
        ? extraAdminLinks
        : (role === "user" ? extraUserLinks : []);
    const unique = [];
    const seen = new Set();
    [...base, ...extras].forEach(l => { if(!seen.has(l.target)){ seen.add(l.target); unique.push(l);} });
    mobileNav.innerHTML =
      unique.map(l => `<a href="#" class="nav-link block px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-sm"
         data-target="${l.target}">${escapeHtml(l.text)}</a>`).join("") +
      `<div class="h-px bg-white/10 my-2"></div>` +
      (role
        ? `<a href="#" class="nav-link block px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-sm"
             data-target="profileInformation/profileInformation.html">Mi perfil</a>
           <button id="btnLogoutMobile"
             class="w-full text-left block px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-sm">Cerrar sesión</button>`
        : `<a href="#" class="nav-link block px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-sm"
             data-target="register/register.html">Iniciar sesión</a>`);
  }

  // Acciones derecha: quitar botón extra de “+ Crear vuelo” (solo dropdown usuario / login)
  if (rightActions) {
    if (role === "airline" || role === "user" || role === "admin") {
      const avatar = getUserAvatar();
      const icon = role === 'airline' ? '✈️' : (role === 'admin' ? '🛡️' : '👤');
      rightActions.innerHTML = `
        <div id="user-menu" class="relative">
          <button id="user-menu-trigger"
            class="flex items-center gap-2 pl-2 pr-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 font-semibold
                   focus:outline-none focus:ring-2 focus:ring-white/30 text-sm"
            aria-haspopup="true" aria-expanded="false" aria-controls="user-menu-dropdown" type="button">
            <img src="${avatar}" alt="avatar" class="w-7 h-7 rounded-full object-cover ring-1 ring-white/30">
            <span class="flex items-center gap-1">${icon} ${escapeHtml(name || (role==='airline' ? 'Aerolínea' : 'Usuario'))}</span>
            <svg class="w-4 h-4 opacity-80" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 9l6 6 6-6"/>
            </svg>
          </button>
          <div id="user-menu-dropdown"
               class="absolute right-0 mt-2 w-52 bg-white text-gray-700 rounded-md shadow-lg border border-black/5 py-1 text-sm hidden z-50"
               role="menu" aria-hidden="true">
            <a href="#" data-target="profileInformation/profileInformation.html"
               class="nav-link block px-4 py-2 hover:bg-gray-50" role="menuitem">Mi perfil</a>
            <button id="btnLogout" class="w-full text-left px-4 py-2 hover:bg-gray-50" role="menuitem">Cerrar sesión</button>
          </div>
        </div>`;
    } else {
      rightActions.innerHTML = `
        <a href="#" class="nav-link px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-sm"
           data-target="register/register.html">Iniciar sesión</a>`;
    }
    rightActions.classList.add("md:flex","items-center","gap-3");
  }

  try { attachLogoutHandlers(); } catch {}
  enhanceUserMenu();
}

// Helper para avatar
function getUserAvatar() {
  try {
    const ud = JSON.parse(localStorage.getItem('userData') || 'null');
    return (ud && ud.avatar) ? ud.avatar : '/assets/images/default-avatar.png';
  } catch { return '/assets/images/default-avatar.png'; }
}

/* ---------- Handlers de logout ---------- */
function attachLogoutHandlers() {
  if (window.__logoutDesktopHandler) {
    const old = document.getElementById('btnLogout');
    old && old.removeEventListener('click', window.__logoutDesktopHandler);
  }
  if (window.__logoutMobileHandler) {
    const oldm = document.getElementById('btnLogoutMobile');
    oldm && oldm.removeEventListener('click', window.__logoutMobileHandler);
  }

  const btnDesktop = document.getElementById('btnLogout');
  const btnMobile  = document.getElementById('btnLogoutMobile');

  window.__logoutDesktopHandler = (e) => {
    e.preventDefault();
    window.logout();
  };
  window.__logoutMobileHandler = (e) => {
    e.preventDefault();
    window.logout();
  };

  if (btnDesktop) btnDesktop.addEventListener('click', window.__logoutDesktopHandler);
  if (btnMobile)  btnMobile.addEventListener('click',  window.__logoutMobileHandler);
}

/* ---------- Toggle de menú usuario ---------- */
function enhanceUserMenu() {
  const tryBind = () => {
    const trigger  = document.getElementById('user-menu-trigger');
    const dropdown = document.getElementById('user-menu-dropdown');
    const wrapper  = document.getElementById('user-menu');
    if (!trigger || !dropdown || !wrapper) return false;

    // Limpieza previa
    if (window.__userMenuDocHandler)  document.removeEventListener('click', window.__userMenuDocHandler);
    if (window.__userMenuEscHandler)  document.removeEventListener('keydown', window.__userMenuEscHandler);
    if (window.__userMenuTriggerHandler) trigger.removeEventListener('click', window.__userMenuTriggerHandler);

    let open = false;
    const show = () => {
      if (open) return;
      dropdown.classList.remove('hidden');
      dropdown.setAttribute('aria-hidden', 'false');
      trigger.setAttribute('aria-expanded', 'true');
      open = true;
    };
    const hide = () => {
      if (!open) return;
      dropdown.classList.add('hidden');
      dropdown.setAttribute('aria-hidden', 'true');
      trigger.setAttribute('aria-expanded', 'false');
      open = false;
    };

    window.__userMenuTriggerHandler = (e) => {
      e.preventDefault();
      e.stopPropagation();
      open ? hide() : show();
    };
    trigger.addEventListener('click', window.__userMenuTriggerHandler);

    window.__userMenuDocHandler = (e) => {
      if (!wrapper.contains(e.target)) hide();
    };
    document.addEventListener('click', window.__userMenuDocHandler);

    window.__userMenuEscHandler = (e) => { if (e.key === 'Escape') hide(); };
    document.addEventListener('keydown', window.__userMenuEscHandler);

    trigger.addEventListener('keydown', (e) => {
      if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (!open) show();
        const first = dropdown.querySelector('a,button');
        if (first) first.focus();
      } else if (e.key === 'Escape') {
        hide();
      }
    });

    return true;
  };
  setTimeout(() => { if (!tryBind()) setTimeout(tryBind, 50); }, 0);
}

/* ---------- Mobile toggle + bootstrap ---------- */
function initHeader() {
  // NO marcar todavía __headerInit hasta enganchar elementos
  if (window.__headerInit && document.getElementById('btnMenu') && document.getElementById('mobileMenu')) return;

  let attempts = 0;
  const maxAttempts = 25; // ~2s (25 * 80ms)
  function ensure() {
    const btn = document.getElementById('btnMenu');
    const menu = document.getElementById('mobileMenu');

    if (!btn || !menu) {
      attempts++;
      if (attempts < maxAttempts) return setTimeout(ensure, 80);
      console.warn('[header] No se encontraron elementos de menú móvil tras varios intentos.');
      return;
    }

    // Limpieza previa de posibles handlers antiguos
    if (window.__headerBtnHandler) {
      btn.removeEventListener('click', window.__headerBtnHandler);
    }

    function toggleMobileMenu(forceClose = false) {
      const isHidden = menu.classList.contains('hidden');
      if (forceClose) {
        if (!isHidden) menu.classList.add('hidden');
        btn.setAttribute('aria-expanded', 'false');
        return;
      }
      if (isHidden) {
        menu.classList.remove('hidden');
        btn.setAttribute('aria-expanded', 'true');
      } else {
        menu.classList.add('hidden');
        btn.setAttribute('aria-expanded', 'false');
      }
    }

    window.__headerBtnHandler = () => toggleMobileMenu();
    btn.addEventListener('click', window.__headerBtnHandler);

    // Atributos de accesibilidad
    btn.setAttribute('aria-controls', 'mobileMenu');
    btn.setAttribute('aria-expanded', 'false');

    // Cerrar al hacer click en un link
    if (window.__headerDocClickHandler) {
      document.removeEventListener('click', window.__headerDocClickHandler);
    }
    window.__headerDocClickHandler = (e) => {
      const link = e.target.closest('.nav-link');
      if (link && !menu.classList.contains('hidden')) {
        toggleMobileMenu(true);
      }
    };
    document.addEventListener('click', window.__headerDocClickHandler);

    // Cerrar con Escape
    if (window.__headerEscHandler) {
      document.removeEventListener('keydown', window.__headerEscHandler);
    }
    window.__headerEscHandler = (e) => {
      if (e.key === 'Escape') toggleMobileMenu(true);
    };
    document.addEventListener('keydown', window.__headerEscHandler);

    // Render y enlaces
    try { renderHeaderByRole(); } catch {}
    setupHeaderLinks();
    forceLoginLink();

    window.__headerInit = true;
  }

  ensure();
}

// Re-vincular manual si el header se reemplaza dinámicamente post-carga
window.forceRebindHeaderMobile = function() {
  window.__headerInit = false;
  initHeader();
};

/* ---------- Refrescar header ---------- */
function refreshHeader(){
  try { renderHeaderByRole(); } catch {}
  try { setupHeaderLinks(); } catch {}
  try { attachLogoutHandlers(); } catch {}
  enhanceUserMenu();
  forceLoginLink();
  // Reasegurar toggles si el botón fue reinyectado
  if (!window.__headerInit) initHeader();
}
window.refreshHeader = refreshHeader;

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

/* ---------- Auto-init ---------- */
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initHeader);
} else {
  initHeader();
}
window.refreshHeader = refreshHeader;

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

/* ---------- Auto-init ---------- */
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initHeader);
} else {
  initHeader();
}
