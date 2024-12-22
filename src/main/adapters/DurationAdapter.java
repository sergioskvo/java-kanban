package adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationAdapter implements JsonDeserializer<Duration>, JsonSerializer<Duration> {

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json != null && !json.getAsString().isEmpty()) {
            try {
                return Duration.parse(json.getAsString());
            } catch (Exception e) {
                throw new JsonParseException("Invalid duration format", e);
            }
        }
        return null;  // Если JSON пустой или null, возвращаем null
    }

    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src != null ? src.toString() : null);
    }
}
