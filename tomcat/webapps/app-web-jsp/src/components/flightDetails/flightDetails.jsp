<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="bg-white shadow-md rounded-2xl p-8 border border-gray-200">
    <h2 class="text-2xl font-semibold mb-4 text-brand text-center">Detalles del vuelo</h2>

    <div class="space-y-2 text-gray-700">
        <p><strong>Nombre:</strong> ${flight.name}</p>
        <p><strong>Ruta:</strong> ${flight.flightRouteName}</p>
        <p><strong>Aerolinea:</strong> ${flight.airlineNickname}</p>
        <p><strong>Duracion estimada:</strong> ${flight.duration} minutos</p>
        <p><strong>Salida:</strong> ${flight.departureTime}</p>
        <p><strong>Asientos Economy:</strong> ${flight.maxEconomySeats}</p>
        <p><strong>Asientos Business:</strong> ${flight.maxBusinessSeats}</p>
        <p><strong>Fecha de creacion:</strong> ${flight.createdAt}</p>
    </div>

    <c:if test="${not empty flight.image}">
        <div class="mt-6 flex justify-center">
            <img src="${rootUrl}image?resourceClassName=${flight.getClass().getSimpleName()}&key=${flight.getName()}" alt="Imagen del vuelo"
                 class="rounded-xl shadow-lg w-full max-w-md object-cover">
        </div>
    </c:if>

    <div class="mt-6 text-center">
        <a href="${pageContext.request.contextPath}/flightRoute?airline=${param.airline}&route=${param.route}"
           class="inline-block text-brand font-medium hover:underline">
             Volver a ruta
        </a>
    </div>
</section>
