package servlets.index;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import controllers.flight.IFlightController;
import controllers.flightRoutePackage.IFlightRoutePackageController;

import domain.dtos.flight.BaseFlightDTO;
import domain.dtos.flightRoutePackage.BaseFlightRoutePackageDTO;

import factory.ControllerFactory;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    private final IFlightRoutePackageController pkgCtrl =
            ControllerFactory.getFlightRoutePackageController();
    private final IFlightController flightCtrl =
            ControllerFactory.getFlightController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        try { req.setCharacterEncoding("UTF-8"); }
        catch (Exception e) { log("No se pudo setear encoding UTF-8", e); }


        HttpSession session = req.getSession(false);
        if (session != null) {
            String toast = (String) session.getAttribute("toastMessage");
            String toastType = (String) session.getAttribute("toastType");
            if (toast != null) {
                req.setAttribute("toastMessage", toast);
                req.setAttribute("toastType", (toastType != null) ? toastType : "success");
                session.removeAttribute("toastMessage");
                session.removeAttribute("toastType");
            }
        }

        List<BaseFlightRoutePackageDTO> packages = getPackagesPreferWithRoutes();
        req.setAttribute("packages", packages);

        List<BaseFlightDTO> flights = getFlightsSafe();
        req.setAttribute("flights", flights);
        req.setAttribute("flightsCount", flights.size());

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private List<BaseFlightRoutePackageDTO> getPackagesPreferWithRoutes() {
        try {
            List<BaseFlightRoutePackageDTO> list =
                    pkgCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            return (list != null) ? list : Collections.emptyList();
        } catch (Exception e) {
            log("Fallo getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes()", e);
            try {
                List<BaseFlightRoutePackageDTO> list =
                        pkgCtrl.getAllFlightRoutesPackagesSimpleDetails();
                return (list != null) ? list : Collections.emptyList();
            } catch (Exception e2) {
                log("Fallo getAllFlightRoutesPackagesSimpleDetails()", e2);
                return Collections.emptyList();
            }
        }
    }

    private List<BaseFlightDTO> getFlightsSafe() {
        try {
            List<BaseFlightDTO> list = flightCtrl.getAllFlightsSimpleDetails();
            return (list != null) ? list : Collections.emptyList();
        } catch (Exception e) {
            log("Fallo getAllFlightsSimpleDetails()", e);
            return Collections.emptyList();
        }
    }
}
