<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
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
  <title>Login / Register</title>
</head>
<body class="flex items-center justify-center min-h-screen bg-gradient-to-r from-brand to-blue-300 px-4">
  <div class="relative w-full max-w-4xl bg-white rounded-2xl shadow-lg overflow-hidden">
    <div class="flex flex-col md:flex-row">
      <!-- Left side (Switch Panel) -->
      <div id="switchPanel" class="w-full md:w-1/2 bg-brand text-white flex flex-col items-center justify-center p-10 transition-all duration-700 relative">
        <!-- Flecha atrás -->
        <a href="../../../index.html" class="absolute top-6 left-6 group" title="Volver al inicio">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white group-hover:text-yellow-300 transition" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
        </a>
        <h2 id="panelTitle" class="text-3xl font-bold mb-4">Bienvenido, capo!</h2>
        <p id="panelText" class="mb-6">No tienes una cuenta?</p>
        <button id="switchBtn" onclick="showRegister()" class="px-6 py-2 bg-white text-brand font-semibold rounded-full hover:bg-gray-100 transition">
          Registro
        </button>
      </div>

      <!-- Right side (Forms) -->
      <div class="w-full md:w-1/2 bg-white flex items-center justify-center p-8">
        <!-- Login Form -->
        <form id="loginForm" class="w-full max-w-sm">
          <h2 class="text-2xl font-bold text-center mb-6">Login</h2>
          <input id="login-username" type="text" placeholder="Username" class="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
          <input id="login-password" type="password" placeholder="Password" class="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
          <label class="block mb-2 font-semibold">Entrar como:</label>
          <select id="login-role" class="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
            <option value="cliente">Cliente</option>
            <option value="aerolinea">Aerolínea</option>
            <option value="admin">Admin</option>
          </select>
           <p class="text-sm text-right mb-4"><a href="#" class="text-brand hover:underline">Olvidaste la contraseña?</a></p>
          <button id="btn-login" type="button" class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-700 transition">Login</button>
         </form>

         <!-- Register Form -->
         <form id="registerForm" class="hidden w-full max-w-sm">
           <h2 class="text-2xl font-bold text-center mb-6">Registro</h2>

           <!-- Selector de tipo de usuario -->
           <label class="block mb-2 font-semibold">Registro como:</label>
           <select id="userType" onchange="toggleUserType()" class="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">
             <option value="">Seleccione...</option>
             <option value="cliente">Cliente</option>
             <option value="aerolinea">Aerolínea</option>
           </select>

           <!-- Campos para Cliente -->
           <div id="clienteFields" class="hidden">
            <input id="reg-nickname" type="text" placeholder="Nickname" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-nombre" type="text" placeholder="Nombre" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-apellido" type="text" placeholder="Apellido" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-email" type="email" placeholder="Email" class="w-full mb-4 px-4 py-2 border rounded-lg">
             <label class="block mb-1 text-sm font-medium">Fecha de nacimiento:</label>
            <input id="reg-dob" type="date" class="w-full mb-4 px-4 py-2 border rounded-lg">
             <label class="block mb-1 text-sm font-medium">Tipo de documento:</label>
            <select id="reg-doc-type" class="w-full mb-4 px-4 py-2 border rounded-lg">
              <option value="ci">CI</option>
              <option value="pasaporte">Pasaporte</option>
            </select>
            <input id="reg-nacionalidad" type="text" placeholder="Nacionalidad" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-doc-number" type="text" placeholder="CI o Pasaporte" class="w-full mb-4 px-4 py-2 border rounded-lg">
           </div>

           <!-- Campos para Aerolínea -->
           <div id="aerolineaFields" class="hidden">
            <input id="reg-nickname-a" type="text" placeholder="Nickname" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-name-a" type="text" placeholder="Nombre" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-email-a" type="email" placeholder="Email" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <input id="reg-web-a" type="url" placeholder="Página web" class="w-full mb-4 px-4 py-2 border rounded-lg">
            <textarea id="reg-desc-a" placeholder="Descripción" class="w-full mb-4 px-4 py-2 border rounded-lg"></textarea>
           </div>

          <button id="btn-register" type="button" class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-700 transition">Registro</button>
         </form>
      </div>
    </div>
  </div>
  <script src="register.js"></script>
</body>
</html>
