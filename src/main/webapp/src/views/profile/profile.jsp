<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: { brand: "#0B3C5D" }
                }
            }
        }
    </script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Perfil</title>
</head>
<body class="min-h-screen bg-gradient-to-r from-brand to-blue-300 flex items-center justify-center px-4">
<div class="w-full max-w-2xl bg-white rounded-2xl shadow-lg p-8">
    <h2 class="text-3xl font-bold text-center mb-6 text-brand">Editar Perfil</h2>

    <form id="profileForm" action="${pageContext.request.contextPath}/updateUser" method="post" class="space-y-4">
        <div>
            <label class="block mb-1 font-semibold">Nickname</label>
            <input type="text" name="nickname" id="nickname" value="${param.nickname}"
                   class="w-full px-4 py-2 border rounded-lg bg-gray-100 cursor-not-allowed" readonly>
        </div>

        <div>
            <label class="block mb-1 font-semibold">Nombre</label>
            <input type="text" name="name" id="nombre" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
        </div>

        <div id="apellidoField">
            <label class="block mb-1 font-semibold">Apellido</label>
            <input type="text" name="surname" id="apellido" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
        </div>

        <div>
            <label class="block mb-1 font-semibold">Email</label>
            <input type="email" id="email" class="w-full px-4 py-2 border rounded-lg bg-gray-100 cursor-not-allowed" readonly>
        </div>

        <div id="fechaNacimientoField">
            <label class="block mb-1 font-semibold">Fecha de nacimiento</label>
            <input type="date" name="birthDate" id="fechaNacimiento" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
        </div>

        <div id="docFields">
            <label class="block mb-1 font-semibold">Tipo de documento</label>
            <select name="docType" id="tipoDoc" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
                <option value="CI">CI</option>
                <option value="PASAPORTE">Pasaporte</option>
            </select>
            <input type="text" name="numDoc" id="documento" placeholder="Número de documento" class="mt-2 w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
        </div>

        <div id="nacionalidadField">
            <label class="block mb-1 font-semibold">Nacionalidad</label>
            <input type="text" name="citizenship" id="nacionalidad" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
        </div>

        <div id="webField" class="hidden">
            <label class="block mb-1 font-semibold">Página Web</label>
            <input type="url" name="web" id="web" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
        </div>

        <div id="descripcionField" class="hidden">
            <label class="block mb-1 font-semibold">Descripción</label>
            <textarea name="description" id="descripcion" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"></textarea>
        </div>

        <button type="button" onclick="window.location.href='index.jsp'" id="volver" class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-500 transition">
            Volver al inicio
        </button>

        <button type="submit" class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-500 transition">
            Guardar cambios
        </button>
    </form>
</div>
<script src="profile.js"></script>
</body>
</html>
