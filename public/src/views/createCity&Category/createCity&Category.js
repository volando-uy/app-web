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

// ===== nueva lógica para gestionar ciudades y categorías (localStorage) =====
document.addEventListener('DOMContentLoaded', () => {
  // Keys
  const KEY_CITIES = 'cities';
  const KEY_CATEGORIES = 'categories';

  // Helpers storage
  const read = (k) => {
    try { return JSON.parse(localStorage.getItem(k) || '[]'); } catch { return []; }
  };
  const write = (k, v) => localStorage.setItem(k, JSON.stringify(v));

  // Elements
  const cityName = document.getElementById('city-name');
  const cityCountry = document.getElementById('city-country');
  const cityLat = document.getElementById('city-lat');
  const cityLng = document.getElementById('city-lng');
  const btnCitySave = document.getElementById('btn-city-save');
  const btnCityClear = document.getElementById('btn-city-clear');
  const citiesList = document.getElementById('cities-list');

  const categoryName = document.getElementById('category-name');
  const btnCategorySave = document.getElementById('btn-category-save');
  const btnCategoryClear = document.getElementById('btn-category-clear');
  const categoriesList = document.getElementById('categories-list');

  // Renderers
  function renderCities() {
    const list = read(KEY_CITIES);
    citiesList.innerHTML = list.length
      ? list.map((c, idx) => `
        <div class="flex items-center justify-between p-2 border rounded">
          <div>
            <div class="font-semibold">${escapeHtml(c.name)}</div>
            <div class="text-xs text-gray-500">${escapeHtml(c.country)} • ${c.lat || '-'} , ${c.lng || '-'}</div>
          </div>
          <div class="flex gap-2">
            <button data-idx="${idx}" class="edit-city px-2 py-1 text-sm border rounded">Editar</button>
            <button data-idx="${idx}" class="del-city px-2 py-1 text-sm text-red-500 border rounded">Eliminar</button>
          </div>
        </div>
      `).join('')
      : `<div class="text-sm text-gray-500">No hay ciudades creadas.</div>`;
  }

  function renderCategories() {
    const list = read(KEY_CATEGORIES);
    categoriesList.innerHTML = list.length
      ? list.map((c, idx) => `
        <div class="flex items-center justify-between p-2 border rounded">
          <div class="font-semibold">${escapeHtml(c.name)}</div>
          <div class="flex gap-2">
            <button data-idx="${idx}" class="edit-cat px-2 py-1 text-sm border rounded">Editar</button>
            <button data-idx="${idx}" class="del-cat px-2 py-1 text-sm text-red-500 border rounded">Eliminar</button>
          </div>
        </div>
      `).join('')
      : `<div class="text-sm text-gray-500">No hay categorías creadas.</div>`;
  }

  // Actions
  btnCitySave?.addEventListener('click', () => {
    const name = (cityName?.value || '').trim();
    const country = (cityCountry?.value || '').trim();
    if (!name) { alert('El nombre de la ciudad es obligatorio'); return; }
    const list = read(KEY_CITIES);
    list.push({ name, country, lat: cityLat?.value || '', lng: cityLng?.value || '' });
    write(KEY_CITIES, list);
    renderCities();
    // limpiar
    cityName.value = cityCountry.value = cityLat.value = cityLng.value = '';
    alert('Ciudad registrada correctamente');
  });

  btnCityClear?.addEventListener('click', () => {
    cityName.value = cityCountry.value = cityLat.value = cityLng.value = '';
  });

  btnCategorySave?.addEventListener('click', () => {
    const name = (categoryName?.value || '').trim();
    if (!name) { alert('El nombre de la categoría es obligatorio'); return; }
    const list = read(KEY_CATEGORIES);
    // evitar duplicados simples
    if (list.some(c => c.name.toLowerCase() === name.toLowerCase())) { alert('La categoría ya existe'); return; }
    list.push({ name });
    write(KEY_CATEGORIES, list);
    renderCategories();
    categoryName.value = '';
    alert('Categoría creada correctamente');
  });

  btnCategoryClear?.addEventListener('click', () => { categoryName.value = ''; });

  // Delegation for edit/delete
  citiesList?.addEventListener('click', (e) => {
    const del = e.target.closest('.del-city');
    const edit = e.target.closest('.edit-city');
    if (del) {
      const idx = Number(del.getAttribute('data-idx'));
      const list = read(KEY_CITIES);
      if (!confirm('Eliminar ciudad "' + list[idx].name + '" ?')) return;
      list.splice(idx,1);
      write(KEY_CITIES, list);
      renderCities();
    } else if (edit) {
      const idx = Number(edit.getAttribute('data-idx'));
      const list = read(KEY_CITIES);
      const c = list[idx];
      // rellenar formulario para editar (simple: eliminar y rellenar inputs)
      cityName.value = c.name; cityCountry.value = c.country; cityLat.value = c.lat; cityLng.value = c.lng;
      list.splice(idx,1); write(KEY_CITIES, list); renderCities();
    }
  });

  categoriesList?.addEventListener('click', (e) => {
    const del = e.target.closest('.del-cat');
    const edit = e.target.closest('.edit-cat');
    if (del) {
      const idx = Number(del.getAttribute('data-idx'));
      const list = read(KEY_CATEGORIES);
      if (!confirm('Eliminar categoría "' + list[idx].name + '" ?')) return;
      list.splice(idx,1);
      write(KEY_CATEGORIES, list);
      renderCategories();
    } else if (edit) {
      const idx = Number(edit.getAttribute('data-idx'));
      const list = read(KEY_CATEGORIES);
      const c = list[idx];
      categoryName.value = c.name;
      list.splice(idx,1); write(KEY_CATEGORIES, list); renderCategories();
    }
  });

  // Inicializar render
  renderCities();
  renderCategories();

  // pequeño helper
  function escapeHtml(s){ return String(s||'').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }
});