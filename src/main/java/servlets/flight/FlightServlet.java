package servlets.flight;

import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import domain.dtos.flight.BaseFlightDTO;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/flight/list")
public class FlightServlet extends HttpServlet {

    private final IFlightController flightCtrl = ControllerFactory.getFlightController();
    private final IFlightRouteController routeCtrl = ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        try {
            req.setCharacterEncoding("UTF-8");
        } catch (Exception ignored) {
        }

        List<BaseFlightDTO> flights = flightCtrl.getAllFlightsSimpleDetails();
        req.setAttribute("flights", flights);

        req.getRequestDispatcher("/src/views/flight/bookFlight.jsp").forward(req, resp);
    }
}
