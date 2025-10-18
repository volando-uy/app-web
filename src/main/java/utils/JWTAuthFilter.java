package utils;


import controllers.auth.IAuthController;
import factory.ControllerFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class JWTAuthFilter implements Filter {


    private final IAuthController authController = ControllerFactory.getAuthController();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session     = req.getSession(false);

        String token = session != null ? (String) session.getAttribute("jwt") : null;
        boolean isAuthenticated = token != null && authController.isAuthenticated(token);

        if (isAuthenticated) {
            // Refrescar nickname en sesión si no está
            String nickname = (String) session.getAttribute("nickname");
            if (nickname == null || nickname.isBlank()) {
                String extractedNickname = authController.getNicknameFromToken(token);
                session.setAttribute("nickname", extractedNickname);
            }

            chain.doFilter(request, response); // Continuar normalmente
        } else {
            // Guardar la URL actual para redirigir después del login
            if (session == null) {
                session = req.getSession(true);
            }

            String originalUrl = req.getRequestURI();
            if (req.getQueryString() != null) {
                originalUrl += "?" + req.getQueryString();
            }

            session.setAttribute("redirectAfterLogin", originalUrl);
            session.setAttribute("toastMessage", "Debes iniciar sesión para acceder a esta página");
            session.setAttribute("toastType", "warning");

            res.sendRedirect(req.getContextPath() + "/users/login");
        }
    }
}