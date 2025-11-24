package servlets.createFlightRoute;

import com.labpa.appweb.airport.AirportSoapAdapter;
import com.labpa.appweb.airport.AirportSoapAdapterService;
import com.labpa.appweb.airport.BaseAirportDTO;
import com.labpa.appweb.category.CategorySoapAdapter;
import com.labpa.appweb.category.CategorySoapAdapterService;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;
import com.labpa.appweb.flightroute.SoapBaseFlightRouteDTO;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapterService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mappers.DateMapper;
import mappers.LocalDateMapper;
import utils.FileBase64Util;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/createFlightRoute")
@MultipartConfig
public class createFlightRouteServlet extends HttpServlet {

//    private final IFlightRouteController controller = ControllerFactory.getFlightRouteController();

    private final FlightRouteSoapAdapter flightRouteSoapAdapter = new FlightRouteSoapAdapterService().getFlightRouteSoapAdapterPort();

    private final AirportSoapAdapter airportSoapAdapter = new AirportSoapAdapterService().getAirportSoapAdapterPort();

    private final CategorySoapAdapter categorySoapAdapter = new CategorySoapAdapterService().getCategorySoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Controladores
//        IAirportController airportController = ControllerFactory.getAirportController();
//        ICategoryController categoryController = ControllerFactory.getCategoryController();

        // Datos para los selects
        List<BaseAirportDTO> airports = new ArrayList<>(airportSoapAdapter.getAllAirportsSimpleDetails().getItem());
        List<String> categories = categorySoapAdapter.getAllCategoriesNames().getItem();

        airports.sort(Comparator.comparing(BaseAirportDTO::getCode));

        // Pasar al JSP
        req.setAttribute("airports", airports);
        req.setAttribute("categories", categories);

        // Redirigir al JSP
        req.getRequestDispatcher("/src/views/createflightRoute/createflightRoute.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try {
            // Parámetros básicos
            String name = trimToNull(req.getParameter("name"));
            String description = trimToNull(req.getParameter("description"));
            String createdAtStr = trimToNull(req.getParameter("createdAt"));
            String priceExtraStr = trimToNull(req.getParameter("priceExtra"));
            String priceTouristStr = trimToNull(req.getParameter("priceTouristClass"));
            String priceBusinessStr = trimToNull(req.getParameter("priceBusinessClass"));
            String originAeroCode = trimToNull(req.getParameter("originAeroCode"));
            String destinationAeroCode = trimToNull(req.getParameter("destinationAeroCode"));

            // === Aerolínea desde sesión ===
            HttpSession session = req.getSession(false);
            String airlineNickname = null;

            if (session != null) {
                airlineNickname = (String) session.getAttribute("airlineNickname");

                // Si no existe con ese nombre, usar el nickname general
                if (airlineNickname == null) {
                    airlineNickname = (String) session.getAttribute("nickname");
                }
            }

            // Validar que no esté nulo
            if (airlineNickname == null) {
                throw new IllegalArgumentException("El usuario de aerolínea no está identificado en la sesión.");
            }

            // === Categorías seleccionadas ===
            String[] cats = req.getParameterValues("categories");
            List<String> categories = new ArrayList<>();
            if (cats != null) {
                categories = Arrays.asList(cats);
            }
            // === Procesar imagen (opcional) ===
            Part imagePart = req.getPart("image");
            File imageFile = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                String uploadPath = getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String submitted = imagePart.getSubmittedFileName();
                String fileName = (submitted != null)
                        ? new File(submitted).getName()
                        : "upload_" + System.currentTimeMillis();
                imageFile = new File(uploadDir, fileName);

                try (InputStream in = imagePart.getInputStream();
                     FileOutputStream out = new FileOutputStream(imageFile)) {
                    in.transferTo(out);
                }
            }


            Double priceExtra = parseDouble(priceExtraStr);
            Double priceTourist = parseDouble(priceTouristStr);
            Double priceBusiness = parseDouble(priceBusinessStr);

            // === Construir DTO ===
            SoapBaseFlightRouteDTO dto = new SoapBaseFlightRouteDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setCreatedAt(createdAtStr);
            dto.setPriceExtraUnitBaggage(priceExtra);
            dto.setPriceTouristClass(priceTourist);
            dto.setPriceBusinessClass(priceBusiness);
            dto.setImage((imageFile != null) ? imageFile.getName() : null);
            dto.setStatus(null);

            // === Crear ruta de vuelo ===
            String imageBase64 = FileBase64Util.fileToBase64(imageFile);

            com.labpa.appweb.flightroute.StringArray categoriesArray = new com.labpa.appweb.flightroute.StringArray();
            categoriesArray.getItem().addAll(categories);

//            controller.createFlightRoute(dto, originAeroCode, destinationAeroCode, airlineNickname, categories, imageFile);
            flightRouteSoapAdapter.createFlightRoute(dto, originAeroCode, destinationAeroCode, airlineNickname, categoriesArray, imageBase64);

            session.setAttribute("toastMessage", "Ruta de vuelo creada correctamente.");
            session.setAttribute("toastType", "success");
            resp.sendRedirect(req.getContextPath() + "/createFlightRoute");

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

    private static Double parseDouble(String v) {
        try {
            return (v == null || v.isEmpty()) ? null : Double.parseDouble(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
