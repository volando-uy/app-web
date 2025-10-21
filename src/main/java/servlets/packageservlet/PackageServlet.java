package servlets.packageservlet;

import controllers.flightRoute.IFlightRouteController;
import controllers.flightRoutePackage.IFlightRoutePackageController;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.dtos.flightRoutePackage.BaseFlightRoutePackageDTO;
import domain.models.enums.EnumEstatusRuta;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/packages/list")
public class PackageServlet extends HttpServlet {

    private final IFlightRoutePackageController packageController =
            ControllerFactory.getFlightRoutePackageController();
    private final IFlightRouteController routeCtrl =
            ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { process(req, resp); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { process(req, resp); }

    private void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        boolean hadError = false;

        final String modalParam = opt(req.getParameter("modal"));  // nombre del paquete
        final String routeParam = opt(req.getParameter("route"));  // ruta

         List<BaseFlightRoutePackageDTO> pkgs;
        try {
            pkgs = packageController.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            if (pkgs == null) pkgs = Collections.emptyList();
        } catch (Exception e) {
            hadError = true;
            log("Error obteniendo paquetes", e);
            pkgs = Collections.emptyList();
        }

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

            List<FlightRouteDTO> confirmed = new ArrayList<>();
            for (FlightRouteDTO r : routes) {
                if (r != null && r.getStatus() == EnumEstatusRuta.CONFIRMADA) {
                    confirmed.add(r);
                }
            }
            confirmed.sort(Comparator.comparing(
                    FlightRouteDTO::getName,
                    String.CASE_INSENSITIVE_ORDER
            ));

            pkgRoutes.put(pkgName, confirmed);
        }

        String modalPkgName = modalParam.isEmpty() ? null : modalParam;
        String safeRouteParam = null;
        if (modalPkgName != null && !routeParam.isEmpty()) {
            List<FlightRouteDTO> routes = pkgRoutes.getOrDefault(modalPkgName, Collections.emptyList());
            for (FlightRouteDTO r : routes) {
                if (r != null && routeParam.equalsIgnoreCase(r.getName())) {
                    safeRouteParam = routeParam;
                    break;
                }
            }
        }

        req.setAttribute("pkgs", pkgs);
        req.setAttribute("pkgRoutes", pkgRoutes);
        req.setAttribute("modalPkgName", modalPkgName);
        req.setAttribute("routeParam", safeRouteParam);
        req.setAttribute("hadError", hadError);

        req.getRequestDispatcher("/src/views/package/package.jsp").forward(req, resp);
    }

    private static String opt(String s) { return s == null ? "" : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
