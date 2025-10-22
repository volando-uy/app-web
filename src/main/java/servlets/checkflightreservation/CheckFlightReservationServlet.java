package servlets.checkflightreservation;

import controllers.booking.IBookingController;
import controllers.flight.IFlightController;
import controllers.flightroute.IFlightRouteController;
import controllers.ticket.ITicketController;
import controllers.user.IUserController;

import domain.dtos.bookflight.BookFlightDTO;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightroute.FlightRouteDTO;
import domain.dtos.ticket.TicketDTO;
import domain.dtos.user.*;

import domain.models.enums.EnumEstatusRuta;

import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@WebServlet("/booking/check")
public class CheckFlightReservationServlet extends HttpServlet {

    private final IUserController        users   = ControllerFactory.getUserController();
    private final IFlightRouteController routes  = ControllerFactory.getFlightRouteController();
    private final IFlightController      flights = ControllerFactory.getFlightController();
    private final IBookingController     books   = ControllerFactory.getBookingController();
    private final ITicketController      tickets = ControllerFactory.getTicketController();

    // ===== Helpers =====
    private static Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        ZoneId zone = ZoneId.of("America/Montevideo");
        return Date.from(ldt.atZone(zone).toInstant());
    }
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private static <T> List<T> safe(List<T> list) { return list == null ? List.of() : list; }
    // ====================

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        // Para <fmt:...> en el JSP
        req.setAttribute("uiLocale", "es_UY");

        UserDTO usuario = (UserDTO) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        // Normalizo params
        String airline = trimOrNull(req.getParameter("airline"));
        String route   = trimOrNull(req.getParameter("route"));
        String flight  = trimOrNull(req.getParameter("flight"));
        String booking = trimOrNull(req.getParameter("booking"));

        // Identidad
        String tipoUsuario;
        if (usuario instanceof BaseCustomerDTO) {
            CustomerDTO c = users.getCustomerDetailsByNickname(usuario.getNickname());
            req.setAttribute("usuario", c);
            req.setAttribute("cliente", c);
            req.getSession().setAttribute("usuario", c);
            tipoUsuario = "cliente";
        } else if (usuario instanceof BaseAirlineDTO) {
            AirlineDTO a = users.getAirlineDetailsByNickname(usuario.getNickname());
            req.setAttribute("usuario", a);
            req.setAttribute("aerolinea", a);
            req.getSession().setAttribute("usuario", a);
            tipoUsuario = "aerolinea";
        } else {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }
        req.setAttribute("tipoUsuario", tipoUsuario);

        // ============ AEROLÍNEA ============
        if ("aerolinea".equals(tipoUsuario)) {
            String airlineNick = ((AirlineDTO) req.getAttribute("usuario")).getNickname();
            req.setAttribute("airlineName", airlineNick);

            // Paso 1: rutas de la aerolínea
            if (route == null) {
                List<FlightRouteDTO> rs = safe(routes.getAllFlightRoutesDetailsByAirlineNickname(airlineNick));
                req.setAttribute("routes", rs);
                forward(req, resp);
                return;
            }
            req.setAttribute("routeName", route);

            // Paso 2: vuelos de la ruta
            if (flight == null) {
                List<FlightDTO> fs = safe(flights.getAllFlightsDetailsByRouteName(route));
                List<Map<String,Object>> flightsView = new ArrayList<>();
                for (FlightDTO f : fs) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("name", f.getName());
                    m.put("departure", toDate(f.getDepartureTime())); // Date para fmt
                    flightsView.add(m);
                }
                req.setAttribute("flightsView", flightsView);
                forward(req, resp);
                return;
            }
            req.setAttribute("flightName", flight);

            // Paso 3: lista de reservas del vuelo (todas las de ese vuelo)
            if (booking == null) {
                List<BookFlightDTO> bs = safe(books.getBookFlightsDetailsByFlightName(flight));

                // Para cada reserva, además de los datos base, agregamos la lista de pasajeros (tickets)
                List<Map<String,Object>> bookingsView = new ArrayList<>();
                for (BookFlightDTO b : bs) {
                    Map<String,Object> bm = new HashMap<>();
                    bm.put("id", b.getId());
                    bm.put("customerNickname", b.getCustomerNickname()); // “dueño” de la reserva
                    bm.put("seatType", b.getSeatType());
                    bm.put("totalPrice", b.getTotalPrice());
                    bm.put("createdAt", toDate(b.getCreatedAt()));

                    // Passengers (tickets de esta reserva)
                    List<Map<String,Object>> passengers = new ArrayList<>();
                    if (b.getTicketIds() != null) {
                        for (Long tid : b.getTicketIds()) {
                            TicketDTO t = tickets.getTicketDetailsById(tid);
                            if (t == null) continue;
                            Map<String,Object> tm = new HashMap<>();
                            tm.put("name", t.getName());
                            tm.put("surname", t.getSurname());
                            tm.put("docType", t.getDocType());
                            tm.put("numDoc", t.getNumDoc());
                            tm.put("seatNumber", t.getSeatNumber());
                            passengers.add(tm);
                        }
                    }
                    bm.put("passengers", passengers);
                    bm.put("passengerCount", passengers.size());

                    bookingsView.add(bm);
                }
                req.setAttribute("bookingsView", bookingsView);
                forward(req, resp);
                return;
            }

            // Paso 4: detalle de reserva
            Long id;
            try { id = Long.valueOf(booking); }
            catch (Exception e) {
                resp.sendRedirect(req.getContextPath()+"/booking/check?route="+route+"&flight="+flight);
                return;
            }
            BookFlightDTO bd = books.getBookFlightDetailsById(id);
            req.setAttribute("booking", bd);
            req.setAttribute("bookingCreatedAtDate", toDate(bd != null ? bd.getCreatedAt() : null));

            List<TicketDTO> ts = new ArrayList<>();
            if (bd != null && bd.getTicketIds() != null) {
                for (Long tid : bd.getTicketIds()) ts.add(tickets.getTicketDetailsById(tid));
            }
            req.setAttribute("tickets", ts);

            forward(req, resp);
            return;
        }

        // Paso 0: elegir aerolínea
        if (airline == null) {
            List<AirlineDTO> airlines = safe(users.getAllAirlinesDetails());
            req.setAttribute("airlines", airlines);
            forward(req, resp);
            return;
        }
        req.setAttribute("airlineName", airline);

        // Paso 1: rutas confirmadas de esa aerolínea
        if (route == null) {
            List<FlightRouteDTO> rs = safe(
                    routes.getAllFlightRoutesDetailsByAirlineNickname(airline)
            ).stream().filter(r -> r.getStatus() == EnumEstatusRuta.CONFIRMADA).toList();
            req.setAttribute("routes", rs);
            forward(req, resp);
            return;
        }
        req.setAttribute("routeName", route);

        // Paso 2: vuelos de la ruta
        if (flight == null) {
            List<FlightDTO> fs = safe(flights.getAllFlightsDetailsByRouteName(route));
            List<Map<String,Object>> flightsView = new ArrayList<>();
            for (FlightDTO f : fs) {
                Map<String,Object> m = new HashMap<>();
                m.put("name", f.getName());
                m.put("departure", toDate(f.getDepartureTime()));
                flightsView.add(m);
            }
            req.setAttribute("flightsView", flightsView);
            forward(req, resp);
            return;
        }
        req.setAttribute("flightName", flight);

        // Paso 3: ¿tengo reserva en ese vuelo?
        String customerNick = ((CustomerDTO) req.getAttribute("usuario")).getNickname();
        List<BookFlightDTO> allForFlight = safe(books.getBookFlightsDetailsByFlightName(flight));
        BookFlightDTO mine = allForFlight.stream()
                .filter(b -> Objects.equals(customerNick, b.getCustomerNickname()))
                .findFirst().orElse(null);

        if (mine == null) {
            req.setAttribute("noBooking", true);
            req.setAttribute("tickets", List.of());
            forward(req, resp);
            return;
        }

        BookFlightDTO bd = books.getBookFlightDetailsById(mine.getId());
        req.setAttribute("booking", bd);
        req.setAttribute("bookingCreatedAtDate", toDate(bd != null ? bd.getCreatedAt() : null));

        List<TicketDTO> ts = new ArrayList<>();
        if (bd != null && bd.getTicketIds() != null) {
            for (Long tid : bd.getTicketIds()) ts.add(tickets.getTicketDetailsById(tid));
        }
        req.setAttribute("tickets", ts);

        forward(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/src/views/checkflightreservation/checkflightreservation.jsp").forward(req, resp);
    }
}
