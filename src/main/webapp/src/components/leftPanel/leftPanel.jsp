<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/src/components/layout/libs.jspf" %>

<%
    domain.dtos.user.UserDTO usuario = (domain.dtos.user.UserDTO) session.getAttribute("usuario");

    if (usuario != null) {
        String tipoUsuario = usuario.getClass().getSimpleName().toLowerCase();
%>

<button id="togglePanelBtn"
        class="fixed bottom-6 right-6 bg-brand text-white rounded-full w-14 h-14 shadow-lg flex items-center justify-center text-2xl md:hidden z-50">
    ☰
</button>

<!-- Panel móvil desplegable -->
<div id="mobilePanel"
     class="fixed bottom-20 right-6 bg-brand text-white rounded-2xl shadow-lg p-4 flex flex-col gap-2 w-[180px] hidden z-50">
    <% if (tipoUsuario.contains("airline")) { %>
    <a href="/app-web-jsp/createFlight" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">✈️ Crear vuelo</a>
    <a href="/app-web-jsp/createFlightRoute" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">🧭 Crear ruta</a>
    <a href="/app-web-jsp/createPackage" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">🎒 Crear paquete</a>
    <a href="/app-web-jsp/packages/list" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">📦 Mis paquetes</a>
    <a href="" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">🧾 Reservas</a>

    <% } else if (tipoUsuario.contains("customer")) { %>
    <a href="/app-web-jsp/perfil" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">ℹ️ Mi perfil</a>
    <a href="/app-web-jsp/flight/list" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">✈️ Buscar vuelos</a>
    <a href="" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">🧾 Mis reservas</a>
    <a href="/app-web-jsp/packages/list" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">🎒 Paquetes</a>
    <% } %>
</div>

<script>
    // Mostrar/ocultar panel móvil
    document.addEventListener("DOMContentLoaded", () => {
        const btn = document.getElementById("togglePanelBtn");
        const panel = document.getElementById("mobilePanel");

        btn.addEventListener("click", () => {
            panel.classList.toggle("hidden");
            // Animación simple de aparición
            panel.classList.toggle("animate-fade-in");
        });
    });
</script>

<style>
    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(10px); }
        to { opacity: 1; transform: translateY(0); }
    }
    .animate-fade-in {
        animation: fadeIn 0.2s ease-out;
    }
</style>

<%
    }
%>
