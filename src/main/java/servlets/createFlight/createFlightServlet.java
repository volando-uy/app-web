package servlets.createFlight;

import controllers.flight.IFlightController;
import controllers.flightroute.IFlightRouteController;
import controllers.user.IUserController;
import domain.dtos.flight.BaseFlightDTO;
import domain.dtos.flightroute.BaseFlightRouteDTO;
import domain.dtos.user.BaseAirlineDTO;
import factory.ControllerFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@WebServlet("/createFlight")
@MultipartConfig
public class createFlightServlet extends HttpServlet {

    private final IFlightController ctrl = ControllerFactory.getFlightController();
    private final IFlightRouteController flightRouteController = ControllerFactory.getFlightRouteController();
    private final IUserController userController = ControllerFactory.getUserController();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<String> airlines = userController.getAllAirlinesNicknames();
        req.setAttribute("airlines", airlines);
        req.getRequestDispatcher("/src/views/createFlight/createFlight.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");


        try {
            String name = req.getParameter("name");
            String duration = req.getParameter("duration");
            String airlineNickname = req.getParameter("airlineNickname");
            String flightRouteName = req.getParameter("flightRouteName");
            String createdAtStr = req.getParameter("createdAt");
            String departureTimeStr = req.getParameter("departureTime");
            Integer maxBusinessSeats = parseInt(req.getParameter("maxBusinessSeats"));
            Integer maxEconomySeats = parseInt(req.getParameter("maxEconomySeats"));

            // Procesar imagen
            Part imagePart = req.getPart("image");
            File imageFile = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                String uploadPath = getServletContext().getRealPath("/uploads");
                new File(uploadPath).mkdirs();
                String fileName = new File(imagePart.getSubmittedFileName()).getName();
                imageFile = new File(uploadPath, fileName);
                try (InputStream in = imagePart.getInputStream();
                     FileOutputStream out = new FileOutputStream(imageFile)) {
                    in.transferTo(out);
                }
            }

            // Convertir fechas
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, fmt);
            LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, fmt);

            // DTO
            BaseFlightDTO dto = new BaseFlightDTO();
            dto.setName(name);
            dto.setDuration(duration != null && !duration.isEmpty() ? Long.parseLong(duration) : null);
            dto.setMaxBusinessSeats(maxBusinessSeats);
            dto.setMaxEconomySeats(maxEconomySeats);
            dto.setCreatedAt(createdAt);
            dto.setDepartureTime(departureTime);

            // Crear vuelo
            ctrl.createFlight(dto, airlineNickname, flightRouteName, imageFile);

            HttpSession newSession = req.getSession(true);

            newSession.setAttribute("toastMessage", "Ruta creada correctamente.");
            newSession.setAttribute("toastType", "success");

            resp.sendRedirect(req.getContextPath() + "/createFlight");


        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            handleError(req, resp, e.getMessage());

        }
    }


    private void handleError(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", message);
        session.setAttribute("toastType", "error");
        resp.sendRedirect(req.getContextPath() + "/createFlight");
    }
    private Integer parseInt(String val) {
        try {
            return (val == null || val.isEmpty()) ? null : Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
