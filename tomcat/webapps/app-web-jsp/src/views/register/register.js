function toggleUserType() {
    const userType = document.getElementById("userType").value;
    const clienteFields = document.getElementById("clienteFields");
    const aerolineaFields = document.getElementById("aerolineaFields");

    clienteFields.classList.add("hidden");
    aerolineaFields.classList.add("hidden");

    if (userType === "cliente") {
        clienteFields.classList.remove("hidden");
    } else if (userType === "aerolinea") {
        aerolineaFields.classList.remove("hidden");
    }
}

let timeout = null;

const nicknameInput = document.getElementById('reg-nickname');
const nicknameMsg = document.getElementById('nicknameMsg');
let existsNickname = false;

const mailInput = document.getElementById('reg-email');
const mailMsg = document.getElementById('emailMsg');
let existsMail = false;

nicknameInput.addEventListener('input', () => {
    clearTimeout(timeout);

    timeout = setTimeout(async () => {
        const nickname = nicknameInput.value.trim();
        if (!nickname) {
            nicknameMsg.textContent = '';
            resetNicknameStyles();
            existsNickname = false;
            return;
        }

        try {
            const res = await fetch(`/app-web-jsp/existsUser?nickname=${encodeURIComponent(nickname)}`);
            const data = await res.json();

            if (data.nicknameExists === false) {
                existsNickname = false;
                nicknameMsg.textContent = 'Nickname disponible';
                nicknameMsg.className = 'mt-2 text-sm text-emerald-600';
                nicknameInput.className = baseInputClasses + ' border-emerald-600 bg-emerald-50';
            } else {
                existsNickname = true;
                nicknameMsg.textContent = 'Nickname no disponible';
                nicknameMsg.className = 'mt-2 text-sm text-red-600';
                nicknameInput.className = baseInputClasses + ' border-red-600 bg-red-50';
            }
        } catch (err) {
            existsNickname = true; // por si falla, bloqueamos
            nicknameMsg.textContent = 'Error al verificar nickname';
            nicknameMsg.className = 'mt-2 text-sm text-yellow-600';
            nicknameInput.className = baseInputClasses + ' border-yellow-600 bg-yellow-50';
        }
    }, 500);
});


mailInput.addEventListener('input', () => {
    clearTimeout(timeout);

    timeout = setTimeout(async () => {
        const email = mailInput.value.trim();

        if (!email) {
            resetEmailStyles();
            mailMsg.textContent = '';
            existsMail = false;
            return;
        }

        try {
            const res = await fetch(`/app-web-jsp/existsUser?email=${encodeURIComponent(email)}`);
            const data = await res.json();

            if (data.mailExists === false) {
                existsMail = false;

                mailMsg.textContent = 'Email disponible';
                mailMsg.className = 'mt-1 text-sm font-medium text-emerald-600';

                mailInput.className = baseInputClasses + ' border-emerald-600 bg-emerald-50';

            } else {
                existsMail = true;

                mailMsg.textContent = 'Email no disponible';
                mailMsg.className = 'mt-1 text-sm font-medium text-red-600';

                mailInput.className = baseInputClasses + ' border-red-600 bg-red-50';
            }

        } catch (err) {
            existsMail = true;

            mailMsg.textContent = 'Error al verificar email';
            mailMsg.className = 'mt-1 text-sm font-medium text-yellow-600';

            mailInput.className = baseInputClasses + ' border-yellow-600 bg-yellow-50';
        }

    }, 500);
});


// Función para resetear estilos del input si está vacío
function resetNicknameStyles() {
    nicknameInput.className = baseInputClasses + ' border-gray-300 bg-white';
}

function resetEmailStyles() {
    mailInput.className = baseInputClasses + ' border-gray-300 bg-white';
}

// Base común del input
const baseInputClasses = 'w-full mb-4 px-4 py-2 border rounded-lg transition-all duration-200 outline-none focus:ring-2 focus:ring-brand';
const registerButton = document.getElementById('btn-register');


registerButton.addEventListener('click', async (e) => {
    e.preventDefault();

    const userType = document.getElementById('userType').value;

    // Validaciones comunes
    if (!validarGenerales()) return;

    // Validaciones según tipo
    if (userType === 'cliente') {
        if (!validarCliente()) return;
    } else if (userType === 'aerolinea') {
        if (!validarAerolinea()) return;
    }

    // Si todo ok, enviamos el form
    document.getElementById('registerForm').submit();
});

function validarGenerales() {
    const name = document.getElementById('reg-nombre').value.trim();
    const password = document.getElementById('reg-password').value.trim();

    if (existsNickname) {
        alert('Por favor, elija un nickname disponible antes de registrarse.');
        return false;
    }

    if (existsMail) {
        alert('Por favor, elija un email disponible antes de registrarse.');
        return false;
    }

    if (!campoRequerido(name, 'El nombre no puede estar vacío.')) return false;
    if (!campoRequerido(password, 'La contraseña no puede estar vacía.')) return false;

    if (!validar_email()) return false;
    if (!validar_password()) return false;

    return true;
}


function validarCliente() {
    const birthDate = document.getElementById('reg-dob').value;
    const apellido = document.getElementById('reg-apellido').value.trim();
    const tipo_documento = document.getElementById('reg-doc-type').value;
    const nacionalidad = document.getElementById('reg-nacionalidad').value.trim();
    const numero_documento = document.getElementById('reg-doc-number').value.trim();

    const campos = [
        { valor: birthDate, mensaje: 'La fecha de nacimiento no puede estar vacía.' },
        { valor: apellido, mensaje: 'El apellido no puede estar vacío.' },
        { valor: nacionalidad, mensaje: 'La nacionalidad no puede estar vacía.' },
        { valor: numero_documento, mensaje: 'El número de documento no puede estar vacío.' },
        { valor: tipo_documento, mensaje: 'El tipo de documento no puede estar vacío.' },
    ];

    for (const campo of campos) {
        if(!campoRequerido(campo.valor, campo.mensaje)) return false;
    }

    if (isNaN(Date.parse(birthDate))) {
        alert('La fecha de nacimiento no es válida.');
        return false;
    }

    return true;
}

function validarAerolinea() {
    const web = document.getElementById('reg-web-a').value.trim();
    const descripcion = document.getElementById('reg-desc-a').value.trim();

    const campos = [
        { valor: web, mensaje: 'La página web no puede estar vacía.' },
        { valor: descripcion, mensaje: 'La descripción no puede estar vacía.' },
    ];
    for (const campo of campos) {
        if (!campoRequerido(campo.valor, campo.mensaje)) return false;
    }

    return true;
}

function campoRequerido(valor, mensaje) {
    if (!valor || valor.trim() === '') {
        alert(mensaje);
        return false;
    }
    return true;
}

function validar_password() {
    const password = document.getElementById('reg-password').value;
    const confirmPassword = document.getElementById('reg-confirm-password').value;

    if (password !== confirmPassword) {
        alert('Las contraseñas no coinciden.');
        return false;
    }
    return true;
}

function validar_email() {
    const email = document.getElementById('reg-email').value;
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {
        alert('El formato del email no es válido.');
        return false;
    }
    return true;
}