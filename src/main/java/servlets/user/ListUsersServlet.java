package servlets.user;

import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.user.SoapUserDTO;
import com.labpa.appweb.user.UserSoapAdapter;
import com.labpa.appweb.user.UserSoapAdapterService;


import config.SoapServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/users/list")
public class ListUsersServlet extends HttpServlet {
    UserSoapAdapter port = SoapServiceFactory.getUserService();


    ConstantsSoapAdapter constantsPort = new ConstantsSoapAdapterService().getConstantsSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String airline = constantsPort.getValueConstants().getUSERTYPEAIRLINE();
        final String customer = constantsPort.getValueConstants().getUSERTYPECUSTOMER();
        List<SoapUserDTO> users = port.getAllUsersSimpleDetails().getItem();

        Map<String, String> tiposPorUsuario = new HashMap<>();
        for (SoapUserDTO user : users) {
            String tipo = user.getUserType();

            if (tipo.equalsIgnoreCase(airline)) {
                tipo = "Aerolínea";
            } else if (tipo.equalsIgnoreCase(customer)) {
                tipo = "Cliente";
            } else {
                tipo = "Visitante";
            }
            tiposPorUsuario.put(user.getNickname(), tipo);
        }

        req.setAttribute("users", users);
        req.setAttribute("tiposUsuarios", tiposPorUsuario);
        req.setAttribute("pageTitle", "Consulta de Perfil - Volando.uy");
        req.setAttribute("pageScript", null); // no JS
        req.getRequestDispatcher("/src/views/listUsers/listUsers.jsp").forward(req, resp);
    }
}
