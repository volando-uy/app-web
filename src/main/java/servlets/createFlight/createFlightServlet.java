package servlets.createFlight;

import com.labpa.appweb.flight.FlightSoapAdapter;
import com.labpa.appweb.flight.FlightSoapAdapterService;
import com.labpa.appweb.flight.SoapBaseFlightDTO;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;

import com.labpa.appweb.flightroute.SoapBaseFlightRouteDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mappers.LocalDateTimeMapper;
import utils.FileBase64Util;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@WebServlet("/createFlight")
@MultipartConfig
public class createFlightServlet extends HttpServlet {

//    private final IFlightController ctrl = ControllerFactory.getFlightController();
//    private final IFlightRouteController flightRouteController = ControllerFactory.getFlightRouteController();
    private FlightRouteSoapAdapterService flightRouteSoapAdapterService = new FlightRouteSoapAdapterService();
    private FlightRouteSoapAdapter flightRouteController =flightRouteSoapAdapterService.getFlightRouteSoapAdapterPort();

    private FlightSoapAdapterService flightSoapAdapterService = new FlightSoapAdapterService();
    private FlightSoapAdapter flightSoapAdapter = flightSoapAdapterService.getFlightSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String airlineNickname = (String) req.getSession().getAttribute("nickname");

//        List<BaseFlightRouteDTO> routes =
//                flightRouteController.getAllFlightRoutesSimpleDetailsByAirlineNickname(airlineNickname);
        List<SoapBaseFlightRouteDTO> routes =
                flightRouteController.getAllFlightRoutesSimpleDetailsByAirlineNickname(airlineNickname).getItem();

        req.setAttribute("airlineNickname", airlineNickname);
        req.setAttribute("flightRoutes", routes);

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
//            if (imagePart != null && imagePart.getSize() > 0) {
//                String uploadPath = getServletContext().getRealPath("/uploads");
//                new File(uploadPath).mkdirs();
//                String fileName = new File(imagePart.getSubmittedFileName()).getName();
//                imageFile = new File(uploadPath, fileName);
//                try (InputStream in = imagePart.getInputStream();
//                     FileOutputStream out = new FileOutputStream(imageFile)) {
//                    in.transferTo(out);
//                }
//            }
            File imageFile = null;
            String base64Image = null;

            if (imagePart != null && imagePart.getSize() > 0) {
                String uploadPath = getServletContext().getRealPath("/uploads");
                new File(uploadPath).mkdirs();
                String fileName = new File(imagePart.getSubmittedFileName()).getName();
                imageFile = new File(uploadPath, fileName);
                try (InputStream in = imagePart.getInputStream();
                     FileOutputStream out = new FileOutputStream(imageFile)) {
                    in.transferTo(out);
                }
                try {
                    base64Image = FileBase64Util.fileToBase64(imageFile); // ✅ Utilizás tu clase utilitaria
                } catch (IOException e) {
                    e.printStackTrace(); // En producción: log + manejo elegante
                }
            }

            // Convertir fechas
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            // DTO
            SoapBaseFlightDTO dto = new SoapBaseFlightDTO();
            dto.setName(name);
            dto.setDuration(duration != null && !duration.isEmpty() ? Long.parseLong(duration) : null);
            dto.setMaxBusinessSeats(maxBusinessSeats);
            dto.setMaxEconomySeats(maxEconomySeats);

            dto.setCreatedAt(createdAtStr);

            dto.setDepartureTime(departureTimeStr);

            // Crear vuelo
//            flightSoapAdapter.createFlight(dto, airlineNickname, flightRouteName, imageFile);
            flightSoapAdapter.createFlight(dto, airlineNickname, flightRouteName, base64Image);

            HttpSession newSession = req.getSession(true);

            newSession.setAttribute("toastMessage", "Vuelo creado correctamente.");
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
