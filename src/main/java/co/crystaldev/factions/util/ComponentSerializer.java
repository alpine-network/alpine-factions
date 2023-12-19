package co.crystaldev.factions.util;

import co.crystaldev.factions.Reference;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;

import java.io.IOException;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class ComponentSerializer extends TypeAdapter<Component> {

    @Override
    public void write(JsonWriter jsonWriter, Component component) throws IOException {
        jsonWriter.value(Reference.MINI_MESSAGE.serialize(component));
    }

    @Override
    public Component read(JsonReader jsonReader) throws IOException {
        return Reference.MINI_MESSAGE.deserialize(jsonReader.nextString());
    }
}
