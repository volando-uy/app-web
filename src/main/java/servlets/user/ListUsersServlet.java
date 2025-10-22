package servlets.user;

import controllers.user.IUserController;
import domain.dtos.user.BaseAirlineDTO;
import domain.dtos.user.BaseCustomerDTO;
import domain.dtos.user.UserDTO;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/users/list")
public class ListUsersServlet extends HttpServlet {
    private final IUserController userController = ControllerFactory.getUserController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserDTO> users = userController.getAllUsersSimpleDetails();

        Map<String, String> tiposPorUsuario = new HashMap<>();
        for (UserDTO user : users) {
            String tipo;
            if (user instanceof BaseCustomerDTO) {
                tipo = "Cliente";
            } else if (user instanceof BaseAirlineDTO) {
                tipo = "Aerolínea";
            } else {
                tipo = "Visitante";
            }
            tiposPorUsuario.put(user.getNickname(), tipo);
        }

        req.setAttribute("users", users);
        req.setAttribute("tiposUsuarios", tiposPorUsuario);
        req.setAttribute("pageTitle", "Consulta de Perfil - Volando.uy");
        req.setAttribute("pageScript", null); // no JS
        req.getRequestDispatcher("/src/views/listUsers/listUsers.jsp").forward(req, resp);
    }
}
