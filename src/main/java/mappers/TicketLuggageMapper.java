package mappers;

import com.labpa.appweb.booking.BaseTicketDTO;
import com.labpa.appweb.booking.LuggageDTO;
import com.labpa.appweb.booking.TicketLuggageArray;
import com.labpa.appweb.booking.TicketWithLuggage;

import java.util.List;
import java.util.Map;

public class TicketLuggageMapper {

    public static TicketLuggageArray toSoapArray(Map<BaseTicketDTO, List<LuggageDTO>> ticketMap) {
        TicketLuggageArray array = new TicketLuggageArray();

        for (Map.Entry<BaseTicketDTO, List<LuggageDTO>> entry : ticketMap.entrySet()) {
            TicketWithLuggage twl = new TicketWithLuggage();
            twl.setTicket(entry.getKey());
            twl.getBasicLuggageOrExtraLuggage().addAll(entry.getValue());
            array.getItems().add(twl);
        }

        return array;

    }
}