package servlets.pdf;

import com.labpa.appweb.pdf.SoapPDFAdapter;
import com.labpa.appweb.pdf.SoapPDFAdapterService;
import config.SoapServiceFactory;
import jakarta.jws.WebService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@WebServlet("/pdf/booking")
public class PDFBookingServlet extends HttpServlet {

    private final SoapPDFAdapter pdfAdapter = SoapServiceFactory.getPdfService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Parámetro 'id' requerido.");
            return;
        }

        try {
            Long bookFlightId = Long.parseLong(idParam);

            // Llamada al SOAP
            String nickname = (String) req.getSession().getAttribute("nickname");
            String token = (String) req.getSession().getAttribute("jwt");
            String base64PDF = pdfAdapter.getBookflightPDFBase64(bookFlightId, token);

            if (base64PDF == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("No se pudo generar el PDF.");
                return;
            }

            byte[] pdfBytes = Base64.getDecoder().decode(base64PDF);

            // Set headers
            resp.setContentType("application/pdf");
            String pattern = "dd_MM_yyyy_HH_mm_ss";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));

            String fileName = String.format("%s_%s_%d.pdf", nickname, timestamp, bookFlightId);
            resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            resp.setContentLength(pdfBytes.length);

            // Write PDF to output stream
            try (OutputStream out = resp.getOutputStream()) {
                out.write(pdfBytes);
                out.flush();
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("ID inválido");
        }catch (SecurityException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("No tienes permiso para acceder a este recurso.");
        }
    }
}
