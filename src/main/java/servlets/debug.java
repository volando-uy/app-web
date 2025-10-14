package servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet("/debug")
public class debug extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        out.println("📥 Parámetros recibidos:");
        req.getParameterMap().forEach((key, values) -> {
            out.println(" - " + key + ": " + String.join(", ", values));
        });

        out.println("\n🏷️ Atributos disponibles:");
        Enumeration<String> attributeNames = req.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            Object attrValue = req.getAttribute(attrName);
            out.println(" - " + attrName + ": " + attrValue);
        }

        out.println("\n📦 Headers HTTP:");
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            String value = req.getHeader(header);
            out.println(" - " + header + ": " + value);
        }

        out.println("\n🍪 Cookies:");
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                out.println(" - " + cookie.getName() + ": " + cookie.getValue());
            }
        } else {
            out.println(" - No hay cookies");
        }

        out.println("\n📡 Método: " + req.getMethod());
        out.println("🛣️ URI: " + req.getRequestURI());
        out.println("🧭 Context Path: " + req.getContextPath());
        out.println("🧾 Query String: " + req.getQueryString());
    }


}
