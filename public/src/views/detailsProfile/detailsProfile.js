function importFooter() {
  fetch("../footer/footer.html")
    .then(res => res.text())
    .then(data => {
      document.getElementById("footer").innerHTML = data;
    });
}

if (document.getElementById("footer")) {
  importFooter();
}
fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    document.getElementById("header").innerHTML = data;

    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => { initHeader(); };
    document.body.appendChild(script);
  });
const usuarios = [
  {
    nombre: "Aerolínea SkyFly",
    tipo: "Aerolínea",
    imagen: "https://cdn-icons-png.flaticon.com/512/190/190601.png",
    rutas: {
      confirmadas: ["Montevideo - Madrid", "Buenos Aires - Roma"],
      ingresadas: ["Lima - Bogotá"],
      rechazadas: ["Quito - Santiago"]
    }
  },
  {
    nombre: "Juan Pérez",
    tipo: "Cliente",
    imagen: "https://cdn-icons-png.flaticon.com/512/194/194938.png",
    reservas: ["Vuelo 123 - Montevideo a Miami"],
    paquetes: ["Paquete Verano 2025"]
  },
  {
    nombre: "Visitante",
    tipo: "Visitante",
    imagen: "https://cdn-icons-png.flaticon.com/512/1077/1077012.png"
  }
];

// === REFERENCIAS DOM ===
const listaUsuarios = document.getElementById("listaUsuarios");
const perfilUsuario = document.getElementById("perfilUsuario");
const tablaUsuarios = document.getElementById("tablaUsuarios");
const btnVolver = document.getElementById("btnVolver");

const nombreUsuario = document.getElementById("nombreUsuario");
const tipoUsuario = document.getElementById("tipoUsuario");
const imagenUsuario = document.getElementById("imagenUsuario");
const infoExtra = document.getElementById("infoExtra");

function cargarUsuarios() {
  tablaUsuarios.innerHTML = usuarios
    .map(
      (u) => `
      <tr class="hover:bg-gray-50 transition">
        <td class="p-3">${u.nombre}</td>
        <td class="p-3">${u.tipo}</td>
        <td class="p-3 text-center">
          <button 
            class="btnVer bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm"
            data-nombre="${u.nombre}"
          >
            Ver perfil
          </button>
        </td>
      </tr>
    `
    )
    .join("");

  document.querySelectorAll(".btnVer").forEach((btn) =>
    btn.addEventListener("click", () => {
      const nombre = btn.getAttribute("data-nombre");
      mostrarPerfil(nombre);
    })
  );
}

function mostrarPerfil(nombre) {
  const user = usuarios.find((u) => u.nombre === nombre);
  if (!user) return;

  nombreUsuario.textContent = user.nombre;
  tipoUsuario.textContent = `Tipo: ${user.tipo}`;
  imagenUsuario.src = user.imagen;

  let contenido = "";

  if (user.tipo === "Aerolínea") {
    contenido += crearBloque("✈️ Rutas Confirmadas", user.rutas.confirmadas);
    contenido += crearBloque("🟡 Rutas Ingresadas", user.rutas.ingresadas);
    contenido += crearBloque("🔴 Rutas Rechazadas", user.rutas.rechazadas);
  } else if (user.tipo === "Cliente") {
    contenido += crearBloque("🧳 Reservas de Vuelo", user.reservas);
    contenido += crearBloque("🎁 Paquetes Comprados", user.paquetes);
  } else {
    contenido += `<p class="text-gray-500 italic">Este usuario no posee información adicional disponible.</p>`;
  }

  infoExtra.innerHTML = contenido;

  listaUsuarios.classList.add("hidden");
  perfilUsuario.classList.remove("hidden");
}

function crearBloque(titulo, items) {
  if (!items || items.length === 0) return "";
  return `
    <div class="border-t pt-4">
      <h3 class="font-semibold text-lg mb-2">${titulo}</h3>
      <ul class="list-disc ml-6 space-y-1 text-gray-700">
        ${items.map((i) => `<li>${i}</li>`).join("")}
      </ul>
    </div>
  `;
}

btnVolver.addEventListener("click", () => {
  perfilUsuario.classList.add("hidden");
  listaUsuarios.classList.remove("hidden");
});

document.addEventListener("DOMContentLoaded", cargarUsuarios);
document.getElementById("btnVolverAtras").addEventListener("click", () => {
  window.location.href = "/public/src/views/profileInformation/profileInformation.html";
});