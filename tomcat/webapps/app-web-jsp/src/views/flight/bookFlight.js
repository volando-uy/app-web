(function () {
    const rows = document.getElementById("rows");
    const addBtn = document.getElementById("addRow");
    const pcount = document.getElementById("pcount");
    const paxInput = document.getElementById("passengers");
    const seatSel = document.getElementById("seatSelect");
    const totalText = document.getElementById("totalText");
    const totalInput = document.getElementById("totalInput");
    const baseLine = document.getElementById("baseLine");

    function priceFromSeat() {
        const opt = seatSel.options[seatSel.selectedIndex];
        const val = parseFloat(opt.getAttribute("data-price") || "0");
        return isNaN(val) ? 0 : val;
    }

    function reindex() {
        const trs = rows.querySelectorAll("tr.row");
        trs.forEach((tr, idx) => {
            tr.dataset.idx = String(idx);
            tr.querySelectorAll("input,select").forEach(el => {
                const n = el.getAttribute("name");
                if (!n) return;
                const base = n.split("_")[0]; // name_0 -> name
                el.setAttribute("name", base + "_" + idx);
            });
        });
        pcount.value = String(trs.length);
        paxInput.value = String(trs.length);
    }

    function recalc() {
        const pax = parseInt(pcount.value || "1", 10);
        const seatPrice = priceFromSeat();
        const total = (seatPrice || 0) * pax;

        totalText.textContent = "USD " + (total.toFixed(2));
        totalInput.value = String(total);

        // Línea de base (informativa)
        if (seatPrice > 0) {
            baseLine.textContent = "USD " + seatPrice.toFixed(2) + " / " + seatSel.options[seatSel.selectedIndex].text.split("(")[0].trim();
        } else {
            baseLine.textContent = "--";
        }
    }

    addBtn?.addEventListener("click", () => {
        const idx = rows.querySelectorAll("tr.row").length;
        const tr = document.createElement("tr");
        tr.className = "row";
        tr.dataset.idx = String(idx);
        tr.innerHTML = `
      <td class="px-3 py-2">
        <select name="doctype_${idx}" class="border rounded px-2 py-1 w-28">
          <option value="CI">CI</option>
          <option value="PASAPORTE">PASAPORTE</option>
        </select>
      </td>
      <td class="px-3 py-2"><input name="docnum_${idx}" class="border rounded px-2 py-1 w-36"></td>
      <td class="px-3 py-2"><input name="name_${idx}"   class="border rounded px-2 py-1 w-36"></td>
      <td class="px-3 py-2"><input name="surname_${idx}"class="border rounded px-2 py-1 w-36"></td>
      <td class="px-3 py-2">
        <select name="basic_${idx}" class="border rounded px-2 py-1">
          <option value="">--</option>
          <option value="BOLSO">Bolso</option>
          <option value="MANO">De mano</option>
          <option value="VALIJA">Valija</option>
        </select>
      </td>
      <td class="px-3 py-2">
        <label class="inline-flex items-center gap-1 mr-2">
          <input type="checkbox" name="extra_${idx}" value="EXTRA_VALIJA"><span>Extra valija</span>
        </label>
        <label class="inline-flex items-center gap-1">
          <input type="checkbox" name="extra_${idx}" value="EQUIPO_DEPORTIVO"><span>Equipo deportivo</span>
        </label>
      </td>
      <td class="px-3 py-2 text-center">
        <button type="button" class="delRow text-red-600 hover:underline">Eliminar</button>
      </td>
    `;
        rows.appendChild(tr);
        reindex();
        recalc();
    });

    rows.addEventListener("click", (e) => {
        const btn = e.target.closest(".delRow");
        if (!btn) return;
        const tr = btn.closest("tr.row");
        if (!tr) return;
        tr.remove();
        reindex();
        recalc();
    });

    seatSel?.addEventListener("change", recalc);

    // init
    reindex();
    recalc();
})();
