package mappers;

import java.time.LocalDateTime;

public class LocalDateTimeMapper {
    public static LocalDateTime fromString(String createdAt) {
        if (createdAt == null) return null;
        return LocalDateTime.parse(createdAt);
    }
}
