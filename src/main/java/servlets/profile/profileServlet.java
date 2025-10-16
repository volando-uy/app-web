package servlets.profile;

import controllers.user.IUserController;
import domain.dtos.user.*;
import domain.models.enums.EnumTipoDocumento;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/perfil")
public class profileServlet extends HttpServlet {

    private final IUserController userController = ControllerFactory.getUserController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("nickname") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String nickname = (String) session.getAttribute("nickname");
        UserDTO user = userController.getCustomerDetailsByNickname(nickname);
        req.setAttribute("user", user);

        req.getRequestDispatcher("/src/views/profile/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("nickname") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String nickname = (String) session.getAttribute("nickname");
        UserDTO userDetails = userController.getCustomerDetailsByNickname(nickname);

        if (userDetails instanceof CustomerDTO customer) {
            BaseCustomerDTO dto = new BaseCustomerDTO();
            dto.setName(req.getParameter("name"));
            dto.setSurname(req.getParameter("surname"));
            dto.setBirthDate(LocalDate.parse(req.getParameter("birthDate")));
            dto.setCitizenship(req.getParameter("citizenship"));
            dto.setDocType(EnumTipoDocumento.valueOf(req.getParameter("docType")));
            dto.setNumDoc(req.getParameter("numDoc"));

            userController.updateUser(customer.getNickname(), dto, null);

        } else if (userDetails instanceof AirlineDTO airline) {
            BaseAirlineDTO dto = new BaseAirlineDTO();
            dto.setName(req.getParameter("name"));
            dto.setDescription(req.getParameter("description"));
            dto.setWeb(req.getParameter("web"));

            userController.updateUser(airline.getNickname(), dto, null);
        }

        session.setAttribute("toastMessage", "Perfil actualizado con éxito");
        session.setAttribute("toastType", "success");
        resp.sendRedirect(req.getContextPath() + "/perfil");
    }
}
