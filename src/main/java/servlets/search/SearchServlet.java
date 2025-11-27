package servlets.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.labpa.appweb.flight.FlightSoapAdapter;
import com.labpa.appweb.flight.FlightSoapAdapterService;
import com.labpa.appweb.flight.SoapFlightDTO;

import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;
import com.labpa.appweb.flightroute.SoapFlightRouteDTO;
import com.labpa.appweb.flightroute.EnumEstatusRuta;

import com.labpa.appweb.flightroutepackage.BaseFlightRoutePackageDTO;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapter;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapterService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@WebServlet("/buscar")
public class SearchServlet extends HttpServlet {

    private final FlightSoapAdapter flightCtrl =
            new FlightSoapAdapterService().getFlightSoapAdapterPort();

    private final FlightRouteSoapAdapter routeCtrl =
            new FlightRouteSoapAdapterService().getFlightRouteSoapAdapterPort();

    private final FlightRoutePackageSoapAdapter packageCtrl =
            new FlightRoutePackageSoapAdapterService().getFlightRoutePackageSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json; charset=UTF-8");

        String query = Optional.ofNullable(req.getParameter("query"))
                .orElse("")
                .trim()
                .toLowerCase();

        JsonObject json = new JsonObject();

        /* ============================================================
         * 1) RUTAS CONFIRMADAS A PARTIR DE LOS VUELOS
         * ============================================================ */
        List<SoapFlightDTO> allFlights;
        try {
            allFlights = flightCtrl.getAllFlightsDetails().getItem();
            if (allFlights == null) allFlights = Collections.emptyList();
        } catch (Exception e) {
            allFlights = Collections.emptyList();
        }

        // nombres de rutas
        Set<String> routeNames = new HashSet<>();
        for (SoapFlightDTO f : allFlights) {
            if (f != null && f.getFlightRouteName() != null) {
                routeNames.add(f.getFlightRouteName());
            }
        }

        // obtener detalles de la ruta
        List<SoapFlightRouteDTO> rutasConfirmadas = new ArrayList<>();
        for (String rn : routeNames) {
            try {
                SoapFlightRouteDTO r = routeCtrl.getFlightRouteDetailsByName(rn);
                if (r != null && r.getStatus() == EnumEstatusRuta.CONFIRMADA) {
                    rutasConfirmadas.add(r);
                }
            } catch (Exception ignored) {}
        }

        JsonArray rutasJson = new JsonArray();
        for (SoapFlightRouteDTO r : rutasConfirmadas) {
            if (r == null) continue;

            String name = Optional.ofNullable(r.getName()).orElse("").toLowerCase();
            String desc = Optional.ofNullable(r.getDescription()).orElse("").toLowerCase();

            if (!query.isEmpty() && !(name.contains(query) || desc.contains(query)))
                continue;

            JsonObject o = new JsonObject();
            o.addProperty("name", r.getName());
            o.addProperty("description", r.getDescription());

            // URL correcta → flight/list?route=
            o.addProperty(
                    "url",
                    req.getContextPath() + "/flight/list?route=" +
                            URLEncoder.encode(r.getName(), "UTF-8")
            );

            rutasJson.add(o);
        }

        json.add("rutas", rutasJson);


        /* ============================================================
         * 2) PAQUETES
         * ============================================================ */
        List<BaseFlightRoutePackageDTO> paquetes;
        try {
            paquetes = packageCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes().getItem();
            if (paquetes == null) paquetes = Collections.emptyList();
        } catch (Exception e) {
            paquetes = Collections.emptyList();
        }

        JsonArray paquetesJson = new JsonArray();
        for (BaseFlightRoutePackageDTO p : paquetes) {
            if (p == null) continue;

            String name = Optional.ofNullable(p.getName()).orElse("").toLowerCase();
            String desc = Optional.ofNullable(p.getDescription()).orElse("").toLowerCase();

            if (!query.isEmpty() && !(name.contains(query) || desc.contains(query)))
                continue;

            JsonObject o = new JsonObject();
            o.addProperty("name", p.getName());
            o.addProperty("description", p.getDescription());

            o.addProperty(
                    "url",
                    req.getContextPath() + "/packages/list?q=" +
                            URLEncoder.encode(p.getName(), "UTF-8")
            );

            paquetesJson.add(o);
        }

        json.add("paquetes", paquetesJson);

        /* ============================================================ */
        resp.getWriter().print(json.toString());
    }
}
