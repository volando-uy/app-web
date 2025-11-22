package mappers;

import adapters.LocalDateTimeWithValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeMapper {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTimeWithValue toSoapLocalDateTime(LocalDateTime date) {
        if (date == null) return null;
        return new LocalDateTimeWithValue(ISO_FORMATTER.format(date));
    }

    public static LocalDateTime fromSoapLocalDateTime(LocalDateTimeWithValue soapDate) {
        if (soapDate == null || soapDate.getValue() == null) return null;
        return LocalDateTime.parse(soapDate.getValue(), ISO_FORMATTER);
    }


}
