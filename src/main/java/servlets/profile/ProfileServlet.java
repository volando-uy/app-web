package servlets.profile;

import adapters.BookedFlightLinkDTO;
import adapters.PackageLinkDTO;
import com.labpa.appweb.booking.BookingSoapAdapter;
import com.labpa.appweb.booking.BookingSoapAdapterService;
import com.labpa.appweb.booking.SoapBookFlightDTO;
import com.labpa.appweb.buypackage.BuyPackageDTO;
import com.labpa.appweb.buypackage.BuyPackageSoapAdapter;
import com.labpa.appweb.buypackage.BuyPackageSoapAdapterService;
import com.labpa.appweb.constants.ConstantsSoapAdapter;
import com.labpa.appweb.constants.ConstantsSoapAdapterService;
import com.labpa.appweb.flight.FlightSoapAdapter;
import com.labpa.appweb.flight.FlightSoapAdapterService;
import com.labpa.appweb.flight.SoapFlightDTO;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageDTO;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapter;
import com.labpa.appweb.flightroutepackage.FlightRoutePackageSoapAdapterService;
import com.labpa.appweb.user.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/perfil")
public class ProfileServlet extends HttpServlet {

    private final UserSoapAdapter userPort = new UserSoapAdapterService().getUserSoapAdapterPort();
    private final ConstantsSoapAdapter constantsPort = new ConstantsSoapAdapterService().getConstantsSoapAdapterPort();
    private final BuyPackageSoapAdapter buyPackageService = new BuyPackageSoapAdapterService().getBuyPackageSoapAdapterPort();
    private final FlightSoapAdapter flightService = new FlightSoapAdapterService().getFlightSoapAdapterPort();
    private final FlightRoutePackageSoapAdapter flightRoutePackageSoapAdapter = new FlightRoutePackageSoapAdapterService().getFlightRoutePackageSoapAdapterPort();
    private final BookingSoapAdapter bookingSoapAdapter = new BookingSoapAdapterService().getBookingSoapAdapterPort();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        SoapUserDTO usuario = (SoapUserDTO) session.getAttribute("usuario");
        String nickname = usuario.getNickname();

        String tipoCustomer = constantsPort.getValueConstants().getUSERTYPECUSTOMER();
        String tipoAirline = constantsPort.getValueConstants().getUSERTYPEAIRLINE();

        try {
            if (tipoCustomer.equalsIgnoreCase(usuario.getUserType())) {
                SoapCustomerDTO cliente = userPort.getCustomerDetailsByNickname(nickname);
                List<PackageLinkDTO> links = new ArrayList<>();
                List<BookedFlightLinkDTO> bookedFlightLinks = new ArrayList<>();

                // --- Paquetes comprados
                if (cliente.getBoughtPackagesIds() != null) {
                    for (Long packageId : cliente.getBoughtPackagesIds()) {
                        try {
                            BuyPackageDTO boughtPackage = buyPackageService.getBuyPackageDetailsById(packageId);
                            if (boughtPackage == null) continue;

                            String flightRoutePackageName = boughtPackage.getFlightRoutePackageName();
                            FlightRoutePackageDTO flightRoutePackage = flightRoutePackageSoapAdapter.getFlightRoutePackageDetailsByName(flightRoutePackageName);
                            if (flightRoutePackage == null || flightRoutePackage.getFlightRouteNames() == null || flightRoutePackage.getFlightRouteNames().isEmpty())
                                continue;

                            boolean agregado = false;
                            for (String flightRouteName : flightRoutePackage.getFlightRouteNames()) {
                                List<SoapFlightDTO> flights = flightService.getAllFlightsDetailsByRouteName(flightRouteName).getItem();
                                if (flights == null || flights.isEmpty()) continue;

                                SoapFlightDTO flight = flights.get(0);
                                links.add(new PackageLinkDTO(
                                        boughtPackage.getId(),
                                        flightRouteName,
                                        flight.getName(),
                                        flight.getAirlineNickname()
                                ));
                                agregado = true;
                                break;
                            }

                            if (!agregado) {
                                links.add(new PackageLinkDTO(boughtPackage.getId(), "-", "-", "-"));
                            }

                        } catch (Exception ex) {
                            System.err.println("Error al procesar paquete con ID " + packageId + ": " + ex.getMessage());
                        }
                    }
                }

//                // --- Reservas de vuelos
                // --- Reservas de vuelos
                if (cliente.getBookFlightsIds() != null) {
                    for (Long bookingId : cliente.getBookFlightsIds()) {
                        try {
                            SoapBookFlightDTO booked = bookingSoapAdapter.getBookFlightDetailsById(bookingId);
                            if (booked == null) continue;

                            // Hay que descubrir a qué vuelo pertenece esta reserva
                            List<SoapFlightDTO> allFlights = flightService.getAllFlightsDetails().getItem();
                            boolean agregado = false;

                            for (SoapFlightDTO flight : allFlights) {
                                List<SoapBookFlightDTO> bookingsForFlight = bookingSoapAdapter
                                        .getBookFlightsDetailsByFlightName(flight.getName())
                                        .getItem();

                                for (SoapBookFlightDTO b : bookingsForFlight) {
                                    if (b != null && b.getId().equals(bookingId)) {
                                        bookedFlightLinks.add(new BookedFlightLinkDTO(
                                                bookingId,
                                                flight.getFlightRouteName(),
                                                flight.getName(),
                                                flight.getAirlineNickname(),
                                                b.isIsBooked()
                                        ));
                                        agregado = true;
                                        break;
                                    }
                                }

                                if (agregado) break;
                            }

                            if (!agregado) {
                                System.err.println("No se encontró vuelo asociado a la reserva " + bookingId);
                            }

                        } catch (Exception e) {
                            System.err.println("Error al procesar reserva con ID " + bookingId + ": " + e.getMessage());
                        }
                    }
                }

                req.setAttribute("bookedFlightLinks", bookedFlightLinks);
                req.setAttribute("boughtPackageLinks", links);
                actualizarYRedirigir(req, resp, cliente, tipoCustomer);
                return;
            }

            if (tipoAirline.equalsIgnoreCase(usuario.getUserType())) {
                SoapAirlineDTO airline = userPort.getAirlineDetailsByNickname(nickname);
                actualizarYRedirigir(req, resp, airline, tipoAirline);
                return;
            }

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de usuario desconocido");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener información del perfil");
        }
    }

    private void actualizarYRedirigir(HttpServletRequest req, HttpServletResponse resp,
                                      SoapUserDTO userDetail, String tipoUsuario)
            throws ServletException, IOException {

        req.setAttribute("usuario", userDetail);
        req.setAttribute("tipoUsuario", tipoUsuario);
        req.setAttribute("isCustomer", tipoUsuario.equalsIgnoreCase(constantsPort.getValueConstants().getUSERTYPECUSTOMER()));
        req.setAttribute("isAirline", tipoUsuario.equalsIgnoreCase(constantsPort.getValueConstants().getUSERTYPEAIRLINE()));
        req.getSession().setAttribute("usuario", userDetail);

        req.getRequestDispatcher("/src/views/profile/info/profileInformation.jsp").forward(req, resp);
    }
}
