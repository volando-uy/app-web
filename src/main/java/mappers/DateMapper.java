package mappers;

import adapters.LocalDateWithValue;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.lang.reflect.Field;
public class DateMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // ✅ De LocalDate normal → LocalDateWithValue (SOAP)
    public static LocalDateWithValue toSoapLocalDate(LocalDate date) {
        if (date == null) return null;
        return new LocalDateWithValue(ISO_FORMATTER.format(date));
    }

    // ✅ De LocalDateWithValue (SOAP) → LocalDate normal
    public static LocalDate fromSoapLocalDate(LocalDateWithValue soapDate) {
        if (soapDate == null || soapDate.getValue() == null) return null;
        try {
            return LocalDate.parse(soapDate.getValue(), ISO_FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir SOAP LocalDate a LocalDate", e);
        }
    }
    public static LocalDate toLocalDate(Object obj) {
        if (obj == null) return null;
        try {
            Field valueField = obj.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            String value = (String) valueField.get(obj);
            if (value == null) return null;
            return LocalDate.parse(value, ISO_FORMATTER);
        } catch (NoSuchFieldException | IllegalAccessException | DateTimeParseException e) {
            throw new RuntimeException("No se pudo convertir el objeto a LocalDate", e);
        }
    }
}