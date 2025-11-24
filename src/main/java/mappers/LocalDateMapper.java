package mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    public static LocalDate toLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            // Aquí puedes loggear si quieres
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
    }

    public static String toString(LocalDate date) {
        return (date != null) ? FORMATTER.format(date) : null;
    }
}
