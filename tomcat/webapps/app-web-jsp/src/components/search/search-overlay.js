

const overlay = document.getElementById("search-overlay");
const openBtn = document.getElementById("open-search");
const closeBtn = document.getElementById("close-search");
const resultsBox = document.getElementById("search-results");
const sortSelect = document.getElementById("search-sort");


openBtn?.addEventListener("click", () => {
    overlay.classList.remove("hidden");
    resultsBox.innerHTML = "";
});


closeBtn?.addEventListener("click", () => {
    overlay.classList.add("hidden");
});

overlay?.addEventListener("click", (e) => {
    if (e.target === overlay) overlay.classList.add("hidden");
});


function applySorting(data, mode) {
    const asc = (a, b) => a.name.localeCompare(b.name);
    const desc = (a, b) => b.name.localeCompare(a.name);

    if (mode === "name-asc") {
        data.rutas.sort(asc);
        data.paquetes.sort(asc);
    } else if (mode === "name-desc") {
        data.rutas.sort(desc);
        data.paquetes.sort(desc);
    }
}


document.getElementById("search-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const q = document.getElementById("search-query").value.trim();
    const sort = sortSelect.value;

    resultsBox.innerHTML = "<p class='text-center text-gray-500'>Buscando...</p>";

    try {
        let url=`${APP_CTX}/buscar?format=json&query=${encodeURIComponent(q)}`;
        console.log(url);
        const resp = await fetch(`${APP_CTX}/buscar?format=json&query=${encodeURIComponent(q)}`);

        if (!resp.ok) throw new Error("Servidor no disponible");

        const data = await resp.json();

        applySorting(data, sort);

        let html = "";


        html += "<h3 class='text-xl font-semibold mt-4 mb-2'>Rutas</h3>";

        if (data.rutas.length === 0) {
            html += "<p class='text-gray-500'>No se encontraron rutas.</p>";
        } else {
            html += `<div class="flex flex-col gap-3">`;
            data.rutas.forEach(r => {
                html += `
                    <a href="${r.url}"
                        class="flex flex-col p-3 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition">
                        
                        <div class="font-semibold text-brand flex items-center gap-2">
                            <span class="text-lg">✈️</span> ${r.name}
                        </div>

                        <span class="text-sm text-gray-600 mt-1">
                            ${r.description}
                        </span>
                    </a>
                `;
            });
            html += "</div>";
        }

        html += "<h3 class='text-xl font-semibold mt-6 mb-2'>Paquetes</h3>";

        if (data.paquetes.length === 0) {
            html += "<p class='text-gray-500'>No se encontraron paquetes.</p>";
        } else {
            html += `<div class="flex flex-col gap-3">`;
            data.paquetes.forEach(p => {
                html += `
                    <a href="${p.url}"
                        class="flex flex-col p-3 bg-gray-100 border border-gray-200 rounded-lg hover:bg-gray-200 transition">
                        
                        <div class="font-semibold flex items-center gap-2">
                            <span class="text-lg">📦</span> ${p.name}
                        </div>

                        <span class="text-sm text-gray-600 mt-1">
                            ${p.description}
                        </span>
                    </a>
                `;
            });
            html += "</div>";
        }

        resultsBox.innerHTML = html;

    } catch (err) {
        resultsBox.innerHTML = `
            <p class="text-red-500 text-center">${err}</p>
        `;
    }
});

sortSelect.addEventListener("change", () => {
    const q = document.getElementById("search-query").value.trim();
    if (q.length > 0) {
        document.getElementById("search-form").dispatchEvent(new Event("submit"));
    }
});
