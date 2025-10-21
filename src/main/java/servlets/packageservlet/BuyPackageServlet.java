package servlets.packageservlet;

import controllers.flightRoute.IFlightRouteController;
import controllers.flightRoutePackage.IFlightRoutePackageController;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.dtos.flightRoutePackage.BaseFlightRoutePackageDTO;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet()
public class  BuyPackageServlet extends HttpServlet {

    private final IFlightRoutePackageController pkgCtrl =
            ControllerFactory.getFlightRoutePackageController();
    private final IFlightRouteController routeCtrl =
            ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        try { req.setCharacterEncoding("UTF-8"); } catch (Exception ignored) {}

        String q = nvl(req.getParameter("q")).trim().toLowerCase(Locale.ROOT);

        List<BaseFlightRoutePackageDTO> pkgs = getPackagesPreferWithRoutes();

        // ====== Cards para grilla ======
        List<Map<String,Object>> cards = new ArrayList<>();
        for (BaseFlightRoutePackageDTO p : pkgs) {
            if (p == null) continue;

            String name = nvl(p.getName());
            String desc = nvl(p.getDescription());
            Double total = p.getTotalPrice();

            // Rutas del paquete (solo CONFIRMADA)
            List<FlightRouteDTO> routes = getConfirmedRoutesByPackageName(name);

            // Cover: primera imagen disponible
            String cover = "";
            for (FlightRouteDTO r : routes) {
                if (r != null && r.getImage() != null && !r.getImage().isBlank()) {
                    cover = r.getImage(); break;
                }
            }

            // Suma referencia (turista)
            double sumRef = 0D;
            for (FlightRouteDTO r : routes) {
                if (r != null && r.getPriceTouristClass() != null) {
                    sumRef += r.getPriceTouristClass();
                }
            }

            boolean okQ = q.isEmpty()
                    || name.toLowerCase(Locale.ROOT).contains(q)
                    || desc.toLowerCase(Locale.ROOT).contains(q)
                    || routes.stream().anyMatch(r ->
                    nvl(r != null ? r.getOriginAeroCode()      : "").toLowerCase(Locale.ROOT).contains(q)
                            || nvl(r != null ? r.getDestinationAeroCode() : "").toLowerCase(Locale.ROOT).contains(q)
                            || nvl(r != null ? r.getAirlineNickname()     : "").toLowerCase(Locale.ROOT).contains(q)
                            || nvl(r != null ? r.getName()                : "").toLowerCase(Locale.ROOT).contains(q)
            );
            if (!okQ) continue;

            double tot = (total == null ? 0D : total);

            Map<String,Object> card = new LinkedHashMap<>();
            card.put("name",        name);
            card.put("description", desc);
            card.put("total",       tot);
            card.put("totalStr",    String.format(Locale.US, "US$ %.2f", tot));
            card.put("cover",       cover);
            card.put("sumRef",      sumRef);
            card.put("sumRefStr",   String.format(Locale.US, "US$ %.2f", sumRef));
            card.put("pkgName",     name);
            cards.add(card);
        }

        req.setAttribute("packageCards", cards);

        // ====== Modal paquete ======
        String modalParam = req.getParameter("modal");
        if (modalParam != null && !modalParam.isBlank()) {
            BaseFlightRoutePackageDTO found = null;
            for (BaseFlightRoutePackageDTO p : pkgs) {
                if (p != null && modalParam.equals(p.getName())) { found = p; break; }
            }

            if (found != null) {
                List<FlightRouteDTO> routes = getConfirmedRoutesByPackageName(found.getName());

                List<Map<String,Object>> rows = new ArrayList<>();
                for (FlightRouteDTO r : routes) {
                    if (r == null) continue;
                    double pt = (r.getPriceTouristClass() != null ? r.getPriceTouristClass() : 0D);

                    Map<String,Object> row = new LinkedHashMap<>();
                    row.put("origin",          nvl(r.getOriginAeroCode()));
                    row.put("destination",     nvl(r.getDestinationAeroCode()));
                    row.put("code",            nvl(r.getName()));
                    row.put("airline",         nvl(r.getAirlineNickname()));
                    row.put("priceTouristStr", String.format(Locale.US, "US$ %.2f", pt));
                    rows.add(row);
                }

                Map<String,Object> modalVm = new LinkedHashMap<>();
                modalVm.put("name",        nvl(found.getName()));
                modalVm.put("description", nvl(found.getDescription()));
                double total = (found.getTotalPrice() != null ? found.getTotalPrice() : 0D);
                modalVm.put("totalStr",    String.format(Locale.US, "US$ %.2f", total));

                // seatType opcional (sin reflection)
                modalVm.put("seatType", "");

                modalVm.put("routes", rows);
                req.setAttribute("modalPackage", modalVm);
            }
        }

        // ====== Modal ruta dentro del paquete ======
        String routeParam = req.getParameter("route");
        if (modalParam != null && !modalParam.isBlank()
                && routeParam != null && !routeParam.isBlank()) {
            try {
                FlightRouteDTO r = routeCtrl.getFlightRouteDetailsByName(routeParam);
                if (r != null) {
                    Map<String,Object> routeVm = new LinkedHashMap<>();
                    routeVm.put("pkgName",     modalParam);
                    routeVm.put("code",        nvl(r.getName()));
                    routeVm.put("origin",      nvl(r.getOriginAeroCode()));
                    routeVm.put("destination", nvl(r.getDestinationAeroCode()));
                    routeVm.put("airline",     nvl(r.getAirlineNickname()));
                    routeVm.put("status",      (r.getStatus() != null ? r.getStatus().name() : ""));
                    routeVm.put("image",       nvl(r.getImage()));

                    Double pt = r.getPriceTouristClass();
                    Double pb = r.getPriceBusinessClass();
                    routeVm.put("priceTouristStr",  String.format(Locale.US, "US$ %.2f", (pt==null?0D:pt)));
                    routeVm.put("priceBusinessStr", String.format(Locale.US, "US$ %.2f", (pb==null?0D:pb)));

                    // seatType del paquete (sin reflection)
                    routeVm.put("seatType", "");

                    req.setAttribute("modalRoute", routeVm);
                }
            } catch (Throwable ignored) {}
        }

        // Forward (sin mover carpetas)
        req.getRequestDispatcher("/src/views/package/package.jsp").forward(req, resp);
    }

    private List<BaseFlightRoutePackageDTO> getPackagesPreferWithRoutes() {
        try {
            List<BaseFlightRoutePackageDTO> list =
                    pkgCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            return (list != null) ? list : Collections.emptyList();
        } catch (Throwable t) {
            try {
                List<BaseFlightRoutePackageDTO> list =
                        pkgCtrl.getAllFlightRoutesPackagesSimpleDetails();
                return (list != null) ? list : Collections.emptyList();
            } catch (Throwable t2) {
                return Collections.emptyList();
            }
        }
    }

    // utilidades locales

    private List<FlightRouteDTO> getConfirmedRoutesByPackageName(String pkgName) {
        List<FlightRouteDTO> routes;
        try {
            routes = routeCtrl.getAllFlightRoutesDetailsByPackageName(pkgName);
            if (routes == null) routes = Collections.emptyList();
        } catch (Throwable t) {
            routes = Collections.emptyList();
        }
        // filtrar CONFIRMADA
        List<FlightRouteDTO> filtered = new ArrayList<>();
        for (FlightRouteDTO r : routes) {
            if (r != null && r.getStatus() != null
                    && "CONFIRMADA".equalsIgnoreCase(r.getStatus().name())) {
                filtered.add(r);
            }
        }
        return filtered;
        // si preferís mantener el orden original, podés no reordenar aquí
    }

    private static String nvl(String s) { return s == null ? "" : s; }
}
