package servlets.user;

import com.labpa.appweb.user.UserDTO;
import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;


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
    //    private final IUserController userController = ControllerFactory.getUserController();
    UserSoapAdapterService service = new UserSoapAdapterService();
    UserSoapAdapter port = service.getUserSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<UserDTO> users = port.getAllUsersSimpleDetails().getItem();

        Map<String, String> tiposPorUsuario = new HashMap<>();
        for (UserDTO user : users) {
            String tipo;
            String className = user.getClass().getSimpleName();
            switch (className) {
                case "BaseCustomerDTO":
                    tipo = "Cliente";
                    break;
                case "BaseAirlineDTO":
                    tipo = "Aerolínea";
                    break;
                default:
                    tipo = "Visitante";
                    break;
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
