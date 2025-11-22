package servlets.profile;

import adapters.LocalDateWithValue;
import com.labpa.appweb.images.ImageConstantsDTO;
import com.labpa.appweb.images.ImagesSoapAdapter;
import com.labpa.appweb.images.ImagesSoapAdapterService;
import com.labpa.appweb.user.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mappers.DateMapper;
import utils.FileBase64Util;
import utils.ImageStorageUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1MB
        maxFileSize = 1024 * 1024 * 10,   // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet("/perfil/update")
public class UpdateProfileServlet extends HttpServlet {

    private final UserSoapAdapter port = new UserSoapAdapterService().getUserSoapAdapterPort();
    private final ImagesSoapAdapter imageport = new ImagesSoapAdapterService().getImagesSoapAdapterPort();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("nickname") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }
        String tipo = (String) req.getSession().getAttribute("type");
        String nickname = (String) session.getAttribute("nickname");

        // Obtenemos los datos simples del usuario
        UserDTO user = port.getUserSimpleDetailsByNickname(nickname);
        if (tipo.equals("customer")) {
            SoapBaseCustomerDTO customer = port.getCustomerSimpleDetailsByNickname(user.getNickname());
            System.out.println("fecha nac: " + customer.getBirthDate().toString());
            LocalDateWithValue birthDate = new LocalDateWithValue(customer.getBirthDate());
            System.out.println("birthdate + value: " + birthDate.getValue());
            req.setAttribute("customer", customer);
        }
        req.setAttribute("user", user);


        req.getRequestDispatcher("/src/views/profile/update/updateProfile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("nickname") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        ImageConstantsDTO imageConstantsDTO = imageport.getImageConstants();
        String nickname = (String) session.getAttribute("nickname");
        String type = (String) session.getAttribute("type");

        boolean isCustomer = type.equals("customer");

        UserDTO userDetails = port.getUserSimpleDetailsByNickname(nickname);
        System.out.println("userDetails class: " + userDetails.getClass().getSimpleName());
        Part imagePart = req.getPart("profileImage");

        File finalImage = null;

        try {
            String imageBase64 = null;
            if (isCustomer) {

                if (imagePart != null && imagePart.getSize() > 0) {
                    finalImage = ImageStorageUtils.saveImage(
                            getServletContext(),
                            imagePart,
                            imageConstantsDTO.getImagesPath() + imageConstantsDTO.getCustomersPath(),
                            nickname
                    );
                    imageBase64 = FileBase64Util.fileToBase64(finalImage);
                }

                String fechaNacimientoStr = req.getParameter("birthDate");

                SoapBaseCustomerDTO dto = new SoapBaseCustomerDTO();
                dto.setName(req.getParameter("name"));
                dto.setSurname(req.getParameter("surname"));
//                dto.setBirthDate(
//                        DateMapper.toSoapLocalDate(
//                                LocalDate.parse(req.getParameter("birthDate"), DateTimeFormatter.ISO_LOCAL_DATE)
//                        )
//                );
                LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr, DateTimeFormatter.ISO_LOCAL_DATE);
                dto.setBirthDate(fechaNacimiento.toString()); // yyyy-MM-dd
                dto.setCitizenship(req.getParameter("citizenship"));
                dto.setDocType(EnumTipoDocumento.valueOf(req.getParameter("docType")));
                dto.setNumDoc(req.getParameter("numDoc"));

                // Update por SOAP
                try {
                    System.out.println("imagebase64" + imageBase64);
                    UserDTO updated = (UserDTO) port.updateUserC(nickname, dto, imageBase64 != null ? imageBase64 : "");
                    session.setAttribute("usuario", updated);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (userDetails instanceof BaseAirlineDTO) {
                if (imagePart != null && imagePart.getSize() > 0) {

                    finalImage = ImageStorageUtils.saveImage(
                            getServletContext(),
                            imagePart,
                            imageConstantsDTO.getImagesPath() + imageConstantsDTO.getAirlinesPath(),
                            nickname
                    );
                    imageBase64 = FileBase64Util.fileToBase64(finalImage);
                }
                BaseAirlineDTO dto = new BaseAirlineDTO();
                dto.setName(req.getParameter("name"));
                dto.setDescription(req.getParameter("description"));
                dto.setWeb(req.getParameter("web"));

                UserDTO updated = (UserDTO) port.updateUser(nickname, dto, imageBase64 != null ? imageBase64 : "");
                session.setAttribute("usuario", updated);
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("toastMessage", "Error al actualizar el perfil: " + e.getMessage());
            session.setAttribute("toastType", "error");
            resp.sendRedirect(req.getContextPath() + "/perfil");
            return;
        }

        // Borrar imagen subida del disco (ya está en base64)
        ImageStorageUtils.deleteImage(finalImage);

        // ✅ Confirmación y redirección
        session.setAttribute("toastMessage", "Perfil actualizado con éxito");
        session.setAttribute("toastType", "success");
        resp.sendRedirect(req.getContextPath() + "/perfil");
    }
}
