package servlets.profile;

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

    private final UserSoapAdapter port = new UserSoapAdapterService().getUserSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Obtener el objeto del usuario desde sesión (ya es un objeto SOAP)
        Object usuario = req.getSession().getAttribute("usuario");

        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String nickname;
        if (usuario instanceof BaseCustomerDTO customer) {
            nickname = customer.getNickname();
        } else if (usuario instanceof BaseAirlineDTO airline) {
            nickname = airline.getNickname();
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de usuario desconocido");
            return;
        }

        try {
            // 🔍 Intentar obtener datos completos de Customer
            CustomerDTO cliente = port.getCustomerDetailsByNickname(nickname);

            req.setAttribute("tipoUsuario", "cliente");
            req.setAttribute("usuario", cliente);
            req.getSession().setAttribute("usuario", cliente);

            req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp")
                    .forward(req, resp);
            return;

        } catch (Exception ignored) {
            // no es cliente
        }

        try {
            // 🔍 Intentar obtener datos completos de Airline
            AirlineDTO airline = port.getAirlineDetailsByNickname(nickname);

            req.setAttribute("tipoUsuario", "aerolinea");
            req.setAttribute("usuario", airline);
            req.getSession().setAttribute("usuario", airline);

            req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp")
                    .forward(req, resp);
            return;

        } catch (Exception ignored) {
            // no es aerolinea
        }

        // Si no es ni cliente ni aerolínea, enviar error
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de usuario desconocido");
    }
}
