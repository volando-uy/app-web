package servlets.followers;

import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;
import com.labpa.appweb.user.SoapCustomerDTO;
import com.labpa.appweb.user.SoapUserDTO;

import config.SoapServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/followers/unfollow")
public class unfollowServlet extends HttpServlet {

    private final UserSoapAdapter port = SoapServiceFactory.getUserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        Object loggedUserObj = session.getAttribute("usuario");
        String loggedUser = null;

        if (loggedUserObj instanceof SoapUserDTO) {
            loggedUser = ((SoapUserDTO) loggedUserObj).getNickname();
        } else if (loggedUserObj instanceof SoapCustomerDTO) {
            loggedUser = ((SoapCustomerDTO) loggedUserObj).getNickname();
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Tipo de usuario no reconocido.");
            return;
        }

        String targetNickname = req.getParameter("target");

        if (targetNickname == null || targetNickname.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/users/list");
            return;
        }

        try {
            port.unfollowUser(loggedUser, targetNickname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/users/view?nick=" + targetNickname);
    }
}
