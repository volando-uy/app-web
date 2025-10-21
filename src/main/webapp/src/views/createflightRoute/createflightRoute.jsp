<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="domain.dtos.airport.BaseAirportDTO" %>

<%
    List<BaseAirportDTO> airports = (List<BaseAirportDTO>) request.getAttribute("airports");
    List<String> categories = (List<String>) request.getAttribute("categories");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear Ruta de Vuelo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: { brand: "#0B4C73" } } }
        }
    </script>
</head>
<body class="bg-gray-100 min-h-screen flex flex-col">

<jsp:include page="../header/header.jsp" />

<main class="flex-grow flex items-center justify-center p-4">
    <div class="bg-white w-full max-w-3xl rounded-xl shadow-lg p-6 sm:p-8">
        <h1 class="text-xl sm:text-2xl font-bold text-center text-brand mb-6">
            Crear ruta de vuelo
        </h1>

        <form action="${pageContext.request.contextPath}/createFlightRoute"
              method="post"
              enctype="multipart/form-data"
              class="space-y-6">

            <!-- Aerolínea -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Aerolínea:</label>
                <p class="px-3 py-2 bg-gray-100 border rounded-md text-gray-600 text-sm">
                    <%= (session.getAttribute("airlineNickname") != null)
                            ? session.getAttribute("airlineNickname")
                            : (session.getAttribute("nickname") != null
                            ? session.getAttribute("nickname")
                            : "Mi Aerolínea") %>
                </p>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Nombre:</label>
                    <input name="name" type="text" required class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand"/>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Descripción:</label>
                    <input name="description" type="text" class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand"/>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Fecha de alta:</label>
                    <input name="createdAt" type="date" class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand"/>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Equipaje extra ($):</label>
                    <input name="priceExtra" type="number" step="1" min="0" class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand"/>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Asiento turista ($):</label>
                    <input name="priceTouristClass" type="number" step="1" min="0" class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand"/>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Asiento ejecutivo ($):</label>
                    <input name="priceBusinessClass" type="number" step="1" min="0" class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand"/>
                </div>

                <!-- Aeropuerto origen -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Aeropuerto origen:</label>
                    <select name="originAeroCode" required
                            class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand">
                        <option value="" disabled selected>Selecciona aeropuerto de origen</option>
                        <% if (airports != null) {
                            for (BaseAirportDTO a : airports) { %>
                        <option value="<%= a.getCode() %>">
                            <%= a.getCode() %> - <%= a.getName() != null ? a.getName() : "" %>
                        </option>
                        <%   }
                        } %>
                    </select>
                </div>

                <!-- Aeropuerto destino -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Aeropuerto destino:</label>
                    <select name="destinationAeroCode" required
                            class="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-brand">
                        <option value="" disabled selected>Selecciona aeropuerto de destino</option>
                        <% if (airports != null) {
                            for (BaseAirportDTO a : airports) { %>
                        <option value="<%= a.getCode() %>">
                            <%= a.getCode() %> - <%= a.getName() != null ? a.getName() : "" %>
                        </option>
                        <%   }
                        } %>
                    </select>
                </div>
            </div>

            <!-- Categorías -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Categorías:</label>
                <div class="overflow-x-auto border rounded-md p-3">
                    <% if (categories != null && !categories.isEmpty()) { %>
                    <% for (String cat : categories) { %>
                    <label class="inline-flex items-center mr-4 mb-2">
                        <input type="checkbox" name="categories" value="<%= cat %>" class="mr-2" />
                        <%= cat %>
                    </label>
                    <% } %>
                    <% } else { %>
                    <p class="text-gray-500 text-sm">No hay categorías registradas.</p>
                    <% } %>
                </div>
            </div>

            <!-- Imagen -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Imagen (opcional):</label>
                <input name="image" type="file" accept="image/*" class="w-full text-sm"/>
            </div>

            <div class="text-center">
                <button type="submit" class="px-5 py-2.5 bg-brand text-white text-sm font-semibold rounded-md hover:bg-brand/90">
                    + Crear Ruta de Vuelo
                </button>
            </div>

        </form>
    </div>
</main>

<jsp:include page="../footer/footer.jspf" flush="true" />

</body>
</html>
