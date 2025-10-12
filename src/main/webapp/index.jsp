<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    // Si entran directo al JSP (sin pasar por el servlet), reenviamos al servlet /index
    if (request.getAttribute("packages") == null || request.getAttribute("flights") == null) {
        request.getRequestDispatcher("/index").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Volando Index</title>

    <!-- Tailwind -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = { theme: { extend: { colors: { brand: "#0B4C73" } } } }
    </script>

</head>
<body class="bg-brand/10 min-h-screen flex flex-col">

<jsp:include page="src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-4 py-8 flex flex-col gap-12">
    <section id="paquetes" class="w-full">
        <%@ include file="/src/views/components/packageList/packageList.jspf" %>
    </section>

    <section id="vuelos" class="w-full">
        <%@ include file="/src/views/components/flightList/flightList.jspf" %>
    </section>
</main>

<jsp:include page="src/views/footer/footer.jspf" />

<script>window.__BASE__ = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/index.js"></script>
</body>
</html>
