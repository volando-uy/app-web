package adapters;

public class BookedFlightLinkDTO {
    private Long bookingId;
    private String routeName;
    private String flightName;
    private String airline;
    private boolean isBooked;

    public BookedFlightLinkDTO() {
    }

    public BookedFlightLinkDTO(Long bookingId, String routeName, String flightName, String airline, boolean isBooked) {
        this.bookingId = bookingId;
        this.routeName = routeName;
        this.flightName = flightName;
        this.airline = airline;
        this.isBooked = isBooked;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getFlightName() {
        return flightName;
    }

    public String getAirline() {
        return airline;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public boolean isBooked() {
        return isBooked;
    }

}
