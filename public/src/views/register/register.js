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
      const username = document.getElementById('login-username')?.value || '';
      const role = document.getElementById('login-role')?.value || 'cliente';
      // Validación mínima
      if (!username) { alert('Ingresa un username'); return; }

      if (role === 'aerolinea') {
        // Guardar como aerolinea en sessionStorage
        const airline = { name: username };
        sessionStorage.setItem('role', 'airline');
        sessionStorage.setItem('airline', JSON.stringify(airline));
      } else {
        // Guardar como usuario
        const user = { name: username };
        sessionStorage.setItem('role', 'user');
        sessionStorage.setItem('user', JSON.stringify(user));
      }
      // Redirigir al inicio para que header lo lea
      location.href = '/public/index.html';
    });
  }

  if (btnRegister) {
    btnRegister.addEventListener('click', () => {
      const userType = document.getElementById('userType')?.value;
      if (!userType) { alert('Selecciona tipo de registro'); return; }

      if (userType === 'cliente') {
        const nickname = document.getElementById('reg-nickname')?.value || '';
        const nombre = document.getElementById('reg-nombre')?.value || '';
        const apellido = document.getElementById('reg-apellido')?.value || '';
        const email = document.getElementById('reg-email')?.value || '';
        const dob = document.getElementById('reg-dob')?.value || '';
        const docType = document.getElementById('reg-doc-type')?.value || 'ci';
        const nacionalidad = document.getElementById('reg-nacionalidad')?.value || '';
        const docNumber = document.getElementById('reg-doc-number')?.value || '';

        if (!nickname || !nombre || !apellido) { alert('Completa nickname, nombre y apellido'); return; }

        const userData = {
          tipo: 'cliente',
          nickname, nombre, apellido, email, fechaNacimiento: dob,
          tipoDoc: docType, documento: docNumber, nacionalidad
        };
        // Guardar persistente y como sesión activa
        localStorage.setItem('userData', JSON.stringify(userData));
        sessionStorage.setItem('role', 'user');
        sessionStorage.setItem('user', JSON.stringify({ name: nombre }));

        location.href = '/public/index.html';
      } else if (userType === 'aerolinea') {
        const nickname = document.getElementById('reg-nickname-a')?.value || '';
        const name = document.getElementById('reg-name-a')?.value || '';
        const email = document.getElementById('reg-email-a')?.value || '';
        const web = document.getElementById('reg-web-a')?.value || '';
        const desc = document.getElementById('reg-desc-a')?.value || '';

        if (!nickname || !name) { alert('Completa nickname y nombre'); return; }

        const airlineData = {
          tipo: 'aerolinea',
          nickname, nombre: name, email, web, descripcion: desc
        };
        localStorage.setItem('userData', JSON.stringify(airlineData));
        sessionStorage.setItem('role', 'airline');
        sessionStorage.setItem('airline', JSON.stringify({ name }));

        location.href = '/public/index.html';
      }
    }); 
  }
});
