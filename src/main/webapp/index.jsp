<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<%@ include file="/src/components/layout/libs.jspf" %> <!-- Taglibs aquí una sola vez -->

<%
    // Si entran directo al JSP sin pasar por el servlet, redirigimos
    if (request.getAttribute("packages") == null || request.getAttribute("flights") == null) {
//        request.getRequestDispatcher("/index").forward(request, response);
        response.sendRedirect(request.getContextPath() + "/index");
        return;
    }
%>

<html lang="es">
<jsp:include page="/src/components/layout/head.jspf"/>

<body class="min-h-screen bg-brand/10 flex flex-col">

<!-- Header -->
<jsp:include page="/src/views/header/header.jsp"/>

<!-- Contenido principal -->
<main class="flex-1 container mx-auto px-4 py-8 flex flex-col gap-12">
    <section id="paquetes" class="w-full">
        <%@ include file="/src/components/packageList/packageList.jspf" %>
    </section>

    <section id="vuelos" class="w-full">
        <%@ include file="/src/components/flightList/flightList.jspf" %>
    </section>
</main>

<!-- Footer -->
<jsp:include page="/src/views/footer/footer.jspf"/>

<!-- Script específico para esta página -->
<%
    request.setAttribute("pageScript", "index.js");
%>
<%@ include file="/src/components/layout/scripts.jspf" %>

</body>
</html>
