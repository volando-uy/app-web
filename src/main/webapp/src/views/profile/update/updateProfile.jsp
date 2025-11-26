<%@ page import="com.labpa.appweb.user.*" %>
<%@ page import="mappers.DateMapper" %>
<%@ page import="utils.SoapDateParser" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<%

    request.setAttribute("pageTitle", "Editar Perfil - Volando.uy");

    SoapUserDTO user = (SoapUserDTO) request.getAttribute("user");
    SoapBaseCustomerDTO c = (SoapBaseCustomerDTO) request.getAttribute("customer");
    boolean isCustomer = user instanceof SoapBaseCustomerDTO;
    boolean isAirline = user instanceof SoapBaseAirlineDTO;

    String userImage = user.getImage() != null ? user.getImage() : "/public/images/users/default-user.jpg";
    String profileUpdateUrl = request.getContextPath() + "/perfil/update";
    String profileUrl = request.getContextPath() + "/perfil";
%>

<%@ include file="/src/components/layout/libs.jspf" %>
<%@ include file="/src/components/layout/head.jspf" %>

<body class="min-h-screen bg-gradient-to-r from-blue-900 to-blue-400 flex items-center justify-center px-4 py-8">

<div class="w-full max-w-3xl bg-white rounded-2xl shadow-2xl overflow-hidden">
    <!-- Encabezado con imagen -->
    <div class="bg-blue-900 text-white p-6 flex flex-col items-center">
        <div onclick="document.getElementById('profileImageInput').click()"
             class="w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-lg mb-4 cursor-pointer">
            <img id="profileImagePreview" src="<%= userImage %>" alt="Foto de perfil"
                 class="w-full h-full object-cover"/>
        </div>
    </div>

    <form action="<%= profileUpdateUrl %>" method="post" enctype="multipart/form-data" class="p-8 space-y-6">
        <input type="file" id="profileImageInput" name="profileImage" accept="image/*" class="hidden"
               onchange="previewImage(event)"/>

        <!-- NICKNAME -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Nickname</label>
            <input type="text" name="nickname" value="<%= user.getNickname() %>"
                   class="w-full px-4 py-2 border rounded-lg bg-gray-100 cursor-not-allowed text-gray-500" readonly>
        </div>

        <!-- NOMBRE -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Nombre</label>
            <input type="text" name="name" value="<%= user.getName() %>"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
        </div>

        <% if (c != null || isCustomer) {



        %>

        <!-- APELLIDO -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Apellido</label>
            <input type="text" name="surname" value="<%= c.getSurname() %>"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
        </div>


        <div>
            <label class="block mb-1 font-semibold text-gray-700">Fecha de nacimiento</label>

            <input type="date" name="birthDate"
                   value="<%= c.getBirthDate() %>" max="9999-12-31" min="1900-01-01"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
        </div>

        <!-- TIPO DOCUMENTO -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Tipo de documento</label>
            <select name="docType" class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
                <option value="CI" <%= "CI".equals(c.getDocType().toString()) ? "selected" : "" %>>CI</option>
                <option value="PASAPORTE" <%= "PASAPORTE".equals(c.getDocType().toString()) ? "selected" : "" %>>Pasaporte</option>
            </select>
        </div>

        <!-- NÚMERO DOCUMENTO -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Número de documento</label>
            <input type="text" name="numDoc" value="<%= c.getNumDoc() %>"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
        </div>

        <!-- NACIONALIDAD -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Nacionalidad</label>
            <input type="text" name="citizenship" value="<%= c.getCitizenship() %>"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
        </div>

        <% } else if (isAirline) {
            SoapBaseAirlineDTO a = (SoapBaseAirlineDTO) user;
        %>
        <!-- PÁGINA WEB -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Página Web</label>
            <input type="url" name="web" value="<%= a.getWeb() %>"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900">
        </div>

        <!-- DESCRIPCIÓN -->
        <div>
            <label class="block mb-1 font-semibold text-gray-700">Descripción</label>
            <textarea name="description"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-900"
                      rows="4"><%= a.getDescription() %></textarea>
        </div>
        <% } %>

        <!-- BOTONES -->
        <div class="flex gap-4 pt-4">
            <a href="<%= profileUrl %>"
               class="w-1/2 text-center bg-gray-500 hover:bg-gray-600 text-white py-2 rounded-lg transition duration-200">
                Volver
            </a>
            <button type="submit" id="guPerfil"
                    class="w-1/2 bg-blue-900 hover:bg-blue-700 text-white py-2 rounded-lg transition duration-200">
                Guardar cambios
            </button>
        </div>
    </form>
</div>

<%@ include file="/src/components/layout/scripts.jspf" %>

<script>
    function previewImage(event) {
        const reader = new FileReader();
        reader.onload = function () {
            const preview = document.getElementById('profileImagePreview');
            preview.src = reader.result;
        };
        reader.readAsDataURL(event.target.files[0]);
    }
</script>

</body>
</html>
