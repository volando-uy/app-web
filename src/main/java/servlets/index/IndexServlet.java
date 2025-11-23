package servlets.index;

import adapters.FlightViewDTO;
import com.labpa.appweb.flight.BaseFlightDTO;
import com.labpa.appweb.flight.BaseFlightSoapViewDTO;
import com.labpa.appweb.flight.FlightSoapAdapter;
import com.labpa.appweb.flight.FlightSoapAdapterService;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapter;
import com.labpa.appweb.flightroute.FlightRouteSoapAdapterService;
import com.labpa.appweb.flightroutepackage.BaseFlightRoutePackageDTO;
import com.labpa.appweb.flightroutepackage.FlightRoutePackage;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapter;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mappers.FlightMapper;

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
    private FlightSoapAdapterService flightSoapAdapterService = new FlightSoapAdapterService();
    private FlightSoapAdapter flightPort = flightSoapAdapterService.getFlightSoapAdapterPort();
    private FlightRoutePackageSoapAdapterService flightRouteSoapAdapterService = new FlightRoutePackageSoapAdapterService();
    private FlightRoutePackageSoapAdapter flightRoutePackagePort = flightRouteSoapAdapterService.getFlightRoutePackageSoapAdapterPort();
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

        List<BaseFlightSoapViewDTO> list = flightPort.getAllFlightsSimpleDetails().getItem();

        List<FlightViewDTO> viewList = new ArrayList<>();
        for (BaseFlightSoapViewDTO f : list) {
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
