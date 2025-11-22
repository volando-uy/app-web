package adapters;

import com.labpa.appweb.flight.LocalDateTime;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

//NO TENGO NI PUTA IDEA LO QUE HACE ESTO, PERO FUNCIONA (osea inserta campos de fecha y hora en los SOAP objects)
public class LocalDateTimeAdapter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Convierte java.time.LocalDateTime a com.labpa.appweb.flight.LocalDateTime (SOAP) usando reflection
     */
    public static LocalDateTime fromJavaTime(java.time.LocalDateTime fecha) {
        if (fecha == null) return null;

        try {
            LocalDateTime soapDate = new LocalDateTime();

            // Acceder al campo oculto 'value' generado por JAXB
            Field valueField = soapDate.getClass().getDeclaredField("value");
            valueField.setAccessible(true);

            // Asignar el valor ISO-8601 al campo JAXB oculto
            valueField.set(soapDate, FORMATTER.format(fecha));

            return soapDate;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo construir LocalDateTime SOAP con reflection", e);
        }
    }

    /**
     * Convierte com.labpa.appweb.flight.LocalDateTime (SOAP) a java.time.LocalDateTime
     */
    public static java.time.LocalDateTime toJavaTime(LocalDateTime soapDate) {
        if (soapDate == null) return null;

        try {
            Field valueField = soapDate.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            String value = (String) valueField.get(soapDate);
            if (value == null || value.isBlank()) return null;
            return java.time.LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer valor ISO de LocalDateTime SOAP", e);
        }
    }
    private static Date toDate(com.labpa.appweb.flight.LocalDateTime soapDateTime) {
        return java.sql.Timestamp.valueOf(LocalDateTimeAdapter.toJavaTime(soapDateTime));
    }
}
