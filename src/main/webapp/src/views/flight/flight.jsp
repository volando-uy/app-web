<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vuelos</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>tailwind.config = { theme:{ extend:{ colors:{ brand:"#0B4C73" }}}}</script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body class="bg-[#f5f6fa] min-h-screen flex flex-col">
<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-2 md:px-4 py-8">
    <h1 class="text-3xl font-bold text-brand mb-8 text-center">Resultados de Vuelos</h1>

    <form class="flex flex-col md:flex-row gap-4 mb-10 bg-white rounded-2xl shadow-lg p-6 items-stretch justify-between text-xs md:text-base border border-gray-200"
          action="<c:url value='/flight/list'/>" method="get">

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Origen</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-location-dot"></i></span>
                <input type="text" id="origen" name="origen"
                       class="bg-transparent outline-none flex-1 placeholder:text-gray-400"
                       placeholder="Montevideo, Uruguay" autocomplete="off"
                       value="${fn:escapeXml(param.origen)}">
            </div>
        </div>

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Destino</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-location-dot"></i></span>
                <input type="text" id="destino" name="destino"
                       class="bg-transparent outline-none flex-1 placeholder:text-gray-400"
                       placeholder="Ciudad destino" autocomplete="off"
                       value="${fn:escapeXml(param.destino)}">
            </div>
        </div>

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Fecha de partida</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-calendar-days"></i></span>
                <input type="date" id="fecha" name="fecha"
                       class="bg-transparent outline-none flex-1 text-gray-700"
                       value="${fn:escapeXml(param.fecha)}">
            </div>
        </div>

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Aerolínea</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-plane"></i></span>
                <select id="aerolinea-filter" name="aerolinea" class="bg-transparent outline-none flex-1 text-gray-700">
                    <option value="">Todas las aerolíneas</option>
                    <c:forEach var="a" items="${airlines}">
                        <option value="${a}" <c:if test="${param.aerolinea == a}">selected</c:if>>${a}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="flex items-end gap-2">
            <button type="submit" class="px-4 py-2 bg-brand text-white rounded-lg font-bold hover:opacity-90">
                Aplicar
            </button>
            <a href="<c:url value='/flight/list'/>" class="px-4 py-2 border border-gray-300 rounded-lg">Limpiar</a>
        </div>
    </form>

    <div id="flights-list">
        <c:forEach var="v" items="${flightCards}" varStatus="st">
            <div class="bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col md:flex-row items-center mb-6 p-4 gap-4">
                <div class="flex flex-col items-center w-32">
                    <img src="${v.logo}" alt="${v.aerolinea}" class="h-8 object-contain mb-2" onerror="this.style.display='none'"/>
                    <div class="text-xs text-gray-700">
                        <c:choose>
                            <c:when test="${v.conexiones == 0}">Directo</c:when>
                            <c:otherwise>${v.conexiones} conexiones</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="text-xs text-gray-500">${v.duracion}</div>
                    <div class="text-xs text-gray-400 mt-1">${v.aerolinea}</div>
                </div>

                <div class="flex-1 flex flex-col md:flex-row items-center justify-between gap-4">
                    <div class="flex flex-col items-center">
                        <div class="text-lg font-bold">${v.salidaHora}</div>
                        <div class="text-xs text-gray-500">${v.salidaCiudad}</div>
                        <div class="text-xs text-gray-400">${v.salidaFechaPretty}</div>
                    </div>

                    <div class="flex flex-col items-center">
                        <div class="text-lg font-bold">${v.llegadaHora}</div>
                        <div class="text-xs text-gray-500">${v.llegadaCiudad}</div>
                        <c:if test="${v.nextDay}">
                            <div class="text-xs text-orange-500 font-semibold">al día siguiente</div>
                        </c:if>
                    </div>

                    <div class="flex flex-col items-center">
                        <c:url var="urlDetalles" value="/flight/list">
                            <c:param name="modal" value="${v.flightName}" />
                            <c:if test="${not empty param.origen}"><c:param name="origen" value="${param.origen}" /></c:if>
                            <c:if test="${not empty param.destino}"><c:param name="destino" value="${param.destino}" /></c:if>
                            <c:if test="${not empty param.fecha}"><c:param name="fecha" value="${param.fecha}" /></c:if>
                            <c:if test="${not empty param.aerolinea}"><c:param name="aerolinea" value="${param.aerolinea}" /></c:if>
                        </c:url>
                        <a class="text-xs text-brand border border-brand rounded px-3 py-1 mt-2 hover:bg-brand hover:text-white transition"
                           href="${urlDetalles}#modal-flight">
                            Detalles del vuelo
                        </a>
                    </div>

                    <div class="flex flex-col items-center">
                        <div class="text-right text-xs text-gray-500">${v.tipo}</div>
                        <div class="text-2xl font-bold text-orange-600">
                            <c:choose>
                                <c:when test="${v.precio == 0}">+US$0.00</c:when>
                                <c:otherwise>${v.precioStr}</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="text-xs text-gray-400 text-center">Precio por persona<br/>(impuestos y tasas incl.)</div>

                        <c:url var="urlSelect" value="/flight/list">
                            <c:param name="flight" value="${v.flightName}" />
                        </c:url>
                        <a class="mt-2 px-4 py-2 bg-orange-500 text-white rounded-lg font-bold hover:bg-orange-600 transition"
                           href="${urlSelect}">
                            Selecciona este ${v.tipo}
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <%@ include file="/src/views/components/modalFlight/modalFlight.jspf" %>
</main>

<jsp:include page="/src/views/footer/footer.jspf" />
</body>
</html>
