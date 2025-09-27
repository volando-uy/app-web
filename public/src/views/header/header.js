 
const btn = document.getElementById('btnMenu');
const menu = document.getElementById('mobileMenu');
btn?.addEventListener('click', () => {
menu.classList.toggle('hidden');
    const open = btn.getAttribute('aria-expanded') === 'true';
    btn.setAttribute('aria-expanded', String(!open));
  });