package servlets.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

import controllers.user.IUserController;
import domain.dtos.user.UserDTO;
import factory.ControllerFactory;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private final IUserController userController = ControllerFactory.getUserController();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserDTO> users = userController.getAllUsersSimpleDetails();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }
}
