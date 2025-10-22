package servlets.checkFlightRoute;

import controllers.flightRoute.IFlightRouteController;
import controllers.flight.IFlightController;
import controllers.user.IUserController;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.dtos.user.AirlineDTO;
import domain.dtos.user.UserDTO;
import domain.models.enums.EnumEstatusRuta;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/flightRoute")
public class FlightRouteServlet extends HttpServlet {

    private final IFlightRouteController flightRouteController = ControllerFactory.getFlightRouteController();
    private final IFlightController flightController = ControllerFactory.getFlightController();
    private final IUserController userController = ControllerFactory.getUserController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("nickname") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String nickname = (String) session.getAttribute("nickname");

        // Detectar tipo de usuario logueado
        Object user = null;
        try {
            // Intenta obtenerlo como cliente
            user = userController.getCustomerDetailsByNickname(nickname);
        } catch (IllegalArgumentException e) {
            try {
                // Si no es cliente, intentar como aerolínea
                user = userController.getAirlineDetailsByNickname(nickname);
            } catch (IllegalArgumentException ignored) {
                resp.sendRedirect(req.getContextPath() + "/users/login");
                return;
            }
        }

        req.setAttribute("user", user);

        String airlineParam = req.getParameter("airline");
        String routeParam = req.getParameter("route");
        String flightParam = req.getParameter("flight");

        if (airlineParam == null) {
            List<AirlineDTO> airlines = userController.getAllAirlinesDetails();
            req.setAttribute("airlines", airlines);
            req.getRequestDispatcher("/src/views/checkflightroute/checkflightroute.jsp").forward(req, resp);
            return;
        }

        if (routeParam == null) {
            List<FlightRouteDTO> routes = flightRouteController.getAllFlightRoutesDetailsByAirlineNickname(airlineParam);

            routes = routes.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() == EnumEstatusRuta.CONFIRMADA)
                    .toList();

            req.setAttribute("airlineName", airlineParam);
            req.setAttribute("routes", routes);
            req.getRequestDispatcher("/src/views/checkflightroute/checkflightroute.jsp").forward(req, resp);
            return;
        }

        FlightRouteDTO route = flightRouteController.getFlightRouteDetailsByName(routeParam);
        req.setAttribute("route", route);

        if (flightParam != null) {
            FlightDTO flight = flightController.getFlightDetailsByName(flightParam);
            req.setAttribute("flight", flight);
        }

        req.getRequestDispatcher("/src/views/checkflightroute/checkflightroute.jsp").forward(req, resp);
    }
}
