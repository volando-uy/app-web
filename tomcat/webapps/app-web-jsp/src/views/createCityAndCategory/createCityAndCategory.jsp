<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear Ciudad y Categoría</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = { theme: { extend: { colors: { brand: "#0B4C73" } } } };
    </script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body class="bg-gray-100 min-h-screen flex flex-col">
<jsp:include page="../header/header.jsp" />
<main class="flex-1 max-w-5xl mx-auto p-6">
    <h1 class="text-2xl font-bold text-center text-brand mb-8">Gestion de Ciudades y Categorías</h1>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <!-- Formulario ciudad -->
        <div class="bg-white rounded-xl shadow p-6">
            <h2 class="text-xl font-semibold text-gray-700 mb-4">Registrar nueva ciudad</h2>
            <form id="city-form" method="post" class="space-y-4">
                <input type="hidden" name="action" value="createCity">
                <div>
                    <label class="block text-sm font-medium text-gray-600">Nombre *</label>
                    <input name="name" type="text" class="mt-1 w-full border rounded-lg px-3 py-2" placeholder="Ej: Montevideo" required>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-600">País *</label>
                    <input name="country" type="text" class="mt-1 w-full border rounded-lg px-3 py-2" placeholder="Ej: Uruguay" required>
                </div>
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-600">Latitud</label>
                        <input name="latitude" type="text" class="mt-1 w-full border rounded-lg px-3 py-2" placeholder="-34.9011">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-600">Longitud</label>
                        <input name="longitude" type="text" class="mt-1 w-full border rounded-lg px-3 py-2" placeholder="-56.1645">
                    </div>
                </div>
                <button type="submit" class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-700 transition">Registrar ciudad</button>
            </form>
            <div id="cityResponse" class="mt-3 text-sm"></div>
        </div>

        <!-- Formulario categoría -->
        <div class="bg-white rounded-xl shadow p-6">
            <h2 class="text-xl font-semibold text-gray-700 mb-4">Crear nueva categoría</h2>
            <form id="category-form" method="post" class="space-y-4">
                <input type="hidden" name="action" value="createCategory">
                <div>
                    <label class="block text-sm font-medium text-gray-600">Nombre *</label>
                    <input name="name" type="text" class="mt-1 w-full border rounded-lg px-3 py-2" placeholder="Ej: Primera Clase" required>
                </div>
                <button type="submit" class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-700 transition">Crear categoría</button>
            </form>
            <div id="categoryResponse" class="mt-3 text-sm"></div>
        </div>
    </div>

</main>
<jsp:include page="../footer/footer.jspf" />
<script>
    const CONTEXT_PATH = "<%= request.getContextPath() %>";
</script>
<script src="createCityAndCategory.js"></script>
</body>
</html>
