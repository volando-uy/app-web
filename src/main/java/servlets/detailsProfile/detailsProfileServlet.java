//detailsProfile con problemas por JAVA

package servlets.detailsProfile;

import controllers.user.UserController;
import controllers.user.IUserController;
import domain.dtos.user.UserDTO;
import domain.dtos.user.AirlineDTO;
import domain.services.user.UserService;
import domain.services.user.IUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/Profileusers")
public class detailsProfileServlet extends HttpServlet {

    private IUserController userController;

    @Override
    public void init() {
        IUserService userService = new UserService();
        this.userController = new UserController(userService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nickname = req.getParameter("nickname");

        if (nickname != null && !nickname.isEmpty()) {
            // Intentar buscar como Aerolínea
            AirlineDTO airline = userController.getAirlineDetailsByNickname(nickname);
            if (airline != null) {
                req.setAttribute("airline", airline);
            } else {
                // Si no es aerolínea, buscar como usuario genérico
                UserDTO user = userController.getUserSimpleDetailsByNickname(nickname);
                req.setAttribute("user", user);
            }
        } else {
            // Si no hay nickname, obtener todos los usuarios
            List<UserDTO> users = userController.getAllUsersDetails();
            req.setAttribute("users", users);
        }

        req.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(req, resp);
    }
}
