<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Paquetes</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>tailwind.config={theme:{extend:{colors:{brand:"#0B4C73"}}}}</script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body class="bg-gray-100 min-h-screen flex flex-col">

<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold text-brand mb-8 text-center">Paquetes disponibles</h1>

    <c:if test="${not empty sessionScope.toastMessage}">
        <div class="max-w-3xl mx-auto mb-6 p-3 rounded-lg
                  ${sessionScope.toastType == 'success' ? 'bg-emerald-50 text-emerald-800 border border-emerald-200' :
                    sessionScope.toastType == 'warning' ? 'bg-yellow-50 text-yellow-800 border border-yellow-200' :
                    sessionScope.toastType == 'error'   ? 'bg-red-50 text-red-700 border border-red-200' :
                                                          'bg-blue-50 text-blue-800 border border-blue-200'}">
                ${sessionScope.toastMessage}
        </div>
        <c:remove var="toastMessage" scope="session"/>
        <c:remove var="toastType" scope="session"/>
    </c:if>

    <c:if test="${requestScope.hadError}">
        <div class="max-w-3xl mx-auto mb-6 p-3 rounded-lg bg-red-50 text-red-700 border border-red-200">
            Hubo un problema al cargar algunas rutas. Mostramos lo que pudimos.
        </div>
    </c:if>

    <c:url var="packagesListUrl" value="/packages/list"/>

    <!-- Buscador (server-side) -->
    <form class="mb-8 max-w-xl mx-auto flex gap-2" action="${packagesListUrl}" method="get">
        <input name="q" value="${fn:escapeXml(param.q)}"
               class="flex-1 px-3 py-2 rounded-lg border border-gray-300"
               placeholder="Buscar paquete o ruta…"/>
        <button class="px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110" type="submit">
            Buscar
        </button>
        <a class="px-4 py-2 rounded-lg border border-gray-300" href="${packagesListUrl}">Limpiar</a>
    </form>

    <div class="text-xs text-gray-500 text-center mb-6">
        Cantidad de paquetes: ${fn:length(requestScope.pkgs)}
    </div>

    <!-- Lista de paquetes -->
    <c:choose>
        <c:when test="${empty requestScope.pkgs}">
            <p class="text-center text-gray-500">No hay paquetes disponibles.</p>
        </c:when>
        <c:otherwise>
            <div id="packages-list" class="grid gap-8 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
                <c:forEach var="pkg" items="${requestScope.pkgs}">
                    <c:set var="pkgName" value="${pkg.name}" />
                    <c:set var="routes"  value="${requestScope.pkgRoutes[pkgName]}" />

                    <c:set var="cover" value=""/>
                    <c:forEach var="r" items="${routes}">
                        <c:if test="${empty cover and not empty r.image}">
                            <c:set var="cover" value="${r.image}"/>
                        </c:if>
                    </c:forEach>
                    <c:if test="${empty cover}">
                        <c:set var="cover" value="https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=800&auto=format&fit=crop"/>
                    </c:if>

                    <c:set var="sumRef" value="0"/>
                    <c:forEach var="r" items="${routes}">
                        <c:if test="${not empty r.priceTouristClass}">
                            <c:set var="sumRef" value="${sumRef + r.priceTouristClass}"/>
                        </c:if>
                    </c:forEach>

                    <article class="group bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col transition hover:-translate-y-1">
                        <div class="relative">
                            <img src="${cover}" alt="${fn:escapeXml(pkg.name)}"
                                 class="w-full h-48 object-cover"
                                 onerror="this.src='https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=800&auto=format&fit=crop'">
                        </div>
                        <div class="p-5 flex-1 flex flex-col justify-between">
                            <header>
                                <h3 class="text-lg font-bold text-gray-900 mb-1 group-hover:text-brand">
                                        ${fn:escapeXml(pkg.name)}
                                </h3>
                                <p class="text-sm text-gray-600 line-clamp-3">
                                        ${fn:escapeXml(pkg.description)}
                                </p>
                            </header>

                            <div class="mt-4">
                                <div class="flex items-end justify-between">
                                    <div class="text-xs text-gray-500">TOTAL</div>
                                    <div class="text-2xl font-bold text-emerald-600">
                                        <fmt:formatNumber value="${empty pkg.totalPrice ? 0 : pkg.totalPrice}"
                                                          type="currency" currencySymbol="US$ " minFractionDigits="2"/>
                                    </div>
                                </div>
                                <div class="text-xs text-right text-gray-400">
                                    Ref. rutas:
                                    <fmt:formatNumber value="${sumRef}" type="currency" currencySymbol="US$ " minFractionDigits="2"/>
                                </div>

                                <!-- Abrir modal paquete -->
                                <c:url var="urlModal" value="/packages/list">
                                    <c:param name="modal" value="${pkgName}" />
                                </c:url>
                                <a href="${urlModal}#modal-package"
                                   class="mt-3 w-full px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110 block text-center">
                                    Ver paquete
                                </a>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="/src/views/footer/footer.jspf" %>

<%@ include file="/src/components/modalPackage/modalPackage.jspf" %>
<%@ include file="/src/components/modalPackage/modalRoute.jspf" %>

</body>
</html>
