package servlets.reservations;

import controllers.booking.IBookingController;
import controllers.flight.IFlightController;
import controllers.flightroute.IFlightRouteController;
import controllers.seat.ISeatController;

import domain.dtos.bookflight.BaseBookFlightDTO;
import domain.dtos.bookflight.BookFlightDTO;
import domain.dtos.flight.FlightDTO;
import domain.dtos.flightroute.FlightRouteDTO;
import domain.dtos.luggage.BaseBasicLuggageDTO;
import domain.dtos.luggage.BaseExtraLuggageDTO;
import domain.dtos.luggage.LuggageDTO;
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
    private final IFlightController  flights    = ControllerFactory.getFlightController();
    private final IFlightRouteController routes = ControllerFactory.getFlightRouteController();
    private final ISeatController    seats      = ControllerFactory.getSeatController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nickname");
        if (isBlank(nick)) {
            req.getSession(true).setAttribute("redirectAfterLogin",
                    req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String flightName = req.getParameter("flight");
        if (isBlank(flightName)) { resp.sendError(400); return; }

        FlightDTO flight = flights.getFlightDetailsByName(flightName);
        if (flight == null) { resp.sendError(404); return; }
        FlightRouteDTO route = routes.getFlightRouteDetailsByName(flight.getFlightRouteName());

        EnumTipoAsiento seatType = parseSeatType(req.getParameter("seatType"));
        int passengersCount = Math.max(1, i(req.getParameter("passengersCount"), 1));

        SeatAvailability avail = availability(flightName, flight);
        int maxSel = (seatType == EnumTipoAsiento.EJECUTIVO) ? avail.ejecutivo : avail.turista;
        if (maxSel > 0 && passengersCount > maxSel) passengersCount = maxSel;

        boolean existing = hasUserBookingForFlight(nick, flightName);

        setView(req, flight, route, seatType.name(), passengersCount, unitPrice(route, seatType), avail, existing);
        req.getRequestDispatcher("/src/views/bookFlight/bookflight.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nickname");
        if (isBlank(nick)) { resp.sendRedirect(req.getContextPath() + "/users/login"); return; }

        String flightName = req.getParameter("flight");
        if (isBlank(flightName)) { resp.sendError(400); return; }

        String action = req.getParameter("action");
        EnumTipoAsiento seatType = parseSeatType(req.getParameter("seatType"));
        int passengersCount = i(req.getParameter("passengersCount"), 0);

        if (passengersCount < 1) {
            toast(req, "Indicá al menos 1 pasajero.", "error");
            resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + enc(flightName));
            return;
        }

        FlightDTO flight = flights.getFlightDetailsByName(flightName);
        if (flight == null) { resp.sendError(404); return; }
        FlightRouteDTO route = routes.getFlightRouteDetailsByName(flight.getFlightRouteName());

        SeatAvailability avail = availability(flightName, flight);
        int maxSel = (seatType == EnumTipoAsiento.EJECUTIVO) ? avail.ejecutivo : avail.turista;
        boolean capConocida = (maxSel != Integer.MAX_VALUE);
        if (capConocida && (maxSel <= 0 || passengersCount > maxSel)) {
            toast(req, "Quedan " + Math.max(0, maxSel) + " asientos en " + seatType + ". Ajustá la cantidad.", "error");
            resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + enc(flightName)
                    + "&seatType=" + seatType.name()
                    + "&passengersCount=" + Math.max(0, maxSel));
            return;
        }

        boolean existing = hasUserBookingForFlight(nick, flightName);
        double unitPrice = unitPrice(route, seatType);

        if ("calc".equalsIgnoreCase(action)) {
            // Validación mínima antes de calcular
            for (int k = 0; k < passengersCount; k++) {
                String p = "passengers[" + k + "]";
                if (isBlank(req.getParameter(p + ".name")) ||
                        isBlank(req.getParameter(p + ".surname")) ||
                        isBlank(req.getParameter(p + ".docType")) ||
                        isBlank(req.getParameter(p + ".numDoc")) ||
                        isBlank(req.getParameter(p + ".extraLuggageType"))) {
                    toast(req, "Completá los datos básicos de todos los pasajeros para calcular.", "error");
                    setView(req, flight, route, seatType.name(), passengersCount, unitPrice, avail, existing);
                    req.getRequestDispatcher("/src/views/bookFlight/bookflight.jsp").forward(req, resp);
                    return;
                }
            }

            CostBreakdown cb = computeCost(req, passengersCount, unitPrice, route);
            setView(req, flight, route, seatType.name(), passengersCount, unitPrice, avail, existing);
            req.setAttribute("calcSeatSubtotal", cb.seatSubtotal);
            req.setAttribute("calcExtraSubtotal", cb.extraSubtotal);
            req.setAttribute("priceExtraUnit", route.getPriceExtraUnitBaggage() == null ? 0.0 : route.getPriceExtraUnitBaggage());
            req.setAttribute("calcTotal", cb.total());
            req.setAttribute("calcDone", true);
            req.getRequestDispatcher("/src/views/bookFlight/bookflight.jsp").forward(req, resp);
            return;
        }

        // confirm: continuar aunque exista una reserva previa
        Map<BaseTicketDTO, List<LuggageDTO>> ticketMap = new LinkedHashMap<>();
        for (int k = 0; k < passengersCount; k++) {
            String p = "passengers[" + k + "]";

            String name    = req.getParameter(p + ".name");
            String surname = req.getParameter(p + ".surname");
            String dtStr   = req.getParameter(p + ".docType");
            String doc     = req.getParameter(p + ".numDoc");

            String basicTypeStr = req.getParameter(p + ".basicLuggageType");
            double basicWeight  = d(req.getParameter(p + ".basicLuggageWeight"), 0.0);

            String extraTypeStr = req.getParameter(p + ".extraLuggageType");
            int    extraUnits   = i(req.getParameter(p + ".extraLuggageUnits"), 0);
            double extraWeight  = d(req.getParameter(p + ".extraLuggageWeight"), 0.0);

            if (isBlank(name) || isBlank(surname) || isBlank(dtStr) || isBlank(doc) ||
                    isBlank(basicTypeStr) || isBlank(extraTypeStr)) {
                toast(req, "Completá los datos de todos los pasajeros.", "error");
                resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + enc(flightName));
                return;
            }

            BaseTicketDTO t = new BaseTicketDTO();
            t.setName(name);
            t.setSurname(surname);
            t.setNumDoc(doc);
            t.setDocType(EnumTipoDocumento.valueOf(dtStr));

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

        CostBreakdown cbConfirm = computeCost(req, passengersCount, unitPrice, route);

        BaseBookFlightDTO bookingDTO = new BaseBookFlightDTO();
        bookingDTO.setSeatType(seatType);
        bookingDTO.setCreatedAt(LocalDateTime.now());
        bookingDTO.setTotalPrice(cbConfirm.total());

        try {
            booking.createBooking(bookingDTO, ticketMap, nick, flightName);
            toast(req, "Reserva creada correctamente.", "success");
            resp.sendRedirect(req.getContextPath() + "/perfil");
        } catch (Exception e) {
            getServletContext().log("createBooking error", e);
            toast(req, "No se pudo crear la reserva: " + safeMsg(e), "error");
            resp.sendRedirect(req.getContextPath() + "/reservas?flight=" + enc(flightName));
        }
    }

    private boolean hasUserBookingForFlight(String nickname, String flightName) {
        if (isBlank(nickname) || isBlank(flightName)) return false;
        try {
            List<BookFlightDTO> list = booking.getBookFlightsDetailsByFlightName(flightName);
            if (list == null) return false;
            for (BookFlightDTO bf : list) {
                if (bf == null) continue;
                String who = bf.getCustomerNickname();
                if (who != null && who.trim().equalsIgnoreCase(nickname.trim())) {
                    return true;
                }
            }
        } catch (Exception ex) {
            getServletContext().log("hasUserBookingForFlight failed", ex);
        }
        return false;
    }

    private SeatAvailability availability(String flightName, FlightDTO f) {
        int capT = (f.getMaxEconomySeats()  == null) ? 0 : f.getMaxEconomySeats();
        int capE = (f.getMaxBusinessSeats() == null) ? 0 : f.getMaxBusinessSeats();

        int occT = 0, occE = 0;
        try {
            List<BookFlightDTO> list = booking.getBookFlightsDetailsByFlightName(flightName);
            if (list != null) {
                for (BookFlightDTO bf : list) {
                    if (bf == null || bf.getTicketIds() == null) continue;
                    int n = bf.getTicketIds().size();
                    if (bf.getSeatType() == EnumTipoAsiento.TURISTA)   occT += n;
                    if (bf.getSeatType() == EnumTipoAsiento.EJECUTIVO) occE += n;
                }
            }
        } catch (Exception ex) {
            getServletContext().log("avail lookup failed for flight=" + flightName, ex);
        }

        int dispT = (capT > 0) ? Math.max(0, capT - occT) : Integer.MAX_VALUE;
        int dispE = (capE > 0) ? Math.max(0, capE - occE) : Integer.MAX_VALUE;
        return new SeatAvailability(dispT, dispE);
    }

    private static final class CostBreakdown {
        double seatSubtotal, extraSubtotal;
        double total() { return seatSubtotal + extraSubtotal; }
    }

    private CostBreakdown computeCost(HttpServletRequest req, int pax, double unitSeatPrice, FlightRouteDTO route) {
        CostBreakdown cb = new CostBreakdown();
        cb.seatSubtotal = unitSeatPrice * Math.max(pax, 0);

        double perExtra = (route.getPriceExtraUnitBaggage() == null) ? 0.0 : route.getPriceExtraUnitBaggage();
        double extra = 0.0;
        for (int k = 0; k < pax; k++) {
            int u = i(req.getParameter("passengers[" + k + "].extraLuggageUnits"), 0);
            if (u > 0) extra += perExtra * u;
        }
        cb.extraSubtotal = extra;
        return cb;
    }

    private void setView(HttpServletRequest req, FlightDTO flight, FlightRouteDTO route,
                         String seatType, int pax, double unitPrice,
                         SeatAvailability avail, boolean existingBooking) {

        req.setAttribute("docTypes", EnumTipoDocumento.values());
        req.setAttribute("basicLuggages", EnumEquipajeBasico.values());
        req.setAttribute("extraLuggages", EnumEquipajeExtra.values());

        req.setAttribute("flight", flight);
        req.setAttribute("route", route);
        req.setAttribute("seatTypePreview", seatType);
        req.setAttribute("passengersCount", pax);
        req.setAttribute("unitPrice", unitPrice);

        int maxT = (avail.turista   == Integer.MAX_VALUE) ? 9 : avail.turista;
        int maxE = (avail.ejecutivo == Integer.MAX_VALUE) ? 9 : avail.ejecutivo;
        req.setAttribute("availTurista", maxT);
        req.setAttribute("availEjecutivo", maxE);
        req.setAttribute("passengersMax", "EJECUTIVO".equalsIgnoreCase(seatType) ? maxE : maxT);

        req.setAttribute("existingBooking", existingBooking);
    }

    // util mínimos
    private static final class SeatAvailability { final int turista, ejecutivo; SeatAvailability(int t, int e){this.turista=t; this.ejecutivo=e;} }
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static int i(String s, int d){ try { return Integer.parseInt(s); } catch (Exception e) { return d; } }
    private static double d(String s, double v){ try { return Double.parseDouble(s.replace(',', '.')); } catch (Exception e) { return v; } }
    private static String enc(String s){ try { return URLEncoder.encode(s, StandardCharsets.UTF_8); } catch (Exception e) { return s; } }

    private static String safeMsg(Exception e) {
        String m = e.getMessage();
        if (m == null) return "Error inesperado.";
        return m.length() > 180 ? m.substring(0, 180) + "…" : m;
    }

    private static EnumTipoAsiento parseSeatType(String s){
        try { return EnumTipoAsiento.valueOf(isBlank(s) ? "TURISTA" : s); }
        catch (Exception e) { return EnumTipoAsiento.TURISTA; }
    }

    private static void toast(HttpServletRequest req, String msg, String type) {
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", msg);
        session.setAttribute("toastType", type);
    }

    private static double unitPrice(FlightRouteDTO route, EnumTipoAsiento st){
        Double v = (st == EnumTipoAsiento.EJECUTIVO) ? route.getPriceBusinessClass() : route.getPriceTouristClass();
        return v == null ? 0.0 : v;
    }
}
