package servlets.reservations;

import controllers.booking.IBookingController;
import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import controllers.seat.ISeatController;

import domain.dtos.bookFlight.BaseBookFlightDTO;
import domain.dtos.bookFlight.BookFlightDTO;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.dtos.luggage.BaseBasicLuggageDTO;
import domain.dtos.luggage.BaseExtraLuggageDTO;
import domain.dtos.luggage.LuggageDTO;
import domain.dtos.seat.SeatDTO;
import domain.dtos.ticket.BaseTicketDTO;

import domain.models.enums.EnumTipoAsiento;
import domain.models.enums.EnumTipoDocumento;
import domain.models.luggage.EnumEquipajeBasico;
import domain.models.luggage.EnumEquipajeExtra;

import factory.ControllerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;


@WebServlet("/reservas")
public class BookFlightServlet extends HttpServlet {

    private final IBookingController booking    = ControllerFactory.getBookingController();
    private final IFlightController flights     = ControllerFactory.getFlightController();
    private final IFlightRouteController routes = ControllerFactory.getFlightRouteController();
    private final ISeatController seats         = ControllerFactory.getSeatController();



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setupEncoding(resp, req);

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nickname");
        if (nick == null || nick.isBlank()) {

            req.getSession(true).setAttribute(
                    "redirectAfterLogin",
                    req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "")
            );
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String flightName = req.getParameter("flight");
        if (isBlank(flightName)) { resp.sendError(400); return; }

        boolean already = hasExistingBookingForFlight(nick, flightName);
        boolean proceedAllowed = "1".equals(req.getParameter("proceed"));

        FlightDTO flight = flights.getFlightDetailsByName(flightName);
        if (flight == null) { resp.sendError(404); return; }

        FlightRouteDTO route = routes.getFlightRouteDetailsByName(flight.getFlightRouteName());

        String seatTypePreview = nz(req.getParameter("seatType"), "TURISTA");
        int passengersCount = parseInt(req.getParameter("passengersCount"), 1);
        if (passengersCount < 1) passengersCount = 1;

        // Disponibilidad por clase (capacidad - tickets emitidos)
        SeatAvailability avail = getAvailabilityForFlight(flightName, flight);
        int maxForSelected = "EJECUTIVO".equalsIgnoreCase(seatTypePreview) ? avail.ejecutivo : avail.turista;
        if (passengersCount > maxForSelected) passengersCount = maxForSelected;
        if (maxForSelected <= 0) {
            toast(req, "No hay asientos disponibles en " + seatTypePreview + " para este vuelo.", "warning");
        }

        // Precio por asiento según clase
        Double unitPrice = "EJECUTIVO".equalsIgnoreCase(seatTypePreview)
                ? route.getPriceBusinessClass()
                : route.getPriceTouristClass();
        if (unitPrice == null) unitPrice = 0.0;

        // Catálogos para selects
        req.setAttribute("docTypes", EnumTipoDocumento.values());
        req.setAttribute("basicLuggages", EnumEquipajeBasico.values());
        req.setAttribute("extraLuggages", EnumEquipajeExtra.values());

        // Datos base para la vista
        req.setAttribute("flight", flight);
        req.setAttribute("route", route);
        req.setAttribute("seatTypePreview", seatTypePreview);
        req.setAttribute("passengersCount", passengersCount);
        req.setAttribute("unitPrice", unitPrice);

        // Disponibilidad / límites UI
        req.setAttribute("availTurista", avail.turista);
        req.setAttribute("availEjecutivo", avail.ejecutivo);
        req.setAttribute("passengersMax", Math.max(0, maxForSelected));

        // Flags de UX
        req.setAttribute("existingBooking", already);
        req.setAttribute("proceedAllowed", proceedAllowed);

        if (already && !proceedAllowed) {
            toast(req, "Ya tienes una reserva para este vuelo.", "warning");
        }

        req.getRequestDispatcher("/src/views/bookFlight/bookflight.jsp").forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setupEncoding(resp, req);

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nickname");
        if (nick == null || nick.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String flightName   = req.getParameter("flight");
        String seatTypeStr  = req.getParameter("seatType");
        int passengersCount = parseInt(req.getParameter("passengersCount"), 0);
        boolean proceedAllowed = "1".equals(req.getParameter("proceed"));
        String action = nz(req.getParameter("action"), "confirm"); // "calc" | "confirm"

        if (isBlank(flightName)) { resp.sendError(400); return; }


        // -   CALCULAR costo
        // - Bloquea CONFIRMAR salvo que venga proceed=1.
        boolean exists = hasExistingBookingForFlight(nick, flightName);
        if ("confirm".equalsIgnoreCase(action) && exists && !proceedAllowed) {
            resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + url(flightName));
            return;
        }

        if (passengersCount < 1) {
            toast(req, "Debes indicar al menos 1 pasajero.", "error");
            resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + url(flightName));
            return;
        }

        FlightDTO flight = flights.getFlightDetailsByName(flightName);
        if (flight == null) { resp.sendError(404); return; }
        FlightRouteDTO route = routes.getFlightRouteDetailsByName(flight.getFlightRouteName());

        // Validación de disponibilidad en runtime
        SeatAvailability avail = getAvailabilityForFlight(flightName, flight);
        EnumTipoAsiento st = EnumTipoAsiento.valueOf(seatTypeStr);
        int maxForSelected = (st == EnumTipoAsiento.EJECUTIVO) ? avail.ejecutivo : avail.turista;
        if (passengersCount > maxForSelected) {
            toast(req, "Quedan " + maxForSelected + " asientos en " + st + ". Ajustá la cantidad.", "error");
            String back = req.getContextPath() + "/reservas?flight=" + url(flightName)
                    + "&seatType=" + url(seatTypeStr)
                    + "&passengersCount=" + maxForSelected
                    + (proceedAllowed ? "&proceed=1" : "");
            resp.sendRedirect(back);
            return;
        }

        // Precio por asiento según clase
        Double unitPrice = (st == EnumTipoAsiento.EJECUTIVO)
                ? route.getPriceBusinessClass()
                : route.getPriceTouristClass();
        if (unitPrice == null) unitPrice = 0.0;

        if ("calc".equalsIgnoreCase(action)) {


            for (int i = 0; i < passengersCount; i++) {
                String pfx = "passengers[" + i + "]";
                String name         = req.getParameter(pfx + ".name");
                String surname      = req.getParameter(pfx + ".surname");
                String docTypeStr   = req.getParameter(pfx + ".docType");
                String doc          = req.getParameter(pfx + ".numDoc");
                String basicTypeStr = req.getParameter(pfx + ".basicLuggageType");
                String extraTypeStr = req.getParameter(pfx + ".extraLuggageType");

                if (isBlank(name) || isBlank(surname) || isBlank(docTypeStr) || isBlank(doc)
                        || isBlank(basicTypeStr) || isBlank(extraTypeStr)) {

                    toast(req, "Completa los datos de todos los pasajeros para calcular.", "error");

                    req.setAttribute("docTypes", EnumTipoDocumento.values());
                    req.setAttribute("basicLuggages", EnumEquipajeBasico.values());
                    req.setAttribute("extraLuggages", EnumEquipajeExtra.values());

                    req.setAttribute("flight", flight);
                    req.setAttribute("route", route);
                    req.setAttribute("seatTypePreview", seatTypeStr);
                    req.setAttribute("passengersCount", passengersCount);
                    req.setAttribute("unitPrice", unitPrice);

                    req.setAttribute("availTurista", avail.turista);
                    req.setAttribute("availEjecutivo", avail.ejecutivo);
                    req.setAttribute("passengersMax", Math.max(0, maxForSelected));

                    req.setAttribute("existingBooking", exists);
                    req.setAttribute("proceedAllowed", proceedAllowed);
                    req.setAttribute("suppressExistingBanner", true);

                    req.getRequestDispatcher("/src/views/bookFlight/bookflight.jsp").forward(req, resp);
                    return;
                }
            }

            CostBreakdown cb = computeCost(req, passengersCount, unitPrice, route);


            req.setAttribute("docTypes", EnumTipoDocumento.values());
            req.setAttribute("basicLuggages", EnumEquipajeBasico.values());
            req.setAttribute("extraLuggages", EnumEquipajeExtra.values());

            req.setAttribute("flight", flight);
            req.setAttribute("route", route);
            req.setAttribute("seatTypePreview", seatTypeStr);
            req.setAttribute("passengersCount", passengersCount);
            req.setAttribute("unitPrice", unitPrice);

            req.setAttribute("availTurista", avail.turista);
            req.setAttribute("availEjecutivo", avail.ejecutivo);
            req.setAttribute("passengersMax", Math.max(0, maxForSelected));

            req.setAttribute("calcSeatSubtotal", cb.seatSubtotal);
            req.setAttribute("calcExtraSubtotal", cb.extraSubtotal);
            req.setAttribute("calcTotal", cb.total());
            req.setAttribute("calcDone", true);
            req.setAttribute("priceExtraUnit", route.getPriceExtraUnitBaggage());

            req.setAttribute("existingBooking", exists);
            req.setAttribute("proceedAllowed", proceedAllowed);
            req.setAttribute("suppressExistingBanner", true);

            req.getRequestDispatcher("/src/views/bookFlight/bookflight.jsp").forward(req, resp);
            return;
        }


        Map<BaseTicketDTO, List<LuggageDTO>> ticketMap = new LinkedHashMap<>();

        for (int i = 0; i < passengersCount; i++) {
            String pfx = "passengers[" + i + "]";

            String name         = req.getParameter(pfx + ".name");
            String surname      = req.getParameter(pfx + ".surname");
            String docTypeStr   = req.getParameter(pfx + ".docType");
            String doc          = req.getParameter(pfx + ".numDoc");

            String basicTypeStr = req.getParameter(pfx + ".basicLuggageType");
            double basicWeight  = parseDouble(req.getParameter(pfx + ".basicLuggageWeight"), 0.0);

            String extraTypeStr = req.getParameter(pfx + ".extraLuggageType");
            int    extraUnits   = parseInt(req.getParameter(pfx + ".extraLuggageUnits"), 0);
            double extraWeight  = parseDouble(req.getParameter(pfx + ".extraLuggageWeight"), 0.0);

            if (isBlank(name) || isBlank(surname) || isBlank(docTypeStr) || isBlank(doc)
                    || isBlank(basicTypeStr) || isBlank(extraTypeStr)) {
                toast(req, "Completa los datos de todos los pasajeros.", "error");
                resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + url(flightName));
                return;
            }

            BaseTicketDTO t = new BaseTicketDTO();
            t.setName(name);
            t.setSurname(surname);
            t.setNumDoc(doc);
            t.setDocType(EnumTipoDocumento.valueOf(docTypeStr));

            List<LuggageDTO> l = new ArrayList<>();

            BaseBasicLuggageDTO basic = new BaseBasicLuggageDTO();
            basic.setCategory(EnumEquipajeBasico.valueOf(basicTypeStr));
            basic.setWeight(basicWeight);
            l.add(basic);

            EnumEquipajeExtra extraCat = EnumEquipajeExtra.valueOf(extraTypeStr);
            for (int x = 0; x < extraUnits; x++) {
                BaseExtraLuggageDTO ex = new BaseExtraLuggageDTO();
                ex.setCategory(extraCat);
                ex.setWeight(extraWeight);
                l.add(ex);
            }

            ticketMap.put(t, l);
        }

        CostBreakdown confirmCost = computeCost(req, passengersCount, unitPrice, route);

        BaseBookFlightDTO bookingDTO = new BaseBookFlightDTO();
        bookingDTO.setSeatType(st);
        bookingDTO.setCreatedAt(LocalDateTime.now());
        bookingDTO.setTotalPrice(confirmCost.total()); // ← ahora sí guardamos el total

        try {
            booking.createBooking(bookingDTO, ticketMap, nick, flightName);
            toast(req, "Reserva creada correctamente", "success");
            resp.sendRedirect(req.getContextPath() + "/flight/list");
        } catch (Exception e) {
            getServletContext().log("createBooking error", e);
            toast(req, "No se pudo crear la reserva: " + e.getMessage(), "error");
            resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + url(flightName) + "&proceed=1");
        }
    }


    /** Disponibilidad por clase: capacidad del vuelo menos tickets emitidos por clase. */
    private SeatAvailability getAvailabilityForFlight(String flightName, FlightDTO flight) {
        int capTurista   = safeInt(flight.getMaxEconomySeats());
        int capEjecutivo = safeInt(flight.getMaxBusinessSeats());

        int occTurista = 0, occEjecutivo = 0;
        try {
            List<BookFlightDTO> bookings = booking.getBookFlightsDetailsByFlightName(flightName);
            if (bookings != null) {
                for (BookFlightDTO bf : bookings) {
                    if (bf == null || bf.getTicketIds() == null) continue;
                    int cant = bf.getTicketIds().size();
                    if (bf.getSeatType() == EnumTipoAsiento.TURISTA)   occTurista   += cant;
                    if (bf.getSeatType() == EnumTipoAsiento.EJECUTIVO) occEjecutivo += cant;
                }
            }
        } catch (Exception ex) {
            getServletContext().log("avail lookup failed for flight=" + flightName, ex);
        }

        int dispTurista   = Math.max(0, capTurista   - occTurista);
        int dispEjecutivo = Math.max(0, capEjecutivo - occEjecutivo);
        return new SeatAvailability(dispTurista, dispEjecutivo);
    }

    private boolean hasExistingBookingForFlight(String nickname, String flightName) {
        try {
            List<BookFlightDTO> list = booking.getBookFlightsDetailsByCustomerNickname(nickname);
            if (list == null || list.isEmpty()) return false;
            String target = flightName == null ? "" : flightName.trim();

            for (BookFlightDTO bf : list) {
                if (bf == null || bf.getTicketIds() == null) continue;
                for (Long tid : bf.getTicketIds()) {
                    if (tid == null) continue;
                    try {
                        SeatDTO seat = seats.getSeatDetailsByTicketId(tid);
                        if (seat != null && seat.getFlightName() != null) {
                            if (seat.getFlightName().trim().equalsIgnoreCase(target)) {
                                return true;
                            }
                        }
                    } catch (Exception ex) {
                        getServletContext().log("Seat lookup failed for ticketId=" + tid, ex);
                    }
                }
            }
        } catch (Exception ex) {
            getServletContext().log("existing booking check failed", ex);
        }
        return false;
    }


    /**   asientos + extra   */
    private static final class CostBreakdown {
        double seatSubtotal;
        double extraSubtotal;
        double total() { return seatSubtotal + extraSubtotal; }
    }

    /**
     * Calcula el total
     * - Asientos = unitSeatPrice * pasajeros
     * - Extra= (suma de unidades) * route.priceExtraUnitBaggage
     */
    private CostBreakdown computeCost(HttpServletRequest req,
                                      int passengersCount,
                                      double unitSeatPrice,
                                      FlightRouteDTO route) {
        CostBreakdown cb = new CostBreakdown();
        cb.seatSubtotal = unitSeatPrice * Math.max(passengersCount, 0);

        double extra = 0.0;
        double pricePerExtraUnit = route.getPriceExtraUnitBaggage() == null ? 0.0
                : route.getPriceExtraUnitBaggage();

        for (int i = 0; i < passengersCount; i++) {
            String pfx = "passengers[" + i + "]";
            int extraUnits = parseInt(req.getParameter(pfx + ".extraLuggageUnits"), 0);
            if (extraUnits > 0) extra += pricePerExtraUnit * extraUnits;
        }
        cb.extraSubtotal = extra;
        return cb;
    }


    private static final class SeatAvailability {
        final int turista;
        final int ejecutivo;
        SeatAvailability(int t, int e) { this.turista = t; this.ejecutivo = e; }
    }

    private static void setupEncoding(HttpServletResponse resp , HttpServletRequest req  ) {
        try {
            req.setCharacterEncoding("UTF-8");
        } catch (Exception ignored) {
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
    }

    private static int parseInt(String s, int d){
        try { return Integer.parseInt(s); } catch (Exception e) { return d; }
    }

    private static double parseDouble(String s, double d){
        try { return Double.parseDouble(s.replace(',', '.')); } catch (Exception e) { return d; }
    }

    private static int safeInt(Integer x){ return x == null ? 0 : x; }
    private static String nz(String s, String d){ return (s==null || s.isBlank()) ? d : s.trim(); }
    private static String url(String s){ try{ return URLEncoder.encode(s, StandardCharsets.UTF_8);}catch(Exception e){ return s; } }
    private static boolean isBlank(String s){ return s==null || s.trim().isEmpty(); }

    private static void toast(HttpServletRequest req, String msg, String type) {
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", msg);
        session.setAttribute("toastType", type);
    }
}
