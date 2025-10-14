<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url var="homeUrl"     value="/index"/>
<c:url var="flightsUrl"  value="/flight/list"/>
<c:url var="packagesUrl" value="/packages/list"/>
<c:url var="loginUrl"    value="/"/>

<header class="sticky top-0 z-50 bg-brand text-white shadow-md">
    <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <a href="${homeUrl}" class="flex items-center gap-2">
            <span class="text-xl font-bold">Volando<span class="text-yellow-300">.uy</span></span>
        </a>

        <button id="btnMenu" type="button" class="md:hidden p-2 rounded hover:bg-white/10"
                aria-expanded="false" aria-controls="mobileMenu" aria-label="Abrir menú">
            <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
        </button>

        <nav id="nav-desktop" class="hidden md:flex items-center gap-6">
            <a href="${flightsUrl}"  class="hover:text-yellow-300">Vuelos</a>
            <a href="${packagesUrl}" class="hover:text-yellow-300">Paquetes</a>
        </nav>

        <div id="header-actions" class="hidden md:flex items-center gap-3">
            <a href="${loginUrl}" class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">Iniciar sesión</a>
        </div>
    </div>

    <div id="mobileMenu" class="md:hidden hidden border-t border-white/10 bg-brand">
        <nav id="nav-mobile" class="container mx-auto px-4 py-3 flex flex-col gap-1">
            <a href="${flightsUrl}"  class="block px-2 py-3 rounded">Vuelos</a>
            <a href="${packagesUrl}" class="block px-2 py-3 rounded">Paquetes</a>
            <div class="h-px bg-white/10 my-2"></div>
            <a href="${loginUrl}" class="block px-2 py-3 rounded">Iniciar sesión</a>
        </nav>
    </div>
</header>
