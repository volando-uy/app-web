document.addEventListener("DOMContentLoaded", () => {
  // Simulación: datos guardados al iniciar sesión
  const user = JSON.parse(localStorage.getItem("userData")) || {
    tipo: "cliente", // "aerolinea" o "cliente"
    nickname: "juan123",
    nombre: "Juan",
    apellido: "Pérez",
    email: "juan@example.com",
    fechaNacimiento: "1995-05-20",
    tipoDoc: "ci",
    documento: "12345678",
    nacionalidad: "Uruguayo",
    web: "https://mi-aerolinea.com",
    descripcion: "Aerolinea líder en el mercado"
  };

  // Rellenar formulario
  document.getElementById("nickname").value = user.nickname;
  document.getElementById("nombre").value = user.nombre;
  document.getElementById("email").value = user.email;

  if (user.tipo === "cliente") {
    document.getElementById("apellido").value = user.apellido;
    document.getElementById("fechaNacimiento").value = user.fechaNacimiento;
    document.getElementById("tipoDoc").value = user.tipoDoc;
    document.getElementById("documento").value = user.documento;
    document.getElementById("nacionalidad").value = user.nacionalidad;
  } else if (user.tipo === "aerolinea") {
    // Ocultar campos de cliente
    document.getElementById("apellidoField").classList.add("hidden");
    document.getElementById("fechaNacimientoField").classList.add("hidden");
    document.getElementById("docFields").classList.add("hidden");
    document.getElementById("nacionalidadField").classList.add("hidden");

    // Mostrar campos de aerolínea
    document.getElementById("webField").classList.remove("hidden");
    document.getElementById("descripcionField").classList.remove("hidden");

    document.getElementById("web").value = user.web;
    document.getElementById("descripcion").value = user.descripcion;
  }

  // Guardar cambios
  document.getElementById("profileForm").addEventListener("submit", (e) => {
    e.preventDefault();

    if (user.tipo === "cliente") {
      user.nombre = document.getElementById("nombre").value;
      user.apellido = document.getElementById("apellido").value;
      user.email = document.getElementById("email").value;
      user.fechaNacimiento = document.getElementById("fechaNacimiento").value;
      user.tipoDoc = document.getElementById("tipoDoc").value;
      user.documento = document.getElementById("documento").value;
      user.nacionalidad = document.getElementById("nacionalidad").value;
    } else {
      user.nombre = document.getElementById("nombre").value;
      user.email = document.getElementById("email").value;
      user.web = document.getElementById("web").value;
      user.descripcion = document.getElementById("descripcion").value;
    }

    // Guardar en localStorage (simulación de guardar en BD)
    localStorage.setItem("userData", JSON.stringify(user));

    alert("✅ Datos actualizados correctamente");
  });

  document.getElementById("volver").addEventListener("click", () => {
    window.location.href = "../header/header.html";
  });
});

