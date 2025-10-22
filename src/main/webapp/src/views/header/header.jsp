<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/src/components/layout/libs.jspf" %>
<!-- Taglibs aquí una sola vez -->

<header class="sticky top-0 z-50 bg-brand text-white shadow-md">
    <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <a href="${homeUrl}" class="flex items-center gap-2">
            <span class="text-xl font-bold">Volando<span class="text-yellow-300">.uy</span></span>
        </a>


        <nav id="nav-desktop" class="hidden md:flex items-center gap-6">
            <a href="${flightsUrl}" class="hover:text-yellow-300">Vuelos</a>
            <a href="${packagesUrl}" class="hover:text-yellow-300">Paquetes</a>
            <a href="${listUsersUrl}" class="hover:text-yellow-300"> Usuarios </a>
        </nav>

        <c:choose>
            <c:when test="${empty sessionScope.nickname}">
                <div id="header-actions" class="hidden md:flex items-center gap-3">
                    <a href="${loginUrl}" class="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20">Iniciar sesión</a>
                </div>
            </c:when>
            <c:otherwise>
                <div id="header-actions" class="hidden md:flex items-center gap-3">
                    <a href="${profileUrl}"
                       class="inline-flex items-center justify-center px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-center">
                        Ver Perfil
                    </a>

                    <form action="${logoutUrl}" method="post" title="Cerrar sesión de ${nickname}">
                        <button type="submit"
                                class="inline-flex items-center justify-center px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-center appearance-none">
                            Cerrar sesión
                        </button>
                    </form>

                </div>
            </c:otherwise>

        </c:choose>
    </div>

</header>
