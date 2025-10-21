<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Reservar vuelo</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>tailwind.config={theme:{extend:{colors:{brand:"#0B4C73"}}}};</script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>

<body class="bg-[#f5f6fa] min-h-screen flex flex-col">
<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-4 py-8">

    <c:if test="${existingBooking and not proceedAllowed and not suppressExistingBanner}">
        <%@ include file="/src/components/bookflight/warning_existing_booking.jspf" %>
    </c:if>

    <%@ include file="/src/components/bookflight/info.jspf" %>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <section class="lg:col-span-2 space-y-4">
            <div class="bg-white p-5 rounded-2xl shadow-sm border">
                <h3 class="text-sm font-semibold mb-1">Detalles y pasajeros</h3>

                <%@ include file="/src/components/bookflight/filters.jspf" %>

                <form id="postForm" method="post" action="${pageContext.request.contextPath}/reservas">
                    <input type="hidden" name="flight" value="${fn:escapeXml(flight.name)}"/>
                    <input type="hidden" name="seatType" value="${seatTypePreview}"/>
                    <input type="hidden" name="passengersCount" value="${passengersCount}"/>

                    <c:if test="${proceedAllowed}">
                        <input type="hidden" name="proceed" value="1"/>
                    </c:if>

                    <%@ include file="/src/components/bookflight/passengers_table.jspf" %>

                    <div class="mt-4 flex gap-2">
                        <button type="submit" name="action" value="calc"
                                class="px-5 py-2 border rounded inline-block">
                            Calcular costo
                        </button>

                        <a class="px-5 py-2 border rounded inline-block"
                           href="${pageContext.request.contextPath}/flight/list">Volver</a>
                    </div>
                </form>
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
