package utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class IsMobileFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userAgent = httpRequest.getHeader("User-Agent");

        boolean isMobile = false;

        if (userAgent != null) {
            String ua = userAgent.toLowerCase();
            isMobile = ua.contains("mobi") || ua.contains("android") || ua.contains("iphone");
        }

        // SIEMPRE permitimos continuar
        request.setAttribute("isMobile", isMobile);

        chain.doFilter(request, response);
    }


    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
