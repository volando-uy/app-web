package servlets.profile;

import controllers.user.IUserController;
import domain.dtos.user.*;
import domain.models.enums.EnumTipoDocumento;
import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import shared.constants.Images;
import utils.ImageStorageUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;


@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1MB
        maxFileSize = 1024 * 1024 * 10,   // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet("/perfil/update")
public class UpdateProfileServlet extends HttpServlet {

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
//        UserDTO user = userController.getCustomerDetailsByNickname(nickname);
        UserDTO user = userController.getUserSimpleDetailsByNickname(nickname);
        req.setAttribute("user", user);

        req.getRequestDispatcher("/src/views/profile/update/updateProfile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        if (session == null || session.getAttribute("nickname") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String nickname = (String) session.getAttribute("nickname");
        UserDTO userDetails = userController.getUserSimpleDetailsByNickname(nickname);

        Part imagePart = req.getPart("profileImage");


        File finalImage = null;
        try {

            if (userDetails instanceof BaseCustomerDTO customer) {
                finalImage = ImageStorageUtils.saveImage(
                        getServletContext(),
                        imagePart,
                        Images.IMAGES_PATH + Images.CUSTOMERS_PATH,
                        nickname
                );


                BaseCustomerDTO dto = new BaseCustomerDTO();
                dto.setName(req.getParameter("name"));
                dto.setSurname(req.getParameter("surname"));
                dto.setBirthDate(LocalDate.parse(req.getParameter("birthDate")));
                dto.setCitizenship(req.getParameter("citizenship"));
                dto.setDocType(EnumTipoDocumento.valueOf(req.getParameter("docType")));
                dto.setNumDoc(req.getParameter("numDoc"));

                UserDTO newUser = userController.updateUser(customer.getNickname(), dto, finalImage);
                System.out.println(newUser);

            } else if (userDetails instanceof BaseAirlineDTO airline) {
                finalImage = ImageStorageUtils.saveImage(
                        getServletContext(),
                        imagePart,
                        Images.IMAGES_PATH + Images.AIRLINES_PATH,
                        nickname
                );


                BaseAirlineDTO dto = new BaseAirlineDTO();
                dto.setName(req.getParameter("name"));
                dto.setDescription(req.getParameter("description"));
                dto.setWeb(req.getParameter("web"));


                UserDTO newUser = userController.updateUser(airline.getNickname(), dto, finalImage);
                System.out.println(newUser);
            }
        } catch (Exception e) {

        }

        ImageStorageUtils.deleteImage(finalImage);

        // ✅ Confirmación y redirección
        session.setAttribute("toastMessage", "Perfil actualizado con éxito");
        session.setAttribute("toastType", "success");
        resp.sendRedirect(req.getContextPath() + "/perfil");
    }
}


