package servlets.image;

import controllers.category.ICategoryController;
import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import controllers.user.IUserController;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.models.enums.EnumEstatusRuta;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shared.utils.ImageProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get the parameters from the request
        String resourceClass = request.getParameter("resourceClassName");
        String key = request.getParameter("key");

        if (resourceClass == null || key == null || resourceClass.isEmpty() || key.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters.");
            return;
        }

        // 2. Use your custom method to get the absolute path
        String imagePath = ImageProcessor.getImageAbsolutePath(resourceClass, key);

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found at path: " + imagePath);
            return;
        }

        // Set the Content-Type header based on the image type
        response.setContentType("image/jpeg"); // Adjust for other types like image/png, image/gif

        response.setContentLength((int) imageFile.length());

        // Get the output stream of the response
        ServletOutputStream sos = response.getOutputStream();

        // Read the image file and write it to the response
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                sos.write(buffer, 0, bytesRead);
            }
        } finally {
            sos.close();
        }

    }
}
