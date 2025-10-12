<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vuelos</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: { brand: "#0B4C73" } } }
        }
    </script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body class="bg-[#f5f6fa] min-h-screen flex flex-col">
<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-2 md:px-4 py-8">
    <h1 class="text-3xl font-bold text-brand mb-8 text-center">Resultados de Vuelos</h1>

    <!-- Filtros -->
    <form class="flex flex-col md:flex-row gap-4 mb-10 bg-white rounded-2xl shadow-lg p-6 items-stretch justify-between text-xs md:text-base border border-gray-200">

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Origen</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-location-dot"></i></span>
                <input type="text" id="origen" class="bg-transparent outline-none flex-1 placeholder:text-gray-400" placeholder="Montevideo, Uruguay" autocomplete="off">
            </div>
        </div>

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Destino</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-location-dot"></i></span>
                <input type="text" id="destino" class="bg-transparent outline-none flex-1 placeholder:text-gray-400" placeholder="Ciudad destino" autocomplete="off">
            </div>
        </div>

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Fecha de partida</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-calendar-days"></i></span>
                <input type="date" id="fecha" class="bg-transparent outline-none flex-1 text-gray-700">
            </div>
        </div>

        <div class="flex flex-col flex-1 min-w-[180px] justify-end">
            <label class="font-bold text-[#ff8000] mb-2 uppercase tracking-wide text-sm md:text-base">Aerolínea</label>
            <div class="flex items-center bg-[#f5f6fa] rounded-lg px-3 border border-gray-200 h-[48px]">
                <span class="text-[#ff8000] mr-2"><i class="fa-solid fa-plane"></i></span>
                <select id="aerolinea-filter" class="bg-transparent outline-none flex-1 text-gray-700">
                    <option value="">Todas las aerolíneas</option>
                    <c:forEach var="a" items="${airlines}">
                        <option value="${a}">${a}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
    </form>

    <!-- Lista de vuelos -->
    <div id="flights-list">
        <c:forEach var="v" items="${flightCards}" varStatus="st">
            <div class="bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col md:flex-row items-center mb-6 p-4 gap-4"
                 data-card
                 data-idx="${st.index}"
                 data-aero="${v.aerolinea}"
                 data-date="${v.salidaFechaISO}">
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
                        <button class="text-xs text-brand border border-brand rounded px-3 py-1 mt-2 hover:bg-brand hover:text-white transition" data-details>
                            Detalles del vuelo
                        </button>
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
                        <button class="mt-2 px-4 py-2 bg-orange-500 text-white rounded-lg font-bold hover:bg-orange-600 transition">
                            Selecciona este ${v.tipo}
                        </button>
                    </div>
                </div>

                <!-- Datos JSON -->
                <script type="application/json" id="legs-${st.index}">${v.legsJson}</script>
                <script type="application/json" id="meta-${st.index}">${v.metaJson}</script>
            </div>
        </c:forEach>
    </div>

    <div id="modal-root"></div>
</main>

<jsp:include page="/src/views/footer/footer.jspf" />

<script defer src="<%=ctx%>/src/views/flight/flight.js"></script>
</body>
</html>
