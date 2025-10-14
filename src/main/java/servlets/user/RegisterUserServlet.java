package servlets.user;

import controllers.user.IUserController;
import domain.dtos.user.*;
import domain.models.enums.EnumTipoDocumento;
import domain.models.user.User;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/users/register")
public class RegisterUserServlet extends HttpServlet {
    IUserController userController = ControllerFactory.getUserController();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/src/views/register/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userType = req.getParameter("userType");
        System.out.println("Tipo de usuario seleccionado: " + userType);
        if ("cliente".equals(userType)) {
            System.out.println("Registrando cliente...");
            BaseCustomerDTO customer = new BaseCustomerDTO();

            String nickname = req.getParameter("reg-nickname");
            String nombre = req.getParameter("reg-nombre");
            String apellido = req.getParameter("reg-apellido");
            String email = req.getParameter("reg-email");
            String fechaNacimiento = req.getParameter("reg-dob");
            String tipoDocumento = req.getParameter("reg-doc-type");
            EnumTipoDocumento docType = EnumTipoDocumento.valueOf(tipoDocumento.toUpperCase());
            String nacionalidad = req.getParameter("reg-nacionalidad");
            String numeroDocumento = req.getParameter("reg-doc-number");
            String password = req.getParameter("reg-password");

            customer.setNickname(nickname);
            customer.setName(nombre);
            customer.setSurname(apellido);
            customer.setMail(email);
            customer.setBirthDate(LocalDate.parse(fechaNacimiento));
            customer.setDocType(docType);
            customer.setCitizenship(nacionalidad);
            customer.setNumDoc(numeroDocumento);
            customer.setPassword(password);

            userController.registerCustomer(customer, null);

            req.setAttribute("Customer", customer);
        } else if ("aerolinea".equals(userType)) {
            System.out.println("Registrando aerolínea...");
            BaseAirlineDTO airline = new BaseAirlineDTO();


            String nickname = req.getParameter("reg-nickname-a");
            String nombre = req.getParameter("reg-name-a");
            String email = req.getParameter("reg-email-a");
            String web = req.getParameter("reg-web-a");
            String descripcion = req.getParameter("reg-desc-a");
            String password = req.getParameter("reg-password-a");

            airline.setNickname(nickname);
            airline.setName(nombre);
            airline.setMail(email);
            airline.setWeb(web);
            airline.setDescription(descripcion);
            airline.setPassword(password);

            // Aquí podrías crear un objeto Aerolinea y guardarlo
            req.setAttribute("Airline", airline);

            userController.registerAirline(airline, null);
        }

        // Redirigir o reenviar
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", "Usuario registrado exitosamente");
        session.setAttribute("toastType", "success"); // también puede ser "error", "info", "warning"
        resp.sendRedirect(req.getContextPath() + "/index");
    }

}
