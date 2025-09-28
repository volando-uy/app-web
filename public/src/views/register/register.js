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
