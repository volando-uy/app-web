package adapters;

import com.labpa.appweb.flightroute.LocalDate;

import java.time.format.DateTimeFormatter;

public class LocalDateAdapter {

    public static LocalDate toSoapLocalDate(java.time.LocalDate javaDate) {
        if (javaDate == null) return null;

        LocalDate soapDate = new LocalDate() {
            @Override
            public String toString() {
                return javaDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
        };
        return soapDate;
    }
}