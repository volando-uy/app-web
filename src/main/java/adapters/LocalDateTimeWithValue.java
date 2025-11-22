package adapters;

import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.XmlType;
import com.labpa.appweb.booking.LocalDateTime;

/**
 * Wrapper para serializar java.time.LocalDateTime como texto ISO-8601
 * Ejemplo XML: <createdAt>2025-05-20T14:30:00</createdAt>
 */
@XmlType(name = "localDateTime")
public class LocalDateTimeWithValue extends LocalDateTime {

    @XmlValue
    private String value;

    public LocalDateTimeWithValue() {}

    public LocalDateTimeWithValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
