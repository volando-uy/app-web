package servlets.user;

import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;

import config.SoapServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/userssoap")
public class UsersSoapServlet extends HttpServlet {
    UserSoapAdapter port = SoapServiceFactory.getUserService();


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> user = port.getAllUsersNicknames().getItem();
        response.setContentType("text/plain");
        for (String nickname : user) {
            response.getWriter().println(nickname);
        }
    }
}
