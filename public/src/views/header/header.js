function getRelativePath(target) {
  const current = window.location.pathname;
  const idx = current.indexOf('/src/views/');
  let base = '';
  if (idx !== -1) {
    const after = current.substring(idx + 11);
    const depth = after.split('/').length - 1;
    base = './' + '../'.repeat(depth);
  } else {
    base = './src/views/';
  }
  return base + target;
}

function setupHeaderLinks() {
  document.querySelectorAll('.nav-link').forEach(link => {
    const target = link.getAttribute('data-target');
    if (target) {
      link.setAttribute('href', getRelativePath(target));
      link.onclick = null;
    }
  });
  const logo = document.getElementById('logo-link');
  if (logo) logo.setAttribute('href', getRelativePath('index.html'));
}

function initHeader() {
  const btn = document.getElementById('btnMenu');
  const menu = document.getElementById('mobileMenu');

  btn?.addEventListener('click', () => {
    menu.classList.toggle('hidden');
    const open = btn.getAttribute('aria-expanded') === 'true';
    btn.setAttribute('aria-expanded', String(!open));
  });

  setupHeaderLinks();
}