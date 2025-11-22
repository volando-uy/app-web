package servlets.user;

import com.labpa.appweb.user.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/users/view")
public class ViewUserProfileServlet extends HttpServlet {
//    private final IUserController userController = ControllerFactory.getUserController();

    UserSoapAdapterService service = new UserSoapAdapterService();
    UserSoapAdapter port = service.getUserSoapAdapterPort();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String nickname = req.getParameter("nick");

        if (nickname == null || nickname.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/users/list");
            return;
        }

//        UserDTO baseUser = userController.getUserSimpleDetailsByNickname(nickname);
        UserDTO baseUser = port.getUserSimpleDetailsByNickname(nickname);
        if (baseUser instanceof BaseCustomerDTO) {
//            CustomerDTO cliente = userController.getCustomerDetailsByNickname(nickname);
            CustomerDTO cliente = port.getCustomerDetailsByNickname(nickname);

            req.setAttribute("cliente", cliente);
            req.setAttribute("tipoUsuario", "cliente");
            req.setAttribute("usuario", cliente);

        } else if (baseUser instanceof BaseAirlineDTO) {
//            AirlineDTO aerolinea = userController.getAirlineDetailsByNickname(nickname);
            AirlineDTO aerolinea = port.getAirlineDetailsByNickname(nickname);

            req.setAttribute("aerolinea", aerolinea);
            req.setAttribute("tipoUsuario", "aerolinea");
            req.setAttribute("usuario", aerolinea);

        }

        req.setAttribute("pageTitle", "Perfil de Usuario - " + baseUser.getNickname());

        req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp").forward(req, resp);
    }
}
