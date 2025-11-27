package mappers;


import com.labpa.appweb.booking.*;

public class LuggageMapper {

    public static LuggageDTO toSoapLuggage(BaseBasicLuggageDTO dto) {
        BaseBasicLuggageDTO soap = new BaseBasicLuggageDTO();
        soap.setWeight(dto.getWeight());
        soap.setCategory(dto.getCategory());
        soap.setId(dto.getId()); // Opcional, si querés mantener el ID
        return soap;
    }

    public static LuggageDTO toSoapLuggage(BaseExtraLuggageDTO dto) {
        BaseExtraLuggageDTO soap = new BaseExtraLuggageDTO();
        soap.setWeight(dto.getWeight());
        soap.setCategory(dto.getCategory());
        soap.setId(dto.getId());
        return soap;
    }
}
