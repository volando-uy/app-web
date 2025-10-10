package servlets.index;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
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

        List<BaseFlightRoutePackageDTO> packages;
        try {
            packages = pkgCtrl.getAllFlightRoutesPackagesSimpleDetails();
        } catch (Throwable t) {
            try {
                packages = pkgCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            } catch (Throwable t2) {
                packages = java.util.Collections.emptyList();
            }
        }
        req.setAttribute("packages", packages);

        List<BaseFlightDTO> flights;
        try {
            flights = flightCtrl.getAllFlightsSimpleDetails();
            if (flights == null) flights = java.util.Collections.emptyList();
        } catch (Throwable t) {
            flights = java.util.Collections.emptyList();
        }
        req.setAttribute("flights", flights);
        req.setAttribute("flightsCount", flights.size()); // debug visual en el JSP

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
