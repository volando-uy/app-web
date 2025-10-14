package utils;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class JWTAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        boolean loggedIn = session != null && session.getAttribute("jwt") != null;

        if (loggedIn) {
            chain.doFilter(request, response); // continuar
        } else {
            //Enviar toastr
            if (session != null) {
                session.setAttribute("toastMessage", "Debes iniciar sesión para acceder a esta página");
                session.setAttribute("toastType", "warning");
            }
            res.sendRedirect(req.getContextPath() + "/users/login");
        }
    }
}