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

const categorias = [
  { nombre: "Económico" },
  { nombre: "Premium" },
  { nombre: "VIP" }
];

const tablaCategorias = document.getElementById("tablaCategorias");
categorias.forEach(cat => {
  const row = document.createElement("tr");
  row.innerHTML = `
    <td class="border px-4 py-2">${cat.nombre}</td>
  `;
  tablaCategorias.appendChild(row);
});

const form = document.getElementById("formRuta");
form.addEventListener("submit", (e) => {
  e.preventDefault();

  const data = {
    aerolinea: document.getElementById("nombreAerolínea").textContent,
    nombre: document.getElementById("nombre").value,
    descripcion: document.getElementById("descripcion").value,
    fecha: document.getElementById("fecha").value,
    equipaje: document.getElementById("equipaje").value,
    turista: document.getElementById("turista").value,
    ejecutivo: document.getElementById("ejecutivo").value,
    origen: document.getElementById("origen").value,
    destino: document.getElementById("destino").value,
    categorias: categorias.map(c => c.nombre)
  };
  console.log("Ruta creada:", data);
  alert("Ruta de vuelo creada con éxito ✅");
  form.reset();
});
