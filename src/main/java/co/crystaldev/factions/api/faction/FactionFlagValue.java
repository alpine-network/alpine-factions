package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.Reference;
import co.crystaldev.factions.util.ReflectionHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/19/2023
 */
@AllArgsConstructor @Getter @Setter
public final class FactionFlagValue<T> {
    private final Class<T> type;
    private T value;

    @Override
    public String toString() {
        return "FactionFlagValue(type=" + this.type.getName() + ", value=" + this.value + ")";
    }

    public static final class Adapter extends TypeAdapter<FactionFlagValue<?>> {
        @Override
        public void write(JsonWriter jsonWriter, FactionFlagValue<?> factionFlagValue) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("type").value(factionFlagValue.getType().getName());

            jsonWriter.name("value");
            Reference.GSON.toJson(factionFlagValue.getValue(), factionFlagValue.getType(), jsonWriter);

            jsonWriter.endObject();
        }

        @Override
        public FactionFlagValue<?> read(JsonReader jsonReader) throws IOException {
            Class<?> type = null;
            Object value = null;

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("type".equals(name)) {
                    type = ReflectionHelper.getClass(ReflectionHelper.getContextClassLoader(), jsonReader.nextString());
                }
                else if ("value".equals(name)) {
                    value = Reference.GSON.fromJson(jsonReader, type);
                }
            }
            jsonReader.endObject();

            return (type != null && value != null) ? new FactionFlagValue(type, value) : null;
        }
    }
}
