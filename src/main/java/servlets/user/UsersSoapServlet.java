package servlets.user;

import com.labpa.appweb.client.UserSoapAdapter;
import com.labpa.appweb.client.UserSoapAdapterService;
import controllers.user.IUserController;
import domain.dtos.user.UserDTO;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/userssoap")
public class UsersSoapServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserSoapAdapterService service = new UserSoapAdapterService();
        UserSoapAdapter port = service.getUserSoapAdapterPort();

        List<String> user = port.getAllUsersNicknames().getItem();
        response.setContentType("text/plain");
        for (String nickname : user) {
            response.getWriter().println(nickname);
        }
    }
}
