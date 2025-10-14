package servlets.user;

import controllers.auth.IAuthController;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/users/login")
public class LoginUser extends HttpServlet {

    private final IAuthController authController = ControllerFactory.getAuthController();

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

        String token = authController.login(nickname, password); //

        if (token != null) {
            HttpSession session = req.getSession();
            session.setAttribute("jwt", token);
            session.setAttribute("nickname", nickname); // útil para mostrar nombre en UI
            session.setAttribute("toastMessage", nickname + " logueado con éxito");
            session.setAttribute("toastType", "success");

            resp.sendRedirect(req.getContextPath() + "/index");
        } else {
            StringBuilder errorMsg = new StringBuilder("Usuario o contraseña inválidos");
            if (nickname == null || nickname.isBlank()) errorMsg.append(" (falta nickname)");
            if (password == null || password.isBlank()) errorMsg.append(" (falta password)");

            req.setAttribute("error", errorMsg.toString());
            req.getRequestDispatcher("/debug").forward(req, resp);
        }
    }
}
