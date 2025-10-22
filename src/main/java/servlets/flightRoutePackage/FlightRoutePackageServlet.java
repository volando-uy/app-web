package servlets.flightRoutePackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

import controllers.flightroutepackage.IFlightRoutePackageController;
import domain.dtos.flightroutepackage.FlightRoutePackageDTO;
import factory.ControllerFactory;


@WebServlet("/packages")
public class FlightRoutePackageServlet extends HttpServlet {
    private IFlightRoutePackageController ctrl = ControllerFactory.getFlightRoutePackageController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        var packages = ctrl.getAllFlightRoutesPackagesDetails();
        req.setAttribute("packages", packages);
        req.getRequestDispatcher("/src/components/packageList/packageList.jspf")
                .forward(req, resp);
    }
}