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