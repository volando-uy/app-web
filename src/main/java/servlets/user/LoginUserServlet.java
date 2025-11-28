package servlets.user;

import com.labpa.appweb.auth.AuthSoapAdapter;
import com.labpa.appweb.auth.AuthSoapAdapterService;
import com.labpa.appweb.auth.SoapLoginResponseDTO;
import com.labpa.appweb.auth.SoapUserDTO;

import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.user.SoapBaseAirlineDTO;
import com.labpa.appweb.user.SoapBaseCustomerDTO;
import servlets.SoapServiceFactory;import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/users/login")
public class LoginUserServlet extends HttpServlet {

    private final AuthSoapAdapter port = SoapServiceFactory.getAuthService();
    private final ConstantsSoapAdapter constantsPort = SoapServiceFactory.getConstantsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/src/views/login/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nickname = req.getParameter("nickname");
        String password = req.getParameter("password");

        if (nickname == null || password == null || nickname.isBlank() || password.isBlank()) {
            handleLoginError(req, resp, "", "Debes ingresar usuario y contraseña.");
            return;
        }

        try {
            // Llamada al servicio SOAP para login
            SoapLoginResponseDTO soapResponse = port.login(nickname, password);

            if (soapResponse == null || soapResponse.getUser() == null) {
                handleLoginError(req, resp, nickname, "Usuario o contraseña inválidos");
                return;
            }

            SoapUserDTO soapUser = soapResponse.getUser();
            String token = soapResponse.getToken();

            // Determinar tipo de usuario (CUSTOMER o AIRLINE)
            boolean isCustomer = port.isCustomer(token);

            // Mapear SOAPUserDTO a BaseCustomerDTO o BaseAirlineDTO (del paquete SOAP)
            Object usuario;
            String userType = soapUser.getUserType();
            System.out.println("User type detected: " + userType);
            System.out.println("User: " + soapUser);
            if (constantsPort.getValueConstants().getUSERTYPECUSTOMER().equals(userType)) {
                SoapBaseCustomerDTO customer = new SoapBaseCustomerDTO();
                customer.setNickname(soapUser.getNickname());
                customer.setName(soapUser.getName());
                customer.setMail(soapUser.getMail());
                customer.setImage(soapUser.getImage());
                customer.setUserType(constantsPort.getValueConstants().getUSERTYPECUSTOMER());
                usuario = customer;
            } else if (constantsPort.getValueConstants().getUSERTYPEAIRLINE().equals(userType)) {
                SoapBaseAirlineDTO airline = new SoapBaseAirlineDTO();
                airline.setNickname(soapUser.getNickname());
                airline.setName(soapUser.getName());
                airline.setMail(soapUser.getMail());
                airline.setImage(soapUser.getImage());
                airline.setUserType(constantsPort.getValueConstants().getUSERTYPEAIRLINE());
                usuario = airline;
            } else {
                handleLoginError(req, resp, nickname, "Tipo de usuario desconocido");
                return;
            }

            // Iniciar sesión
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("jwt", token);
            newSession.setAttribute("nickname", soapUser.getNickname());
            newSession.setAttribute("usuario", usuario);  // IMPORTANTE: objeto SOAP
            newSession.setAttribute("type", isCustomer ? "customer" : "airline");
            newSession.setAttribute("toastMessage", soapUser.getNickname() + " logueado con éxito");
            newSession.setAttribute("toastType", "success");
            String resourceClassName = usuario.getClass().getSimpleName();
            newSession.setAttribute("resourceClassName", resourceClassName);

            System.out.println("Usuario logueado: " + soapUser.getNickname());

            String redirectUrl = (String) newSession.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                newSession.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectUrl);
            } else {
                resp.sendRedirect(req.getContextPath() + "/perfil");
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            handleLoginError(req, resp, nickname, "Credenciales inválidas");
        } catch (Exception e) {
            e.printStackTrace();
            handleLoginError(req, resp, nickname, "Error inesperado: " + e.getMessage());
        }
    }

    private void handleLoginError(HttpServletRequest req, HttpServletResponse resp,
                                  String nickname, String message) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", message);
        session.setAttribute("toastType", "error");
        session.setAttribute("nickname", nickname);
        resp.sendRedirect(req.getContextPath() + "/users/login");
    }
}
