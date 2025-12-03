
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<!DOCTYPE html>
<html lang="es">

<%@ include file="/src/components/layout/libs.jspf" %>
<%@ include file="/src/components/layout/head.jspf" %>

<body class="bg-gray-100 min-h-screen flex flex-col">

<jsp:include page="/src/views/header/header.jsp" />

<main class="flex-1 container mx-auto px-4 py-8">
    <div class="max-w-2xl mx-auto bg-white rounded-2xl shadow p-6">
        <h1 class="text-2xl font-bold text-brand mb-4">Confirmar compra</h1>

        <c:choose>
            <c:when test="${empty requestScope.pkg}">
                <div class="p-4 rounded bg-red-50 text-red-700 border border-red-200">
                    No se encontró el paquete.
                </div>
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/packages/list"
                       class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Volver</a>
                </div>
            </c:when>

            <c:otherwise>
                <div class="space-y-2 text-sm text-gray-700">
                    <div><span class="font-semibold">Cliente:</span> <c:out value="${requestScope.customerNickname}"/></div>
                    <div><span class="font-semibold">Paquete:</span> <c:out value="${requestScope.pkg.name}"/></div>
                    <div><span class="font-semibold">Descripción:</span> <c:out value="${requestScope.pkg.description}"/></div>
                    <div>
                        <span class="font-semibold">Precio total:</span>
                        <fmt:formatNumber value="${empty requestScope.pkg.totalPrice ? 0 : requestScope.pkg.totalPrice}"
                                          type="currency" currencySymbol="US$ " minFractionDigits="2"/>
                    </div>
                    <div><span class="font-semibold">Validez:</span> <c:out value="${requestScope.pkg.validityPeriodDays}"/> días</div>

                    <div><span class="font-semibold">Compra:</span>
                        <fmt:formatDate value="${requestScope.purchaseDateLegacy}" pattern="dd/MM/yyyy"/>
                    </div>
                    <div><span class="font-semibold">Vence:</span>
                        <fmt:formatDate value="${requestScope.expiryDateLegacy}" pattern="dd/MM/yyyy"/>
                    </div>
                </div>

                <form class="mt-6 flex items-center justify-end gap-2"
                      method="post"
                      action="${pageContext.request.contextPath}/package/buypackage">
                    <input type="hidden" name="pkgName" value="${requestScope.pkg.name}"/>
                    <a href="${pageContext.request.contextPath}/packages/list?modal=${requestScope.pkg.name}"
                       class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-50">Cancelar</a>
                    <button type="submit"
                            class="px-4 py-2 rounded-lg bg-orange-500 text-white hover:bg-orange-600">
                        Confirmar compra
                    </button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/src/views/footer/footer.jspf" %>
<%@ include file="/src/components/layout/scripts.jspf" %>
</body>
</html>
