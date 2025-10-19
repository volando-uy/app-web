<%@ page import="domain.dtos.user.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<%
    request.setAttribute("pageTitle", "Perfil - Volando.uy");
%>

<%@ include file="/src/components/layout/libs.jspf" %>
<%@ include file="/src/components/layout/head.jspf" %>
<body class="min-h-screen bg-gradient-to-r from-blue-900 to-blue-400 flex items-center justify-center px-4">
<%
    UserDTO user = (UserDTO) request.getAttribute("user");
    boolean isCustomer = user instanceof CustomerDTO;
    boolean isAirline = user instanceof AirlineDTO;
%>

    <div class="w-full max-w-2xl bg-white rounded-2xl shadow-lg p-8">
        <h2 class="text-3xl font-bold text-center mb-6 text-blue-900">Editar Perfil</h2>

        <form action="${pageContext.request.contextPath}/perfil/update" method="post" class="space-y-4">
            <div>
                <label class="block mb-1 font-semibold">Nickname</label>
                <input type="text" name="nickname" value="<%= user.getNickname() %>"
                       class="w-full px-4 py-2 border rounded-lg bg-gray-100 cursor-not-allowed" readonly>
            </div>

            <div>
                <label class="block mb-1 font-semibold">Nombre</label>
                <input type="text" name="name" value="<%= user.getName() %>"
                       class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
            </div>

            <% if (isCustomer) {
                CustomerDTO c = (CustomerDTO) user;
            %>
            <div>
                <label class="block mb-1 font-semibold">Apellido</label>
                <input type="text" name="surname" value="<%= c.getSurname() %>"
                       class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
            </div>

            <div>
                <label class="block mb-1 font-semibold">Fecha de nacimiento</label>
                <input type="date" name="birthDate" value="<%= c.getBirthDate() %>"
                       class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
            </div>

            <div>
                <label class="block mb-1 font-semibold">Tipo de documento</label>
                <select name="docType" class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
                    <option value="CI" <%= c.getDocType().toString().equals("CI") ? "selected" : "" %>>CI</option>
                    <option value="PASAPORTE" <%= c.getDocType().toString().equals("PASAPORTE") ? "selected" : "" %>>Pasaporte</option>
                </select>
            </div>

            <div>
                <label class="block mb-1 font-semibold">Número de documento</label>
                <input type="text" name="numDoc" value="<%= c.getNumDoc() %>"
                       class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
            </div>

            <div>
                <label class="block mb-1 font-semibold">Nacionalidad</label>
                <input type="text" name="citizenship" value="<%= c.getCitizenship() %>"
                       class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
            </div>
            <% } else if (isAirline) {
                AirlineDTO a = (AirlineDTO) user;
            %>
            <div>
                <label class="block mb-1 font-semibold">Página Web</label>
                <input type="url" name="web" value="<%= a.getWeb() %>"
                       class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
            </div>

            <div>
                <label class="block mb-1 font-semibold">Descripción</label>
                <textarea name="description"
                          class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900"><%= a.getDescription() %></textarea>
            </div>
            <% } %>

            <div class="flex gap-2 mt-4">
                <button type="button"
                        onclick="window.location.href='${pageContext.request.contextPath}/index'"
                        class="w-1/2 bg-gray-400 text-white py-2 rounded-lg hover:bg-gray-500 transition">
                    Volver
                </button>

                <button type="submit" id="guPerfil" class="w-1/2 bg-blue-900 text-white py-2 rounded-lg hover:bg-blue-700 transition">
                    Guardar cambios
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Script específico para esta página -->
<%
    request.setAttribute("pageScript", "src/views/profile/profile.js");
%>
<%@ include file="/src/components/layout/scripts.jspf" %>

</body>
</html>

