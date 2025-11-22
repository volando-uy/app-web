package servlets.user;

import com.labpa.appweb.user.UserDTO;
import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;



@WebServlet("/users")
public class UsersServlet extends HttpServlet {

//    private final IUserController userController = ControllerFactory.getUserController();
    private final UserSoapAdapter userSoapAdapter = new UserSoapAdapterService().getUserSoapAdapterPort();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserDTO> users = userSoapAdapter.getAllUsersSimpleDetails().getItem();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }
}
