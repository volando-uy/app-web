package adapters;


public class FlightViewDTO {
    private String name;
    private String image;
    private String departureTime;
    private String createdAt;
    private String duration;
    private String maxEconomySeats;
    private String maxBusinessSeats;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMaxEconomySeats() {
        return maxEconomySeats;
    }

    public void setMaxEconomySeats(String maxEconomySeats) {
        this.maxEconomySeats = maxEconomySeats;
    }

    public String getMaxBusinessSeats() {
        return maxBusinessSeats;
    }

    public void setMaxBusinessSeats(String maxBusinessSeats) {
        this.maxBusinessSeats = maxBusinessSeats;
    }
}