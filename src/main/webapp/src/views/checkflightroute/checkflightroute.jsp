<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Consulta de Ruta de Vuelo</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        brand: "#0B4C73",
                        brandLight: "#E3F2FD"
                    }
                }
            }
        }
    </script>
</head>

<body class="bg-gray-50 min-h-screen flex flex-col text-gray-800 font-sans">
<jsp:include page="../header/header.jsp" />

<main class="flex-1 max-w-5xl mx-auto p-6 md:p-10">

    <!-- Seleccionar aerolínea -->
    <c:if test="${empty param.airline}">
        <section class="bg-white shadow-md rounded-2xl p-8 border border-gray-200">
            <h2 class="text-3xl font-semibold mb-6 text-brand text-center">Seleccione una Aerolínea</h2>
            <form method="get" action="${pageContext.request.contextPath}/flightRoute" class="space-y-6">
                <select name="airline"
                        class="w-full border border-gray-300 rounded-lg p-3 bg-white text-gray-800 font-medium focus:ring-2 focus:ring-brand focus:border-brand cursor-pointer transition">
                    <c:forEach var="a" items="${airlines}">
                        <option value="${a.nickname}">${a.name}</option>
                    </c:forEach>
                </select>
                <div class="text-center">
                    <button type="submit"
                            class="bg-brand hover:bg-sky-900 text-white font-medium px-8 py-3 rounded-lg transition transform hover:scale-[1.02]">
                        Ver rutas
                    </button>
                </div>
            </form>
        </section>
    </c:if>

    <!-- Rutas de la aerolínea -->
    <c:if test="${not empty param.airline and empty param.route}">
        <section class="bg-white shadow-md rounded-2xl p-8 border border-gray-200">
            <h2 class="text-2xl font-semibold mb-6 text-center">
                Rutas confirmadas de <span class="text-brand">${airlineName}</span>
            </h2>

            <c:choose>
                <c:when test="${not empty routes}">
                    <div class="overflow-x-auto rounded-xl border border-gray-200">
                        <table class="w-full text-sm text-left">
                            <thead class="bg-brandLight text-brand uppercase text-xs font-semibold">
                            <tr>
                                <th class="px-6 py-3 border-b">Nombre</th>
                                <th class="px-6 py-3 border-b">Origen</th>
                                <th class="px-6 py-3 border-b">Destino</th>
                                <th class="px-6 py-3 border-b text-center">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="r" items="${routes}">
                                <tr class="hover:bg-gray-50 transition">
                                    <td class="border-b px-6 py-3">${r.name}</td>
                                    <td class="border-b px-6 py-3">${r.originAeroCode}</td>
                                    <td class="border-b px-6 py-3">${r.destinationAeroCode}</td>
                                    <td class="border-b px-6 py-3 text-center">
                                        <form method="get" action="${pageContext.request.contextPath}/flightRoute">
                                            <input type="hidden" name="airline" value="${param.airline}">
                                            <input type="hidden" name="route" value="${r.name}">
                                            <button type="submit" class="text-brand font-medium hover:underline">
                                                Ver detalles
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-gray-600 text-center py-4">No hay rutas confirmadas para esta aerolínea.</p>
                </c:otherwise>
            </c:choose>

            <div class="mt-6 text-center">
                <a href="${pageContext.request.contextPath}/flightRoute"
                   class="inline-block text-brand font-medium hover:underline">
                    ← Volver a aerolíneas
                </a>
            </div>
        </section>
    </c:if>

    <!-- Detalle de ruta -->
    <c:if test="${not empty param.route and empty param.flight}">
        <section class="bg-white shadow-md rounded-2xl p-8 border border-gray-200">
            <h2 class="text-3xl font-semibold mb-4 text-brand text-center">${route.name}</h2>

            <div class="space-y-3 text-gray-700">
                <p><strong>Descripción:</strong> ${route.description}</p>
                <p><strong>Origen:</strong> ${route.originAeroCode}</p>
                <p><strong>Destino:</strong> ${route.destinationAeroCode}</p>

                <div>
                    <strong>Categorías:</strong>
                    <div class="mt-1 flex flex-wrap gap-2">
                        <c:forEach var="cat" items="${route.categoriesNames}">
                            <span class="bg-brand text-white px-3 py-1 rounded-full text-xs">${cat}</span>
                        </c:forEach>
                    </div>
                </div>

                <c:if test="${not empty route.image}">
                    <div class="mt-6 flex justify-center">
                        <img src="${route.image}" alt="Imagen de la ruta"
                             class="rounded-xl shadow-lg w-full max-w-md object-cover">
                    </div>
                </c:if>

                <div class="mt-6">
                    <h3 class="text-lg font-semibold mb-2 text-brand">Vuelos asociados:</h3>
                    <c:choose>
                        <c:when test="${not empty route.flightsNames}">
                            <ul class="list-disc list-inside space-y-1">
                                <c:forEach var="f" items="${route.flightsNames}">
                                    <li>
                                        <a href="${pageContext.request.contextPath}/flightRoute?airline=${param.airline}&route=${param.route}&flight=${f}"
                                           class="text-brand hover:underline">
                                                ${f}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <p class="text-gray-600">No hay vuelos asociados a esta ruta.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="mt-6 text-center">
                <a href="${pageContext.request.contextPath}/flightRoute?airline=${param.airline}"
                   class="inline-block text-brand font-medium hover:underline">
                    ← Volver a rutas
                </a>
            </div>
        </section>
    </c:if>

    <!-- Detalle del vuelo (JSPF incluido dinámicamente) -->
    <c:if test="${not empty param.flight}">
        <jsp:include page="../../components/flightDetails/flightDetails.jsp" />
    </c:if>

</main>

<jsp:include page="../footer/footer.jspf" />
</body>
</html>
