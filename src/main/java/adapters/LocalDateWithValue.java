package adapters;

import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Subclase que permite pasar una fecha real como string en formato ISO-8601.
 * Se serializa como: <birthDate>yyyy-MM-dd</birthDate>
 */
@XmlType(name = "localDate")

public class LocalDateWithValue extends com.labpa.appweb.user.LocalDate {

    @XmlValue
    private String value;

    public LocalDateWithValue() {}

    public LocalDateWithValue(String value) {
        this.value = value;
    }



    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
