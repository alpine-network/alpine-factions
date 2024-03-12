package co.crystaldev.factions.util.adapter;

import co.crystaldev.factions.util.ComponentHelper;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;

import java.io.IOException;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class ComponentTypeAdapter extends TypeAdapter<Component> {

    @Override
    public void write(JsonWriter jsonWriter, Component component) throws IOException {
        if (component == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.value(ComponentHelper.mini(component));
    }

    @Override
    public Component read(JsonReader jsonReader) throws IOException {
        String next = jsonReader.nextString();
        if (next == null) {
            return null;
        }

        return ComponentHelper.mini(next);
    }
}
