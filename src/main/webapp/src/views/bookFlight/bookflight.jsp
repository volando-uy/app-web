<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Reservar vuelo</title>

    <script src="${pageContext.request.contextPath}/resources/js/tailwind.min.js"></script>
    <script>
        tailwind.config = { theme: { extend: { colors: { brand: "#0B4C73" } } } };
    </script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/fontawesome.min.css"></link>
</head>

<body class="bg-[#f5f6fa] min-h-screen flex flex-col">
<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-4 py-8">



    <%@ include file="/src/components/bookflight/info.jspf" %>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <section class="lg:col-span-2 space-y-4">
            <div class="bg-white p-5 rounded-2xl shadow-sm border">
                <h3 class="text-sm font-semibold mb-1">Detalles y pasajeros</h3>
                <%@ include file="/src/components/bookflight/filters.jspf" %>
            </div>
        </section>

        <aside class="lg:col-span-1">
            <%@ include file="/src/components/bookflight/summary.jspf" %>
        </aside>
    </div>
</main>

<jsp:include page="/src/views/footer/footer.jspf" />
</body>
</html>
