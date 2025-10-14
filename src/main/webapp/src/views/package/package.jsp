<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

    <!-- URL base -->
    <c:url var="packagesListUrl" value="/packages/list"/>

    <!-- Buscador -->
    <form class="mb-8 max-w-xl mx-auto flex gap-2" action="${packagesListUrl}" method="get">
        <input name="q" value="${fn:escapeXml(param.q)}"
               class="flex-1 px-3 py-2 rounded-lg border border-gray-300"
               placeholder="Buscar por nombre, ciudad, aerolínea o código de ruta…"/>
        <button class="px-4 py-2 rounded-lg bg-brand text-white hover:brightness-110">Buscar</button>
        <a class="px-4 py-2 rounded-lg border border-gray-300" href="${packagesListUrl}">Limpiar</a>
    </form>

    <c:set var="cards" value="${packageCards}" />
    <c:choose>
        <c:when test="${empty cards}">
            <p class="text-center text-gray-500">No hay paquetes disponibles.</p>
        </c:when>
        <c:otherwise>
            <div id="packages-list" class="grid gap-8 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
                <c:forEach var="card" items="${cards}">
                    <article class="group bg-white rounded-2xl shadow-lg overflow-hidden flex flex-col transition hover:-translate-y-1">
                        <div class="relative">
                            <img src="${card.cover}" alt="${fn:escapeXml(card.name)}"
                                 onerror="this.src='https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=800&auto=format&fit=crop'"
                                 class="w-full h-48 object-cover">
                        </div>
                        <div class="p-5 flex-1 flex flex-col justify-between">
                            <header>
                                <h3 class="text-lg font-bold text-gray-900 mb-1 group-hover:text-brand">
                                        ${fn:escapeXml(card.name)}
                                </h3>
                                <p class="text-sm text-gray-600 line-clamp-3">
                                        ${fn:escapeXml(card.description)}
                                </p>
                            </header>

                            <div class="mt-4">
                                <div class="flex items-end justify-between">
                                    <div class="text-xs text-gray-500">TOTAL</div>
                                    <div class="text-2xl font-bold text-emerald-600">${card.totalStr}</div>
                                </div>
                                <div class="text-xs text-right text-gray-400">Ref. rutas: ${card.sumRefStr}</div>

                                <!-- Abrir modal paquete -->
                                <c:url var="urlModal" value="/packages/list">
                                    <c:param name="modal" value="${card.pkgName}" />
                                    <c:if test="${not empty param.q}"><c:param name="q" value="${param.q}" /></c:if>
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

<!-- Modales SSR -->
<%@ include file="/src/views/components/modalPackage/modalPackage.jspf" %>
<%@ include file="/src/views/components/modalPackage/modalRoute.jspf" %>

</body>
</html>
