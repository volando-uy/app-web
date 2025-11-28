package servlets.user;

import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;
import config.SoapServiceFactory;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/existsUser")
public class ExistsUserServlet extends HttpServlet {
    UserSoapAdapter port = SoapServiceFactory.getUserService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String nickname = request.getParameter("nickname");
        String mail = request.getParameter("mail");

        boolean nicknameExists = false;
        boolean mailExists = false;

        if (nickname != null && !nickname.isBlank()) {
            nicknameExists = port.existsUserByNickname(nickname);
        }

        if (mail != null && !mail.isBlank()) {
            mailExists = port.existsUserByEmail(mail);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"nicknameExists\": %b, \"mailExists\": %b}", nicknameExists, mailExists)
        );
    }
}