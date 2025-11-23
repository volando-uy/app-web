package mappers;

import com.labpa.appweb.flight.BaseFlightDTO;
import adapters.FlightViewDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class FlightMapper {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static FlightViewDTO toViewDTO(BaseFlightDTO flight) {
        FlightViewDTO view = new FlightViewDTO();
        view.setName(flight.getName());
        view.setImage(flight.getImage());

        view.setDuration(
                (flight.getDuration() != null) ? String.valueOf(flight.getDuration()) : "--"
        );

        view.setMaxEconomySeats(
                (flight.getMaxEconomySeats() != null) ? String.valueOf(flight.getMaxEconomySeats()) : "--"
        );

        view.setMaxBusinessSeats(
                (flight.getMaxBusinessSeats() != null) ? String.valueOf(flight.getMaxBusinessSeats()) : "--"
        );

        // 🔥 Aquí hacés el parseo defensivo
        try {
            Object depObj = flight.getDepartureTime();
            if (depObj instanceof String) {
                LocalDateTime ldt = LocalDateTime.parse((String) depObj);
                view.setDepartureTime(fmt.format(ldt));
            }
        } catch (Exception ignored) {}

        try {
            Object createdObj = flight.getCreatedAt();
            if (createdObj instanceof String) {
                LocalDateTime ldt = LocalDateTime.parse((String) createdObj);
                view.setCreatedAt(fmt.format(ldt));
            }
        } catch (Exception ignored) {}

        return view;
    }

    public static List<FlightViewDTO> toViewDTOList(List<BaseFlightDTO> flightList) {
        List<FlightViewDTO> result = new ArrayList<>();
        for (BaseFlightDTO f : flightList) {
            result.add(toViewDTO(f));
        }
        return result;
    }
}
