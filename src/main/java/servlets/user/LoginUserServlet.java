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
public class LoginUserServlet extends HttpServlet {

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

        // Validación básica de inputs
        if (nickname == null || password == null || nickname.isBlank() || password.isBlank()) {
            handleLoginError(req, resp, "", "Debes ingresar usuario y contraseña.");
            return;
        }

        try {
            String token = authController.login(nickname, password);

            if (token != null) {
                // Invalidar sesión anterior y crear una nueva
                HttpSession oldSession = req.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }

                HttpSession newSession = req.getSession(true);
                newSession.setAttribute("jwt", token);
                newSession.setAttribute("nickname", nickname);
                newSession.setAttribute("toastMessage", nickname + " logueado con éxito");
                newSession.setAttribute("toastType", "success");
                System.out.println("Usuario " + nickname + " ha iniciado sesión.");

                // 🔁 Redirección condicional si hay una URL previa almacenada
                String redirectUrl = (String) newSession.getAttribute("redirectAfterLogin");
                if (redirectUrl != null) {
                    newSession.removeAttribute("redirectAfterLogin"); // Evitar redirección infinita
                    resp.sendRedirect(redirectUrl);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/perfil");
                }
                return;
            }

            handleLoginError(req, resp, nickname, "Usuario o contraseña inválidos");

        } catch (IllegalArgumentException e) {
            handleLoginError(req, resp, nickname, "Credenciales inválidas");
        } catch (Exception e) {
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
