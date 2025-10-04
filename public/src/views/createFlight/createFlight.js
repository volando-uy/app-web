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

// Placeholder para futura lógica de creación de vuelos.
// Puedes implementar guardado en localStorage o futura llamada a API aquí.
console.log('[createFlight] listo para implementar.');