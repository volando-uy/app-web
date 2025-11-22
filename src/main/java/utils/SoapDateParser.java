package utils;

import java.lang.reflect.Field;

public class SoapDateParser {

    public static String toIsoString(Object soapLocalDate) {
        try {
            Field f = soapLocalDate.getClass().getDeclaredField("value");
            f.setAccessible(true);
            Object v = f.get(soapLocalDate);
            return v != null ? v.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
