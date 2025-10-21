package servlets.profile;

import controllers.file.FileController;
import controllers.user.IUserController;
import domain.dtos.user.*;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shared.constants.Images;
import utils.ImageFunctions;

import java.io.File;
import java.io.IOException;

@WebServlet("/perfil")
public class ProfileServlet extends HttpServlet {

    private final IUserController userController = ControllerFactory.getUserController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO usuario = (UserDTO) req.getSession().getAttribute("usuario");

        // Si no hay sesión, redirigimos al login
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String imagePath = ImageFunctions.getImage(usuario.getImage());
        System.out.println("📸 Ruta imagen usuario: " + imagePath);
        req.setAttribute("imageUrl", imagePath);

        // Refrescamos los datos completos del usuario según su tipo
        if (usuario instanceof BaseCustomerDTO) {
            CustomerDTO cliente = userController.getCustomerDetailsByNickname(usuario.getNickname());
            req.setAttribute("cliente", cliente);
            req.setAttribute("tipoUsuario", "cliente");
            req.setAttribute("usuario", cliente); // por si el JSP necesita datos comunes
            req.getSession().setAttribute("usuario", cliente); // opcional, si querés mantener actualizado en sesión

        } else if (usuario instanceof BaseAirlineDTO) {
            AirlineDTO aerolinea = userController.getAirlineDetailsByNickname(usuario.getNickname());
            req.setAttribute("aerolinea", aerolinea);
            req.setAttribute("tipoUsuario", "aerolinea");
            req.setAttribute("usuario", aerolinea);
            req.getSession().setAttribute("usuario", aerolinea);
        }

        // Enviamos al JSP de perfil
        req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp").forward(req, resp);
    }
}
