package servlets.index;

import adapters.FlightViewDTO;

import com.labpa.appweb.flight.FlightSoapAdapter;
import com.labpa.appweb.flight.FlightSoapAdapterService;
import com.labpa.appweb.flight.SoapBaseFlightDTO;
import com.labpa.appweb.flightroute.EnumEstatusRuta;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;
import com.labpa.appweb.flightroute.SoapFlightRouteDTO;
import com.labpa.appweb.flightroutepackage.BaseFlightRoutePackageDTO;
import com.labpa.appweb.flightroutepackage.FlightRoutePackage;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapter;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapterService;
import com.labpa.appweb.user.SoapUserDTO;
import config.SoapServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@WebServlet("/index")
public class IndexServlet extends HttpServlet {

//    private final IFlightRoutePackageController pkgCtrl =
//            ControllerFactory.getFlightRoutePackageController();
////    private final IFlightController flightCtrl =
////            ControllerFactory.getFlightController();
    private FlightSoapAdapter flightPort = SoapServiceFactory.getFlightService();
    private FlightRoutePackageSoapAdapter flightRoutePackagePort = SoapServiceFactory.getFlightRoutePackageService();
    private FlightRouteSoapAdapter flightRouteSoapAdapter = SoapServiceFactory.getFlightRouteService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session != null) {
            String toast = (String) req.getSession().getAttribute("toastMessage");
            String toastType = (String) req.getSession().getAttribute("toastType");
            System.out.println("Toastr en IndexServlet: " + toast + " (type: " + toastType + ")");

            if (toast != null) {
                req.setAttribute("toastMessage", toast);
                req.setAttribute("toastType", (toastType != null) ? toastType : "success");

//                // limpiar para que no se muestre de nuevo en reloads
//                req.getSession().removeAttribute("toastMessage");
//                req.getSession().removeAttribute("toastType");
            }
        }

        List<BaseFlightRoutePackageDTO> packages = getPackagesPreferWithRoutes();
        req.setAttribute("packages", packages);

        List<SoapBaseFlightDTO> list = flightPort.getAllFlightsSimpleDetails().getItem();

        List<FlightViewDTO> viewList = new ArrayList<>();
        for (SoapBaseFlightDTO f : list) {
            FlightViewDTO dto = new FlightViewDTO();

            dto.setName(f.getName());
            dto.setImage(f.getImage());
            dto.setDepartureTime(f.getDepartureTime() != null ? f.getDepartureTime() : "--");
            dto.setCreatedAt(f.getCreatedAt() != null ? f.getCreatedAt() : "--");
            dto.setDuration(f.getDuration() != null ? String.valueOf(f.getDuration()) : "--");
            dto.setMaxEconomySeats(f.getMaxEconomySeats() != null ? String.valueOf(f.getMaxEconomySeats()) : "--");
            dto.setMaxBusinessSeats(f.getMaxBusinessSeats() != null ? String.valueOf(f.getMaxBusinessSeats()) : "--");

            viewList.add(dto);
        }

        req.setAttribute("flights", viewList);
        req.setAttribute("flightsCount", viewList.size());

        List<BaseFlightRoutePackageDTO> pkgs;
        Map<String, List<SoapFlightRouteDTO>> pkgRoutes = new HashMap<>();
        try {
            pkgs = flightRoutePackagePort.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes().getItem();
            if (pkgs == null) pkgs = Collections.emptyList();
        } catch (Exception e) {
            pkgs = Collections.emptyList();
        }

// rutas confirmadas por paquete
        for (BaseFlightRoutePackageDTO p : pkgs) {
            String name = Optional.ofNullable(p.getName()).orElse("");
            if (name.isBlank()) continue;

            List<SoapFlightRouteDTO> routes;
            try {
                routes = flightRouteSoapAdapter.getAllFlightRoutesDetailsByPackageName(name).getItem();
                if (routes == null) routes = Collections.emptyList();
            } catch (Exception e) {
                routes = Collections.emptyList();
            }

            List<SoapFlightRouteDTO> confirmed = routes.stream()
                    .filter(r -> r != null && r.getStatus() == EnumEstatusRuta.CONFIRMADA)
                    .sorted(Comparator.comparing(SoapFlightRouteDTO::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();

            pkgRoutes.put(name, confirmed);
        }

// asignar al request
        req.setAttribute("pkgs", pkgs);
        req.setAttribute("pkgRoutes", pkgRoutes);

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private List<BaseFlightRoutePackageDTO> getPackagesPreferWithRoutes() {
        try {
//            List<BaseFlightRoutePackageDTO> list =
//                    pkgCtrl.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes();
            List<BaseFlightRoutePackageDTO> list =
                    flightRoutePackagePort.getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes().getItem();
            return (list != null) ? list : Collections.emptyList();
        } catch (Exception e) {
            log("Fallo getAllFlightRoutesPackagesSimpleDetailsWithFlightRoutes()", e);
            try {
//                List<BaseFlightRoutePackageDTO> list =
//                        pkgCtrl.getAllFlightRoutesPackagesSimpleDetails();
                List<BaseFlightRoutePackageDTO> list =
                        flightRoutePackagePort.getAllFlightRoutesPackagesSimpleDetails().getItem();
                return (list != null) ? list : Collections.emptyList();
            } catch (Exception e2) {
                log("Fallo getAllFlightRoutesPackagesSimpleDetails()", e2);
                return Collections.emptyList();
            }
        }
    }


}
