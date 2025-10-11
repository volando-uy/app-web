document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('flightForm');
    const msgBox = document.getElementById('responseMsg');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        msgBox.textContent = 'Enviando...';
        msgBox.className = 'mt-4 text-sm text-gray-600';
        try {
            const formData = new FormData(form);
            const response = await fetch('createFlight', {
                method: 'POST',
                body: formData
            });
            const data = await response.json();
            if (data.status === 'ok') {
                msgBox.textContent = data.message;
                msgBox.className = 'mt-4 text-sm text-emerald-600';
                form.reset();
            } else {
                msgBox.textContent = data.message || 'Error al crear el vuelo.';
                msgBox.className = 'mt-4 text-sm text-red-600';
            }
        } catch (err) {
            msgBox.textContent = 'Error de conexión con el servidor.';
            msgBox.className = 'mt-4 text-sm text-red-600';
        }
    });
});
