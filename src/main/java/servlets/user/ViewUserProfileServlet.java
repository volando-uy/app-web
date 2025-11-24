package servlets.user;

import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.user.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/users/view")
public class ViewUserProfileServlet extends HttpServlet {

    private final UserSoapAdapter port = new UserSoapAdapterService().getUserSoapAdapterPort();
    private final ConstantsSoapAdapter constantsPort = new ConstantsSoapAdapterService().getConstantsSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String nickname = req.getParameter("nick");

        if (nickname == null || nickname.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/users/list");
            return;
        }

        // Obtener usuario base
        SoapUserDTO baseUser = port.getUserSimpleDetailsByNickname(nickname);

        // Inicializamos tipoUsuario por defecto
        String tipoUsuario = "visitante";  // fallback si no es ni cliente ni aerolínea

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

        // Constantes del sistema
        String tipoCustomer = constantsPort.getValueConstants().getUSERTYPECUSTOMER();
        String tipoAirline = constantsPort.getValueConstants().getUSERTYPEAIRLINE();

        // Atributos que el JSP espera
        req.setAttribute("tipoUsuario", tipoUsuario);
        req.setAttribute("isCustomer", "cliente".equalsIgnoreCase(tipoUsuario));
        req.setAttribute("isAirline", "aerolinea".equalsIgnoreCase(tipoUsuario));
        req.setAttribute("pageTitle", "Perfil de Usuario - " + baseUser.getNickname());

        req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp").forward(req, resp);
    }
}
