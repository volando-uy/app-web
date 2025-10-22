package servlets.packageservlet;

import controllers.flightroute.IFlightRouteController;
import controllers.flightroutepackage.IFlightRoutePackageController;

import domain.dtos.flightroute.FlightRouteDTO;
import domain.dtos.flightroutepackage.BaseFlightRoutePackageDTO;
import domain.models.enums.EnumEstatusRuta;

import factory.ControllerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/packages/list")
public class PackageServlet extends HttpServlet {

    private final IFlightRoutePackageController packageController =
            ControllerFactory.getFlightRoutePackageController();
    private final IFlightRouteController routeCtrl =
            ControllerFactory.getFlightRouteController();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        final String qParam     = trimLower(opt(req.getParameter("q")));
        final String modalParam = opt(req.getParameter("modal"));
        final String routeParam = opt(req.getParameter("route"));

        boolean hadError = false;

        // 1) Paquetes "simples con rutas" (tu método que ya venías usando)
        List<BaseFlightRoutePackageDTO> pkgs;
        try {
            pkgs = packageController.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            if (pkgs == null) pkgs = Collections.emptyList();
        } catch (Exception e) {
            hadError = true;
            log("Error obteniendo paquetes", e);
            pkgs = Collections.emptyList();
        }

        // 2) Rutas por paquete
        Map<String, List<FlightRouteDTO>> pkgRoutes = new HashMap<>();
        for (BaseFlightRoutePackageDTO p : pkgs) {
            if (p == null) continue;
            String pkgName = p.getName();
            if (isBlank(pkgName)) continue;

            List<FlightRouteDTO> routes;
            try {
                routes = routeCtrl.getAllFlightRoutesDetailsByPackageName(pkgName);
                if (routes == null) routes = Collections.emptyList();
            } catch (Exception ex) {
                hadError = true;
                log("No se pudieron obtener rutas para paquete: " + pkgName, ex);
                routes = Collections.emptyList();
            }

            List<FlightRouteDTO> confirmed = routes.stream()
                    .filter(r -> r != null && r.getStatus() == EnumEstatusRuta.CONFIRMADA)
                    .sorted(Comparator.comparing(FlightRouteDTO::getName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());

            pkgRoutes.put(pkgName, confirmed);
        }

        // 3) Sacar paquetes sin rutas confirmadas
        List<BaseFlightRoutePackageDTO> withConfirmed = pkgs.stream()
                .filter(p -> !pkgRoutes.getOrDefault(opt(p.getName()), Collections.emptyList()).isEmpty())
                .collect(Collectors.toList());

        // 4) Filtro
        List<BaseFlightRoutePackageDTO> filtered = withConfirmed;
        if (!qParam.isEmpty()) {
            filtered = withConfirmed.stream().filter(p -> {
                String haystack = (opt(p.getName()) + " " + opt(p.getDescription())).toLowerCase();
                if (haystack.contains(qParam)) return true;

                List<FlightRouteDTO> rs = pkgRoutes.getOrDefault(opt(p.getName()), Collections.emptyList());
                for (FlightRouteDTO r : rs) {
                    String rStr = (opt(r.getName()) + " " + opt(r.getAirlineNickname()) + " "
                            + opt(r.getOriginAeroCode()) + " " + opt(r.getDestinationAeroCode())).toLowerCase();
                    if (rStr.contains(qParam)) return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        // 5) Modal: solo si el paquete existe en el resultado filtrado
        String modalPkgName = null;
        String safeRouteParam = null;

        if (!modalParam.isEmpty() && filtered.stream().anyMatch(p -> opt(p.getName()).equals(modalParam))) {
            modalPkgName = modalParam;
            if (!routeParam.isEmpty()) {
                List<FlightRouteDTO> routes = pkgRoutes.getOrDefault(modalPkgName, Collections.emptyList());
                for (FlightRouteDTO r : routes) {
                    if (r != null && routeParam.equalsIgnoreCase(opt(r.getName()))) {
                        safeRouteParam = routeParam;
                        break;
                    }
                }
            }
        }


        req.setAttribute("pkgs", filtered);
        req.setAttribute("pkgRoutes", pkgRoutes); // ya son solo confirmadas
        req.setAttribute("modalPkgName", modalPkgName);
        req.setAttribute("routeParam", safeRouteParam);
        req.setAttribute("hadError", hadError);

        req.getRequestDispatcher("/src/views/package/package.jsp").forward(req, resp);
    }

    // POST: buscador → GET con ?q=
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        final String q = opt(req.getParameter("q"));

        StringBuilder target = new StringBuilder(req.getContextPath()).append("/packages/list");
        if (!q.isBlank()) {
            target.append("?q=").append(URLEncoder.encode(q, StandardCharsets.UTF_8));
        }
        resp.sendRedirect(target.toString());
    }

    private static String opt(String s) { return s == null ? "" : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trimLower(String s) { return opt(s).toLowerCase(); }
}
