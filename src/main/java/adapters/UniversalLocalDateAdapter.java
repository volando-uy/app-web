package adapters;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

/**
 * Mapper universal para tipos SOAP LocalDate generados por wsimport.
 * Usa reflection para insertar el valor ISO-8601.
 */
public class UniversalLocalDateAdapter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static Field getFieldRecursive(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // seguir subiendo la jerarquía
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * Convierte java.time.LocalDate → instancia de cualquier clase LocalDate generada por wsimport
     */
    public static <T> T fromJavaTime(java.time.LocalDate date, Class<T> soapLocalDateClass) {
        if (date == null) return null;
        try {
            T soapDate = soapLocalDateClass.getDeclaredConstructor().newInstance();
            Field valueField = getFieldRecursive(soapLocalDateClass, "value"); // CAMBIO AQUÍ
            valueField.setAccessible(true);
            valueField.set(soapDate, FORMATTER.format(date));
            System.out.println("Clase del LocalDate SOAP: " + soapLocalDateClass.getName());

            return soapDate;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir LocalDate a SOAP LocalDate", e);
        }
    }

    /**
     * Convierte instancia de cualquier LocalDate SOAP → java.time.LocalDate
     */
    public static java.time.LocalDate toJavaTime(Object soapDate) {
        if (soapDate == null) return null;
        try {
            Field valueField = soapDate.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            String value = (String) valueField.get(soapDate);
            if (value == null || value.isBlank()) return null;
            return java.time.LocalDate.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir SOAP LocalDate a LocalDate", e);
        }
    }
}
