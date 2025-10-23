<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Consulta de Rutas y Vuelos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = { theme:{ extend:{ colors:{ brand:"#0B4C73" }}} }
    </script>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<%@ include file="/src/components/layout/libs.jspf" %>

<body class="bg-[#f5f6fa] min-h-screen flex flex-col">

<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-2 md:px-4 py-8">
    <h1 class="text-3xl font-bold text-brand mb-6 text-center">Consulta de Rutas y Vuelos</h1>

    <c:url var="filtersAction" value="/flight/list"/>
    <c:url var="reservationBase" value="/reservas"/>

    <%@ include file="/src/components/filters/filters.jspf" %>

    <c:if test="${empty selectedRouteName}">
        <p class="text-sm text-gray-600 mb-4">
            Rutas confirmadas encontradas:
            <strong><c:out value="${fn:length(routes)}"/></strong>
        </p>

        <c:choose>
            <c:when test="${empty routes}">
                <div class="bg-white border rounded-xl p-6 shadow-sm text-gray-600">
                    No hay rutas para mostrar.
                </div>
            </c:when>
            <c:otherwise>
                <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
                    <c:forEach var="r" items="${routes}">
                        <%@ include file="/src/components/routeCard/route_card.jspf" %>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${not empty selectedRoute}">
        <div class="bg-white rounded-2xl shadow-lg p-6 border border-gray-200 mb-6">
            <div class="flex flex-col md:flex-row gap-6">
                <div class="md:w-1/3">
                    <div class="aspect-video bg-gray-50 border rounded-lg overflow-hidden flex items-center justify-center">
                        <c:choose>
                            <c:when test="${not empty selectedRoute.image}">
                                <img src="${rootUrl}image?resourceClassName=${selectedRoute.getClass().getSimpleName()}&key=${selectedRoute.getName()}"
                                     alt="${fn:escapeXml(selectedRoute.name)}"
                                     class="w-full h-full object-cover"/>
                            </c:when>
                            <c:otherwise><span class="text-gray-400">Sin imagen</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="md:flex-1">
                    <h2 class="text-2xl font-bold text-gray-900 mb-1">
                            ${fn:escapeXml(selectedRoute.name)}
                    </h2>
                    <p class="text-gray-600 mb-2">
                            ${fn:escapeXml(selectedRoute.description)}
                    </p>
                    <p class="text-sm text-gray-600">
                        Origen/Destino:
                        <strong>${fn:escapeXml(selectedRoute.originAeroCode)} → ${fn:escapeXml(selectedRoute.destinationAeroCode)}</strong><br/>
                        Aerolínea: <strong>${fn:escapeXml(selectedRoute.airlineNickname)}</strong><br/>
                        Precio Turista:
                        <strong><fmt:formatNumber value="${selectedRoute.priceTouristClass}" type="currency" currencySymbol="US$"/></strong><br/>
                        Precio Ejecutivo:
                        <strong><fmt:formatNumber value="${selectedRoute.priceBusinessClass}" type="currency" currencySymbol="US$"/></strong>
                    </p>
                </div>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty selectedRouteName and empty selectedFlight}">
        <p class="text-sm text-gray-600 mb-4">
            Vuelos para
            <strong>${fn:escapeXml(selectedRoute.name)}</strong>:
            <strong><c:out value="${fn:length(flights)}"/></strong>
        </p>

        <c:choose>
            <c:when test="${empty flights}">
                <div class="bg-white border rounded-xl p-6 shadow-sm text-gray-600">
                    No hay vuelos para mostrar.
                </div>
            </c:when>
            <c:otherwise>
                <div class="space-y-3">
                    <c:forEach var="v" items="${flights}">
                        <%@ include file="/src/components/flight_card/flight_card.jspf" %>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${not empty selectedFlight}">
        <c:set var="sfDepRaw" value="${selectedFlight.departureTime}" />
        <c:set var="sfTimeHHmm"
               value="${empty sfDepRaw ? '' : (fn:contains(sfDepRaw,'T') ? fn:substring(fn:substringAfter(sfDepRaw,'T'),0,5) : fn:substring(sfDepRaw,0,5))}" />
        <c:set var="sfDateStr" value="${empty sfDepRaw ? '' : fn:substringBefore(sfDepRaw,'T')}" />
        <c:choose>
            <c:when test="${not empty sfDateStr}">
                <fmt:parseDate value="${sfDateStr}" pattern="yyyy-MM-dd" var="sfDate" />
                <fmt:formatDate value="${sfDate}" pattern="dd/MM/yyyy" var="sfDateHuman"/>
            </c:when>
            <c:otherwise>
                <c:set var="sfDateHuman" value="—"/>
            </c:otherwise>
        </c:choose>

        <div class="bg-white rounded-2xl shadow-lg p-6 border border-gray-200 mt-6">
            <div class="flex flex-col md:flex-row gap-6">
                <div class="md:w-1/3">
                    <div class="w-full aspect-video bg-gray-50 border border-gray-200 rounded-lg overflow-hidden flex items-center justify-center">
                        <c:choose>
                            <c:when test="${not empty selectedFlight.image}">
                                <img src="${rootUrl}image?resourceClassName=${selectedFlight.getClass().getSimpleName()}&key=${selectedFlight.getName()}"
                                     alt="Imagen del vuelo"
                                     class="w-full h-full object-contain"/>
                            </c:when>
                            <c:otherwise><span class="text-gray-400">Sin imagen</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="md:flex-1">
                    <h2 class="text-2xl font-bold text-gray-900 mb-2">
                            ${fn:escapeXml(selectedFlight.name)}
                    </h2>

                    <div class="mb-3">
                      <span class="inline-flex items-center gap-2 text-[13px] leading-none text-gray-700 bg-gray-100 border border-gray-200 rounded-md px-2 py-1">
                        <i class="fa-solid fa-calendar-days text-[#ff8000]"></i>
                        <span>Salida:</span>
                        <strong class="text-gray-900">${sfDateHuman}</strong>
                      </span>
                    </div>

                    <p class="text-gray-600">
                        Aerolínea: <strong>${fn:escapeXml(selectedFlight.airlineNickname)}</strong><br/>
                        Ruta: <strong>${fn:escapeXml(selectedFlight.flightRouteName)}</strong><br/>
                        Asientos Económicos:
                        <strong>${empty selectedFlight.maxEconomySeats ? '—' : selectedFlight.maxEconomySeats}</strong><br/>
                        Asientos Business:
                        <strong>${empty selectedFlight.maxBusinessSeats ? '—' : selectedFlight.maxBusinessSeats}</strong><br/>
                        Duración:
                        <strong>${empty selectedFlight.duration ? '—' : selectedFlight.duration} min</strong><br/>
                        Precio Turista:
                        <strong><fmt:formatNumber value="${selectedRoute.priceTouristClass}" type="currency" currencySymbol="US$"/></strong><br/>
                        Precio Ejecutivo:
                        <strong><fmt:formatNumber value="${selectedRoute.priceBusinessClass}" type="currency" currencySymbol="US$"/></strong>
                    </p>

                    <c:url var="backToListHref" value="/flight/list">
                        <c:param name="airline" value="${selectedAirline}"/>
                        <c:param name="category" value="${selectedCategory}"/>
                        <c:param name="route" value="${selectedRouteName}"/>
                    </c:url>

                    <c:url var="reserveHref" value="/reservas">
                        <c:param name="flight" value="${selectedFlight.name}"/>
                    </c:url>

                    <div class="mt-4 flex gap-2">
                        <a class="bg-[#ff8000] hover:bg-[#e67300] text-white px-6 py-2.5 rounded-lg text-sm font-semibold"
                           href="${reserveHref}">
                            Reservar este vuelo
                        </a>
                        <a class="border border-brand text-brand hover:bg-brand hover:text-white px-5 py-2 rounded-lg text-sm font-semibold"
                           href="${backToListHref}">
                            Volver a todos los vuelos
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <jsp:include page="/src/components/leftPanel/leftPanel.jsp"/>
</main>

<jsp:include page="/src/views/footer/footer.jspf"/>
</body>
</html>
