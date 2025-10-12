package servlets.reservations;

import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/book-flight")
public class BookFlightServlet extends HttpServlet {
    private final IFlightController flightCtrl = ControllerFactory.getFlightController();
    private final IFlightRouteController routeCtrl = ControllerFactory.getFlightRouteController();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String flightName = req.getParameter("flight");
        if (flightName != null && !flightName.isBlank()) {
            req.getRequestDispatcher("/src/views/flight/bookFlight.jsp").forward(req, resp);
            return;
        }

        List<FlightDTO> raw;
        try { raw = flightCtrl.getAllFlightsDetails(); } catch (Throwable t) { raw = Collections.emptyList(); }
        List<FlightDTO> flights = new ArrayList<>(raw);
        flights.sort(Comparator.comparing(f -> Optional.ofNullable(f.getDepartureTime()).orElse(LocalDateTime.MAX)));

        List<Map<String,Object>> cards = new ArrayList<>();

        for (FlightDTO f : flights) {
            if (f == null) continue;

            LocalDateTime dep = f.getDepartureTime();
            Duration totalDur = toDurationMinutes(f.getDuration());
            LocalDateTime arr = (dep != null && totalDur != null) ? dep.plus(totalDur) : null;

            String airline = safe(f.getAirlineNickname());
            String logo    = safe(f.getImage());

            FlightRouteDTO fr = null;
            try { if (f.getFlightRouteName() != null) fr = routeCtrl.getFlightRouteDetailsByName(f.getFlightRouteName()); }
            catch (Throwable ignored) {}

            // ------- precio (turista por defecto) ------
            Double priceTurist = (fr != null) ? fr.getPriceTouristClass() : null;
            double price = (priceTurist != null) ? priceTurist : 0.0;

            // ------- tramos (legs) -------
            List<Map<String,String>> legs = new ArrayList<>();
            if (fr != null && fr.getFlightsNames() != null && !fr.getFlightsNames().isEmpty()) {
                for (String legName : fr.getFlightsNames()) {
                    try {
                        FlightDTO leg = flightCtrl.getFlightDetailsByName(legName);
                        if (leg == null) continue;
                        FlightRouteDTO legRoute = null;
                        if (leg.getFlightRouteName() != null) {
                            try { legRoute = routeCtrl.getFlightRouteDetailsByName(leg.getFlightRouteName()); } catch (Throwable ignored2) {}
                        }
                        String legOrigin = (legRoute != null) ? safe(legRoute.getOriginCityName()) : "";
                        String legDest   = (legRoute != null) ? safe(legRoute.getDestinationCityName()) : "";
                        Duration legDur  = toDurationMinutes(leg.getDuration());

                        legs.add(Map.of("origen", legOrigin, "destino", legDest, "tiempo", fmtDuration(legDur)));
                    } catch (Throwable ignored) {}
                }
            }
            if (legs.isEmpty()) {
                String o = (fr != null) ? safe(fr.getOriginCityName())      : "";
                String d = (fr != null) ? safe(fr.getDestinationCityName()) : "";
                legs.add(Map.of("origen", o, "destino", d, "tiempo", fmtDuration(totalDur)));
            }

            String totalTxt = fmtDuration(totalDur);
            int conexiones  = Math.max(0, legs.size()-1);
            boolean nextDay = (dep != null && arr != null)
                    ? arr.toLocalDate().isAfter(dep.toLocalDate())
                    : (totalDur != null && totalDur.toHours() >= 24);

            // ------- fechas formateadas para UI + filtro -------
            String fechaISO    = (dep != null) ? dep.toLocalDate().toString() : "";
            String fechaPretty = (dep != null)
                    ? dep.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "—";

            Map<String,Object> vm = new LinkedHashMap<>();
            vm.put("aerolinea", airline);
            vm.put("logo", logo);
            vm.put("duracion", totalTxt);
            vm.put("salidaHora", fmtTime(dep));
            vm.put("salidaFechaISO", fechaISO);
            vm.put("salidaFechaPretty", fechaPretty);
            vm.put("salidaCiudad", legs.get(0).get("origen"));
            vm.put("llegadaHora", fmtTime(arr));
            vm.put("llegadaCiudad", legs.get(legs.size()-1).get("destino"));
            vm.put("nextDay", nextDay);
            vm.put("conexiones", conexiones);

            vm.put("precio", price);                    // número
            vm.put("precioStr", String.format(Locale.US, "US$%.2f", price)); // formateado
            vm.put("tipo", "Estándar");
            vm.put("operadoPor", airline);

            vm.put("legsJson", toJsonArrayOfLegs(legs));
            String metaJson = "{\"total\":" + jsonQuote(totalTxt) + ",\"operadoPor\":" + jsonQuote(airline) + "}";
            vm.put("metaJson", metaJson);

            cards.add(vm);
        }

        Set<String> airlines = cards.stream()
                .map(m -> (String) m.get("aerolinea"))
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toCollection(TreeSet::new));

        req.setAttribute("flightCards", cards);
        req.setAttribute("airlines", airlines);
        req.getRequestDispatcher("/src/views/flight/flight.jsp").forward(req, resp);
    }

    /* ===== helpers ===== */

    private static String safe(Object o) { return (o == null) ? "" : String.valueOf(o); }

    private static String fmtTime(LocalDateTime dt) {
        if (dt == null) return "—";
        return dt.format(DateTimeFormatter.ofPattern("h:mm a", new Locale("es","UY")))
                .toLowerCase(Locale.ROOT);
    }

    private static String fmtDuration(Duration d) {
        if (d == null) return "—";
        long h = d.toHours();
        long m = d.minusHours(h).toMinutes();
        return h + "h " + String.format("%02dm", m);
    }

    /** Convierte minutos (Integer/Long/etc.) a Duration. */
    private static Duration toDurationMinutes(Object minutes) {
        if (minutes == null) return null;
        if (minutes instanceof Number n) return Duration.ofMinutes(n.longValue());
        // por si llega como String "310" o "310 min"
        try {
            String s = String.valueOf(minutes).trim().toLowerCase(Locale.ROOT).replace("min","").trim();
            long m = Long.parseLong(s);
            return Duration.ofMinutes(m);
        } catch (Exception e) {
            return null;
        }
    }

    private static String jsonQuote(String s) {
        if (s == null) return "\"\"";
        String r = s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
        return "\"" + r + "\"";
    }

    private static String toJsonArrayOfLegs(List<Map<String, String>> legs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < legs.size(); i++) {
            Map<String, String> r = legs.get(i);
            sb.append("{")
                    .append("\"origen\":").append(jsonQuote(r.get("origen"))).append(",")
                    .append("\"destino\":").append(jsonQuote(r.get("destino"))).append(",")
                    .append("\"tiempo\":").append(jsonQuote(r.get("tiempo")))
                    .append("}");
            if (i < legs.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
