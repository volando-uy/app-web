<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/src/components/layout/libs.jspf" %>
<!-- Taglibs aquí una sola vez -->
<%@ page import="com.labpa.appweb.user.SoapUserDTO" %>
<%@ page import="com.labpa.appweb.user.SoapBaseCustomerDTO" %>
<%@ page import="com.labpa.appweb.user.SoapBaseAirlineDTO" %>
<%
    SoapUserDTO user = (SoapUserDTO) session.getAttribute("usuario");

    boolean isCustomer = user instanceof SoapBaseCustomerDTO;
    boolean isAirline = user instanceof SoapBaseAirlineDTO;

    boolean isLogged = (session.getAttribute("nickname") != null);
%>

<header class="sticky top-0 z-50 bg-brand text-white shadow-md">
    <div class="container mx-auto px-4 py-3 flex items-center justify-between">

        <!-- LOGO -->
        <a href="${homeUrl}" class="flex items-center gap-2">
            <span class="text-xl font-bold">Volando<span class="text-yellow-300">.uy</span></span>
        </a>

        <!-- NAV DESKTOP -->
        <nav id="nav-desktop" class="hidden md:flex items-center gap-6">
            <a href="${flightsUrl}" class="hover:text-yellow-300">Vuelos</a>
            <a href="${packagesUrl}" class="hover:text-yellow-300">Paquetes</a>
            <a href="${listUsersUrl}" class="hover:text-yellow-300">Usuarios</a>

            <% if (isCustomer) { %>
            <a href="${listPackageUrl}" class="hover:text-yellow-300">Mis paquetes</a>
            <a href="${checkBookingUrl}" class="hover:text-yellow-300">Mis reservas</a>

            <% } else if (isAirline) { %>
            <a href="${createFlightUrl}" class="hover:text-yellow-300">Crear vuelo</a>
            <a href="${createFlightRouteUrl}" class="hover:text-yellow-300">Crear ruta</a>
            <a href="${createCityAndCategoryUrl}" class="hover:text-yellow-300">Crear ciudad / categoría</a>
            <a href="${checkBookingUrl}" class="hover:text-yellow-300">Reservas</a>
            <% } %>
        </nav>

        <c:choose>
            <c:when test="${empty sessionScope.nickname}">
                <div id="header-actions" class="hidden md:flex items-center gap-3">
                    <a href="${loginUrl}" class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">Iniciar sesión</a>
                </div>
            </c:when>
            <c:otherwise>
                <div id="header-actions" class="hidden md:flex items-center gap-3">
                    <a href="${profileUrl}"
                       class="inline-flex items-center justify-center px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-center">
                        Ver Perfil
                    </a>

                    <form action="${logoutUrl}" method="post" title="Cerrar sesión de ${nickname}">
                        <button type="submit"
                                class="inline-flex items-center justify-center px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-center appearance-none">
                            Cerrar sesión
                        </button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>

        <button id="togglePanelBtn"
                class="md:hidden bg-brand text-white flex items-center justify-center text-3xl p-2">
            ☰
        </button>
    </div>
    <div id="mobilePanel"
         class="md:hidden bg-brand text-white rounded-2xl shadow-lg p-4 flex flex-col gap-2
            w-[200px] hidden absolute right-4 top-[70px] z-[60]">
        <% if (!isLogged) { %>
        <a href="${loginUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Iniciar sesión</a>
        <% } else { %>
        <% if (isCustomer) { %>
        <a href="${profileUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Mi perfil / Check-In</a>
        <a href="${flightsUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Buscar vuelos</a>
        <a href="${checkBookingUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Mis reservas</a>

        <% } else if (isAirline) { %>
        <a href="${createFlightUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Crear vuelo</a>
        <a href="${createFlightRouteUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Crear ruta</a>
        <a href="${createCityAndCategoryUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Crear ciudad/categoría</a>
        <a href="${checkBookingUrl}" class="block text-sm px-3 py-2 rounded-lg hover:bg-white/10">Reservas</a>
        <% } %>

        <form action="${logoutUrl}" method="post">
            <button type="submit"
                    class="block w-full text-left text-sm px-3 py-2 rounded-lg hover:bg-white/10">
                Cerrar sesión
            </button>
        </form>
        <% } %>
    </div>
</header>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        const btn = document.getElementById("togglePanelBtn");
        const panel = document.getElementById("mobilePanel");

        btn.addEventListener("click", () => {
            panel.classList.toggle("hidden");
            panel.classList.toggle("animate-fade-in");
        });
    });
</script>

<style>
    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(10px); }
        to { opacity: 1; transform: translateY(0); }
    }
    .animate-fade-in { animation: fadeIn 0.17s ease-out; }
</style>
