// --- Datos de ejemplo de vuelos ---
const vuelos = [
	{
		aerolinea: "LATAM Airlines",
		logo: "https://upload.wikimedia.org/wikipedia/commons/2/2e/Latam-logo.png",
		rutas: [
			{ origen: "OKA", destino: "HND", tiempo: "1h 20m" },
			{ origen: "HND", destino: "JFK", tiempo: "12h 30m" },
			{ origen: "JFK", destino: "SCL", tiempo: "10h 00m" },
			{ origen: "SCL", destino: "MVD", tiempo: "2h 40m" }
		],
		duracion: "39h 12m",
		salida: { hora: "7:25 am", ciudad: "OKA" },
		llegada: { hora: "10:37 am", ciudad: "MVD", dia: "al día siguiente" },
		operadoPor: "Operado por JAPAN AIRLINES FOR LATAM AIRLINES",
		precio: 0,
		moneda: "USD",
		tipo: "Estándar",
		detalles: {
			total: "39h 12m",
			rutas: [
				{ origen: "OKA", destino: "HND", tiempo: "1h 20m" },
				{ origen: "HND", destino: "JFK", tiempo: "12h 30m" },
				{ origen: "JFK", destino: "SCL", tiempo: "10h 00m" },
				{ origen: "SCL", destino: "MVD", tiempo: "2h 40m" }
			]
		}
	},
	{
		aerolinea: "Aerolineas Argentinas",
		logo: "https://upload.wikimedia.org/wikipedia/commons/7/7e/Aerolineas_Argentinas_logo.svg",
		rutas: [
			{ origen: "MVD", destino: "AEP", tiempo: "0h 55m" }
		],
		duracion: "0h 55m",
		salida: { hora: "9:45 am", ciudad: "MVD" },
		llegada: { hora: "10:40 am", ciudad: "AEP" },
		operadoPor: "Aerolineas Argentinas",
		precio: 184.69,
		moneda: "USD",
		tipo: "Estándar",
		detalles: {
			total: "0h 55m",
			rutas: [
				{ origen: "MVD", destino: "AEP", tiempo: "0h 55m" }
			]
		}
	}
];

let vuelosFiltrados = [...vuelos]; // Array para mantener los vuelos filtrados

function renderFlights() {
	const container = document.getElementById("flights-list");
	if (!container) return;
	container.innerHTML = vuelosFiltrados.map((v, idx) => `
		<div class="bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col md:flex-row items-center mb-6 p-4 gap-4">
			<div class="flex flex-col items-center w-32">
				<img src="${v.logo}" alt="${v.aerolinea}" class="h-8 object-contain mb-2" />
				<div class="text-xs text-gray-700">${v.rutas.length === 1 ? "Directo" : (v.rutas.length - 1) + " conexiones"}</div>
				<div class="text-xs text-gray-500">${v.duracion}</div>
				<div class="text-xs text-gray-400 mt-1">${v.aerolinea}</div>
			</div>
			<div class="flex-1 flex flex-col md:flex-row items-center justify-between gap-4">
				<div class="flex flex-col items-center">
					<div class="text-lg font-bold">${v.salida.hora}</div>
					<div class="text-xs text-gray-500">${v.salida.ciudad}</div>
				</div>
				<div class="flex flex-col items-center">
					<div class="w-32 h-6 flex items-center justify-center">
						<div class="w-full h-1 bg-gray-200 rounded-full relative">
							<div class="absolute left-0 top-0 w-full h-1 flex items-center justify-between">
								${Array.from({length: v.rutas.length}).map((_,i) => `<span class="w-2 h-2 bg-gray-400 rounded-full inline-block"></span>`).join("")}
							</div>
						</div>
						<div class="text-xs text-gray-500 ml-2">${v.rutas.length === 1 ? "Directo" : (v.rutas.length - 1) + " conexiones"}</div>
					</div>
				</div>
				<div class="flex flex-col items-center">
					<div class="text-lg font-bold">${v.llegada.hora}</div>
					<div class="text-xs text-gray-500">${v.llegada.ciudad}</div>
					${v.llegada.dia ? `<div class="text-xs text-orange-500 font-semibold">${v.llegada.dia}</div>` : ""}
				</div>
				<div class="flex flex-col items-center">
					<button class="text-xs text-brand border border-brand rounded px-3 py-1 mt-2 hover:bg-brand hover:text-white transition" onclick="showFlightModal(${vuelos.indexOf(v)})">Detalles del vuelo</button>
				</div>
				<div class="flex flex-col items-center">
					<div class="text-right text-xs text-gray-500">${v.tipo}</div>
					<div class="text-2xl font-bold text-orange-600">${v.precio === 0 ? "+US$0.00" : `US$${v.precio}`}</div>
					<div class="text-xs text-gray-400">Precio por persona<br>(impuestos y tasas incl.)</div>
					<button class="mt-2 px-4 py-2 bg-orange-500 text-white rounded-lg font-bold hover:bg-orange-600 transition" onclick="selectFlight(${vuelos.indexOf(v)})">Selecciona este ${v.tipo}</button>
				</div>
			</div>
		</div>
	`).join("");
}

// Función para poblar el filtro de aerolíneas
function populateAirlineFilter() {
	const select = document.getElementById("aerolinea-filter");
	if (!select) return;
	
	const aerolineas = [...new Set(vuelos.map(v => v.aerolinea))].sort();
	aerolineas.forEach(aerolinea => {
		const option = document.createElement("option");
		option.value = aerolinea;
		option.textContent = aerolinea;
		select.appendChild(option);
	});
}

// Función para filtrar vuelos por aerolínea
function filterByAirline() {
	const selectedAirline = document.getElementById("aerolinea-filter").value;
	
	if (selectedAirline === "") {
		vuelosFiltrados = [...vuelos];
	} else {
		vuelosFiltrados = vuelos.filter(v => v.aerolinea === selectedAirline);
	}
	
	renderFlights();
}

// Modal de detalles
function showFlightModal(idx) {
	const v = vuelos[idx];
	let rutasHtml = v.detalles.rutas.length
		? v.detalles.rutas.map((r, i) => `<li><b>Tramo ${i+1}:</b> ${r.origen} → ${r.destino} (${r.tiempo})</li>`).join("")
		: '<li>Vuelo directo</li>';
	const modalHtml = `
		<div id="flight-modal-bg" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
			<div class="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 relative animate-fade-in">
				<button onclick="closeFlightModal()" class="absolute top-2 right-2 text-gray-400 hover:text-brand text-2xl">&times;</button>
				<h2 class="text-xl font-bold mb-2">Detalles del vuelo</h2>
				<div class="mb-2 text-sm text-gray-700">Duración total: <b>${v.detalles.total}</b></div>
				<ul class="mb-4 text-sm text-gray-700">${rutasHtml}</ul>
				<div class="text-xs text-gray-500">${v.operadoPor}</div>
			</div>
		</div>
		<style>@keyframes fade-in{from{opacity:0;transform:scale(.95);}to{opacity:1;transform:scale(1);}}.animate-fade-in{animation:fade-in .2s;}</style>
	`;
	document.body.insertAdjacentHTML('beforeend', modalHtml);
}

function closeFlightModal() {
	const modal = document.getElementById('flight-modal-bg');
	if (modal) modal.remove();
}

// guardar vuelo y navegar a la reserva
function selectFlight(idx) {
	const vuelo = vuelos[idx];
	try {
		sessionStorage.setItem('bookingFlight', JSON.stringify(vuelo));
		// navegar a la página de reserva (ajusta la ruta si usas JSP en futuro)
		window.location.href = '../bookFlight/bookflight.html';
	} catch (e) {
		console.error('No se pudo guardar el vuelo en sessionStorage', e);
		alert('Error al seleccionar vuelo.');
	}
}

// Render al cargar
document.addEventListener("DOMContentLoaded", () => {
	populateAirlineFilter();
	renderFlights();
	
	// Agregar event listener al filtro de aerolíneas
	const airlineFilter = document.getElementById("aerolinea-filter");
	if (airlineFilter) {
		airlineFilter.addEventListener("change", filterByAirline);
	}
});

// Inyectar header dinámicamente
fetch("../header/header.html")
  .then(res => res.text())
  .then(data => {
    document.getElementById("header").innerHTML = data;

    const script = document.createElement("script");
    script.src = "../header/header.js";
    script.onload = () => {
      if (typeof initHeader === "function") initHeader();
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

// Asegurar que flightRoutes tengan status si existen (para admin)
(function ensureRouteStatuses(){
	try{
		const fr = JSON.parse(localStorage.getItem('flightRoutes')||'[]');
		let changed = false;
		fr.forEach(g=>{
			(g.routes||[]).forEach(r=>{
				if(!r.status){ r.status='Ingresada'; changed=true; }
			});
		});
		if(changed) localStorage.setItem('flightRoutes', JSON.stringify(fr));
	}catch{}
})();
