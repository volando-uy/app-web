package servlets.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;



@WebServlet("/users/logout")
public class LogoutUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String nickname = (String) session.getAttribute("nickname");
            session.invalidate();

            // Crear una nueva sesión para mensajes flash
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("toastMessage", nickname + " ha cerrado sesión.");
            newSession.setAttribute("toastType", "info");
        }

        resp.sendRedirect(req.getContextPath() + "/");
    }
}
