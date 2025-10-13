package servlets.profile;

import domain.dtos.user.BaseAirlineDTO;
import domain.dtos.user.BaseCustomerDTO;
import domain.dtos.user.UserDTO;
import domain.models.enums.EnumTipoDocumento;
import domain.models.user.Airline;
import domain.models.user.Customer;
import domain.models.user.User;
import domain.services.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/updateUser")
public class profileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/src/views/profile/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nickname = req.getParameter("nickname");
        User user = userService.getCustomerByNickname(nickname, false);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
            return;
        }

        // Detectamos tipo por la clase
        if (user instanceof Customer customer) {
            BaseCustomerDTO dto = new BaseCustomerDTO();
            dto.setName(req.getParameter("name"));
            dto.setSurname(req.getParameter("surname"));
            dto.setBirthDate(LocalDate.parse(req.getParameter("birthDate")));
            dto.setCitizenship(req.getParameter("citizenship"));
            dto.setDocType(EnumTipoDocumento.valueOf(req.getParameter("docType")));
            dto.setNumDoc(req.getParameter("numDoc"));

            customer.updateDataFrom(dto);
            userService.update(customer);
        }
        else if (user instanceof Airline airline) {
            BaseAirlineDTO dto = new BaseAirlineDTO();
            dto.setName(req.getParameter("name"));
            dto.setDescription(req.getParameter("description"));
            dto.setWeb(req.getParameter("web"));

            airline.updateDataFrom(dto);
            userService.update(airline);
        }

        resp.sendRedirect(req.getContextPath() + "/profile.jsp?nickname=" + nickname);
    }
}
