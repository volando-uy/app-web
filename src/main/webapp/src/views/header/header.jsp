<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/src/components/layout/libs.jspf" %>
<%@ page import="com.labpa.appweb.user.UserDTO" %>
<%@ page import="com.labpa.appweb.user.BaseCustomerDTO" %>
<%@ page import="com.labpa.appweb.user.BaseAirlineDTO" %>

<%
    UserDTO user = (UserDTO) session.getAttribute("usuario");

    boolean isCustomer = user instanceof BaseCustomerDTO;
    boolean isAirline = user instanceof BaseAirlineDTO;
%>

<header class="sticky top-0 z-50 bg-brand text-white shadow-md">
    <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <a href="${homeUrl}" class="flex items-center gap-2">
            <span class="text-xl font-bold">Volando<span class="text-yellow-300">.uy</span></span>
        </a>

        <nav id="nav-desktop" class="hidden md:flex items-center gap-6">
            <a href="${flightsUrl}" class="hover:text-yellow-300">Vuelos</a>
            <a href="${packagesUrl}" class="hover:text-yellow-300">Paquetes</a>
            <a href="${listUsersUrl}" class="hover:text-yellow-300"> Usuarios </a>

            <%
                if (isCustomer) {
            %>
            <!--Vuelos, paquetes, y consulta de reserva -->
            <a href="${listPackageUrl}" class="hover:text-yellow-300">Mis paquetes</a>
            <a href="${checkBookingUrl}" class="hover:text-yellow-300">Mis reservas</a>

            <%} else if (isAirline) {%>
            <!--Crear vuelo, crear ruta de vuelo, crear cuidad y categoria, consulta de reserva -->
            <a href="${createFlightUrl}" class="hover:text-yellow-300">Crear vuelo</a>
            <a href="${createFlightRouteUrl}" class="hover:text-yellow-300">Crear ruta</a>
            <a href="${createCityAndCategoryUrl}" class="hover:text-yellow-300">Crear ciudad / categoría</a>
            <a href="${checkBookingUrl}" class="hover:text-yellow-300">Reservas</a>


            <%
                }
            %>

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

    </div>

</header>
