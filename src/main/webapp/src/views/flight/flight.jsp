<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Consulta de Rutas y Vuelos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Tailwind -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: { brand: "#0B4C73" } } }
        };
    </script>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <%@ include file="/src/components/layout/libs.jspf" %>
</head>

<body class="bg-[#f5f6fa] min-h-screen flex flex-col">
<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-2 md:px-4 py-8">
    <h1 class="text-3xl font-bold text-brand mb-6 text-center">
        Consulta de Rutas y Vuelos
    </h1>

    <%@ include file="/src/components/flight/flightRouter.jspf" %>
</main>

<jsp:include page="/src/views/footer/footer.jspf" />
</body>
</html>
