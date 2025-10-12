(function () {
    const $ = (s, c = document) => c.querySelector(s);
    const $$ = (s, c = document) => Array.from(c.querySelectorAll(s));

    // --- Modal ---
    function openModal(innerHtml) {
        let root = $("#modal-root");
        if (!root) { root = document.createElement("div"); root.id = "modal-root"; document.body.appendChild(root); }
        root.innerHTML = `
      <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" data-overlay>
        <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 relative">
          <button type="button" data-modal-close class="absolute top-2 right-2 text-gray-400 hover:text-brand text-2xl" aria-label="Cerrar">×</button>
          ${innerHtml}
        </div>
      </div>`;
        root.querySelector('[data-overlay]').addEventListener('click', e => { if (e.target.matches('[data-overlay]')) closeModal(); });
        root.querySelector('[data-modal-close]').addEventListener('click', closeModal);
        document.addEventListener('keydown', escHandler);
        function escHandler(ev){ if (ev.key === 'Escape') { closeModal(); document.removeEventListener('keydown', escHandler); } }
    }
    function closeModal(){ const r = $("#modal-root"); if (r) r.innerHTML = ""; }

    const esc = s => String(s ?? '').replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[m]));

    // --- Detalles del vuelo ---
    $$('#flights-list [data-details]').forEach(btn => {
        btn.addEventListener('click', () => {
            const card = btn.closest('[data-card]');
            const idx = card.getAttribute('data-idx');
            let legs = [], meta = {};
            try { legs = JSON.parse(document.getElementById('legs-'+idx).textContent.trim() || '[]'); } catch {}
            try { meta = JSON.parse(document.getElementById('meta-'+idx).textContent.trim() || '{}'); } catch {}

            const lis = (legs.length ? legs : [{origen:'—', destino:'—', tiempo:'—'}])
                .map((r,i)=>`<li><b>Tramo ${i+1}:</b> ${esc(r.origen)} → ${esc(r.destino)} (${esc(r.tiempo)})</li>`).join('');

            openModal(`
        <h2 class="text-xl font-bold mb-2">Detalles del vuelo</h2>
        <div class="mb-2 text-sm text-gray-700">Duración total: <b>${esc(meta.total||'')}</b></div>
        <ul class="mb-4 text-sm text-gray-700">${lis}</ul>
        <div class="text-xs text-gray-500">${esc(meta.operadoPor||'')}</div>
      `);
        });
    });

    // --- Filtros ---
    const selAir = $('#aerolinea-filter');
    const selDate = $('#fecha');
    const inpOrigin = $('#origen');
    const inpDest = $('#destino');

    [selAir, selDate, inpOrigin, inpDest].forEach(el => {
        if (el) el.addEventListener('input', applyFilters);
    });

    function applyFilters() {
        const a = selAir ? selAir.value.trim().toLowerCase() : '';
        const d = selDate ? selDate.value : '';
        const o = inpOrigin ? inpOrigin.value.trim().toLowerCase() : '';
        const des = inpDest ? inpDest.value.trim().toLowerCase() : '';

        $$('#flights-list [data-card]').forEach(card => {
            const aero = (card.getAttribute('data-aero') || '').toLowerCase();
            const date = card.getAttribute('data-date') || '';
            const salida = (card.querySelector('.text-xs.text-gray-500')?.textContent || '').toLowerCase();

            const okA = !a || aero.includes(a);
            const okD = !d || d === date;
            const okO = !o || salida.includes(o);
            const okDes = !des || salida.includes(des);

            card.style.display = (okA && okD && okO && okDes) ? '' : 'none';
        });
    }
    // HEADER
    fetch("../header/header.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("header").innerHTML = html;

            // Si tu header tiene JS asociado
            const script = document.createElement("script");
            script.src = "../header/header.js";
            document.body.appendChild(script);
        });

    // FOOTER
    fetch("../footer/footer.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("footer").innerHTML = html;
        });
})();
