package co.crystaldev.factions.util;

import com.google.gson.stream.JsonWriter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/19/2023
 */
@UtilityClass
public final class Reflector {
    private static final Field JsonWriter_out = ReflectionHelper.findField(JsonWriter.class, "out");
    private static final Method JsonWriter_writeDeferredName = ReflectionHelper.findMethod(JsonWriter.class, "writeDeferredName", Void.class);
    private static final Method JsonWriter_beforeValue = ReflectionHelper.findMethod(JsonWriter.class, "beforeValue", Void.class);

    @NotNull
    public static JsonWriter jsonValue(@NotNull JsonWriter writer, @NotNull String json) {
        try {
            Writer out = (Writer) JsonWriter_out.get(writer);
            JsonWriter_writeDeferredName.invoke(writer);
            JsonWriter_beforeValue.invoke(writer);
            out.append(json);
        }
        catch (Exception ignored) {
            // NO OP
        }
        return writer;
    }
}
