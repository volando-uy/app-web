package servlets.flight;

import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/flight/list")
public class FlightServlet extends HttpServlet {

    private final IFlightController flightCtrl = ControllerFactory.getFlightController();
    private final IFlightRouteController routeCtrl = ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        try { req.setCharacterEncoding("UTF-8"); } catch (Exception ignored) {}

        String flightNameParam = req.getParameter("flight");
        if (flightNameParam != null && !flightNameParam.isBlank()) {
            req.getRequestDispatcher("/src/views/flight/bookFlight.jsp").forward(req, resp);
            return;
        }

        String qOrigen  = safe(req.getParameter("origen")).trim().toLowerCase(Locale.ROOT);
        String qDestino = safe(req.getParameter("destino")).trim().toLowerCase(Locale.ROOT);
        String qFecha   = safe(req.getParameter("fecha")).trim(); // yyyy-MM-dd
        String qAero    = safe(req.getParameter("aerolinea")).trim().toLowerCase(Locale.ROOT);

        List<FlightDTO> raw;
        try { raw = flightCtrl.getAllFlightsDetails(); }
        catch (Throwable t) { raw = Collections.emptyList(); }

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
            try {
                String frName = f.getFlightRouteName();
                if (frName != null) fr = routeCtrl.getFlightRouteDetailsByName(frName);
            } catch (Throwable ignored) {}

            List<Map<String,String>> legs = buildLegs(f, fr);

            // ---- aplicar filtros ----
            String fechaISO   = (dep != null) ? dep.toLocalDate().toString() : "";
            String origenCity = legs.isEmpty() ? "" : safe(legs.get(0).get("origen"));
            String destCity   = legs.isEmpty() ? "" : safe(legs.get(legs.size()-1).get("destino"));

            boolean okA   = qAero.isEmpty()    || airline.toLowerCase(Locale.ROOT).contains(qAero);
            boolean okF   = qFecha.isEmpty()   || qFecha.equals(fechaISO);
            boolean okOri = qOrigen.isEmpty()  || origenCity.toLowerCase(Locale.ROOT).contains(qOrigen);
            boolean okDes = qDestino.isEmpty() || destCity.toLowerCase(Locale.ROOT).contains(qDestino);
            if (!(okA && okF && okOri && okDes)) continue;

            // ---- view-model  ---
            String totalTxt = fmtDuration(totalDur);
            int conexiones  = Math.max(0, legs.size() - 1);
            boolean nextDay = (dep != null && arr != null)
                    ? arr.toLocalDate().isAfter(dep.toLocalDate())
                    : (totalDur != null && totalDur.toHours() >= 24);

            String fechaPretty = (dep != null)
                    ? dep.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "—";

            Double priceTurist = (fr != null) ? fr.getPriceTouristClass() : null;
            double price = (priceTurist != null) ? priceTurist : 0.0;

            Map<String,Object> vm = new LinkedHashMap<>();
            vm.put("aerolinea", airline);
            vm.put("logo", logo);
            vm.put("duracion", totalTxt);
            vm.put("salidaHora", fmtTime(dep));
            vm.put("salidaFechaISO", fechaISO);
            vm.put("salidaFechaPretty", fechaPretty);
            vm.put("salidaCiudad", origenCity);
            vm.put("llegadaHora", fmtTime(arr));
            vm.put("llegadaCiudad", destCity);
            vm.put("nextDay", nextDay);
            vm.put("conexiones", conexiones);
            vm.put("precio", price);
            vm.put("precioStr", String.format(Locale.US, "US$%.2f", price));
            vm.put("tipo", "Estándar");
            vm.put("operadoPor", airline);
            vm.put("flightName", pickFlightName(f)); // soporta getName() o getFlightName()

            cards.add(vm);
        }

        Set<String> airlines = cards.stream()
                .map(m -> (String) m.get("aerolinea"))
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toCollection(TreeSet::new));

        req.setAttribute("flightCards", cards);
        req.setAttribute("airlines", airlines);

        // ====== Modal server-side: ?modal=<nombreDelVuelo> ======
        String modalParam = req.getParameter("modal");
        if (modalParam != null && !modalParam.isBlank()) {
            try {
                FlightDTO f = flightCtrl.getFlightDetailsByName(modalParam);
                if (f != null) {
                    FlightRouteDTO fr = null;
                    try {
                        String frName = f.getFlightRouteName();
                        if (frName != null) fr = routeCtrl.getFlightRouteDetailsByName(frName);
                    } catch (Throwable ignored) {}

                    List<Map<String,String>> legs = new ArrayList<>();
                    if (fr != null && fr.getFlightsNames() != null && !fr.getFlightsNames().isEmpty()) {
                        for (String legName : fr.getFlightsNames()) {
                            try {
                                FlightDTO leg = flightCtrl.getFlightDetailsByName(legName);
                                if (leg == null) continue;

                                FlightRouteDTO legRoute = null;
                                if (leg.getFlightRouteName() != null) {
                                    try { legRoute = routeCtrl.getFlightRouteDetailsByName(leg.getFlightRouteName()); }
                                    catch (Throwable ignored2) {}
                                }

                                String legOrigin = (legRoute != null) ? safe(legRoute.getOriginCityName()) : safe(tryGet(leg, "getOriginCityName"));
                                String legDest   = (legRoute != null) ? safe(legRoute.getDestinationCityName()) : safe(tryGet(leg, "getDestinationCityName"));
                                Duration legDur  = toDurationMinutes(leg.getDuration());

                                legs.add(Map.of("origen", legOrigin, "destino", legDest, "tiempo", fmtDuration(legDur)));
                            } catch (Throwable ignored3) {}
                        }
                    }

                    // Fallback: al menos 1 tramo coherente
                    if (legs.isEmpty()) {
                        String o = (fr != null) ? safe(fr.getOriginCityName())      : safe(tryGet(f, "getOriginCityName"));
                        String d = (fr != null) ? safe(fr.getDestinationCityName()) : safe(tryGet(f, "getDestinationCityName"));
                        Duration totalDur = toDurationMinutes(f.getDuration());
                        legs.add(Map.of("origen", o, "destino", d, "tiempo", fmtDuration(totalDur)));
                    }

                    Map<String,Object> modalVm = new LinkedHashMap<>();
                    modalVm.put("total", fmtDuration(toDurationMinutes(f.getDuration())));
                    modalVm.put("operadoPor", safe(f.getAirlineNickname()));
                    modalVm.put("legs", legs);

                    req.setAttribute("modalFlight", modalVm);
                }
            } catch (Throwable ignored) { /* sin modal si falla */ }
        }

        req.getRequestDispatcher("/src/views/flight/flight.jsp").forward(req, resp);
    }


    private static String safe(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }

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

    /** Acepta Number o String ("310" o "310 min"). */
    private static Duration toDurationMinutes(Object minutes) {
        if (minutes == null) return null;
        if (minutes instanceof Number n) return Duration.ofMinutes(n.longValue());
        try {
            String s = String.valueOf(minutes).trim().toLowerCase(Locale.ROOT)
                    .replace("min","").trim();
            long m = Long.parseLong(s);
            return Duration.ofMinutes(m);
        } catch (Exception e) { return null; }
    }

    /** Construye legs a partir de la route; si no hay, deja lista vacía. */
    private List<Map<String,String>> buildLegs(FlightDTO f, FlightRouteDTO fr) {
        List<Map<String,String>> legs = new ArrayList<>();

        if (fr != null && fr.getFlightsNames() != null && !fr.getFlightsNames().isEmpty()) {
            for (String legName : fr.getFlightsNames()) {
                try {
                    FlightDTO leg = flightCtrl.getFlightDetailsByName(legName);
                    if (leg == null) continue;

                    FlightRouteDTO legRoute = null;
                    if (leg.getFlightRouteName() != null) {
                        try { legRoute = routeCtrl.getFlightRouteDetailsByName(leg.getFlightRouteName()); }
                        catch (Throwable ignored) {}
                    }

                    String legOrigin = (legRoute != null) ? safe(legRoute.getOriginCityName()) : safe(tryGet(leg, "getOriginCityName"));
                    String legDest   = (legRoute != null) ? safe(legRoute.getDestinationCityName()) : safe(tryGet(leg, "getDestinationCityName"));
                    Duration legDur  = toDurationMinutes(leg.getDuration());

                    legs.add(Map.of("origen", legOrigin, "destino", legDest, "tiempo", fmtDuration(legDur)));
                } catch (Throwable ignored) {}
            }
        }
        return legs;
    }

    private static String pickFlightName(FlightDTO f) {
        try {
            Method m = f.getClass().getMethod("getName");
            Object v = m.invoke(f);
            if (v != null) return String.valueOf(v);
        } catch (Exception ignored) {}

        try {
            Method m = f.getClass().getMethod("getFlightName");
            Object v = m.invoke(f);
            if (v != null) return String.valueOf(v);
        } catch (Exception ignored) {}

        // backup legible
        String air = Optional.ofNullable(f.getAirlineNickname()).orElse("FLIGHT");
        LocalDate d = Optional.ofNullable(f.getDepartureTime()).map(LocalDateTime::toLocalDate).orElse(LocalDate.now());
        return air + "-" + d;
    }

    private static String tryGet(Object obj, String getter) {
        if (obj == null) return "";
        try {
            var m = obj.getClass().getMethod(getter);
            Object v = m.invoke(obj);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception e) {
            return "";
        }
    }
}
