package servlets.index;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import controllers.flight.IFlightController;
import controllers.flightroutepackage.IFlightRoutePackageController;

import domain.dtos.flight.BaseFlightDTO;
import domain.dtos.flightroutepackage.BaseFlightRoutePackageDTO;

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
        HttpSession session = req.getSession();
        if (session != null) {
            String toast = (String) req.getSession().getAttribute("toastMessage");
            String toastType = (String) req.getSession().getAttribute("toastType");
            System.out.println("Toastr en IndexServlet: " + toast + " (type: " + toastType + ")");

            if (toast != null) {
                req.setAttribute("toastMessage", toast);
                req.setAttribute("toastType", (toastType != null) ? toastType : "success");

//                // limpiar para que no se muestre de nuevo en reloads
//                req.getSession().removeAttribute("toastMessage");
//                req.getSession().removeAttribute("toastType");
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
