package config;

import javax.xml.namespace.QName;
import java.net.URL;

import com.labpa.appweb.user.*;
import com.labpa.appweb.auth.*;
import com.labpa.appweb.booking.*;
import com.labpa.appweb.flight.*;
import com.labpa.appweb.flightroute.*;
import com.labpa.appweb.seats.*;
import com.labpa.appweb.flightroutepackage.*;
import com.labpa.appweb.buypackage.*;
import com.labpa.appweb.category.*;
import com.labpa.appweb.city.*;
import com.labpa.appweb.ticket.*;
import com.labpa.appweb.images.*;
import com.labpa.appweb.airport.*;
import com.labpa.appweb.constants.*;
import com.labpa.appweb.countries.*;
import com.labpa.appweb.pdf.*;

public class SoapServiceFactory {

    private static URL fromConfig(String property) {
        try {
            return new URL(ConfigProperties.get(property));
        } catch (Exception e) {
            throw new RuntimeException("URL inválida en application.properties: " + property, e);
        }
    }

    // USER
    public static UserSoapAdapter getUserService() {
        URL endpoint = fromConfig("userService.endpoint");
        QName qname = new QName("http://user.soap.adapters.app/", "UserSoapAdapterService");
        return new UserSoapAdapterService(endpoint, qname).getUserSoapAdapterPort();
    }

    // AUTH
    public static AuthSoapAdapter getAuthService() {
        URL endpoint = fromConfig("authService.endpoint");
        QName qname = new QName("http://auth.soap.adapters.app/", "AuthSoapAdapterService");
        return new AuthSoapAdapterService(endpoint, qname).getAuthSoapAdapterPort();
    }

    // BOOKING
    public static BookingSoapAdapter getBookingService() {
        URL endpoint = fromConfig("bookingService.endpoint");
        QName qname = new QName("http://booking.soap.adapters.app/", "BookingSoapAdapterService");
        return new BookingSoapAdapterService(endpoint, qname).getBookingSoapAdapterPort();
    }

    // FLIGHT
    public static FlightSoapAdapter getFlightService() {
        URL endpoint = fromConfig("flightService.endpoint");
        QName qname = new QName("http://flight.soap.adapters.app/", "FlightSoapAdapterService");
        return new FlightSoapAdapterService(endpoint, qname).getFlightSoapAdapterPort();
    }

    // FLIGHT ROUTE
    public static FlightRouteSoapAdapter getFlightRouteService() {
        URL endpoint = fromConfig("flightRouteService.endpoint");
        QName qname = new QName("http://flightroute.soap.adapters.app/", "FlightRouteSoapAdapterService");
        return new FlightRouteSoapAdapterService(endpoint, qname).getFlightRouteSoapAdapterPort();
    }

    // SEATS
    public static SeatSoapAdapter getSeatService() {
        URL endpoint = fromConfig("seatService.endpoint");
        QName qname = new QName("http://seat.soap.adapters.app/", "SeatSoapAdapterService");
        return new SeatSoapAdapterService(endpoint, qname).getSeatSoapAdapterPort();
    }

    // FLIGHT ROUTE PACKAGE
    public static FlightRoutePackageSoapAdapter getFlightRoutePackageService() {
        URL endpoint = fromConfig("flightRoutePackageService.endpoint");
        QName qname = new QName("http://flightroutepackage.soap.adapters.app/", "FlightRoutePackageSoapAdapterService");
        return new FlightRoutePackageSoapAdapterService(endpoint, qname).getFlightRoutePackageSoapAdapterPort();
    }

    // BUY PACKAGE
    public static BuyPackageSoapAdapter getBuyPackageService() {
        URL endpoint = fromConfig("buyPackageService.endpoint");
        QName qname = new QName("http://buypackage.soap.adapters.app/", "BuyPackageSoapAdapterService");
        return new BuyPackageSoapAdapterService(endpoint, qname).getBuyPackageSoapAdapterPort();
    }

    // CATEGORY
    public static CategorySoapAdapter getCategoryService() {
        URL endpoint = fromConfig("categoryService.endpoint");
        QName qname = new QName("http://category.soap.adapters.app/", "CategorySoapAdapterService");
        return new CategorySoapAdapterService(endpoint, qname).getCategorySoapAdapterPort();
    }

    // CITY
    public static CitySoapAdapter getCityService() {
        URL endpoint = fromConfig("cityService.endpoint");
        QName qname = new QName("http://city.soap.adapters.app/", "CitySoapAdapterService");
        return new CitySoapAdapterService(endpoint, qname).getCitySoapAdapterPort();
    }

    // TICKET
    public static TicketSoapAdapter getTicketService() {
        URL endpoint = fromConfig("ticketService.endpoint");
        QName qname = new QName("http://ticket.soap.adapters.app/", "TicketSoapAdapterService");
        return new TicketSoapAdapterService(endpoint, qname).getTicketSoapAdapterPort();
    }

    // IMAGES
    public static ImagesSoapAdapter getImagesService() {
        URL endpoint = fromConfig("imagesService.endpoint");
        QName qname = new QName("http://images.soap.adapters.app/", "ImagesSoapAdapterService");
        return new ImagesSoapAdapterService(endpoint, qname).getImagesSoapAdapterPort();
    }

    // AIRPORT
    public static AirportSoapAdapter getAirportService() {
        URL endpoint = fromConfig("airportService.endpoint");
        QName qname = new QName("http://airport.soap.adapters.app/", "AirportSoapAdapterService");
        return new AirportSoapAdapterService(endpoint, qname).getAirportSoapAdapterPort();
    }

    // CONSTANTS
    public static ConstantsSoapAdapter getConstantsService() {
        URL endpoint = fromConfig("constantsService.endpoint");
        QName qname = new QName("http://constants.soap.adapters.app/", "ConstantsSoapAdapterService");
        return new ConstantsSoapAdapterService(endpoint, qname).getConstantsSoapAdapterPort();
    }

    // COUNTRIES
    public static SoapCountriesAdapter getCountriesService() {
        URL endpoint = fromConfig("countriesService.endpoint");
        QName qname = new QName("http://countries.soap.adapters.app/", "SoapCountriesAdapterService");
        return new SoapCountriesAdapterService(endpoint, qname).getSoapCountriesAdapterPort();
    }

    // PDF
    public static SoapPDFAdapter getPdfService() {
        URL endpoint = fromConfig("pdfService.endpoint");
        QName qname = new QName("http://pdf.soap.adapters.app/", "SoapPDFAdapterService");
        return new SoapPDFAdapterService(endpoint, qname).getSoapPDFAdapterPort();
    }
}
