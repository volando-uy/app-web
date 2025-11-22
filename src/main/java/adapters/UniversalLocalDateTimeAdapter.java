package adapters;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador universal para convertir cualquier clase SOAP LocalDateTime generada por wsimport
 * (como com.labpa.appweb.flight.LocalDateTime, com.labpa.appweb.booking.LocalDateTime, etc.)
 * hacia/desde java.time.LocalDateTime
 */
public class UniversalLocalDateTimeAdapter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Convierte cualquier instancia SOAP LocalDateTime a java.time.LocalDateTime
     */
    public static java.time.LocalDateTime toJavaTime(Object soapDateTime) {
        if (soapDateTime == null) return null;

        try {
            Field valueField = soapDateTime.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            String value = (String) valueField.get(soapDateTime);
            if (value == null || value.isBlank()) return null;
            return java.time.LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir a java.time.LocalDateTime", e);
        }
    }

    /**
     * Convierte java.time.LocalDateTime a una instancia de la clase SOAP LocalDateTime esperada
     * @param javaTime fecha y hora
     * @param targetClass clase SOAP LocalDateTime (por ejemplo: com.labpa.appweb.flight.LocalDateTime.class)
     */
    public static <T> T fromJavaTime(java.time.LocalDateTime javaTime, Class<T> targetClass) {
        if (javaTime == null) return null;

        try {
            T soapDate = targetClass.getDeclaredConstructor().newInstance();
            Field valueField = targetClass.getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(soapDate, FORMATTER.format(javaTime));
            return soapDate;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear instancia de " + targetClass.getName(), e);
        }
    }
}
