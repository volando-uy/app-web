<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="domain.dtos.user.UserDTO" %>

<!DOCTYPE html>
<html lang="es">
<%
    request.setAttribute("pageTitle", "Perfil - Volando.uy");
%>

<%@ include file="/src/components/layout/libs.jspf" %>
<%@ include file="/src/components/layout/head.jspf" %>

<body>
<div class="max-w-5xl mx-auto mt-12 bg-white shadow-lg rounded-xl p-8">
    <div class="flex flex-col md:flex-row gap-8 items-center">
        <div class="text-white p-6 flex flex-col items-center">
            <div class="w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-lg mb-4">
                <img src="image?resourceClassName=${usuario.getClass().getSimpleName()}&key=${usuario.getNickname()}" alt="Foto de perfil"
                     class="w-full h-full object-cover"/>
            </div>
        </div>

        <!--        C:\Users\Josec\OneDrive\Escritorio\LabPAA\app-web-jsp\target\cargo\configurations\tomcat10x\images\users\customers\nickname.jpg //Aca funca
                C:\Users\Josec\OneDrive\Escritorio\LabPAA\app-web-jsp\target\cargo\configurations\tomcat10x\webapps\app-web-jsp\images\users\customers\nickname.jpg-->

        <div>
            <h1 class="text-3xl font-bold text-gray-800">${usuario.name}</h1>
            <p class="text-gray-600"><strong>Nickname:</strong> ${usuario.nickname}</p>
            <p class="text-gray-600"><strong>Email:</strong> ${usuario.mail}</p>

            <form action="${logoutUrl}" method="post"
                  class="mt-4 inline-block" title="Cerrar sesión de ${usuario.nickname}">
                <button type="submit"
                        class="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-red-600 text-white font-medium hover:bg-red-700 transition-colors">
                    Cerrar sesión
                </button>
            </form>

            <!-- Botón: Actualizar perfil -->
            <form action="${profileUpdateUrl}" method="get"
                  class="inline-block" title="Editar información de ${usuario.nickname}">
                <button type="submit"
                        class="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-blue-600 text-white font-medium hover:bg-blue-700 transition-colors">
                    Actualizar perfil
                </button>
            </form>

        </div>
    </div>

    <hr class="my-6"/>

    <!-- Si es Cliente -->
    <c:if test="${tipoUsuario == 'cliente'}">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
                <p><span class="font-semibold">Apellido:</span> ${cliente.surname}</p>
                <p><span class="font-semibold">Fecha de nacimiento:</span> ${cliente.birthDate}</p>
                <p><span class="font-semibold">Nacionalidad:</span> ${cliente.citizenship}</p>
                <p><span class="font-semibold">Tipo Documento:</span> ${cliente.docType}</p>
                <p><span class="font-semibold">Número Documento:</span> ${cliente.numDoc}</p>
            </div>

            <div>
                <h2 class="text-lg font-semibold mb-2">Paquetes Comprados</h2>
                <c:choose>
                    <c:when test="${not empty cliente.boughtPackagesIds}">
                        <div class="flex flex-wrap gap-2">
                            <c:forEach var="id" items="${cliente.boughtPackagesIds}">
                                <span class="bg-blue-100 text-blue-800 text-sm px-2 py-1 rounded-full">#${id}</span>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="text-gray-500 italic">No tiene paquetes comprados.</p>
                    </c:otherwise>
                </c:choose>

                <h2 class="text-lg font-semibold mt-4 mb-2">Reservas de Vuelo</h2>
                <c:choose>
                    <c:when test="${not empty cliente.bookFlightsIds}">
                        <div class="flex flex-wrap gap-2">
                            <c:forEach var="id" items="${cliente.bookFlightsIds}">
                                <span class="bg-green-100 text-green-800 text-sm px-2 py-1 rounded-full">#${id}</span>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="text-gray-500 italic">No tiene vuelos reservados.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>

    <!-- Si es Aerolínea -->
    <c:if test="${tipoUsuario == 'aerolinea'}">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
                <p><span class="font-semibold">Descripción:</span> ${aerolinea.description}</p>
                <c:if test="${not empty aerolinea.web}">
                    <p><span class="font-semibold">Sitio Web:</span>
                        <a href="${aerolinea.web}" class="text-blue-500 underline" target="_blank">${aerolinea.web}</a>
                    </p>
                </c:if>
            </div>

            <div>
                <h2 class="text-lg font-semibold mb-2">Rutas de Vuelo</h2>
                <c:choose>
                    <c:when test="${not empty aerolinea.flightRoutesNames}">
                        <ul class="list-disc list-inside">
                            <c:forEach var="ruta" items="${aerolinea.flightRoutesNames}">
                                <li>${ruta}</li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="text-gray-500 italic">Sin rutas de vuelo registradas.</p>
                    </c:otherwise>
                </c:choose>

                <h2 class="text-lg font-semibold mt-4 mb-2">Vuelos</h2>
                <c:choose>
                    <c:when test="${not empty aerolinea.flightsNames}">
                        <ul class="list-disc list-inside">
                            <c:forEach var="vuelo" items="${aerolinea.flightsNames}">
                                <li>${vuelo}</li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="text-gray-500 italic">Sin vuelos registrados.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</div>

<%@ include file="/src/components/layout/scripts.jspf" %>
</body>
</html>
