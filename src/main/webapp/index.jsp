<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<%@ include file="/src/components/layout/libs.jspf" %>

<%

    if (request.getAttribute("packages") == null || request.getAttribute("flights") == null) {
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

<jsp:include page="/src/components/leftPanel/leftPanel.jsp"/>
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

<%
    request.setAttribute("pageScript", "index.js");
%>
<%@ include file="/src/components/layout/scripts.jspf" %>

</body>
</html>
