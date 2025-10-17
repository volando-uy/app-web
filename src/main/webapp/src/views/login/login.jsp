<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<%
    request.setAttribute("pageTitle", "Login - Volando.uy");

%>

<%@ include file="/src/components/layout/libs.jspf" %>
<%@ include file="/src/components/layout/head.jspf" %>

<body class="flex items-center justify-center min-h-screen bg-gradient-to-r from-brand to-blue-300 px-4">
<div class="relative w-full max-w-4xl bg-white rounded-2xl shadow-lg overflow-hidden">
    <div class="flex flex-col md:flex-row">
        <!-- Left side (Switch Panel) -->
        <div id="switchPanel"
             class="w-full md:w-1/2 bg-brand text-white flex flex-col items-center justify-center p-10 transition-all duration-700 relative">
            <!-- Flecha atrás -->
            <a href="${pageContext.request.contextPath}/index" class="absolute top-6 left-6 group" title="Volver al inicio">
                <svg xmlns="http://www.w3.org/2000/svg"
                     class="h-8 w-8 text-white group-hover:text-yellow-300 transition" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/>
                </svg>
            </a>
            <h2 id="panelTitle" class="text-3xl font-bold mb-4">Bienvenido, capo!</h2>
            <p id="panelText" class="mb-6">No tienes una cuenta?</p>
            <a href="${pageContext.request.contextPath}/users/register"
               class="px-6 py-2 bg-white text-brand font-semibold rounded-full hover:bg-gray-100 transition">Registro</a>
        </div>

        <!-- Right side (Forms) -->
        <div class="w-full md:w-1/2 bg-white flex items-center justify-center p-8">
            <!-- Login Form -->
            <form id="loginForm"
                  class="w-full max-w-sm"
                  method="POST"
                  action="${pageContext.request.contextPath}/users/login">
                <h2 class="text-2xl font-bold text-center mb-6">Login</h2>

                <input id="login-username" name="nickname" type="text" placeholder="Username"
                       class="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">

                <input id="login-password" name="password" type="password" placeholder="Password"
                       class="w-full mb-4 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand">

                <p class="text-sm text-right mb-4">
                    <a href="#" class="text-brand hover:underline">¿Olvidaste la contraseña?</a>
                </p>

                <button id="btn-login" type="submit"
                        class="w-full bg-brand text-white py-2 rounded-lg hover:bg-blue-700 transition">Login
                </button>


            </form>
        </div>
    </div>
</div>
<%
    request.setAttribute("pageScript", "src/views/login/login.js");
%>
<%@ include file="/src/components/layout/scripts.jspf" %>
</body>
</html>


<%
    String nickname = (String) session.getAttribute("nickname");
    if (nickname != null) {
%>
<script>
    document.getElementById("login-username").value = "<%= nickname %>";
</script>
<%
        session.removeAttribute("nickname");
    }
%>
