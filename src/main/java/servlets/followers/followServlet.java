package servlets.followers;

import com.labpa.appweb.user.SoapCustomerDTO;
import com.labpa.appweb.user.SoapUserDTO;
import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;

import config.SoapServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/followers/follow")
public class followServlet extends HttpServlet {

    private final UserSoapAdapter port = SoapServiceFactory.getUserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
            // Manejo de error si no es ni SoapUserDTO ni SoapCustomerDTO
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Tipo de usuario no reconocido.");
            return;
        }

        String target = req.getParameter("target");

        if (target == null || target.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/users/list");
            return;
        }

        boolean alreadyFollowing = port.isFollowing(loggedUser, target);

        if (alreadyFollowing) {
            port.unfollowUser(loggedUser, target);
        } else {
            port.followUser(loggedUser, target);
        }

        resp.sendRedirect(req.getContextPath() + "/users/view?nick=" + target);
    }
}
