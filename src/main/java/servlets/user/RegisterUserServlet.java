package servlets.user;


import com.labpa.appweb.user.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.beans.ExceptionListener;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;


@WebServlet("/users/register")
public class RegisterUserServlet extends HttpServlet {
    //    IUserController userController = ControllerFactory.getUserController();
    UserSoapAdapterService service = new UserSoapAdapterService();
    UserSoapAdapter port = service.getUserSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/src/views/register/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String userType = req.getParameter("userType");
        System.out.println("Tipo de usuario seleccionado: " + userType);

        String nickname = req.getParameter("reg-nickname");
        String nombre = req.getParameter("reg-nombre");
        String email = req.getParameter("reg-email");
        String password = req.getParameter("reg-password");
        String confirm_password = req.getParameter("reg-confirm-password");

        if (!password.equals(confirm_password)) {
            HttpSession session = req.getSession();
            session.setAttribute("toastMessage", "Las contraseñas no coinciden.");
            session.setAttribute("toastType", "error");
            resp.sendRedirect(req.getContextPath() + "/users/register");
            return;
        }

        if ("cliente".equals(userType)) {
            System.out.println("Registrando cliente...");
            SoapBaseCustomerDTO customer = new SoapBaseCustomerDTO();

            String apellido = req.getParameter("reg-apellido");
            String fechaNacimientoStr = req.getParameter("reg-dob");
            String tipoDocumento = req.getParameter("reg-doc-type");
            String nacionalidad = req.getParameter("reg-nacionalidad");
            String numeroDocumento = req.getParameter("reg-doc-number");

            customer.setNickname(nickname);
            customer.setName(nombre);
            customer.setSurname(apellido);
            customer.setMail(email);

            LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr, DateTimeFormatter.ISO_LOCAL_DATE);
            customer.setBirthDate(fechaNacimiento.toString()); // yyyy-MM-dd

            // Mapeo de Enum
            try {
                com.labpa.appweb.user.EnumTipoDocumento soapDocType =
                        com.labpa.appweb.user.EnumTipoDocumento.valueOf(tipoDocumento.toUpperCase());
                customer.setDocType(soapDocType);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                // manejar error si querés
            }

            customer.setCitizenship(nacionalidad);
            customer.setNumDoc(numeroDocumento);
            customer.setPassword(password);

            try {
                port.registerCustomer(customer, "");
            } catch (Exception e) {
                e.printStackTrace();
                HttpSession session = req.getSession();
                session.setAttribute("toastMessage", "Error al registrar el usuario: " + e.getMessage());
                session.setAttribute("toastType", "error");
                resp.sendRedirect(req.getContextPath() + "/users/register");
                return;
            }

            req.setAttribute("Customer", customer);
        } else if ("aerolinea".equals(userType)) {
            System.out.println("Registrando aerolínea...");
            BaseAirlineDTO airline = new BaseAirlineDTO();


            String web = req.getParameter("reg-web-a");
            String descripcion = req.getParameter("reg-desc-a");


            airline.setNickname(nickname);
            airline.setName(nombre);
            airline.setMail(email);
            airline.setWeb(web);
            airline.setDescription(descripcion);
            airline.setPassword(password);

            // Aquí podrías crear un objeto Aerolinea y guardarlo
            req.setAttribute("Airline", airline);

            try {
//                userController.registerAirline(airline, null);
                port.registerAirline(airline, "");
            } catch (Exception e) {
                e.printStackTrace();
                HttpSession session = req.getSession();
                session.setAttribute("toastMessage", "Error al registrar el usuario: " + e.getMessage());
                session.setAttribute("toastType", "error");
            }
        }

        // Redirigir o reenviar
        HttpSession session = req.getSession();
        System.out.println("Registro exitoso, redirigiendo...");
        session.setAttribute("toastMessage", "Usuario registrado exitosamente");
        session.setAttribute("toastType", "success"); // también puede ser "error", "info", "warning"
        resp.sendRedirect(req.getContextPath() + "/index");
    }

}
