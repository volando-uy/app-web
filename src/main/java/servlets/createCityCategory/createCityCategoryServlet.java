package servlets.createCityCategory;

import controllers.category.CategoryController;
import controllers.city.CityController;
import domain.dtos.category.CategoryDTO;
import domain.dtos.city.BaseCityDTO;
import domain.services.category.CategoryService;
import domain.services.city.CityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/createCityAndCategory")
public class createCityCategoryServlet extends HttpServlet {

    private CategoryController categoryController;
    private CityController cityController;

    @Override
    public void init() throws ServletException {
        categoryController = new CategoryController(new CategoryService());
        cityController = new CityController(new CityService());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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

                    CategoryDTO created = categoryController.createCategory(new CategoryDTO(name));
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
                    dto.setLatitude(latitude != null && !latitude.isEmpty() ? Double.parseDouble(latitude) : null);
                    dto.setLongitude(longitude != null && !longitude.isEmpty() ? Double.parseDouble(longitude) : null);

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

