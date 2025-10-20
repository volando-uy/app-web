package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.stream.Collectors;

@WebServlet("/debug")
@MultipartConfig
public class Debug extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("📡 Método: " + req.getMethod());
        out.println("🛣️ URI: " + req.getRequestURI());
        out.println("🧭 Context Path: " + req.getContextPath());
        out.println("🧾 Query String: " + req.getQueryString());

        System.out.println("--- Estructura del directorio de trabajo ---" + req.getMethod());
        printDirectoryTree(out, new File("."), "", true);
        System.out.println("--------------------------------------------");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("📥 Parámetros recibidos (text/plain):");

        // Si no es multipart, usar getParameterMap()
        if (!req.getContentType().toLowerCase().startsWith("multipart/")) {
            req.getParameterMap().forEach((key, values) ->
                    out.println(" - " + key + ": " + String.join(", ", values))
            );
        } else {
            out.println("📦 multipart/form-data detectado:");
            out.println("------ PARTS ------");

            Collection<Part> parts = req.getParts();
            for (Part part : parts) {
                out.println("🔹 Campo: " + part.getName());
                out.println("📏 Tamaño: " + part.getSize());
                out.println("🧾 Content-Type: " + part.getContentType());
                out.println("📎 Archivo: " + part.getSubmittedFileName());

                // Si es un campo de texto
                if (part.getSubmittedFileName() == null) {
                    String value = new BufferedReader(new InputStreamReader(part.getInputStream()))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    out.println("✏️ Valor: " + value);
                }

                out.println("-------------------------");
            }
        }

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

        System.out.println("--- Estructura del directorio de trabajo ---");
        printDirectoryTree(out, new File("."), "", true);
        System.out.println("--------------------------------------------");
    }

    private void printDirectoryTree(PrintWriter out, File dir, String indent, boolean isLast) {
        if (dir == null || !dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (int i = 0; i < files.length; i++) {
            boolean last = (i == files.length - 1);
            File file = files[i];

            String prefix = indent + (last ? "└── " : "├── ");
            out.println(prefix + file.getName());

            if (file.isDirectory()) {
                String newIndent = indent + (last ? "    " : "│   ");
                printDirectoryTree(out, file, newIndent, last);
            }
        }
    }

}
