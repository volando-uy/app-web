function showLogin() {
  document.getElementById("loginForm").classList.remove("hidden");
  document.getElementById("registerForm").classList.add("hidden");

  // Cambiar panel izquierdo
  document.getElementById("panelTitle").textContent = "Bienvenido, capo!";
  document.getElementById("panelText").textContent = "No tienes una cuenta?";
  document.getElementById("switchBtn").textContent = "Registro";
  document.getElementById("switchBtn").setAttribute("onclick", "showRegister()");
}

function showRegister() {
  document.getElementById("registerForm").classList.remove("hidden");
  document.getElementById("loginForm").classList.add("hidden");

  // Cambiar panel izquierdo
  document.getElementById("panelTitle").textContent = "Bienvenido, genio!";
  document.getElementById("panelText").textContent = "Ya tienes una cuenta?";
  document.getElementById("switchBtn").textContent = "Login";
  document.getElementById("switchBtn").setAttribute("onclick", "showLogin()");
}

function toggleUserType() {
  const userType = document.getElementById("userType").value;
  const clienteFields = document.getElementById("clienteFields");
  const aerolineaFields = document.getElementById("aerolineaFields");

  clienteFields.classList.add("hidden");
  aerolineaFields.classList.add("hidden");

  if (userType === "cliente") {
    clienteFields.classList.remove("hidden");
  } else if (userType === "aerolinea") {
    aerolineaFields.classList.remove("hidden");
  }
}

// Nueva lógica: login y register reales
document.addEventListener('DOMContentLoaded', () => {
  const btnLogin = document.getElementById('btn-login');
  const btnRegister = document.getElementById('btn-register');

  if (btnLogin) {
    btnLogin.addEventListener('click', () => {
      const username = document.getElementById('login-username')?.value.trim() || '';
      const roleSel = document.getElementById('login-role')?.value || 'cliente';
      if (!username) { alert('Ingresa un username'); return; }

      // Intentar login contra usuarios demo sembrados
      let demoData = null;
      if (roleSel === 'aerolinea') {
        const d = getDemoUser('aerolinea');
        if (d && (d.nickname === username || d.nombre === username || d.email === username)) {
          demoData = d;
        }
      } else {
        const d = getDemoUser('cliente');
        if (d && (d.nickname === username || d.nombre === username || d.email === username)) {
          demoData = d;
        }
      }

      if (demoData) {
        // Sesión con datos completos demo
        establishSession(roleSel === 'aerolinea' ? 'airline' : 'user', demoData);
        goProfileInfo();
        return;
      }

      // Si no es demo, fallback simple (manteniendo tu comportamiento, pero ahora consistente)
      if (roleSel === 'aerolinea') {
        const airlineData = {
          tipo: 'aerolinea',
          nickname: username,
            nombre: username,
          email: '',
          descripcion: '',
          web: ''
        };
        establishSession('airline', airlineData);
      } else {
        const userData = {
          tipo: 'cliente',
          nickname: username,
          nombre: username,
          apellido: '',
          email: '',
          fechaNacimiento: '',
          tipoDoc: 'ci',
          documento: '',
          nacionalidad: ''
        };
        establishSession('user', userData);
      }
      goProfileInfo();
    });
  }

  if (btnRegister) {
    btnRegister.addEventListener('click', () => {
      const userType = document.getElementById('userType')?.value;
      if (!userType) { alert('Selecciona tipo de registro'); return; }

      if (userType === 'cliente') {
        const nickname = document.getElementById('reg-nickname')?.value.trim() || '';
        const nombre = document.getElementById('reg-nombre')?.value.trim() || '';
        const apellido = document.getElementById('reg-apellido')?.value.trim() || '';
        const email = document.getElementById('reg-email')?.value.trim() || '';
        const dob = document.getElementById('reg-dob')?.value || '';
        const docType = document.getElementById('reg-doc-type')?.value || 'ci';
        const nacionalidad = document.getElementById('reg-nacionalidad')?.value.trim() || '';
        const docNumber = document.getElementById('reg-doc-number')?.value.trim() || '';

        if (!nickname || !nombre || !apellido) { alert('Completa nickname, nombre y apellido'); return; }

        const userData = {
          tipo: 'cliente',
          nickname, nombre, apellido, email,
          fechaNacimiento: dob,
          tipoDoc: docType,
          documento: docNumber,
          nacionalidad
        };
        establishSession('user', userData);
        goProfileInfo();
      } else if (userType === 'aerolinea') {
        const nickname = document.getElementById('reg-nickname-a')?.value.trim() || '';
        const name = document.getElementById('reg-name-a')?.value.trim() || '';
        const email = document.getElementById('reg-email-a')?.value.trim() || '';
        const web = document.getElementById('reg-web-a')?.value.trim() || '';
        const desc = document.getElementById('reg-desc-a')?.value.trim() || '';

        if (!nickname || !name) { alert('Completa nickname y nombre'); return; }

        const airlineData = {
          tipo: 'aerolinea',
          nickname,
          nombre: name,
          email,
          web,
          descripcion: desc
        };
        establishSession('airline', airlineData);
        goProfileInfo();
      }
    });
  }
});

// Helper: normaliza demo user guardado por header.js
function getDemoUser(kind) {
  try {
    const key = kind === 'aerolinea' ? 'demo:airline' : 'demo:user';
    const raw = localStorage.getItem(key);
    if (!raw) return null;
    const d = JSON.parse(raw);
    // Unificar claves esperadas por profileInformation (nombre, nickname, email, tipo)
    return {
      tipo: d.tipo || (kind === 'aerolinea' ? 'aerolinea' : 'cliente'),
      nickname: d.nickname || '',
      nombre: d.name || d.nombre || '',
      email: d.email || '',
      descripcion: d.descripcion || d.desc || '',
      web: d.web || '',
    };
  } catch { return null; }
}

// Setea toda la sesión consistente para rightMenu + profileInformation
function establishSession(role, userDataObj) {
  // userDataObj ya debe contener al menos { tipo, nombre, nickname, email }
  if (!userDataObj || !role) return;
  localStorage.setItem('userData', JSON.stringify(userDataObj));
  sessionStorage.setItem('role', role);
  // Para mantener compatibilidad con código existente:
  if (role === 'airline') {
    sessionStorage.setItem('airline', JSON.stringify({ name: userDataObj.nombre }));
  } else {
    sessionStorage.setItem('user', JSON.stringify({ name: userDataObj.nombre }));
  }
  // auth unificado (usado por rightMenu / profileInformation primero)
  sessionStorage.setItem('auth', JSON.stringify({
    role,
    name: userDataObj.nombre,
    nickname: userDataObj.nickname
  }));
}

// Redirige directamente a profileInformation (vista de perfil)
function goProfileInfo() {
  window.location.href = '/public/src/views/profileInformation/profileInformation.html';
}
