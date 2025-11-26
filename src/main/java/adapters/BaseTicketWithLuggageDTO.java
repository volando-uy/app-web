package adapters;

import com.labpa.appweb.booking.BaseTicketDTO;
import com.labpa.appweb.booking.LuggageDTO;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "baseTicketWithLuggageDTO")
public class BaseTicketWithLuggageDTO extends BaseTicketDTO {

    @XmlElement(name = "luggages")
    private List<LuggageDTO> luggages;

    public List<LuggageDTO> getLuggages() {
        return luggages;
    }

    public void setLuggages(List<LuggageDTO> luggages) {
        this.luggages = luggages;
    }
}
