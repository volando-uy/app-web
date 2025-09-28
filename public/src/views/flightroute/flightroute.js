fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    document.getElementById("header").innerHTML = data;

    // cargo el script y cuando termina lo ejecuto
    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => {
      initHeader();
    };
    document.body.appendChild(script);
  });

const rutas = {
    montevideo: {
    title: "Montevideo - San José",
    desc: "Vuelo cómodo y rápido hacia San José.",
    origen: "Montevideo",
    destino: "San José",
    duracion: "50 min",
    turista: 30,
    ejecutiva: 20,
    fecha: "14/09/2025"
    },
    madrid: {
    title: "Madrid - París",
    desc: "Vuelo directo y sin escalas hacia París.",
    origen: "Madrid",
    destino: "París",
    duracion: "2h 15m",
    turista: 120,
    ejecutiva: 40,
    fecha: "20/09/2025"
    }
};

function openModal(key) {
    const ruta = rutas[key];
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