package mappers;

import adapters.LuggageDTOImpl;
import com.labpa.appweb.booking.LuggageDTO;
import com.labpa.appweb.ticket.BaseBasicLuggageDTO;
import com.labpa.appweb.ticket.BaseExtraLuggageDTO;

public class LuggageMapper {

    public static LuggageDTO toSoapLuggage(BaseBasicLuggageDTO dto) {
        LuggageDTOImpl soap = new LuggageDTOImpl();
        soap.setWeight(dto.getWeight());
        return soap;
    }

    public static LuggageDTO toSoapLuggage(BaseExtraLuggageDTO dto) {
        LuggageDTOImpl soap = new LuggageDTOImpl();
        soap.setWeight(dto.getWeight());
        return soap;
    }
}
