<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<%@ include file="/src/components/layout/libs.jspf" %>

<%

    if (request.getAttribute("pkgs") == null || request.getAttribute("flights") == null) {
//        request.getRequestDispatcher("/index").forward(request, response);
        response.sendRedirect(request.getContextPath() + "/index");
        return;
    }
%>

<html lang="es">
<%
    request.setAttribute("pageTitle", "Volando.uy");
%>

<html lang="es">
<%@ include file="/src/components/layout/head.jspf" %>

<body class="min-h-screen bg-brand/10 flex flex-col">

<!-- Header -->
<jsp:include page="/src/views/header/header.jsp"/>

<!-- Contenido principal -->
<main class="flex-1 container mx-auto px-4 py-8 flex flex-col gap-12">
    <!-- Paquetes como carrusel de 1 -->
    <section id="paquetes" class="w-full max-w-5xl mx-auto">
        <c:choose>
            <c:when test="${empty requestScope.pkgs}">
                <p class="text-center text-gray-500">No hay paquetes disponibles.</p>
            </c:when>
            <c:otherwise>
                <div id="pkg-carousel"
                     class="relative w-full h-[500px] mx-auto rounded-3xl shadow-xl overflow-hidden">

                    <c:forEach var="pkg" items="${requestScope.pkgs}" varStatus="status">
                        <div class="js-slide absolute inset-0 w-full h-full px-4
                                    transition-all duration-500 ease-out transform
                                    ${status.first ? 'opacity-100 translate-x-0 z-20 pointer-events-auto'
                                                   : 'opacity-0 translate-x-6 z-10 pointer-events-none'}">
                            <%@ include file="/src/components/packages/packageCard.jspf" %>
                        </div>
                    </c:forEach>

                    <!-- Flechas -->
                    <button id="pkg-prev"
                            class="absolute left-3 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-black/70
                                   text-white rounded-full w-9 h-9 flex items-center justify-center z-30"
                            aria-label="Anterior">
                        &#x2039;
                    </button>

                    <button id="pkg-next"
                            class="absolute right-3 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-black/70
                                   text-white rounded-full w-9 h-9 flex items-center justify-center z-30"
                            aria-label="Siguiente">
                        &#x203A;
                    </button>
                </div>

                <div class="text-center mt-6">
                    <a href="${pageContext.request.contextPath}/packages/list"
                       class="inline-block px-5 py-2 rounded-full bg-[#0B4C73] text-white hover:brightness-110">
                        Ver todos los paquetes
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <section id="vuelos" class="w-full">
        <%@ include file="/src/components/flightList/flightList.jspf" %>
    </section>
</main>

<!-- Footer -->
<jsp:include page="/src/views/footer/footer.jspf"/>

<%
    request.setAttribute("pageScript", "index.js");
%>
<script defer src="${pageContext.request.contextPath}/src/components/packageList/packageList.js"></script>


<%@ include file="/src/components/layout/scripts.jspf" %>

</body>
</html>
