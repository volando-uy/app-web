<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="domain.dtos.user.UserDTO" %>
<%@ page import="domain.dtos.user.AirlineDTO" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Usuarios</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800 min-h-screen flex flex-col">
<main class="flex-grow p-6 flex flex-col items-center">

    <h1 class="text-3xl font-bold text-blue-700 mb-6">Usuarios</h1>

    <%
        List<UserDTO> users = (List<UserDTO>) request.getAttribute("users");
        AirlineDTO airline = (AirlineDTO) request.getAttribute("airline");
        UserDTO user = (UserDTO) request.getAttribute("user");
    %>

    <!-- LISTA DE USUARIOS -->
    <section id="listaUsuarios"
             class="w-full max-w-4xl bg-white rounded-lg shadow-md p-4 <%= (airline != null || user != null ? "hidden" : "") %>">
        <h2 class="text-xl font-semibold mb-3 border-b pb-2">Usuarios registrados</h2>

        <div class="overflow-x-auto">
            <table class="w-full text-sm border border-gray-200 rounded">
                <thead class="bg-blue-100">
                <tr>
                    <th class="p-3">Nickname</th>
                    <th class="p-3">Tipo</th>
                    <th class="p-3 text-center">Acción</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (users != null && !users.isEmpty()) {
                        for (UserDTO u : users) {
                %>
                <tr class="border-b hover:bg-gray-50">
                    <td class="p-3"><%= u.getNickname() %></td>
                    <td class="p-3">
                        <%
                            String tipo = (u instanceof AirlineDTO) ? "Aerolínea" : "Usuario";
                        %>
                        <%= tipo %>
                    </td>
                    <td class="p-3 text-center">
                        <form action="Profileusers" method="get">
                            <input type="hidden" name="nickname" value="<%= u.getNickname() %>">
                            <button type="submit"
                                    class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm">
                                Ver perfil
                            </button>
                        </form>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="3" class="p-3 text-center text-gray-500">No hay usuarios registrados.</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </section>

    <!-- PERFIL DE USUARIO -->
    <section id="perfilUsuario"
             class="w-full max-w-4xl bg-white rounded-lg shadow-md p-6 <%= (airline == null && user == null ? "hidden" : "") %>">

        <button id="btnVolver" class="mb-4 bg-gray-200 hover:bg-gray-300 px-3 py-1 rounded">← Volver</button>

        <% if (airline != null) { %>
        <!-- Perfil Aerolínea -->
        <h2 class="text-2xl font-bold mb-3"><%= airline.getName() %> (@<%= airline.getNickname() %>)</h2>
        <p class="text-gray-700 mb-2"><strong>Email:</strong> <%= airline.getMail() %></p>
        <p class="text-gray-700 mb-2"><strong>Web:</strong> <%= airline.getWeb() %></p>
        <p class="text-gray-700 mb-4"><strong>Descripción:</strong> <%= airline.getDescription() %></p>

        <% } else if (user != null) { %>
        <!-- Perfil Usuario genérico -->
        <h2 class="text-2xl font-bold mb-3">@<%= user.getNickname() %></h2>
        <p class="text-gray-700 mb-2"><strong>Email:</strong> <%= user.getMail() %></p>
        <% } %>
    </section>
</main>

<script src="detailsProfile.js"></script>
</body>
</html>
