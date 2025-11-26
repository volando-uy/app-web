package servlets.profile;


import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.constants.ImageConstantsDTO;
import com.labpa.appweb.images.ImagesSoapAdapter;
import com.labpa.appweb.images.ImagesSoapAdapterService;
import com.labpa.appweb.user.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mappers.DateMapper;
import mappers.LocalDateMapper;
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

    private final ConstantsSoapAdapter constantsSoapAdapter = new ConstantsSoapAdapterService().getConstantsSoapAdapterPort();


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
        SoapUserDTO user = port.getUserSimpleDetailsByNickname(nickname);
        if (tipo.equals("customer")) {
            SoapBaseCustomerDTO customer = port.getCustomerSimpleDetailsByNickname(user.getNickname());
            System.out.println("fecha nac: " + customer.getBirthDate().toString());
            LocalDate birthDate = LocalDateMapper.toLocalDate(customer.getBirthDate());
            System.out.println("birthdate VALUE: " + birthDate);
            System.out.println("birthdate CUSTOMER: " + customer.getBirthDate());
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

        ImageConstantsDTO imageConstantsDTO = constantsSoapAdapter.getImageConstants();
        String nickname = (String) session.getAttribute("nickname");


        SoapUserDTO userDetails = port.getUserSimpleDetailsByNickname(nickname);
        String type = userDetails.getUserType();
        boolean isCustomer = constantsSoapAdapter.getValueConstants().getUSERTYPECUSTOMER().equals(type);

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


                SoapBaseCustomerDTO dto = new SoapBaseCustomerDTO();
                dto.setName(req.getParameter("name"));
                dto.setNickname(userDetails.getNickname());
                dto.setMail(userDetails.getMail());
                dto.setPassword(userDetails.getPassword());

                dto.setSurname(req.getParameter("surname"));
                dto.setBirthDate(req.getParameter("birthDate"));
                dto.setCitizenship(req.getParameter("citizenship"));
                dto.setDocType(EnumTipoDocumento.valueOf(req.getParameter("docType")));
                dto.setNumDoc(req.getParameter("numDoc"));

                // Update por SOAP
                try {
                    System.out.println("imagebase64" + imageBase64);
                    SoapUserDTO updated = port.updateUser(nickname, dto, imageBase64 != null ? imageBase64 : "");
                    session.setAttribute("usuario", updated);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (type.equals(constantsSoapAdapter.getValueConstants().getUSERTYPEAIRLINE())) {
                if (imagePart != null && imagePart.getSize() > 0) {

                    finalImage = ImageStorageUtils.saveImage(
                            getServletContext(),
                            imagePart,
                            imageConstantsDTO.getImagesPath() + imageConstantsDTO.getAirlinesPath(),
                            nickname
                    );
                    imageBase64 = FileBase64Util.fileToBase64(finalImage);
                }
                SoapBaseAirlineDTO dto = new SoapBaseAirlineDTO();
                dto.setName(userDetails.getName());
                dto.setNickname(userDetails.getNickname());
                dto.setMail(userDetails.getMail());
                dto.setPassword(userDetails.getPassword());


                dto.setDescription(req.getParameter("description"));
                dto.setWeb(req.getParameter("web"));

                SoapUserDTO updated = port.updateUser(nickname, dto, imageBase64 != null ? imageBase64 : "");
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
