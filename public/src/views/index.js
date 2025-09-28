// Inyectar el header dinámicamente en el index
async function injectHeader() {
  const container = document.getElementById('header-container');
  const res = await fetch('src/views/header/header.html');
  const html = await res.text();
  container.innerHTML = html;
  // Cargar y ejecutar el JS del header
  const script = document.createElement('script');
  script.src = 'src/views/header/header.js';
  script.onload = () => { if (typeof initHeader === 'function') initHeader(); };
  document.body.appendChild(script);
}

// Inyectar el footer dinámicamente en el index
async function injectFooter() {
  const container = document.getElementById('footer-container');
  const res = await fetch('src/views/footer/footer.html');
  const html = await res.text();
  container.innerHTML = html;
}

// Ejecutar al cargar la página
window.addEventListener('DOMContentLoaded', () => {
  injectHeader();
  injectFooter();
});
