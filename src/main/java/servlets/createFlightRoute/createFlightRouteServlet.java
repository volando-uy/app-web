package servlets.createFlightRoute;

import com.labpa.appweb.airport.AirportSoapAdapter;
import com.labpa.appweb.airport.AirportSoapAdapterService;
import com.labpa.appweb.airport.BaseAirportDTO;
import com.labpa.appweb.category.CategorySoapAdapter;
import com.labpa.appweb.category.CategorySoapAdapterService;
import com.labpa.appweb.flightroute.EnumEstatusRuta;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;
import com.labpa.appweb.flightroute.SoapBaseFlightRouteDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import utils.FileBase64Util;

import java.io.*;
import java.util.*;

@WebServlet("/createFlightRoute")
@MultipartConfig
public class createFlightRouteServlet extends HttpServlet {

    private final FlightRouteSoapAdapter flightRouteSoapAdapter =
            new FlightRouteSoapAdapterService().getFlightRouteSoapAdapterPort();

    private final AirportSoapAdapter airportSoapAdapter =
            new AirportSoapAdapterService().getAirportSoapAdapterPort();

    private final CategorySoapAdapter categorySoapAdapter =
            new CategorySoapAdapterService().getCategorySoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<BaseAirportDTO> airports = new ArrayList<>(airportSoapAdapter.getAllAirportsSimpleDetails().getItem());
        List<String> categories = categorySoapAdapter.getAllCategoriesNames().getItem();

        airports.sort(Comparator.comparing(BaseAirportDTO::getCode));

        req.setAttribute("airports", airports);
        req.setAttribute("categories", categories);

        req.getRequestDispatcher("/src/views/createflightRoute/createflightRoute.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try {
            // === Parámetros ===
            String name = trimToNull(req.getParameter("name"));
            String description = trimToNull(req.getParameter("description"));
            String createdAtStr = trimToNull(req.getParameter("createdAt"));
            String priceExtraStr = trimToNull(req.getParameter("priceExtra"));
            String priceTouristStr = trimToNull(req.getParameter("priceTouristClass"));
            String priceBusinessStr = trimToNull(req.getParameter("priceBusinessClass"));
            String originAeroCode = trimToNull(req.getParameter("originAeroCode"));
            String destinationAeroCode = trimToNull(req.getParameter("destinationAeroCode"));
            String videoURL = trimToNull(req.getParameter("videoURL"));


            if (originAeroCode == null || destinationAeroCode == null) {
                handleError(req, resp, "Los códigos de aeropuerto de origen y destino son obligatorios.");
            }

            HttpSession session = req.getSession(false);
            String airlineNickname = null;
            if (session != null) {
                airlineNickname = (String) session.getAttribute("airlineNickname");
                if (airlineNickname == null) {
                    airlineNickname = (String) session.getAttribute("nickname");
                }
            }
            if (airlineNickname == null) {
                throw new IllegalArgumentException("El usuario de aerolínea no está identificado en la sesión.");
            }

            // === Categorías ===
            String[] cats = req.getParameterValues("categories");
            List<String> categories = cats != null ? Arrays.asList(cats) : new ArrayList<>();

            // === Imagen ===
            Part imagePart = req.getPart("image");
            File imageFile = null;
            String imageBase64 = "";
            String fileName = "";

            if (imagePart != null  && imagePart.getSize() > 0) {
                String uploadPath = getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String submitted = imagePart.getSubmittedFileName();
                fileName = (submitted != null) ? new File(submitted).getName() : "upload_" + System.currentTimeMillis();
                imageFile = new File(uploadDir, fileName);

                try (InputStream in = imagePart.getInputStream(); FileOutputStream out = new FileOutputStream(imageFile)) {
                    in.transferTo(out);
                }

                imageBase64 = FileBase64Util.fileToBase64(imageFile);
            }

            // === Construir DTO ===
            SoapBaseFlightRouteDTO dto = new SoapBaseFlightRouteDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setCreatedAt(createdAtStr);
            dto.setPriceExtraUnitBaggage(parseDouble(priceExtraStr));
            dto.setPriceTouristClass(parseDouble(priceTouristStr));
            dto.setPriceBusinessClass(parseDouble(priceBusinessStr));
            dto.setStatus(EnumEstatusRuta.SIN_ESTADO);
            dto.setImage(fileName); // se setea el nombre, aunque no haya base64
            dto.setVideoURL(videoURL);

            com.labpa.appweb.flightroute.StringArray categoriesArray = new com.labpa.appweb.flightroute.StringArray();
            categoriesArray.getItem().addAll(categories);

            // === Crear ruta de vuelo ===
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
}
