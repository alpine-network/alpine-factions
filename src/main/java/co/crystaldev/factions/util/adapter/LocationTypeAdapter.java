package co.crystaldev.factions.util.adapter;

import co.crystaldev.factions.Reference;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

/**
 * @since 0.1.0
 */
public final class LocationTypeAdapter extends TypeAdapter<Location> {
    @Override
    public void write(JsonWriter jsonWriter, Location location) throws IOException {
        if (location == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();

        jsonWriter.name("world");
        if (location.getWorld() == null) {
            jsonWriter.nullValue();
        }
        else {
            jsonWriter.value(location.getWorld().getName());
        }

        jsonWriter.name("x").value(location.getX());
        jsonWriter.name("y").value(location.getY());
        jsonWriter.name("z").value(location.getZ());
        jsonWriter.name("yaw").value(location.getYaw());
        jsonWriter.name("pitch").value(location.getPitch());

        jsonWriter.endObject();
    }

    @Override
    public Location read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            return null;
        }

        JsonObject obj = Reference.GSON.fromJson(jsonReader, JsonObject.class);
        String worldName = obj.has("world") ? obj.get("world").getAsString() : null;
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        // TODO: temporary, as before 0.2.3 all yaw & pitch values are null/don't exist
        // TODO: CHANGE BEFORE MAP 2
//        float yaw = obj.get("yaw").getAsFloat();
//        float pitch = obj.get("pitch").getAsFloat();
        float yaw = obj.has("yaw") && !obj.get("yaw").isJsonNull() ? obj.get("yaw").getAsFloat() : 0.0F;
        float pitch = obj.has("pitch") && !obj.get("pitch").isJsonNull() ? obj.get("pitch").getAsFloat() : 0.0F;

        return new Location(worldName == null ? null : Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
