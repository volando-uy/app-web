package servlets.user;

import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.user.*;

import servlets.SoapServiceFactory;import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/users/view")
public class ViewUserProfileServlet extends HttpServlet {

    UserSoapAdapter port = SoapServiceFactory.getUserService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String nickname = req.getParameter("nick");

        if (nickname == null || nickname.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/users/list");
            return;
        }

        SoapUserDTO baseUser = port.getUserSimpleDetailsByNickname(nickname);

        if (baseUser == null) {
            resp.sendRedirect(req.getContextPath() + "/users/list");
            return;
        }

        String tipoUsuario = "visitante";

        if (baseUser instanceof SoapBaseCustomerDTO) {
            SoapCustomerDTO cliente = port.getCustomerDetailsByNickname(nickname);
            req.setAttribute("cliente", cliente);
            req.setAttribute("usuario", cliente);
            tipoUsuario = "cliente";
        } else if (baseUser instanceof SoapBaseAirlineDTO) {
            SoapAirlineDTO aerolinea = port.getAirlineDetailsByNickname(nickname);
            req.setAttribute("aerolinea", aerolinea);
            req.setAttribute("usuario", aerolinea);
            tipoUsuario = "aerolinea";
        }

        HttpSession session = req.getSession(false);
        boolean isFollowing = false;
        String loggedUser = null;

        if (session != null && session.getAttribute("usuario") != null) {
            loggedUser = ((SoapUserDTO) session.getAttribute("usuario")).getNickname();

            // No puede seguirse a sí mismo
            if (!loggedUser.equals(nickname)) {
                isFollowing = port.isFollowing(loggedUser, nickname);  // Asegura que el estado de seguimiento se actualice
            }

            req.setAttribute("loggedUser", loggedUser);
        }

        req.setAttribute("isFollowing", isFollowing);

        req.setAttribute("tipoUsuario", tipoUsuario);
        req.setAttribute("isCustomer", "cliente".equals(tipoUsuario));
        req.setAttribute("isAirline", "aerolinea".equals(tipoUsuario));
        req.setAttribute("pageTitle", "Perfil de Usuario - " + baseUser.getNickname());

        req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp").forward(req, resp);
    }
}
