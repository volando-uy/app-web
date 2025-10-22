<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<!DOCTYPE html>
<html lang="es">
<%
    request.setAttribute("pageTitle", "Panel de Reservas - Volando.uy");
%>

<%@ include file="/src/components/layout/libs.jspf" %>
<%@ include file="/src/components/layout/head.jspf" %>

<body class="bg-gray-100 min-h-screen flex flex-col">

<jsp:include page="/src/views/header/header.jsp"/>

<main class="flex-1 container mx-auto px-4 py-8">

    <fmt:setLocale value="${uiLocale != null ? uiLocale : 'es_UY'}"/>

    <div class="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-8">

        <section class="lg:col-span-3 space-y-6">
            <div class="flex items-center justify-between">
                <h1 class="text-2xl font-bold text-sky-900">Consulta de Reserva</h1>
                <span class="text-xs rounded-full px-3 py-1 bg-sky-50 text-sky-700 uppercase tracking-wide">
                    <c:out value="${tipoUsuario}"/>
                </span>
            </div>

            <div class="bg-white rounded-xl shadow p-4">
                <div class="flex flex-wrap items-center gap-2 text-sm">
                    <span class="text-gray-500">Aerolínea</span>
                    <span class="px-2 py-0.5 rounded-full bg-gray-100 text-gray-800">
                        <c:out value="${empty airlineName ? '-': airlineName}"/>
                    </span>

                    <span class="text-gray-300">/</span>

                    <span class="text-gray-500">Ruta</span>
                    <span class="px-2 py-0.5 rounded-full bg-gray-100 text-gray-800">
                        <c:out value="${empty routeName ? '-': routeName}"/>
                    </span>

                    <span class="text-gray-300">/</span>

                    <span class="text-gray-500">Vuelo</span>
                    <span class="px-2 py-0.5 rounded-full bg-gray-100 text-gray-800">
                        <c:out value="${empty flightName ? '-': flightName}"/>
                    </span>
                </div>
            </div>

            <c:if test="${tipoUsuario == 'cliente' && empty airlineName}">
                <div class="bg-white rounded-xl shadow p-6">
                    <h2 class="font-semibold mb-4">Elegí una aerolínea</h2>
                    <c:choose>
                        <c:when test="${empty airlines}">
                            <p class="text-sm text-gray-500">No hay aerolíneas disponibles.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="divide-y">
                                <c:forEach var="a" items="${airlines}">
                                    <li class="py-2 flex items-center justify-between">
                                        <div class="font-medium">
                                            <c:out value="${empty a.name ? a.nickname : a.name}"/>
                                        </div>
                                        <div>
                                            <c:url var="next" value="/booking/check">
                                                <c:param name="airline" value="${a.nickname}"/>
                                            </c:url>
                                            <a class="text-blue-600 hover:underline" href="${next}">Ver rutas</a>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <c:if test="${not empty airlineName && empty routeName}">
                <div class="bg-white rounded-xl shadow p-6">
                    <h2 class="font-semibold mb-4">Elegí una ruta</h2>
                    <c:choose>
                        <c:when test="${empty routes}">
                            <p class="text-sm text-gray-500">No hay rutas para mostrar.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="grid sm:grid-cols-2 gap-2">
                                <c:forEach var="r" items="${routes}">
                                    <li class="p-3 rounded-lg border hover:shadow-sm transition">
                                        <div class="flex items-center justify-between">
                                            <div class="font-medium">${r.name}</div>
                                            <c:if test="${not empty r.status}">
                                                <span class="text-xs px-2 py-0.5 rounded bg-gray-100 text-gray-600">${r.status}</span>
                                            </c:if>
                                        </div>
                                        <div class="mt-2">
                                            <c:url var="next" value="/booking/check">
                                                <c:param name="airline" value="${airlineName}"/>
                                                <c:param name="route"   value="${r.name}"/>
                                            </c:url>
                                            <a class="text-blue-600 hover:underline text-sm" href="${next}">Ver vuelos</a>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${tipoUsuario == 'cliente'}">
                        <div class="mt-4">
                            <c:url var="back" value="/booking/check"/>
                            <a class="text-sm text-gray-600 hover:underline" href="${back}">Volver a aerolíneas</a>
                        </div>
                    </c:if>
                </div>
            </c:if>

            <c:if test="${not empty airlineName && not empty routeName && empty flightName}">
                <div class="bg-white rounded-xl shadow p-6">
                    <h2 class="font-semibold mb-4">Elegí un vuelo</h2>
                    <c:choose>
                        <c:when test="${empty flightsView}">
                            <p class="text-sm text-gray-500">No hay vuelos para esta ruta.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="space-y-2">
                                <c:forEach var="f" items="${flightsView}">
                                    <li class="flex items-center justify-between p-3 rounded-lg border hover:shadow-sm">
                                        <div class="flex flex-col">
                                            <span class="font-medium">${f.name}</span>
                                            <span class="text-xs text-gray-500">
                                                <fmt:formatDate value="${f.departure}" type="both" dateStyle="medium" timeStyle="short"/>
                                            </span>
                                        </div>
                                        <div>
                                            <c:url var="next" value="/booking/check">
                                                <c:param name="airline" value="${airlineName}"/>
                                                <c:param name="route"   value="${routeName}"/>
                                                <c:param name="flight"  value="${f.name}"/>
                                            </c:url>
                                            <a class="text-blue-600 hover:underline" href="${next}">Ver reservas</a>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>

                    <div class="mt-4">
                        <c:url var="back" value="/booking/check">
                            <c:param name="airline" value="${airlineName}"/>
                        </c:url>
                        <a class="text-sm text-gray-600 hover:underline" href="${back}">Volver a rutas</a>
                    </div>
                </div>
            </c:if>

            <!-- Paso 3A (aerolínea): reservas del vuelo -->
            <c:if test="${tipoUsuario == 'aerolinea' && empty booking && not empty flightName}">
                <div class="bg-white rounded-xl shadow p-6">
                    <h2 class="font-semibold mb-4">Reservas del vuelo <span class="font-normal">${flightName}</span></h2>
                    <c:choose>
                        <c:when test="${empty bookingsView}">
                            <p class="text-sm text-gray-500">Este vuelo no tiene reservas.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="overflow-auto rounded border">
                                <table class="w-full text-sm">
                                    <thead class="bg-gray-50">
                                    <tr class="text-left">
                                        <th class="px-3 py-2">ID</th>
                                        <th class="px-3 py-2">Dueño</th>
                                        <th class="px-3 py-2">Pasajeros</th>
                                        <th class="px-3 py-2">Asiento</th>
                                        <th class="px-3 py-2">Total</th>
                                        <th class="px-3 py-2">Creada</th>
                                        <th class="px-3 py-2"></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="b" items="${bookingsView}">
                                        <tr class="border-t align-top">
                                            <td class="px-3 py-2">${b.id}</td>
                                            <td class="px-3 py-2">${b.customerNickname}</td>
                                            <td class="px-3 py-2">
                                                <div class="text-xs text-gray-600">
                                                    <strong>${b.passengerCount}</strong> pasajero(s)
                                                </div>
                                                <c:if test="${b.passengerCount > 0}">
                                                    <details class="mt-1">
                                                        <summary class="cursor-pointer text-xs text-blue-700">Ver lista</summary>
                                                        <ul class="mt-1 pl-4 list-disc text-xs text-gray-700">
                                                            <c:forEach var="p" items="${b.passengers}">
                                                                <li>
                                                                        ${p.name} ${p.surname}
                                                                    <c:if test="${not empty p.seatNumber}"> — asiento ${p.seatNumber}</c:if>
                                                                    <c:if test="${not empty p.docType}"> — ${p.docType}</c:if>
                                                                    <c:if test="${not empty p.numDoc}"> ${p.numDoc}</c:if>
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </details>
                                                </c:if>
                                            </td>
                                            <td class="px-3 py-2">${b.seatType}</td>
                                            <td class="px-3 py-2"><fmt:formatNumber value="${b.totalPrice}" type="currency"/></td>
                                            <td class="px-3 py-2">
                                                <fmt:formatDate value="${b.createdAt}" type="both" dateStyle="medium" timeStyle="short"/>
                                            </td>
                                            <td class="px-3 py-2">
                                                <c:url var="next" value="/booking/check">
                                                    <c:param name="route"  value="${routeName}"/>
                                                    <c:param name="flight" value="${flightName}"/>
                                                    <c:param name="booking" value="${b.id}"/>
                                                </c:url>
                                                <a class="text-blue-600 hover:underline" href="${next}">Ver detalle</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="mt-4">
                        <c:url var="back" value="/booking/check">
                            <c:param name="route" value="${routeName}"/>
                        </c:url>
                        <a class="text-sm text-gray-600 hover:underline" href="${back}">Volver a vuelos</a>
                    </div>
                </div>
            </c:if>

            <c:if test="${tipoUsuario == 'cliente' && empty booking && not empty flightName}">
                <div class="bg-white rounded-xl shadow p-6">
                    <h2 class="font-semibold mb-4">Tus reservas para el vuelo <span class="font-normal">${flightName}</span></h2>

                    <c:choose>
                        <c:when test="${empty myBookingsView}">
                            <div class="bg-yellow-50 text-yellow-800 rounded-xl p-4 shadow">
                                No tenés reserva para este vuelo.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="overflow-auto rounded border">
                                <table class="w-full text-sm">
                                    <thead class="bg-gray-50">
                                    <tr class="text-left">
                                        <th class="px-3 py-2">ID</th>
                                        <th class="px-3 py-2">Pasajeros</th>
                                        <th class="px-3 py-2">Asiento</th>
                                        <th class="px-3 py-2">Total</th>
                                        <th class="px-3 py-2">Creada</th>
                                        <th class="px-3 py-2"></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="b" items="${myBookingsView}">
                                        <tr class="border-t align-top">
                                            <td class="px-3 py-2">${b.id}</td>
                                            <td class="px-3 py-2">
                                                <div class="text-xs text-gray-600">
                                                    <strong>${b.passengerCount}</strong> pasajero(s)
                                                </div>
                                                <c:if test="${b.passengerCount > 0}">
                                                    <details class="mt-1">
                                                        <summary class="cursor-pointer text-xs text-blue-700">Ver lista</summary>
                                                        <ul class="mt-1 pl-4 list-disc text-xs text-gray-700">
                                                            <c:forEach var="p" items="${b.passengers}">
                                                                <li>
                                                                        ${p.name} ${p.surname}
                                                                    <c:if test="${not empty p.seatNumber}"> — asiento ${p.seatNumber}</c:if>
                                                                    <c:if test="${not empty p.docType}"> — ${p.docType}</c:if>
                                                                    <c:if test="${not empty p.numDoc}"> ${p.numDoc}</c:if>
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </details>
                                                </c:if>
                                            </td>
                                            <td class="px-3 py-2">${b.seatType}</td>
                                            <td class="px-3 py-2"><fmt:formatNumber value="${b.totalPrice}" type="currency"/></td>
                                            <td class="px-3 py-2">
                                                <fmt:formatDate value="${b.createdAt}" type="both" dateStyle="medium" timeStyle="short"/>
                                            </td>
                                            <td class="px-3 py-2">
                                                <c:url var="next" value="/booking/check">
                                                    <c:param name="airline" value="${airlineName}"/>
                                                    <c:param name="route"   value="${routeName}"/>
                                                    <c:param name="flight"  value="${flightName}"/>
                                                    <c:param name="booking" value="${b.id}"/>
                                                </c:url>
                                                <a class="text-blue-600 hover:underline" href="${next}">Ver detalle</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="mt-4">
                        <c:url var="back" value="/booking/check">
                            <c:param name="airline" value="${airlineName}"/>
                            <c:param name="route"   value="${routeName}"/>
                        </c:url>
                        <a class="text-sm text-gray-600 hover:underline" href="${back}">Volver a vuelos</a>
                    </div>
                </div>
            </c:if>

            <c:if test="${not empty booking}">
                <div class="bg-white rounded-xl shadow p-6 space-y-6">
                    <h2 class="font-semibold">Detalle de reserva</h2>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                        <div class="p-3 rounded-lg bg-gray-50">
                            <div class="text-xs text-gray-500">ID</div>
                            <div class="font-medium">${booking.id}</div>
                        </div>
                        <div class="p-3 rounded-lg bg-gray-50">
                            <div class="text-xs text-gray-500">Cliente</div>
                            <div class="font-medium">${booking.customerNickname}</div>
                        </div>
                        <div class="p-3 rounded-lg bg-gray-50">
                            <div class="text-xs text-gray-500">Tipo de asiento</div>
                            <div class="font-medium">${booking.seatType}</div>
                        </div>
                        <div class="p-3 rounded-lg bg-gray-50">
                            <div class="text-xs text-gray-500">Total</div>
                            <div class="font-medium">
                                <fmt:formatNumber value="${booking.totalPrice}" type="currency"/>
                            </div>
                        </div>
                        <div class="p-3 rounded-lg bg-gray-50">
                            <div class="text-xs text-gray-500">Creada</div>
                            <div class="font-medium">
                                <fmt:formatDate value="${bookingCreatedAtDate}" type="both" dateStyle="medium" timeStyle="short"/>
                            </div>
                        </div>
                    </div>

                    <div>
                        <h3 class="mb-2 font-semibold text-sm">Pasajeros</h3>
                        <div class="overflow-auto rounded border">
                            <table class="w-full text-xs md:text-sm">
                                <thead class="bg-gray-100 text-gray-600">
                                <tr>
                                    <th class="px-3 py-2 text-left">Nombre</th>
                                    <th class="px-3 py-2 text-left">Apellido</th>
                                    <th class="px-3 py-2 text-left">Tipo Doc</th>
                                    <th class="px-3 py-2 text-left">Documento</th>
                                    <th class="px-3 py-2 text-left">Asiento</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty tickets}">
                                        <c:forEach var="t" items="${tickets}">
                                            <tr class="border-t">
                                                <td class="px-3 py-2">${t.name}</td>
                                                <td class="px-3 py-2">${t.surname}</td>
                                                <td class="px-3 py-2">${t.docType}</td>
                                                <td class="px-3 py-2">${t.numDoc}</td>
                                                <td class="px-3 py-2">
                                                    <c:out value="${empty t.seatNumber ? '-' : t.seatNumber}"/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="5" class="px-3 py-4 text-center text-gray-500">
                                                Sin tickets asociados.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="pt-2">
                        <c:choose>
                            <c:when test="${tipoUsuario == 'aerolinea'}">
                                <c:url var="back" value="/booking/check">
                                    <c:param name="route"  value="${routeName}"/>
                                    <c:param name="flight" value="${flightName}"/>
                                </c:url>
                            </c:when>
                            <c:otherwise>
                                <c:url var="back" value="/booking/check">
                                    <c:param name="airline" value="${airlineName}"/>
                                    <c:param name="route"   value="${routeName}"/>
                                </c:url>
                            </c:otherwise>
                        </c:choose>
                        <a class="text-sm text-gray-600 hover:underline" href="${back}">Volver</a>
                    </div>
                </div>
            </c:if>
        </section>

        <aside class="space-y-4">
            <div class="bg-white rounded-xl shadow p-4">
                <h3 class="font-semibold mb-2">Ayuda</h3>
                <ul class="text-sm text-gray-600 list-disc pl-5 space-y-1">
                    <li>Primero elegís Aerolínea → Ruta → Vuelo.</li>
                    <li>Si sos Aerolínea: ves todas las reservas de ese vuelo (con pasajeros).</li>
                    <li>Si sos Cliente: se listan todas tus reservas de ese vuelo; podés abrir el detalle.</li>
                </ul>
            </div>
        </aside>

    </div>
</main>

<jsp:include page="/src/views/footer/footer.jspf"/>
<%@ include file="/src/components/layout/scripts.jspf" %>


</body>
</html>
