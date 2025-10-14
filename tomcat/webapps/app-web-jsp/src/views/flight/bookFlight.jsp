<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.*" %>
<%@ page import="domain.dtos.flight.FlightDTO" %>

<%!
    // Helpers en DECLARATION para evitar errores de compilación
    String esc(String s){
        if (s == null) return "";
        StringBuilder b = new StringBuilder(s.length()+16);
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            switch (c){
                case '&': b.append("&amp;"); break;
                case '<': b.append("&lt;"); break;
                case '>': b.append("&gt;"); break;
                case '"': b.append("&quot;"); break;
                case '\'': b.append("&#39;"); break;
                default: b.append(c);
            }
        }
        return b.toString();
    }
    String fmtMoney(Double n, String cur){
        if (n == null) return (cur==null?"":" "+cur)+"--";
        long cents = Math.round(n * 100.0);
        String s = String.format("%d.%02d", cents/100, Math.abs(cents%100));
        return (cur==null?"":cur+" ")+s;
    }
%>

<%
    String ctx = request.getContextPath();
    FlightDTO f = (FlightDTO) request.getAttribute("flight");
    if (f == null) {
%>
<div class="p-6 text-red-700">No se encontró el vuelo.</div>
<%  return; }

    Double pTur = (Double) request.getAttribute("priceTourist");
    Double pBus = (Double) request.getAttribute("priceBusiness");
    String currency = (String) request.getAttribute("currency"); if (currency==null) currency="USD";

    String ok = (request.getAttribute("ok")!=null) ? "1" : null;
    String error = (String) request.getAttribute("error");

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String airline = f.getAirlineNickname();
    String routeName = f.getFlightRouteName();
    String img = (f.getImage()!=null && !f.getImage().isBlank())? f.getImage()
            : "https://picsum.photos/seed/air/400/200";

    String origin  = (String) request.getAttribute("originCity");   // si lo seteás del servlet
    String dest    = (String) request.getAttribute("destCity");
    if (origin == null) origin = "--";
    if (dest   == null) dest   = "--";

    String depTxt  = (f.getDepartureTime()!=null)? dtf.format(f.getDepartureTime()) : "--";
    Integer durMin = f.getDuration()==null? null : f.getDuration().intValue();
    String  durTxt = (durMin==null)? "--" : (durMin/60)+"h "+(durMin%60)+"m";
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Reserva</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-[#f5f6fa]">

<div class="max-w-6xl mx-auto p-4 md:p-6 space-y-4">

    <% if (ok != null) { %>
    <div class="px-4 py-3 rounded bg-emerald-50 text-emerald-800 border border-emerald-200">
        ¡Reserva creada correctamente!
    </div>
    <% } %>

    <% if (error != null) { %>
    <div class="px-4 py-3 rounded bg-red-50 text-red-800 border border-red-200">
        <%= esc(error) %>
    </div>
    <% } %>

    <!-- Cabecera del vuelo -->
    <div class="bg-white rounded-2xl shadow p-5 flex items-start justify-between">
        <div class="flex items-start gap-4">
            <img src="<%= esc(img) %>" alt="cover" class="w-20 h-12 object-cover rounded">
            <div>
                <div class="text-sm text-gray-500">Aerolínea</div>
                <div class="text-xl font-semibold"><%= esc(airline!=null?airline:"--") %></div>
                <div class="text-gray-500 mt-1"><%= esc(origin) %> → <%= esc(dest) %></div>
                <div class="text-gray-500"><%= esc(depTxt) %> · <%= esc(durTxt) %></div>
            </div>
        </div>
        <div class="text-right">
            <div class="text-sm text-gray-500">Tipo</div>
            <div class="font-semibold">Estándar</div>
        </div>
    </div>

    <!-- Formulario -->
    <form id="bookForm" method="post" action="<%= ctx %>/book-flight" class="grid md:grid-cols-3 gap-4">
        <input type="hidden" name="flight" value="<%= esc(f.getName()) %>">
        <input type="hidden" name="total" id="totalInput" value="">
        <input type="hidden" name="pcount" id="pcount" value="1">

        <!-- Columna izquierda -->
        <div class="md:col-span-2 bg-white rounded-2xl shadow p-5 space-y-4">

            <!-- Cabina y cantidad de pasajeros -->
            <div class="grid md:grid-cols-2 gap-3">
                <div>
                    <label class="block text-gray-700 font-semibold mb-1">Clase</label>
                    <select id="seatSelect" name="seat"
                            class="w-full h-11 rounded border border-gray-300 px-3">
                        <% if (pTur != null) { %>
                        <option value="TURISTA" data-price="<%= pTur %>">Turista (<%= esc(currency) %> <%= pTur %>)</option>
                        <% } %>
                        <% if (pBus != null) { %>
                        <option value="EJECUTIVO" data-price="<%= pBus %>">Business (<%= esc(currency) %> <%= pBus %>)</option>
                        <% } %>
                        <% if (pTur == null && pBus == null) { %>
                        <option value="TURISTA" data-price="0">Turista (—)</option>
                        <% } %>
                    </select>
                </div>

                <div>
                    <label class="block text-gray-700 font-semibold mb-1">Pasajeros</label>
                    <div class="flex">
                        <input id="passengers" type="number" min="1" value="1"
                               class="w-full h-11 rounded-l border border-gray-300 px-3 bg-gray-50 pointer-events-none" readonly>
                        <button type="button" id="addRow"
                                class="h-11 px-3 rounded-r bg-[#0B4C73] text-white hover:brightness-110">
                            + Añadir pasajero
                        </button>
                    </div>
                </div>
            </div>

            <!-- Lista de pasajeros -->
            <div>
                <div class="text-gray-700 font-semibold mb-2">Lista de pasajeros</div>
                <div class="border rounded-xl overflow-hidden">
                    <table class="w-full text-sm">
                        <thead class="bg-gray-50 text-gray-600">
                        <tr>
                            <th class="px-3 py-2 text-left">Tipo Doc</th>
                            <th class="px-3 py-2 text-left">Documento</th>
                            <th class="px-3 py-2 text-left">Nombre</th>
                            <th class="px-3 py-2 text-left">Apellido</th>
                            <th class="px-3 py-2 text-left">Equipaje base</th>
                            <th class="px-3 py-2 text-left">Equipaje extra</th>
                            <th class="px-3 py-2">Acción</th>
                        </tr>
                        </thead>
                        <tbody id="rows">
                        <tr class="row" data-idx="0">
                            <td class="px-3 py-2">
                                <select name="doctype_0" class="border rounded px-2 py-1 w-28">
                                    <!-- Ajustá con tus EnumTipoDocumento -->
                                    <option value="CI">CI</option>
                                    <option value="PASAPORTE">PASAPORTE</option>
                                </select>
                            </td>
                            <td class="px-3 py-2"><input name="docnum_0" class="border rounded px-2 py-1 w-36"></td>
                            <td class="px-3 py-2"><input name="name_0"   class="border rounded px-2 py-1 w-36"></td>
                            <td class="px-3 py-2"><input name="surname_0"class="border rounded px-2 py-1 w-36"></td>
                            <td class="px-3 py-2">
                                <select name="basic_0" class="border rounded px-2 py-1">
                                    <!-- Ajustá con tu EnumEquipajeBasico -->
                                    <option value="">--</option>
                                    <option value="BOLSO">Bolso</option>
                                    <option value="MANO">De mano</option>
                                    <option value="VALIJA">Valija</option>
                                </select>
                            </td>
                            <td class="px-3 py-2">
                                <!-- Ajustá con tu EnumEquipajeExtra -->
                                <label class="inline-flex items-center gap-1 mr-2">
                                    <input type="checkbox" name="extra_0" value="EXTRA_VALIJA">
                                    <span>Extra valija</span>
                                </label>
                                <label class="inline-flex items-center gap-1">
                                    <input type="checkbox" name="extra_0" value="EQUIPO_DEPORTIVO">
                                    <span>Equipo deportivo</span>
                                </label>
                            </td>
                            <td class="px-3 py-2 text-center">
                                <button type="button" class="delRow text-red-600 hover:underline">Eliminar</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="flex gap-2">
                <a href="<%= ctx %>/book-flight-list"
                   class="px-4 py-2 rounded border border-gray-300 hover:bg-gray-50">Volver</a>

                <button type="submit"
                        class="px-5 py-2 rounded bg-orange-500 text-white font-semibold hover:bg-orange-600">
                    Confirmar
                </button>
            </div>
        </div>

        <!-- Columna derecha (resumen) -->
        <aside class="bg-white rounded-2xl shadow p-5">
            <div class="text-gray-700 font-semibold mb-3">Resumen</div>
            <div class="flex items-center gap-3 mb-3">
                <img src="<%= esc(img) %>" class="w-20 h-12 object-cover rounded" alt="">
                <div>
                    <div class="font-semibold"><%= esc(origin) %> → <%= esc(dest) %></div>
                    <div class="text-gray-500 text-sm"><%= esc(airline!=null?airline:"") %></div>
                </div>
            </div>

            <div class="text-sm text-gray-600 flex justify-between border-t pt-3">
                <span>Precio base</span>
                <span id="baseLine">
                    <% if (pTur != null) { %><%= esc(currency) %> <%= pTur %> / Turista<% } else { %>--<% } %>
                </span>
            </div>
            <div class="text-sm text-gray-600 flex justify-between">
                <span>Impuestos y tasas</span>
                <span>USD 0</span>
            </div>

            <div class="mt-3 flex justify-between items-center">
                <div class="font-semibold">Total</div>
                <div id="totalText" class="text-lg font-bold">USD 0.00</div>
            </div>
        </aside>

    </form>
</div>

<script src="<%= ctx %>/src/views/flight/bookFlight.js" defer></script>
</body>
</html>
