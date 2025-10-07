// === HEADER / FOOTER ===
fetch("../header/header.html")
  .then(r => r.text())
  .then(html => {
    const el = document.getElementById("header");
    if (el) el.innerHTML = html;
    const s = document.createElement("script");
    s.src = "../header/header.js";
    s.onload = () => { if (typeof initHeader === "function") initHeader(); };
    document.body.appendChild(s);
  });

fetch("../footer/footer.html")
  .then(r => r.text())
  .then(html => {
    const el = document.getElementById("footer");
    if (el) el.innerHTML = html;
  });

// === LECTURA DE PERFIL ===
function readProfile() {
  let role = null;
  let name = null;
  let email = "";
  let raw = null;
  try {
    const auth = JSON.parse(sessionStorage.getItem('auth') || 'null');
    if (auth?.role) {
      role = auth.role;
      name = auth.name || auth.nickname || null;
      email = auth.email || '';
    } else {
      role = sessionStorage.getItem('role');
    }
    if (role) {
      if (role === 'user' && sessionStorage.getItem('user')) {
        const u = JSON.parse(sessionStorage.getItem('user'));
        name = name || u.name;
      }
      if (role === 'airline' && sessionStorage.getItem('airline')) {
        const a = JSON.parse(sessionStorage.getItem('airline'));
        name = name || a.name;
      }
      const ud = JSON.parse(localStorage.getItem('userData') || 'null');
      if (ud) {
        raw = ud;
        name = name || ud.nombre || ud.nickname || ud.name;
        email = ud.email || email;
      }
    }
  } catch {}
  return { role, name, email, raw };
}

// === RENDERIZADO ===
function renderProfile() {
  const root = document.getElementById("profile-info");
  if (!root) return;
  const profile = readProfile();
  const avatar = profile.raw?.avatar || "/assets/images/default-avatar.png";
  const roleLabel = profile.role === "airline" ? "Aerolínea" : profile.role === "user" ? "Cliente" : "Visitante";

  // Estado sin sesión
  if (!profile.role) {
    root.innerHTML = `
      <div class="text-center max-w-md mx-auto">
        <img src="${avatar}" class="w-24 h-24 mx-auto rounded-full object-cover ring-2 ring-brand/20 mb-4" alt="">
        <h2 class="text-xl font-semibold mb-2 text-brand">Bienvenido</h2>
        <p class="text-sm text-gray-600 mb-4">Inicia sesión para ver tu información de perfil.</p>
        <a href="../register/register.html" class="px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110 inline-block">Iniciar sesión</a>
      </div>`;
    return;
  }

  root.innerHTML = `
    <section class="w-full">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 items-start">
        <div class="flex flex-col items-center md:items-start gap-4">
          <img src="${avatar}" alt="avatar"
               class="w-28 h-28 rounded-full object-cover ring-2 ring-brand/30 shadow-md" />
          <div>
            <h2 class="text-xl font-semibold">${escapeHtml(profile.name || "Sin nombre")}</h2>
            <p class="text-sm text-gray-500 break-all">${escapeHtml(profile.email || "—")}</p>
          </div>
          <span class="inline-flex items-center gap-1 text-xs px-2.5 py-1.5 rounded-full bg-brand/10 text-brand ring-1 ring-brand/20">
            ${escapeHtml(roleLabel)}
          </span>
          <div class="flex gap-2 mt-4 flex-wrap">
            <button id="btn-edit-profile"
                    class="px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110">
              Editar perfil
            </button>
            <button id="btn-back-to-home"
                    class="px-4 py-2 rounded-lg border hover:bg-gray-50">
              Volver
            </button>
          </div>
        </div>
        <div class="md:col-span-2">
          <h3 class="text-lg font-semibold mb-3">Detalles</h3>
          <dl class="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-5 text-sm">
            <div>
              <dt class="text-xs uppercase tracking-wide text-gray-500">Nombre</dt>
              <dd class="font-medium text-gray-800 mt-0.5">${escapeHtml(profile.name || "—")}</dd>
            </div>
            <div>
              <dt class="text-xs uppercase tracking-wide text-gray-500">Email</dt>
              <dd class="font-medium text-gray-800 mt-0.5 break-all">${escapeHtml(profile.email || "—")}</dd>
            </div>
            <div>
              <dt class="text-xs uppercase tracking-wide text-gray-500">Rol</dt>
              <dd class="font-medium text-gray-800 mt-0.5">${escapeHtml(roleLabel)}</dd>
            </div>
            <div>
              <dt class="text-xs uppercase tracking-wide text-gray-500">Datos locales</dt>
              <dd class="text-gray-700 mt-0.5">${profile.raw ? "Sí" : "No"}</dd>
            </div>
          </dl>
        </div>
      </div>
    </section>
  `;

  document.getElementById("btn-edit-profile")?.addEventListener("click", () => {
    location.href = "../profile/profile.html";
  });
  document.getElementById("btn-back-to-home")?.addEventListener("click", () => {
    location.href = "/public/index.html";
  });
}

// === UTIL ===
function escapeHtml(s) {
  return String(s || "").replace(
    /[&<>"']/g,
    (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c])
  );
}

window.refreshProfileInfo = renderProfile;

document.addEventListener("DOMContentLoaded", renderProfile);
