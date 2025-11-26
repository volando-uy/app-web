package adapters;

public class PackageLinkDTO {
    private Long id;
    private String routeName;
    private String flightName;
    private String airline;

    public PackageLinkDTO() {}
    public PackageLinkDTO(Long id, String routeName, String flightName, String airline) {
        this.id = id;
        this.routeName = routeName;
        this.flightName = flightName;
        this.airline = airline;
    }

    public Long getId() { return id; }
    public String getRouteName() { return routeName; }
    public String getFlightName() { return flightName; }
    public String getAirline() { return airline; }

    public void setId(Long id) {
        this.id = id;
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
}
