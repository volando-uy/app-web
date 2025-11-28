package servlets.checkflightreservation;


import com.labpa.appweb.booking.BookingSoapAdapter;
import com.labpa.appweb.booking.BookingSoapAdapterService;
import com.labpa.appweb.booking.SoapBookFlightDTO;
import com.labpa.appweb.flight.FlightSoapAdapter;
import com.labpa.appweb.flight.FlightSoapAdapterService;
import com.labpa.appweb.flight.SoapFlightDTO;
import com.labpa.appweb.flightroute.EnumEstatusRuta;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;
import com.labpa.appweb.flightroute.SoapFlightRouteDTO;
import com.labpa.appweb.ticket.TicketDTO;
import com.labpa.appweb.ticket.TicketSoapAdapter;
import com.labpa.appweb.ticket.TicketSoapAdapterService;
import com.labpa.appweb.user.*;

import servlets.SoapServiceFactory;import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mappers.LocalDateTimeMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@WebServlet("/booking/check")
public class CheckFlightReservationServlet extends HttpServlet {
//
//    private final IUserController        users   = ControllerFactory.getUserController();
//    private final IFlightRouteController routes  = ControllerFactory.getFlightRouteController();
//    private final IFlightController      flights = ControllerFactory.getFlightController();
//    private final IBookingController     books   = ControllerFactory.getBookingController();
//    private final ITicketController      tickets = ControllerFactory.getTicketController();


    private UserSoapAdapter users = SoapServiceFactory.getUserService();

    private FlightRouteSoapAdapter routes = SoapServiceFactory.getFlightRouteService();

    private FlightSoapAdapter flights = SoapServiceFactory.getFlightService();

    private BookingSoapAdapter books = SoapServiceFactory.getBookingService();

    private TicketSoapAdapter tickets = SoapServiceFactory.getTicketService();



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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        req.setAttribute("uiLocale", "es_UY");

        SoapUserDTO usuario = (SoapUserDTO) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String airline = trimOrNull(req.getParameter("airline"));
        String route   = trimOrNull(req.getParameter("route"));
        String flight  = trimOrNull(req.getParameter("flight"));
        String booking = trimOrNull(req.getParameter("booking"));

        // Identidad
        String tipoUsuario;
        if (usuario instanceof SoapBaseCustomerDTO) {
            SoapCustomerDTO c = users.getCustomerDetailsByNickname(usuario.getNickname());
            req.setAttribute("usuario", c);
            req.setAttribute("cliente", c);
            req.getSession().setAttribute("usuario", c);
            tipoUsuario = "cliente";
        } else if (usuario instanceof SoapBaseAirlineDTO) {
            SoapAirlineDTO a = users.getAirlineDetailsByNickname(usuario.getNickname());
            req.setAttribute("usuario", a);
            req.setAttribute("aerolinea", a);
            req.getSession().setAttribute("usuario", a);
            tipoUsuario = "aerolinea";
        } else {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }
        req.setAttribute("tipoUsuario", tipoUsuario);

        // -------- Rama AEROLÍNEA --------
        if ("aerolinea".equals(tipoUsuario)) {
            String airlineNick = ((SoapAirlineDTO) req.getAttribute("usuario")).getNickname();
            req.setAttribute("airlineName", airlineNick);

            if (route == null) {
                List<SoapFlightRouteDTO> rs = safe(routes.getAllFlightRoutesDetailsByAirlineNickname(airlineNick).getItem());
                req.setAttribute("routes", rs);
                forward(req, resp);
                return;
            }
            req.setAttribute("routeName", route);

            if (flight == null) {
                List<SoapFlightDTO> fs = safe(flights.getAllFlightsDetailsByRouteName(route).getItem());
                List<Map<String,Object>> flightsView = new ArrayList<>();
                for (SoapFlightDTO f : fs) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("name", f.getName());


                    LocalDateTime javaTime = LocalDateTimeMapper.fromString(f.getDepartureTime());
                    m.put("departure", toDate(javaTime));

                    flightsView.add(m);
                }
                req.setAttribute("flightsView", flightsView);
                forward(req, resp);
                return;
            }
            req.setAttribute("flightName", flight);

            if (booking == null) {
                List<SoapBookFlightDTO> bs = safe(books.getBookFlightsDetailsByFlightName(flight).getItem());

                List<Map<String,Object>> bookingsView = new ArrayList<>();
                for (SoapBookFlightDTO b : bs) {
                    Map<String,Object> bm = new HashMap<>();
                    bm.put("id", b.getId());
                    bm.put("customerNickname", b.getCustomerNickname());
                    bm.put("seatType", b.getSeatType());
                    bm.put("totalPrice", b.getTotalPrice());
                    LocalDateTime javaTime = LocalDateTimeMapper.fromString(b.getCreatedAt());
                    bm.put("createdAt", toDate(javaTime));

                    // Pasajeros
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

            // Detalle de una reserva (aerolínea)
            Long id;
            try { id = Long.valueOf(booking); }
            catch (Exception e) {
                resp.sendRedirect(req.getContextPath()+"/booking/check?route="+route+"&flight="+flight);
                return;
            }
            SoapBookFlightDTO bd = books.getBookFlightDetailsById(id);
            req.setAttribute("booking", bd);
            req.setAttribute("bookingCreatedAtDate", toDate(bd != null ? LocalDateTimeMapper.fromString(bd.getCreatedAt()) : null));

            List<TicketDTO> ts = new ArrayList<>();
            if (bd != null && bd.getTicketIds() != null) {
                for (Long tid : bd.getTicketIds()) ts.add(tickets.getTicketDetailsById(tid));
            }
            req.setAttribute("tickets", ts);

            forward(req, resp);
            return;
        }


        // Elegir aerolínea
        if (airline == null) {
            List<SoapAirlineDTO> airlines = safe(users.getAllAirlinesDetails().getItem());
            req.setAttribute("airlines", airlines);
            forward(req, resp);
            return;
        }
        req.setAttribute("airlineName", airline);

        // Rutas confirmadas de esa aerolínea
        if (route == null) {
            List<SoapFlightRouteDTO> rs = safe(
                    routes.getAllFlightRoutesDetailsByAirlineNickname(airline).getItem()
            ).stream().filter(r -> r.getStatus() == EnumEstatusRuta.CONFIRMADA).toList();
            req.setAttribute("routes", rs);
            forward(req, resp);
            return;
        }
        req.setAttribute("routeName", route);

        // Vuelos de la ruta
        if (flight == null) {
            List<SoapFlightDTO> fs = safe(flights.getAllFlightsDetailsByRouteName(route).getItem());
            List<Map<String,Object>> flightsView = new ArrayList<>();
            for (SoapFlightDTO f : fs) {
                Map<String,Object> m = new HashMap<>();
                m.put("name", f.getName());

//                LocalDateTime javaTime = UniversalLocalDateTimeAdapter.toJavaTime(f.getDepartureTime());
                //Voy a recibir un String como fecha desde el servicio
                LocalDateTime javaTime = LocalDateTimeMapper.fromString(f.getDepartureTime());
                m.put("departure", toDate(javaTime));
                flightsView.add(m);
            }
            req.setAttribute("flightsView", flightsView);
            forward(req, resp);
            return;
        }
        req.setAttribute("flightName", flight);

        // Listar TODAS mis reservas de ese vuelo
        String customerNick = ((SoapCustomerDTO) req.getAttribute("usuario")).getNickname();
        List<SoapBookFlightDTO> allForFlight = safe(books.getBookFlightsDetailsByFlightName(flight).getItem());

        List<SoapBookFlightDTO> myBookings = allForFlight.stream()
                .filter(b -> b != null && Objects.equals(customerNick, b.getCustomerNickname()))
                .sorted(
                        Comparator.comparing(
                                b -> LocalDateTimeMapper.fromString(b.getCreatedAt()),
                                Comparator.nullsLast(java.time.LocalDateTime::compareTo)
                        )
                )
                .toList();

        // Si piden ver el detalle de una en particular (booking=id), lo mostramos
        if (booking != null) {
            try {
                Long id = Long.valueOf(booking);
                SoapBookFlightDTO bd = books.getBookFlightDetailsById(id);
                req.setAttribute("booking", bd);
                req.setAttribute("bookingCreatedAtDate", toDate(bd != null ? LocalDateTimeMapper.fromString(bd.getCreatedAt()) : null));

                List<TicketDTO> ts = new ArrayList<>();
                if (bd != null && bd.getTicketIds() != null) {
                    for (Long tid : bd.getTicketIds()) ts.add(tickets.getTicketDetailsById(tid));
                }
                req.setAttribute("tickets", ts);
            } catch (Exception ignore) {
            }
        }

        if (myBookings.isEmpty()) {
            req.setAttribute("noBooking", true);
            req.setAttribute("myBookingsView", List.of());
            forward(req, resp);
            return;
        }

        // Armar lista visible de mis reservas
        List<Map<String,Object>> myBookingsView = new ArrayList<>();
        for (SoapBookFlightDTO b : myBookings) {
            Map<String,Object> bm = new HashMap<>();
            bm.put("id", b.getId());
            LocalDateTime javaTime = LocalDateTimeMapper.fromString(b.getCreatedAt());
            bm.put("createdAtDate", toDate(javaTime));
            bm.put("seatType", b.getSeatType());
            bm.put("totalPrice", b.getTotalPrice());

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

            myBookingsView.add(bm);
        }
        req.setAttribute("myBookingsView", myBookingsView);

        forward(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/src/views/checkflightreservation/checkflightreservation.jsp").forward(req, resp);
    }
}
