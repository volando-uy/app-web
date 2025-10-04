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
    email: demo.email
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

/* ---------- Detección de rol/nombre ---------- */
function getRoleAndName() {
  // SOLO sessionStorage define una sesión activa
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
    // Solo usar localStorage para completar nombre (no para “revivir” sesión)
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
  const mobileNav = document.getElementById("nav-mobile");
  const rightActions = document.getElementById("header-actions");

  const userLinks = [
    { text: "Vuelos", target: "flightf/flight.html" },
    { text: "Paquetes", target: "package/package.html" },
  ];
  const airlineLinks = [
    { text: "Varios", target: "createCity&Category/createCity&Category.html" },
    { text: "Vuelos", target: "flightf/flight.html" },
    { text: "Listados", target: "adminPanel/listings.html" },
    { text: "Paquetes", target: "package/package.html" },
  ];

  const makeA = (item, extra = "") =>
    `<a href="#" class="nav-link ${extra}" data-target="${item.target}">${escapeHtml(item.text)}</a>`;

  // Desktop nav
  if (desktopNav) {
    const links = role === "airline" ? airlineLinks : userLinks;
    desktopNav.innerHTML = links.map((l) => makeA(l, "hover:text-yellow-300")).join(" ");
    desktopNav.classList.add("md:flex");
  }

  // Mobile nav
  if (mobileNav) {
    const links = role === "airline" ? airlineLinks : userLinks;
    mobileNav.innerHTML =
      links
        .map(
          (l) =>
            `<a href="#" class="nav-link block px-2 py-3 rounded" data-target="${l.target}">${escapeHtml(l.text)}</a>`
        )
        .join("") +
      `<div class="h-px bg-white/10 my-2"></div>` +
      (role
        ? `<button id="btnLogoutMobile" class="w-full text-left block px-2 py-3 rounded hover:bg-white/10">Cerrar sesión</button>`
        : `<a href="#" class="nav-link block px-2 py-3 rounded" data-target="register/register.html">Iniciar sesión</a>`);
  }

  // Acciones a la derecha (desktop)
  if (rightActions) {
    if (role === "airline") {
      rightActions.innerHTML = `
        <a href="#" class="nav-link px-3 py-2 text-white font-semibold"
           data-target="adminPanel/adminPanel.html">✈️ ${escapeHtml(name || "Aerolínea")}</a>
        <button id="btnLogout" class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">Cerrar sesión</button>
      `;
    } else if (role === "user") {
      rightActions.innerHTML = `
        <div class="text-sm text-white/90 mr-1">👤 ${escapeHtml(name || "Cliente")}</div>
        <button id="btnLogout" class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">Cerrar sesión</button>
      `;
    } else {
      // sin sesión
      rightActions.innerHTML = `
        <a href="#" class="nav-link px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20"
           data-target="register/register.html">Iniciar sesión</a>`;
    }
    rightActions.classList.add("md:flex");
  }

  // Listeners de logout (si hay sesión)
  attachLogoutHandlers();
}

function attachLogoutHandlers() {
  const btn1 = document.getElementById("btnLogout");
  const btn2 = document.getElementById("btnLogoutMobile");
  if (btn1) btn1.onclick = () => window.logout();
  if (btn2) btn2.onclick = () => {
    window.logout();
    // cerrar menú móvil si estaba abierto
    const menu = document.getElementById("mobileMenu");
    if (menu && !menu.classList.contains("hidden")) menu.classList.add("hidden");
  };
}

/* ---------- Mobile toggle + bootstrap ---------- */
function initHeader() {
  // Evita doble init
  if (window.__headerInit) return;
  window.__headerInit = true;

  // Espera a que el header exista (si lo inyectás por fetch)
  const ensure = () => {
    const btn = document.getElementById('btnMenu');
    const menu = document.getElementById('mobileMenu');
    if (!btn || !menu) {
      // reintenta un par de veces
      return setTimeout(ensure, 80);
    }

    // Toggle móvil
    btn.addEventListener('click', () => {
      menu.classList.toggle('hidden');
      const open = btn.getAttribute('aria-expanded') === 'true';
      btn.setAttribute('aria-expanded', String(!open));
    });

    // Cerrar al navegar
    document.addEventListener('click', (e) => {
      const link = e.target.closest('.nav-link');
      if (link && !menu.classList.contains('hidden')) {
        menu.classList.add('hidden');
        btn.setAttribute('aria-expanded', 'false');
      }
    });

    // Render + links
    try { renderHeaderByRole(); } catch {}
    setupHeaderLinks();
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', ensure);
  } else {
    ensure();
  }
}

/* ---------- Nuevo: refrescar header público ---------- */
function refreshHeader(){
  try { renderHeaderByRole(); } catch {}
  try { setupHeaderLinks(); } catch {}
  attachLogoutHandlers();
}
window.refreshHeader = refreshHeader;

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

/* Auto-init si el header ya está en el DOM */
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initHeader);
} else {
  initHeader();
}
