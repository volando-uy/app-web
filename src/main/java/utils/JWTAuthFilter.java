package utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class JWTAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (SessionUtils.isUserAuthenticated(session) && session.getAttribute("jwt") != null) {

            String nickname = SessionUtils.getNickname(session);
            session.setAttribute("jwt_nick", nickname);

            chain.doFilter(request, response);
            return;
        }

        if (session == null) session = req.getSession(true);

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
