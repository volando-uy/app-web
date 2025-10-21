function filterCards(){
    const q = (document.querySelector('input[name="q"]').value || '').trim().toLowerCase();
    document.querySelectorAll('#packages-list article').forEach(card=>{
        const name = card.dataset.name || '';
        const desc = card.dataset.desc || '';
        const show = !q || name.includes(q) || desc.includes(q);
        card.style.display = show ? '' : 'none';
    });
}