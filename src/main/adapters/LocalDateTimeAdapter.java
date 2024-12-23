package adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
        if (json != null && !json.getAsString().isEmpty()) {
            try {
                return LocalDateTime.parse(json.getAsString(), formatter);
            } catch (Exception e) {
                throw new JsonParseException("Invalid LocalDateTime format", e);
            }
        }
        return null;
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src != null ? src.format(formatter) : null);
    }
}