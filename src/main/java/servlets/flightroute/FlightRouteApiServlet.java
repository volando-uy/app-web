package servlets.flightroute;

import com.google.gson.Gson;
import controllers.flightroute.IFlightRouteController;
import domain.dtos.flightroute.BaseFlightRouteDTO;
import factory.ControllerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/flightRoutes")
public class FlightRouteApiServlet extends HttpServlet {

    private final IFlightRouteController flightRouteController = ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String airlineNickname = req.getParameter("airlineNickname");

        if (airlineNickname == null || airlineNickname.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Falta el parámetro 'airlineNickname'\"}");
            return;
        }

        List<BaseFlightRouteDTO> routes = flightRouteController.getAllFlightRoutesSimpleDetailsByAirlineNickname(airlineNickname);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Usá tu herramienta favorita para serializar, por ejemplo Jackson o Gson
        String json = new Gson().toJson(routes); // asegurate de tener Gson en tu proyecto
        resp.getWriter().write(json);
    }
}
