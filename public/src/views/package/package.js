fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    document.getElementById("header").innerHTML = data;

    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => {
      initHeader();
    };
    document.body.appendChild(script);
  });

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

// --- Datos de ejemplo de paquetes ---
const paquetes = [
  {
    nombre: "Buenos Aires - Córdoba",
    imagen: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80",
    duracion: "2 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 120,
    moneda: "USD",
    color: "bg-[#c0392b]", // rojo
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Santiago de Chile - Mendoza",
    imagen: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
    duracion: "3 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 180,
    moneda: "USD",
    color: "bg-[#6c3483]", // violeta
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Montevideo - Punta del Este",
    imagen: "https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=400&q=80",
    duracion: "1 día",
    incluye: ["Viaje ida y vuelta"],
    precio: 60,
    moneda: "USD",
    color: "bg-[#e67e22]", // naranja
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Lima - Cusco",
    imagen: "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80",
    duracion: "2 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 150,
    moneda: "USD",
    color: "bg-[#27ae60]", // verde
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Ciudad de México - Guadalajara",
    imagen: "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=400&q=80",
    duracion: "2 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 130,
    moneda: "USD",
    color: "bg-[#2980b9]", // azul
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Bogotá - Medellín",
    imagen: "https://images.unsplash.com/photo-1465156799763-2c087c332922?auto=format&fit=crop&w=400&q=80",
    duracion: "2 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 110,
    moneda: "USD",
    color: "bg-[#f1c40f]", // amarillo
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Madrid - Barcelona",
    imagen: "https://images.unsplash.com/photo-1465101178521-c1a9136a3fdc?auto=format&fit=crop&w=400&q=80",
    duracion: "2 días",
    incluye: ["Viaje ida y vuelta"],
    precio: 140,
    moneda: "USD",
    color: "bg-[#16a085]", // turquesa
    iconos: ["fa-solid fa-bus"]
  },
  {
    nombre: "Bruselas - Ámsterdam",
    imagen: "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=400&q=80",
    duracion: "1 día",
    incluye: ["Viaje ida y vuelta"],
    precio: 100,
    moneda: "USD",
    color: "bg-[#8e44ad]", // violeta oscuro
    iconos: ["fa-solid fa-bus"]
  }
];

function renderPackages() {
  const container = document.getElementById("packages-list");
  if (!container) return;
  container.innerHTML = paquetes.map(pkg => `
    <div tabindex="0" role="button" class="group bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col transition-all duration-300 hover:shadow-2xl hover:-translate-y-1 cursor-pointer focus:outline-none">
      <div class="relative">
        <img src="${pkg.imagen}" alt="${pkg.nombre}" class="w-full h-48 object-cover" />
        <div class="absolute left-0 bottom-0 flex gap-2 px-4 py-2 ${pkg.color} rounded-tr-2xl">
          ${pkg.iconos.map(icon => `<i class="${icon} text-white text-lg"></i>`).join("")}
        </div>
      </div>
      <div class="p-5 flex-1 flex flex-col justify-between">
        <h3 class="text-lg font-bold text-gray-900 mb-1 group-hover:text-brand">${pkg.nombre}</h3>
        <div class="text-gray-700 text-sm mb-2">Duración: ${pkg.duracion}</div>
        <div class="text-gray-700 text-sm mb-2">Incluye:<ul class="list-disc ml-5 mt-1">${pkg.incluye.map(i => `<li>${i}</li>`).join("")}</ul></div>
        <div class="flex items-end justify-between mt-4">
          <div class="text-xs text-gray-500">DESDE</div>
          <div class="text-2xl font-bold text-[#e67e22]">${pkg.moneda} ${pkg.precio}</div>
        </div>
        <div class="text-xs text-right text-[#e67e22] font-semibold">Precio por persona<br>en base doble</div>
      </div>
    </div>
  `).join("");
}

// Render al cargar
document.addEventListener("DOMContentLoaded", renderPackages);