package servlets.reservations;

import com.labpa.appweb.booking.BookingSoapAdapter;
import com.labpa.appweb.booking.BookingSoapAdapterService;
import com.labpa.appweb.booking.SoapBaseBookFlightDTO;
import servlets.SoapServiceFactory;import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reservations/realize-checkin")
public class RealizeCheckinServlet extends HttpServlet {

    BookingSoapAdapter bookingSoapAdapter = SoapServiceFactory.getBookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Primero preguntar si es celular o no
        boolean isMobile=(boolean)req.getAttribute("isMobile");
        //Si no es mobil, redirigir a perfil
        if(!isMobile){
            resp.sendRedirect(req.getContextPath() + "/perfil");
            return;
        }

        String reservationCode = req.getParameter("reservationCode");
        if (reservationCode == null || reservationCode.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Parámetro 'reservationCode' es requerido.");
            return;
        }

        Long id = Long.parseLong(req.getParameter("reservationCode"));
        String token = (String) req.getSession().getAttribute("jwt");


        log("reservationCode: " + reservationCode);
        log("token: " + token);
        SoapBaseBookFlightDTO success = bookingSoapAdapter.completeBooking(id, token);

        if (success.isIsBooked()) {
            req.getSession().setAttribute("toastMessage", "Check-in realizado con éxito para la reserva " + reservationCode + ".");
            req.getSession().setAttribute("toastType", "success");
            resp.sendRedirect(req.getContextPath() + "/perfil");
        } else {
            req.getSession().setAttribute("toastMessage", "Error al realizar el check-in para la reserva " + reservationCode + ".");
            req.getSession().setAttribute("toastType", "error");
            resp.sendRedirect(req.getContextPath() + "/perfil");
        }


    }
}
