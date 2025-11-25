<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Paquetes disponibles</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script src="${pageContext.request.contextPath}/resources/js/tailwind.min.js"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: { colors: { brand: "#0B4C73" } }
            }
        };
    </script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/fontawesome.min.css"></link>

    <%@ include file="/src/components/layout/libs.jspf" %>
</head>
<body class="bg-gray-100 min-h-screen flex flex-col">

<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold text-brand mb-8 text-center">Paquetes disponibles</h1>

    <%@ include file="/src/components/packages/searchBar.jspf" %>
    <%@ include file="/src/components/packages/packageList.jspf" %>
</main>
<%@ include file="/src/components/modalPackage/modalPackage.jspf" %>
<%@ include file="/src/components/modalPackage/modalRoute.jspf" %>
<jsp:include page="/src/views/footer/footer.jspf" />
</body>
</html>
