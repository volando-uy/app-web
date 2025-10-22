<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/src/components/layout/libs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<%
    request.setAttribute("pageTitle", "Consulta de Perfil - Volando.uy");
%>
<%@ include file="/src/components/layout/head.jspf" %>
<body class="bg-gray-50 text-gray-800 min-h-screen flex flex-col">
<jsp:include page="/src/views/header/header.jsp"/>


<main class="flex-grow p-4 sm:p-8 flex flex-col items-center">

    <h1 class="text-3xl sm:text-4xl font-bold text-blue-700">Consulta de Perfil</h1>
    <p class="text-gray-600 mt-1 mb-6 text-sm sm:text-base">
        Selecciona un usuario para ver su información detallada
    </p>

    <section id="listaUsuarios" class="w-full max-w-5xl bg-white rounded-2xl shadow-md p-4 sm:p-6 transition-all">
        <h2 class="text-lg sm:text-xl font-semibold mb-3 sm:mb-4 border-b pb-2">Usuarios registrados</h2>

        <div class="overflow-x-auto">
            <table class="w-full max-w-4xl border border-gray-300 rounded">
                <thead class="bg-blue-100">
                <tr>
                    <th class="p-3 text-left">Nickname</th>
                    <th class="p-3 text-left">Nombre</th>
                    <th class="p-3 text-left">Tipo</th>
                    <th class="p-3 text-center">Acción</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr class="border-t hover:bg-gray-50">
                        <td class="p-3">${u.nickname}</td>
                        <td class="p-3">${u.name}</td>
                        <td class="p-3">${tiposUsuarios[u.nickname]}</td>
                        <td class="p-3 text-center">
                            <a href="${pageContext.request.contextPath}/users/view?nick=${u.nickname}"
                               class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm">
                                Ver perfil
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                </tbody>
            </table>

        </div>
    </section>

    <!-- PERFIL DE USUARIO -->
    <section id="perfilUsuario" class="hidden w-full max-w-5xl bg-white rounded-2xl shadow-md p-4 sm:p-6 mt-6">
        <button id="btnVolver"
                class="mb-4 flex items-center gap-1 bg-gray-200 hover:bg-gray-300 px-3 py-2 rounded text-sm sm:text-base transition">
            ← Volver
        </button>

        <div class="flex flex-col sm:flex-row items-center sm:items-start sm:space-x-6 mb-6">
            <img
                    id="imagenUsuario"
                    src=""
                    alt="Imagen de usuario"
                    class="w-28 h-28 sm:w-32 sm:h-32 rounded-full border-2 border-gray-300 object-cover mb-4 sm:mb-0"
            />
            <div class="text-center sm:text-left">
                <h2 id="nombreUsuario" class="text-2xl sm:text-3xl font-bold text-gray-800"></h2>
                <p id="tipoUsuario" class="text-gray-600 mt-1"></p>
            </div>
        </div>

    </section>

</main>
<jsp:include page="/src/views/footer/footer.jspf"/>

<%@ include file="/src/components/layout/scripts.jspf" %>
</body>
</html>
