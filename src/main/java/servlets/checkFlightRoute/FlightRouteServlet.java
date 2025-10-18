package servlets.checkFlightRoute;

import controllers.flightRoute.IFlightRouteController;
import controllers.user.IUserController;
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
        UserDTO user = userController.getCustomerDetailsByNickname(nickname);
        req.setAttribute("user", user);

        String airlineParam = req.getParameter("airline");
        String selectedRoute = req.getParameter("route");

        // Si no hay aerolínea seleccionada aún mostrar aerolíneas disponibles
        if (airlineParam == null) {
            List<AirlineDTO> airlines = userController.getAllAirlinesDetails();
            req.setAttribute("airlines", airlines);
            req.getRequestDispatcher("/src/views/checkflightroute/checkflightroute.jsp").forward(req, resp);
            return;
        }

        // Si hay aerolínea, pero no rutalistar rutas confirmadas de esa aerolínea
        if (selectedRoute == null) {
            List<FlightRouteDTO> routes = flightRouteController.getAllFlightRoutesDetailsByAirlineNickname(airlineParam);

            // Filtrar por estado CONFIRMADA (seguro frente a null)
            routes = routes.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() == EnumEstatusRuta.CONFIRMADA)
                    .toList();

            req.setAttribute("airlineName", airlineParam);
            req.setAttribute("routes", routes);
            req.getRequestDispatcher("/src/views/checkflightroute/checkflightroute.jsp").forward(req, resp);
            return;
        }

        FlightRouteDTO route = flightRouteController.getFlightRouteDetailsByName(selectedRoute);
        req.setAttribute("route", route);

        req.getRequestDispatcher("/src/views/checkflightroute/checkflightroute.jsp").forward(req, resp);
    }
}
