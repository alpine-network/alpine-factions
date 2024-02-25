package co.crystaldev.factions.api.faction.flag;

import co.crystaldev.alpinecore.util.ReflectionHelper;
import co.crystaldev.factions.Reference;
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
public final class FlagHolder<T> {

    private final Class<T> type;
    private T value;

    @Override
    public String toString() {
        return "FlagHolder(type=" + this.type.getName() + ", value=" + this.value + ")";
    }

    public static final class Adapter extends TypeAdapter<FlagHolder<?>> {
        @Override
        public void write(JsonWriter jsonWriter, FlagHolder<?> flagHolder) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("type").value(flagHolder.getType().getName());

            jsonWriter.name("value");
            Reference.GSON.toJson(flagHolder.getValue(), flagHolder.getType(), jsonWriter);

            jsonWriter.endObject();
        }

        @Override
        public FlagHolder<?> read(JsonReader jsonReader) throws IOException {
            Class<?> type = null;
            Object value = null;

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("type".equals(name)) {
                    String className = jsonReader.nextString();
                    type = ReflectionHelper.getClass(ReflectionHelper.getContextClassLoader(), className);

                    // attempt to use the system classloader
                    if (type == null) {
                        type = ReflectionHelper.getClass(ClassLoader.getSystemClassLoader(), className);
                    }
                }
                else if ("value".equals(name)) {
                    value = Reference.GSON.fromJson(jsonReader, type);
                }
            }
            jsonReader.endObject();

            return (type != null && value != null) ? new FlagHolder(type, value) : null;
        }
    }
}
