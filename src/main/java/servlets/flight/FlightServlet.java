package servlets.flight;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import controllers.category.ICategoryController;
import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import controllers.user.IUserController;

import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.models.enums.EnumEstatusRuta;

import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/flight/list")
public class FlightServlet extends HttpServlet {

    private final ICategoryController categoryController = ControllerFactory.getCategoryController();
    private final IUserController     userController     = ControllerFactory.getUserController();
    private final IFlightController   flightCtrl         = ControllerFactory.getFlightController();
    private final IFlightRouteController flightRouteCtrl = ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try { req.setCharacterEncoding("UTF-8"); } catch (Exception ignored) {}
        resp.setCharacterEncoding("UTF-8");

        String selectedAirline    = trim(req.getParameter("airline"));
        String selectedCategory   = trim(req.getParameter("category"));
        String selectedRouteName  = trim(req.getParameter("route"));
        String selectedFlightName = trim(req.getParameter("flight"));

        loadFilters(req);

        loadRoutesAndFlights(req, selectedAirline, selectedCategory, selectedRouteName, selectedFlightName);

        req.getRequestDispatcher("/src/views/flight/flight.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String airline  = trim(req.getParameter("airline"));
        String category = trim(req.getParameter("category"));
        String route    = trim(req.getParameter("route"));
        String flight   = trim(req.getParameter("flight"));

        StringBuilder to = new StringBuilder(req.getContextPath()).append("/flight/list?");
        if (airline  != null) to.append("airline=").append(url(airline)).append("&");
        if (category != null) to.append("category=").append(url(category)).append("&");
        if (route    != null) to.append("route=").append(url(route)).append("&");
        if (flight   != null) to.append("flight=").append(url(flight)).append("&");
        if (to.charAt(to.length()-1) == '&' || to.charAt(to.length()-1) == '?') {
            to.deleteCharAt(to.length()-1);
        }
        resp.sendRedirect(to.toString());
    }


    private void loadFilters(HttpServletRequest req) {
        // Categorías
        List<String> categories;
        try {
            List<String> raw = categoryController.getAllCategoriesNames();
            categories = new ArrayList<>(raw != null ? raw : Collections.emptyList());
        } catch (Exception e) {
            log("Error cargando categorías", e);
            categories = new ArrayList<>();
        }
        categories.sort(String.CASE_INSENSITIVE_ORDER);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", trim(req.getParameter("category")));

        List<String> airlines;
        try {
            List<String> raw = userController.getAllAirlinesNicknames();
            airlines = new ArrayList<>(raw != null ? raw : Collections.emptyList());
        } catch (Exception e) {
            log("Error cargando aerolíneas", e);
            airlines = new ArrayList<>();
        }
        airlines.sort(String.CASE_INSENSITIVE_ORDER);
        req.setAttribute("airlines", airlines);
        req.setAttribute("selectedAirline", trim(req.getParameter("airline")));
    }

    private void loadRoutesAndFlights(HttpServletRequest req,
                                      String selectedAirline,
                                      String selectedCategory,
                                      String selectedRouteName,
                                      String selectedFlightName) {

        List<FlightDTO> allFlights;
        try {
            allFlights = flightCtrl.getAllFlightsDetails();
            if (allFlights == null) allFlights = Collections.emptyList();
        } catch (Exception e) {
            log("Error obteniendo todos los vuelos", e);
            allFlights = Collections.emptyList();
        }

        // Rutas  en los vuelos
        Set<String> routeNames = new HashSet<>();
        for (FlightDTO f : allFlights) {
            if (f == null) continue;
            if (selectedAirline != null && !selectedAirline.isBlank()) {
                String a = f.getAirlineNickname();
                if (a == null || !a.equalsIgnoreCase(selectedAirline)) continue;
            }
            String rn = f.getFlightRouteName();
            if (rn != null && !rn.isBlank()) routeNames.add(rn);
        }

        List<FlightRouteDTO> allRoutesRaw = new ArrayList<>();
        for (String rn : routeNames) {
            try {
                FlightRouteDTO r = flightRouteCtrl.getFlightRouteDetailsByName(rn);
                if (r != null) allRoutesRaw.add(r);
            } catch (Exception ex) {
                log("Error obteniendo ruta por nombre: " + rn, ex);
            }
        }

        // Filtrar rutas
        List<FlightRouteDTO> routesFiltered = new ArrayList<>();
        for (FlightRouteDTO r : allRoutesRaw) {
            if (r == null || r.getStatus() != EnumEstatusRuta.CONFIRMADA) continue;

            boolean okCategory = (selectedCategory == null || selectedCategory.isBlank());
            if (!okCategory && r.getCategories() != null) {
                for (String c : r.getCategories()) {
                    if (c != null && c.equalsIgnoreCase(selectedCategory)) { okCategory = true; break; }
                }
            }
            if (!okCategory) continue;

            if (selectedAirline != null && !selectedAirline.isBlank()) {
                String rAir = r.getAirlineNickname();
                if (rAir == null || !rAir.equalsIgnoreCase(selectedAirline)) continue;
            }

            routesFiltered.add(r);
        }
        routesFiltered.sort(Comparator.comparing(FlightRouteDTO::getName, String.CASE_INSENSITIVE_ORDER));
        req.setAttribute("routes", routesFiltered);


        boolean routeMatchesFilter = false;
        if (selectedRouteName != null && !selectedRouteName.isBlank()) {
            for (FlightRouteDTO r : routesFiltered) {
                if (r.getName() != null && r.getName().equalsIgnoreCase(selectedRouteName)) {
                    routeMatchesFilter = true; break;
                }
            }
        }
        if (!routeMatchesFilter) {
            selectedRouteName  = null;
            selectedFlightName = null;
        }
        req.setAttribute("selectedRouteName", selectedRouteName);
        req.setAttribute("selectedFlightName", selectedFlightName);

        // Ruta seleccionada
        FlightRouteDTO selectedRoute = null;
        if (selectedRouteName != null && !selectedRouteName.isBlank()) {
            for (FlightRouteDTO r : routesFiltered) {
                if (r.getName() != null && r.getName().equalsIgnoreCase(selectedRouteName)) {
                    selectedRoute = r; break;
                }
            }
            if (selectedRoute == null) {
                try { selectedRoute = flightRouteCtrl.getFlightRouteDetailsByName(selectedRouteName); }
                catch (Exception e) { log("Error obteniendo ruta seleccionada: " + selectedRouteName, e); }
            }
        }
        req.setAttribute("selectedRoute", selectedRoute);

        // Vuelos a mostrar
        List<FlightDTO> flightsToShow = new ArrayList<>();
        if (selectedRouteName != null && !selectedRouteName.isBlank()) {
            for (FlightDTO f : allFlights) {
                if (f == null) continue;
                String rn = f.getFlightRouteName();
                if (rn != null && rn.equalsIgnoreCase(selectedRouteName)) {
                    if (selectedAirline != null && !selectedAirline.isBlank()) {
                        String a = f.getAirlineNickname();
                        if (a == null || !a.equalsIgnoreCase(selectedAirline)) continue;
                    }
                    flightsToShow.add(f);
                }
            }
        }
        flightsToShow.sort(Comparator.comparing(FlightDTO::getDepartureTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
        req.setAttribute("flights", flightsToShow);

        // Vuelo seleccionado
        FlightDTO selectedFlight = null;
        if (selectedFlightName != null && !selectedFlightName.isBlank()) {
            for (FlightDTO f : flightsToShow) {
                if (f != null && f.getName() != null &&
                        f.getName().equalsIgnoreCase(selectedFlightName)) {
                    selectedFlight = f; break;
                }
            }
        }
        req.setAttribute("selectedFlight", selectedFlight);
    }

    private static String trim(String s) { return (s == null) ? null : s.trim(); }
    private static String url(String s) {
        try { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }
}
