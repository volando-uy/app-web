package servlets.index;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;
import java.util.Locale;

import controllers.flight.IFlightController;
import controllers.flightRoute.IFlightRouteController;
import controllers.flightRoutePackage.IFlightRoutePackageController;

import domain.dtos.flight.BaseFlightDTO;
import domain.dtos.flightRoute.FlightRouteDTO;
import domain.dtos.flightRoutePackage.BaseFlightRoutePackageDTO;

import factory.ControllerFactory;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private final IFlightRoutePackageController pkgCtrl =
            ControllerFactory.getFlightRoutePackageController();
    private final IFlightController flightCtrl =
            ControllerFactory.getFlightController();
    private final IFlightRouteController flightRouteCtrl =
            ControllerFactory.getFlightRouteController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String toast = (String) req.getSession().getAttribute("toastMessage");
            String toastType = (String) req.getSession().getAttribute("toastType");

            if (toast != null) {
                req.setAttribute("toastMessage", toast);
                req.setAttribute("toastType", (toastType != null) ? toastType : "success");

                // limpiar para que no se muestre de nuevo en reloads
                req.getSession().removeAttribute("toastMessage");
                req.getSession().removeAttribute("toastType");
            }

        }


        List<BaseFlightRoutePackageDTO> packages = getPackagesPreferWithRoutes();
        req.setAttribute("packages", packages);

        String pkgModalJson = buildPkgModalJson(packages);
        req.setAttribute("pkgModalJson", (pkgModalJson != null) ? pkgModalJson : "{}");

        List<BaseFlightDTO> flights = getFlightsSafe();
        req.setAttribute("flights", flights);
        req.setAttribute("flightsCount", flights.size());

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private List<BaseFlightRoutePackageDTO> getPackagesPreferWithRoutes() {
        try {
            List<BaseFlightRoutePackageDTO> list =
                    pkgCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            return (list != null) ? list : Collections.emptyList();
        } catch (Throwable t) {
            try {
                List<BaseFlightRoutePackageDTO> list =
                        pkgCtrl.getAllFlightRoutesPackagesSimpleDetails();
                return (list != null) ? list : Collections.emptyList();
            } catch (Throwable t2) {
                return Collections.emptyList();
            }
        }
    }

    private List<BaseFlightDTO> getFlightsSafe() {
        try {
            List<BaseFlightDTO> list = flightCtrl.getAllFlightsSimpleDetails();
            return (list != null) ? list : Collections.emptyList();
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }

    /* ===== JSON con datos ricos para el modal ===== */
    private String buildPkgModalJson(List<BaseFlightRoutePackageDTO> packages) {
        StringBuilder out = new StringBuilder();
        out.append('{');
        boolean firstPkg = true;

        for (BaseFlightRoutePackageDTO p : packages) {
            String pkgName = safe(p.getName());
            if (pkgName.isEmpty()) continue;
            String key = normKey(pkgName);

            String desc = safe(p.getDescription());
            String seat = (p.getSeatType() != null) ? p.getSeatType().name() : "";
            Integer vig = p.getValidityPeriodDays();
            Double disc = p.getDiscount();
            String created = (p.getCreationDate() != null) ? p.getCreationDate().toString() : "";
            Double total = p.getTotalPrice();  // precio total del paquete (actual)

            // Rutas del paquete
            List<FlightRouteDTO> routes = Collections.emptyList();
            try {
                List<FlightRouteDTO> tmp = flightRouteCtrl.getAllFlightRoutesDetailsByPackageName(pkgName);
                routes = (tmp != null) ? tmp : Collections.emptyList();
            } catch (Throwable ignored) {
            }

            if (!firstPkg) out.append(',');
            firstPkg = false;
            out.append('"').append(esc(key)).append('"').append(':').append('{');

            // Campos del paquete
            writeStr(out, "description", desc);
            out.append(',');
            writeStr(out, "seatType", seat);
            out.append(',');
            writeNumOrNull(out, "discount", disc);
            out.append(',');
            writeNumOrNull(out, "validityDays", (vig != null) ? vig.doubleValue() : null);
            out.append(',');
            writeStr(out, "created", created);
            out.append(',');
            writeNumOrNull(out, "priceActual", total);
            out.append(',');
            writeStr(out, "currency", ""); /* si más adelante tenés moneda, setear acá */
            out.append(',');

            // Rutas
            out.append("\"routes\":[");
            boolean firstR = true;
            for (FlightRouteDTO r : routes) {
                if (r == null) continue;
                if (!firstR) out.append(',');
                firstR = false;

                out.append('{');
                // id/nombre
                writeStr(out, "id", nvl(r.getName()));
                out.append(',');
                writeStr(out, "nombre", nvl(r.getName()));
                out.append(',');
                // origen/destino + aerolínea
                writeStr(out, "originAero", nvl(r.getOriginAeroCode()));
                out.append(',');
                writeStr(out, "destinationAero", nvl(r.getDestinationAeroCode()));
                out.append(',');
                writeStr(out, "airline", nvl(r.getAirlineNickname()));
                out.append(',');
                // estado e imagen
                writeStr(out, "status", (r.getStatus() != null) ? r.getStatus().name() : "");
                out.append(',');
                writeStr(out, "image", nvl(r.getImage()));
                out.append(',');
                // precios por cabina
                writeNumOrNull(out, "priceTourist", r.getPriceTouristClass());
                out.append(',');
                writeNumOrNull(out, "priceBusiness", r.getPriceBusinessClass());
                out.append(',');
                // categorías (si vienen)
                out.append("\"categories\":[");
                List<String> cats = r.getCategories();
                if (cats != null) {
                    for (int i = 0; i < cats.size(); i++) {
                        if (i > 0) out.append(',');
                        out.append('"').append(esc(nvl(cats.get(i)))).append('"');
                    }
                }
                out.append("]");

                out.append('}');
            }
            out.append(']'); // routes

            out.append('}'); // pkg
        }

        out.append('}');
        return out.toString();
    }

    /* ===== Utils mínimos ===== */
    private static String nvl(String s) {
        return (s == null) ? "" : s;
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String normKey(String s) {
        if (s == null) return "";
        String x = s.trim().replace('\u00A0', ' ').replace('\u2013', '-').replace('\u2014', '-');
        x = x.replaceAll("\\s+", " ");
        return x.toLowerCase(Locale.ROOT);
    }

    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder b = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '"') b.append('\\').append(c);
            else b.append(c);
        }
        return b.toString();
    }

    private static void writeStr(StringBuilder sb, String k, String v) {
        sb.append('"').append(esc(k)).append('"').append(':').append('"').append(esc(v == null ? "" : v)).append('"');
    }

    private static void writeNumOrNull(StringBuilder sb, String k, Double v) {
        sb.append('"').append(esc(k)).append('"').append(':');
        if (v == null) sb.append("null");
        else {
            if (Math.abs(v - Math.rint(v)) < 1e-9) sb.append(String.valueOf(v.longValue()));
            else sb.append(v.toString());
        }
    }
}
