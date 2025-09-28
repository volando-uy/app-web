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

// Datos de rutas (mock)
const rutas = [
  {
    id: "montevideo",
    title: "Montevideo - San José",
    desc: "Vuelo cómodo y rápido hacia San José.",
    origen: "Montevideo",
    destino: "San José",
    duracion: "50 min",
    turista: 30,
    ejecutiva: 20,
    fecha: "14/09/2025",
    img: "https://img.viajeauruguay.com/san-jose-atractivos.jpg"
  },
  {
    id: "madrid",
    title: "Madrid - París",
    desc: "Vuelo directo y sin escalas hacia París.",
    origen: "Madrid",
    destino: "París",
    duracion: "2h 15m",
    turista: 120,
    ejecutiva: 40,
    fecha: "20/09/2025",
    img: "https://plus.unsplash.com/premium_photo-1661919210043-fd847a58522d?fm=jpg&q=60&w=3000"
  }
];

// Render dinámico de cards
const container = document.getElementById("routes-container");

rutas.forEach(ruta => {
  const card = document.createElement("div");
  card.className = "bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-xl transition ";
  card.innerHTML = `
    <img src="${ruta.img}" alt="${ruta.origen}" class="w-full h-40 object-cover">
    <div class="p-4">
      <h2 class="text-xl font-bold text-gray-800">${ruta.title}</h2>
      <p class="text-gray-600 text-sm mt-1">${ruta.desc}</p>
      <p class="text-gray-500 text-xs mt-2">Creado: ${ruta.fecha}</p>
      <div class="flex justify-between items-center mt-4">
        <span class="text-sm text-gray-700">Duración: ${ruta.duracion}</span>
        <button class="bg-blue-600 text-white px-3 py-1 rounded-lg hover:bg-blue-700"
          onclick="openModal('${ruta.id}')">Reservar</button>
      </div>
    </div>
  `;
  container.appendChild(card);
});

// Abrir modal con datos
function openModal(id) {
  const ruta = rutas.find(r => r.id === id);
  if (!ruta) return;

  document.getElementById("modal-title").textContent = ruta.title;
  document.getElementById("modal-desc").textContent = ruta.desc;
  document.getElementById("modal-origen").textContent = ruta.origen;
  document.getElementById("modal-destino").textContent = ruta.destino;
  document.getElementById("modal-duracion").textContent = ruta.duracion;
  document.getElementById("modal-turista").textContent = ruta.turista;
  document.getElementById("modal-ejecutiva").textContent = ruta.ejecutiva;
  document.getElementById("modal-fecha").textContent = ruta.fecha;

  document.getElementById("modal").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("modal").classList.add("hidden");
}
