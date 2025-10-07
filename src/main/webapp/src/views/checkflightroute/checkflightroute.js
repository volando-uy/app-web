// Inyectar footer dinámicamente
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

// Mock de rutas (poné más de 10 para probar paginación)
const rutas = Array.from({ length: 22 }).map((_, i) => ({
  id: i+1,
  title: `Ruta ${i+1}`,
  desc: `Descripción de la ruta ${i+1}`,
  origen: "Ciudad A",
  destino: "Ciudad B",
  duracion: "1h 30m",
  turista: 50+i,
  ejecutiva: 10,
  fecha: "01/10/2025"
}));

const filasPorPagina = 10;
let paginaActual = 1;

function renderTabla() {
  const tbody = document.getElementById("tabla-rutas");
  tbody.innerHTML = "";

  const inicio = (paginaActual - 1) * filasPorPagina;
  const fin = inicio + filasPorPagina;
  const pagina = rutas.slice(inicio, fin);

  pagina.forEach(ruta => {
    const tr = document.createElement("tr");
    tr.className = "hover:bg-gray-50";
    tr.innerHTML = `
      <td class="px-4 py-2 border">${ruta.title}</td>
      <td class="px-4 py-2 border">${ruta.desc}</td>
      <td class="px-4 py-2 border">${ruta.origen}</td>
      <td class="px-4 py-2 border">${ruta.destino}</td>
      <td class="px-4 py-2 border">${ruta.duracion}</td>
      <td class="px-4 py-2 border">${ruta.turista}</td>
      <td class="px-4 py-2 border">${ruta.ejecutiva}</td>
      <td class="px-4 py-2 border">${ruta.fecha}</td>
    `;
    tbody.appendChild(tr);
  });

  renderPaginacion();
}

function renderPaginacion() {
  const totalPaginas = Math.ceil(rutas.length / filasPorPagina);
  const pagDiv = document.getElementById("pagination");
  pagDiv.innerHTML = "";

  for (let i = 1; i <= totalPaginas; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    btn.className = `px-3 py-1 rounded ${
      i === paginaActual ? "bg-blue-600 text-white" : "bg-gray-200 text-gray-700 hover:bg-gray-300"
    }`;
    btn.onclick = () => {
      paginaActual = i;
      renderTabla();
    };
    pagDiv.appendChild(btn);
  }
}

renderTabla();