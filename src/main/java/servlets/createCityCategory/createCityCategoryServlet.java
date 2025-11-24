package servlets.createCityCategory;

import com.labpa.appweb.category.CategoryDTO;
import com.labpa.appweb.category.CategorySoapAdapter;
import com.labpa.appweb.category.CategorySoapAdapterService;
import com.labpa.appweb.city.BaseCityDTO;
import com.labpa.appweb.city.CitySoapAdapter;
import com.labpa.appweb.city.CitySoapAdapterService;


import com.labpa.appweb.countries.SoapCountriesAdapter;
import com.labpa.appweb.countries.SoapCountriesAdapterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/createCityAndCategory")
public class createCityCategoryServlet extends HttpServlet {

//    private CategoryController categoryController;
//    private CityController cityController;

    private CategorySoapAdapterService categorySoapAdapterService = new CategorySoapAdapterService();
    private CategorySoapAdapter categoryController = categorySoapAdapterService.getCategorySoapAdapterPort();

    private CitySoapAdapterService citySoapAdapterService = new CitySoapAdapterService();
    private CitySoapAdapter cityController = citySoapAdapterService.getCitySoapAdapterPort();


    private SoapCountriesAdapter soapCountriesAdapter=new SoapCountriesAdapterService().getSoapCountriesAdapterPort();
//    @Override
//    public void init() throws ServletException {
////        categoryController = new CategoryController(new CategoryService());
////        cityController = new CityController(new CityService());
//
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Obtener lista de países
        List<String> countries = soapCountriesAdapter.getAllCountries().getItem();

        // Pasarla como atributo al JSP
        req.setAttribute("countries", countries);

        req.getRequestDispatcher("/src/views/createCityAndCategory/createCityAndCategory.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        if (action == null) {
            handleError(request, response, "La acción es obligatoria");
            return;
        }

        try {
            switch (action) {
                case "createCategory" -> {
                    String name = request.getParameter("name");

                    if (name == null || name.trim().isEmpty()) {
                        handleError(request, response, "El nombre de la categoría es obligatorio");
                        return;
                    }
                    CategoryDTO dto = new CategoryDTO();
                    dto.setName(name);
                    CategoryDTO created = categoryController.createCategory(dto);
                    HttpSession session = request.getSession();
                    session.setAttribute("toastMessage", "Categoría creada correctamente: " + created.getName());
                    session.setAttribute("toastType", "success");
                    response.sendRedirect(request.getContextPath() + "/createFlight");
                }


                case "createCity" -> {
                    String name = request.getParameter("name");
                    String country = request.getParameter("country");
                    String latitude = request.getParameter("latitude");
                    String longitude = request.getParameter("longitude");

                    if (name == null || name.trim().isEmpty()) {
                        handleError(request, response, "El nombre de la ciudad es obligatorio");
                        return;
                    }

                    BaseCityDTO dto = new BaseCityDTO();
                    dto.setName(name);
                    dto.setCountry(country);
                    Double lat = null;
                    Double lon = null;

                    try {
                        if (latitude != null && !latitude.trim().isEmpty()) {
                            lat = Double.parseDouble(latitude);
                        }
                        if (longitude != null && !longitude.trim().isEmpty()) {
                            lon = Double.parseDouble(longitude);
                        }
                    } catch (NumberFormatException e) {
                        handleError(request, response, "Latitud o longitud no tienen un formato válido.");
                        return;
                    }

                    dto.setLatitude(lat);
                    dto.setLongitude(lon);

                    BaseCityDTO created = cityController.createCity(dto);
                    HttpSession session = request.getSession();
                    session.setAttribute("toastMessage", "Ciudad creada correctamente: " + created.getName());
                    session.setAttribute("toastType", "success");
                    response.sendRedirect(request.getContextPath() + "/createFlight");
                }

                default -> handleError(request, response, "Acción desconocida: " + action);
            }

        } catch (IllegalArgumentException e) {
            handleError(request, response, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, e.getMessage());
        }

    }
    private void handleError(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", message);
        session.setAttribute("toastType", "error");
        resp.sendRedirect(req.getContextPath() + "/createFlight");
    }

}

