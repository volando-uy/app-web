package servlets.buyPackage;

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

@WebServlet("/packages/list")
public class BuyPackageServlet extends HttpServlet {

    private final IFlightRoutePackageController pkgCtrl =
            ControllerFactory.getFlightRoutePackageController();
    private final IFlightRouteController routeCtrl =
            ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        try { req.setCharacterEncoding("UTF-8"); } catch (Exception ignored) {}

        String q = nvl(req.getParameter("q")).trim().toLowerCase(Locale.ROOT);

        List<BaseFlightRoutePackageDTO> pkgs = getPackagesPreferWithRoutes();

        List<Map<String,Object>> cards = new ArrayList<>();
        for (BaseFlightRoutePackageDTO p : pkgs) {
            if (p == null) continue;

            String name = nvl(p.getName());
            String desc = nvl(p.getDescription());
            Double total = p.getTotalPrice();

            // rutas del paquete
            List<FlightRouteDTO> routes;
            try {
                routes = routeCtrl.getAllFlightRoutesDetailsByPackageName(name);
                if (routes == null) routes = Collections.emptyList();
            } catch (Throwable t) { routes = Collections.emptyList(); }

            String cover = routes.stream()
                    .map(FlightRouteDTO::getImage)
                    .filter(s -> s != null && !s.isBlank())
                    .findFirst().orElse("");

            double sumRef = routes.stream()
                    .map(FlightRouteDTO::getPriceTouristClass)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            boolean okQ = q.isEmpty()
                    || name.toLowerCase(Locale.ROOT).contains(q)
                    || desc.toLowerCase(Locale.ROOT).contains(q)
                    || routes.stream().anyMatch(r ->
                    nvl(r.getOriginCityName()).toLowerCase(Locale.ROOT).contains(q)
                            || nvl(r.getDestinationCityName()).toLowerCase(Locale.ROOT).contains(q)
                            || nvl(r.getAirlineNickname()).toLowerCase(Locale.ROOT).contains(q)
                            || nvl(r.getName()).toLowerCase(Locale.ROOT).contains(q)
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

        String modalParam = req.getParameter("modal");
        if (modalParam != null && !modalParam.isBlank()) {
            BaseFlightRoutePackageDTO found = pkgs.stream()
                    .filter(p -> p != null && modalParam.equals(p.getName()))
                    .findFirst().orElse(null);

            if (found != null) {
                List<FlightRouteDTO> routes;
                try {
                    routes = routeCtrl.getAllFlightRoutesDetailsByPackageName(found.getName());
                    if (routes == null) routes = Collections.emptyList();
                } catch (Throwable t) { routes = Collections.emptyList(); }

                List<Map<String,Object>> rows = new ArrayList<>();
                for (FlightRouteDTO r : routes) {
                    double pt = (r.getPriceTouristClass() != null ? r.getPriceTouristClass() : 0D);
                    Map<String,Object> row = new LinkedHashMap<>();
                    row.put("origin",          nvl(r.getOriginCityName()));
                    row.put("destination",     nvl(r.getDestinationCityName()));
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
                modalVm.put("routes",      rows);
                try {
                    var m = found.getClass().getMethod("getSeatType");
                    Object v = m.invoke(found);
                    modalVm.put("seatType", v == null ? "" : String.valueOf(v));
                } catch (Exception ignored) {}
                req.setAttribute("modalPackage", modalVm);
            }
        }

        String routeParam = req.getParameter("route");
        if (modalParam != null && !modalParam.isBlank()
                && routeParam != null && !routeParam.isBlank()) {
            try {
                FlightRouteDTO r = routeCtrl.getFlightRouteDetailsByName(routeParam);
                if (r != null) {
                    Map<String,Object> routeVm = new LinkedHashMap<>();
                    routeVm.put("pkgName",      modalParam);
                    routeVm.put("code",         nvl(r.getName()));
                    routeVm.put("origin",       nvl(r.getOriginCityName()));
                    routeVm.put("destination",  nvl(r.getDestinationCityName()));
                    routeVm.put("airline",      nvl(r.getAirlineNickname()));
                    routeVm.put("status",       (r.getStatus() != null ? r.getStatus().name() : ""));
                    routeVm.put("image",        nvl(r.getImage()));
                    Double pt = r.getPriceTouristClass();
                    Double pb = r.getPriceBusinessClass();
                    routeVm.put("priceTouristStr",  String.format(Locale.US, "US$ %.2f", (pt==null?0D:pt)));
                    routeVm.put("priceBusinessStr", String.format(Locale.US, "US$ %.2f", (pb==null?0D:pb)));

                    Object seat = "";
                    try {
                        BaseFlightRoutePackageDTO found = pkgs.stream()
                                .filter(p -> p != null && modalParam.equals(p.getName()))
                                .findFirst().orElse(null);
                        if (found != null) {
                            var m = found.getClass().getMethod("getSeatType");
                            seat = m.invoke(found);
                        }
                    } catch (Exception ignored) {}
                    routeVm.put("seatType", seat == null ? "" : String.valueOf(seat));

                    req.setAttribute("modalRoute", routeVm);
                }
            } catch (Throwable ignored) {}
        }

        req.getRequestDispatcher("/src/views/package/package.jsp").forward(req, resp);
    }

    private List<BaseFlightRoutePackageDTO> getPackagesPreferWithRoutes() {
        try {
            var list = pkgCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            return (list != null) ? list : Collections.emptyList();
        } catch (Throwable t) {
            try {
                var list = pkgCtrl.getAllFlightRoutesPackagesSimpleDetails();
                return (list != null) ? list : Collections.emptyList();
            } catch (Throwable t2) { return Collections.emptyList(); }
        }
    }

    private static String nvl(String s) { return s == null ? "" : s; }
}
