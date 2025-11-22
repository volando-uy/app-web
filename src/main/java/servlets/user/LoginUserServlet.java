package servlets.user;

import com.labpa.appweb.auth.AuthSoapAdapter;
import com.labpa.appweb.auth.AuthSoapAdapterService;
import com.labpa.appweb.auth.SoapLoginResponseDTO;
import com.labpa.appweb.auth.SoapUserDTO;
import com.labpa.appweb.user.BaseAirlineDTO;
import com.labpa.appweb.user.BaseCustomerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/users/login")
public class LoginUserServlet extends HttpServlet {

    private final AuthSoapAdapter port = new AuthSoapAdapterService().getAuthSoapAdapterPort();

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
            if (isCustomer) {
                BaseCustomerDTO customer = new BaseCustomerDTO();
                customer.setNickname(soapUser.getNickname());
                customer.setName(soapUser.getName());
                customer.setMail(soapUser.getMail());
                customer.setImage(soapUser.getImage());
                usuario = customer;

            } else {
                BaseAirlineDTO airline = new BaseAirlineDTO();
                airline.setNickname(soapUser.getNickname());
                airline.setName(soapUser.getName());
                airline.setMail(soapUser.getMail());
                airline.setImage(soapUser.getImage());
                usuario = airline;
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
