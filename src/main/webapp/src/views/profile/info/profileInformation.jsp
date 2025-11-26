<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labpa.appweb.user.SoapUserDTO" %>
<%@ page import="com.labpa.appweb.constants.ValuesConstantsDTO" %>
<%@ include file="/src/components/layout/libs.jspf" %>

<%@ include file="/src/components/layout/head.jspf" %>
<%
    request.setAttribute("pageTitle", "Perfil de Usuario");
    ValuesConstantsDTO valuesConstantsDTO = new ValuesConstantsDTO();
    SoapUserDTO usuario = (SoapUserDTO) request.getAttribute("usuario");
%>

<body class="min-h-screen bg-gradient-to-r from-brand to-blue-300 py-12 px-4 flex items-center justify-center">
<div class="relative w-full max-w-5xl bg-white rounded-2xl shadow-lg overflow-hidden">

    <div class="max-w-5xl mx-auto mt-12 bg-white shadow-lg rounded-xl p-8">
        <div class="flex flex-col md:flex-row gap-8 items-center">
            <div class="text-white p-6 flex flex-col items-center">
                <div class="w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-lg mb-4">
                    <img src="${rootUrl}image?resourceClassName=${sessionScope.resourceClassName}&key=${usuario.nickname}"
                         alt="Foto de perfil"
                         class="w-full h-full object-cover"/>
                </div>
            </div>

            <div>
                <h1 class="text-3xl font-bold text-gray-800">${usuario.name}</h1>
                <p class="text-gray-600"><strong>Nickname:</strong> ${usuario.nickname}</p>
                <p class="text-gray-600"><strong>Email:</strong> ${usuario.mail}</p>

                <c:if test="${not empty loggedUser && loggedUser != usuario.nickname}">
                    <form action="${rootUrl}/followers/follow" method="post">
                        <input type="hidden" name="target" value="${usuario.nickname}"/>

                        <c:choose>
                            <c:when test="${isFollowing}">
                                <button type="submit"
                                        class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded">
                                    Dejar de seguir
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button type="submit"
                                        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded">
                                    Seguir
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </c:if>

                <c:if test="${sessionScope.jwt_nick != null && sessionScope.jwt_nick == usuario.nickname}">
                    <form action="${logoutUrl}" method="post"
                          class="mt-4 inline-block" title="Cerrar sesión de ${usuario.nickname}">
                        <button type="submit"
                                class="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-red-600 text-white font-medium hover:bg-red-700 transition-colors">
                            Cerrar sesión
                        </button>
                    </form>

                    <form action="${profileUpdateUrl}" method="get"
                          class="inline-block" title="Editar información de ${usuario.nickname}">
                        <button type="submit"
                                class="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-blue-600 text-white font-medium hover:bg-blue-700 transition-colors">
                            Actualizar perfil
                        </button>
                    </form>
                </c:if>

                <a href="${homeUrl}"
                   class="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-gray-600 text-white font-medium hover:bg-gray-700 transition-colors">
                    Ir al Inicio
                </a>
            </div>
        </div>

        <hr class="my-6"/>

        <c:choose>

            <c:when test="${isCustomer}">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <p><span class="font-semibold">Apellido:</span> ${usuario.surname}</p>
                        <p><span class="font-semibold">Fecha de nacimiento:</span> ${usuario.birthDate}</p>
                        <p><span class="font-semibold">Nacionalidad:</span> ${usuario.citizenship}</p>
                        <p><span class="font-semibold">Tipo Documento:</span> ${usuario.docType}</p>
                        <p><span class="font-semibold">Número Documento:</span> ${usuario.numDoc}</p>
                    </div>

                    <div>
                        <h2 class="text-lg font-semibold mb-2">Paquetes Comprados</h2>
                        <c:choose>
                            <c:when test="${not empty boughtPackageLinks}">
                                <ul class="space-y-2">
                                    <c:forEach var="link" items="${boughtPackageLinks}">
                                        <li class="p-3 rounded-lg border bg-gray-50 hover:bg-gray-100 transition">
                                            <a class="text-blue-700 hover:underline font-medium"
                                               href="${rootUrl}/booking/check?airline=${link.airline}&route=${link.routeName}&flight=${link.flightName}">
                                                Paquete #${link.id}
                                                <br/>
                                                <span class="text-sm text-gray-700">
                                                Ruta: <strong>${link.routeName}</strong><br/>
                                                Vuelo: <strong>${link.flightName}</strong><br/>
                                                Aerolínea: <strong>${link.airline}</strong>
                                                </span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>

                            <c:otherwise>
                                <p class="text-gray-500 italic">No compraste ningún paquete aún.</p>
                            </c:otherwise>
                        </c:choose>
                        <h2 class="text-lg font-semibold mt-4 mb-2">Reservas de Vuelo</h2>
                        <c:choose>
                            <c:when test="${not empty bookedFlightLinks}">
                                <ul class="space-y-2">
                                    <c:forEach var="link" items="${bookedFlightLinks}">
                                        <li class="p-3 rounded-lg border bg-gray-50 hover:bg-gray-100 transition">
                                            <a class="text-blue-700 hover:underline font-medium"
                                               href="${rootUrl}booking/check?airline=${link.airline}&route=${link.routeName}&flight=${link.flightName}&booking=${link.bookingId}">
                                                Reserva #${link.bookingId}
                                                <br/>
                                                <span class="text-sm text-gray-700">
                                                Ruta: <strong>${link.routeName}</strong><br/>
                                                Vuelo: <strong>${link.flightName}</strong><br/>
                                                Aerolínea: <strong>${link.airline}</strong>
                                                </span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-gray-500 italic">No tenés reservas de vuelo aún.</p>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>
            </c:when>

            <c:when test="${isAirline}">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <p><span class="font-semibold">Descripción:</span> ${usuario.description}</p>
                        <c:if test="${not empty usuario.web}">
                            <p><span class="font-semibold">Sitio Web:</span>
                                <a href="${usuario.web}" class="text-blue-500 underline"
                                   target="_blank">${usuario.web}</a>
                            </p>
                        </c:if>
                    </div>

                    <div>
                        <h2 class="text-lg font-semibold mb-2">Rutas de Vuelo</h2>
                        <c:choose>
                            <c:when test="${not empty usuario.flightRoutesNames}">
                                <ul class="list-disc list-inside">
                                    <c:forEach var="ruta" items="${usuario.flightRoutesNames}">
                                        <li>
                                            <a class="text-blue-700 hover:underline"
                                               href="${rootUrl}/booking/check?airline=${usuario.nickname}&route=${fn:replace(ruta, ' ', '+')}">
                                                    ${ruta}
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-gray-500 italic">Sin rutas de vuelo registradas.</p>
                            </c:otherwise>
                        </c:choose>

                        <h2 class="text-lg font-semibold mt-4 mb-2">Vuelos</h2>
                        <c:choose>
                            <c:when test="${not empty usuario.flightsNames}">
                                <ul class="list-disc list-inside">
                                    <c:forEach var="vuelo" items="${usuario.flightsNames}" varStatus="status">
                                        <c:set var="ruta" value="${usuario.flightRoutesNames[status.index]}"/>
                                        <li>
                                            <a class="text-blue-700 hover:underline"
                                               href="${rootUrl}/booking/check?airline=${usuario.nickname}&route=${fn:replace(ruta, ' ', '+')}&flight=${fn:replace(vuelo, ' ', '+')}">
                                                    ${vuelo}
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-gray-500 italic">Sin vuelos registrados.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <p class="text-red-500">Tipo de usuario desconocido.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%
    request.setAttribute("pageScript", "src/views/profile/info/profileInformation.js");
%>

<%@ include file="/src/components/layout/scripts.jspf" %>
</body>
<script>
    function downloadPackagePDF(packageId) {
        // TODO: reemplazar con la URL real de tu endpoint SOAP o REST
        const url = `${CONTEXT_PATH}/pdf/package/${packageId}`;

        // Simulación inicial (luego usar fetch con blob)
        alert("Preparando descarga del PDF para paquete #" + packageId);

        // Ejemplo real más adelante:
        // fetch(url)
        //     .then(response => response.blob())
        //     .then(blob => {
        //         const link = document.createElement('a');
        //         link.href = URL.createObjectURL(blob);
        //         link.download = `paquete-${packageId}.pdf`;
        //         link.click();
        //     })
        //     .catch(err => console.error("Error al generar PDF:", err));
    }
</script>
</html>
