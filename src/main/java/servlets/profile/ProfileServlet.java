package servlets.profile;

import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.user.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/perfil")
public class ProfileServlet extends HttpServlet {

    private final UserSoapAdapter userPort = new UserSoapAdapterService().getUserSoapAdapterPort();
    private final ConstantsSoapAdapter constantsPort = new ConstantsSoapAdapterService().getConstantsSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        // Obtener usuario actual de sesión (SOAP)
        SoapUserDTO usuario = (SoapUserDTO) session.getAttribute("usuario");
        String nickname = usuario.getNickname();  // por defecto


        // Obtener constantes de tipos de usuario
        String tipoCustomer = constantsPort.getValueConstants().getUSERTYPECUSTOMER();
        String tipoAirline = constantsPort.getValueConstants().getUSERTYPEAIRLINE();
        System.out.println("Tipo de usuario recibido: " + usuario.getUserType());
        System.out.println("Esperado CUSTOMER: " + tipoCustomer);
        System.out.println("Esperado AIRLINE: " + tipoAirline);


        try {
            // 🔍 Intentar cargar como cliente
            if (tipoCustomer.equalsIgnoreCase(usuario.getUserType())) {
                SoapCustomerDTO cliente = userPort.getCustomerDetailsByNickname(nickname);

                actualizarYRedirigir(req, resp, cliente, tipoCustomer);
                return;
            }

            // 🔍 Intentar cargar como aerolínea
            if (tipoAirline.equalsIgnoreCase(usuario.getUserType())) {
                SoapAirlineDTO airline = userPort.getAirlineDetailsByNickname(nickname);

                actualizarYRedirigir(req, resp, airline, tipoAirline);
                return;
            }

            // ⚠Si el tipo no coincide con ninguna constante conocida
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de usuario desconocido");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener información del perfil");
        }
    }

    private void actualizarYRedirigir(HttpServletRequest req, HttpServletResponse resp,
                                      SoapUserDTO userDetail, String tipoUsuario)
            throws ServletException, IOException {

        req.setAttribute("usuario", userDetail);
        req.setAttribute("tipoUsuario", tipoUsuario);
        req.setAttribute("isCustomer", tipoUsuario.equalsIgnoreCase(constantsPort.getValueConstants().getUSERTYPECUSTOMER()));
        req.setAttribute("isAirline", tipoUsuario.equalsIgnoreCase(constantsPort.getValueConstants().getUSERTYPEAIRLINE()));

        // Refrescar en sesión también
        req.getSession().setAttribute("usuario", userDetail);

        req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp")
                .forward(req, resp);
    }
}
