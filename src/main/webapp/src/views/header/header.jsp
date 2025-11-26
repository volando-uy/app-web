<%@ page import="domain.dtos.user.BaseAirlineDTO" %>
<%@ page import="domain.dtos.user.BaseCustomerDTO" %>
<%@ page import="domain.dtos.user.UserDTO" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/src/components/layout/libs.jspf" %>

<%
    UserDTO user = (UserDTO) session.getAttribute("usuario");
    boolean isCustomer = user instanceof BaseCustomerDTO;
    boolean isAirline = user instanceof BaseAirlineDTO;
%>

<header class="sticky top-0 z-50 bg-brand text-white shadow-md">
    <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <!-- Logo -->
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

        <!-- ACCIONES DESKTOP -->
        <c:choose>
            <c:when test="${empty sessionScope.nickname}">
                <a href="${loginUrl}" class="hidden md:block px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">
                    Iniciar sesión
                </a>
            </c:when>
            <c:otherwise>
                <div class="hidden md:flex items-center gap-3">
                    <a href="${profileUrl}" class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">Ver Perfil</a>

                    <form action="${logoutUrl}" method="post">
                        <button type="submit"
                                class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">
                            Cerrar sesión
                        </button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- BOTÓN HAMBURGUESA -->
        <button id="btn-menu" class="md:hidden text-white text-3xl focus:outline-none">
            ☰
        </button>
    </div>

    <!-- MENÚ MÓVIL -->
    <div id="mobile-menu" class="hidden flex-col bg-brand text-white px-6 py-4 space-y-4 md:hidden border-t border-white/10">

        <a href="${flightsUrl}" class="block hover:text-yellow-300">Vuelos</a>
        <a href="${packagesUrl}" class="block hover:text-yellow-300">Paquetes</a>
        <a href="${listUsersUrl}" class="block hover:text-yellow-300">Usuarios</a>

        <% if (isCustomer) { %>
        <a href="${listPackageUrl}" class="block hover:text-yellow-300">Mis paquetes</a>
        <a href="${checkBookingUrl}" class="block hover:text-yellow-300">Mis reservas</a>
        <% } else if (isAirline) { %>
        <a href="${createFlightUrl}" class="block hover:text-yellow-300">Crear vuelo</a>
        <a href="${createFlightRouteUrl}" class="block hover:text-yellow-300">Crear ruta</a>
        <a href="${createCityAndCategoryUrl}" class="block hover:text-yellow-300">Crear ciudad / categoría</a>
        <a href="${checkBookingUrl}" class="block hover:text-yellow-300">Reservas</a>
        <% } %>

        <c:choose>
            <c:when test="${empty sessionScope.nickname}">
                <a href="${loginUrl}" class="block hover:text-yellow-300">Iniciar sesión</a>
            </c:when>

            <c:otherwise>
                <a href="${profileUrl}" class="block hover:text-yellow-300">Ver Perfil</a>

                <form action="${logoutUrl}" method="post">
                    <button type="submit" class="block text-left hover:text-yellow-300">Cerrar sesión</button>
                </form>
            </c:otherwise>
        </c:choose>

    </div>
</header>

<script>
    document.getElementById("btn-menu").addEventListener("click", () => {
        document.getElementById("mobile-menu").classList.toggle("hidden");
    });
</script>
