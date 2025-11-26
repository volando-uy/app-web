package servlets.reservations;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reservations/cancel-checkin")
public class CancelCheckinServlet extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reservationCode = req.getParameter("reservationCode");
        if (reservationCode == null || reservationCode.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Parámetro 'reservationCode' es requerido.");
            return;
        }

        // Aquí iría la lógica para cancelar el check-in usando el código de reserva.
        // Por ejemplo:
        // reservationController.cancelCheckin(reservationCode);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Check-in cancelado exitosamente para la reserva: " + reservationCode);
    }
}
