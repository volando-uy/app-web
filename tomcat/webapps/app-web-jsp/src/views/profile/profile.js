document.addEventListener("DOMContentLoaded", async () => {
    const nickname = new URLSearchParams(window.location.search).get("nickname");
    const form = document.getElementById("profileForm");

    if (!nickname) {
        alert("❌ No se encontró el usuario.");
        return;
    }

    // Obtener datos del usuario (desde el backend)
    try {
        const response = await fetch(`getUserData?nickname=${nickname}`);
        const user = await response.json();

        if (!user) throw new Error("Usuario no encontrado");

        document.getElementById("nickname").value = user.nickname;
        document.getElementById("nombre").value = user.name || "";
        document.getElementById("email").value = user.email || "";

        if (user.type === "CUSTOMER") {
            // Mostrar campos cliente
            document.getElementById("apellido").value = user.surname || "";
            document.getElementById("fechaNacimiento").value = user.birthDate || "";
            document.getElementById("tipoDoc").value = user.docType || "CI";
            document.getElementById("documento").value = user.numDoc || "";
            document.getElementById("nacionalidad").value = user.citizenship || "";
        } else if (user.type === "AIRLINE") {
            // Ocultar campos cliente
            document.getElementById("apellidoField").classList.add("hidden");
            document.getElementById("fechaNacimientoField").classList.add("hidden");
            document.getElementById("docFields").classList.add("hidden");
            document.getElementById("nacionalidadField").classList.add("hidden");

            // Mostrar campos aerolínea
            document.getElementById("webField").classList.remove("hidden");
            document.getElementById("descripcionField").classList.remove("hidden");

            document.getElementById("web").value = user.web || "";
            document.getElementById("descripcion").value = user.description || "";
        }

    } catch (err) {
        console.error(err);
        alert("Error al cargar el usuario.");
    }

    // Enviar formulario
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        try {
            const response = await fetch(form.action, {
                method: "POST",
                body: formData
            });

            if (response.ok) {
                alert("✅ Perfil actualizado correctamente");
                window.location.href = `profile.jsp?nickname=${nickname}`;
            } else {
                alert("❌ Error al actualizar el perfil");
            }
        } catch (error) {
            console.error(error);
            alert("Error de conexión al servidor");
        }
    });

    // Volver
    document.getElementById("volver").addEventListener("click", () => {
        window.location.href = "index.jsp";
    });
});
