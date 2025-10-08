// // Animación
// function animateOnScroll(element) {
//   if (!element) return;
//   element.classList.add("opacity-0","translate-y-8","transition-all","duration-700");
//   const observer = new IntersectionObserver((entries, obs) => {
//     entries.forEach(entry => {
//       if (entry.isIntersecting) {
//         entry.target.classList.add("opacity-100","translate-y-0");
//         entry.target.classList.remove("opacity-0","translate-y-8");
//         obs.unobserve(entry.target);
//       }
//     });
//   }, { threshold: 0.2 });
//   observer.observe(element);
// }
//
// // Header
// async function injectHeader() {
//   const container = document.getElementById("header-container");
//   const res = await fetch("src/views/header/header.html");
//   container.innerHTML = await res.text();
//   const script = document.createElement("script");
//   script.src = "src/views/header/header.js";
//   script.onload = () => { if (typeof initHeader === "function") initHeader(); };
//   document.body.appendChild(script);
// }
//
// // Footer
// async function injectFooter() {
//   const container = document.getElementById("footer-container");
//   const res = await fetch("src/views/footer/footer.html");
//   container.innerHTML = await res.text();
// }
//
// // Paquetes
// async function injectPackageList() {
//   const container = document.getElementById("paquetes");
//   const res = await fetch("src/views/components/packageList/packageList.html");
//   container.innerHTML = await res.text();
//   animateOnScroll(container);
//   const script = document.createElement("script");
//   script.src = "src/views/components/packageList/packageList.js";
//   document.body.appendChild(script);
// }
//
// // Vuelos (inyecta HTML y luego llama initFlights cuando cargue el JS)
// async function injectFlightList() {
//   const container = document.getElementById("vuelos");
//   const res = await fetch("src/views/components/flightList/flightList.html");
//   container.innerHTML = await res.text();
//   animateOnScroll(container);
//
//   const script = document.createElement("script");
//   script.src = "src/views/components/flightList/flightList.js";
//   script.onload = () => { if (typeof initFlights === "function") initFlights(); };
//   document.body.appendChild(script);
// }
//
// window.addEventListener("DOMContentLoaded", () => {
//   injectHeader();
//   injectFooter();
//   injectPackageList();
//   injectFlightList();
//   animateOnScroll(document.getElementById("footer-container"));
// });
