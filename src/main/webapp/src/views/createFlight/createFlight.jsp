<%@ page import="domain.dtos.user.BaseAirlineDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="domain.dtos.flightroute.BaseFlightRouteDTO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/src/components/layout/libs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<%
    request.setAttribute("pageTitle", "Crear Vuelo - Volando.uy");
%>
<%@ include file="/src/components/layout/head.jspf" %>

<body class="bg-gray-100 min-h-screen flex flex-col">
<jsp:include page="../header/header.jsp"/>
<main class="flex-1 container mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-brand mb-6">Crear vuelo</h1>

    <form id="flightForm" method="post" action="createFlight" enctype="multipart/form-data"
          class="bg-white rounded-xl shadow p-6 space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
                <input name="name" type="text" class="w-full border rounded-lg px-3 py-2" required>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Duración (minutos)*</label>
                <input name="duration" type="number" class="w-full border rounded-lg px-3 py-2">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Asientos Ejecutivo</label>
                <input name="maxBusinessSeats" type="number" class="w-full border rounded-lg px-3 py-2">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Asientos Turista</label>
                <input name="maxEconomySeats" type="number" class="w-full border rounded-lg px-3 py-2">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Fecha de alta *</label>
                <input name="createdAt" type="datetime-local" class="w-full border rounded-lg px-3 py-2" required>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Fecha del vuelo *</label>
                <input name="departureTime" type="datetime-local" class="w-full border rounded-lg px-3 py-2" required>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Aerolínea</label>
                <input type="text" name="airlineNickname" value="${airlineNickname}" disabled
                       class="w-full border rounded-md px-3 py-2 text-sm bg-gray-100 text-gray-600 cursor-not-allowed" />
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Ruta de vuelo *</label>
                <select name="flightRouteName" required
                        class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand">
                    <option value="" disabled selected>Selecciona una ruta de vuelo</option>
                    <c:forEach var="route" items="${flightRoutes}">
                        <option value="${route.name}">${route.name}</option>
                    </c:forEach>
                </select>
            </div>


            <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1">Imagen</label>
                <input name="image" type="file" accept="image/*" class="w-full border rounded-lg px-3 py-2">
            </div>
        </div>
        <button type="submit" class="px-4 py-2 bg-brand text-white rounded-lg hover:brightness-110">
            Crear vuelo
        </button>
    </form>
    <div id="responseMsg" class="mt-4 text-sm"></div>
</main>
<jsp:include page="../footer/footer.jspf"/>
<%@ include file="/src/components/layout/scripts.jspf" %>
<script src="createFlight.js"></script>

</body>
</html>

