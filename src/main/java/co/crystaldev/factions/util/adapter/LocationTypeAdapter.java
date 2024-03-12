package co.crystaldev.factions.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/11/2024
 */
public final class LocationTypeAdapter extends TypeAdapter<Location> {
    @Override
    public void write(JsonWriter jsonWriter, Location location) throws IOException {
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

        jsonWriter.endObject();
    }

    @Override
    public Location read(JsonReader jsonReader) throws IOException {
        String worldName = null;
        double x = 0.0, y = 0.0, z = 0.0;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "name": {
                    worldName = jsonReader.nextString();
                    break;
                }
                case "x": {
                    x = jsonReader.nextDouble();
                    break;
                }
                case "y": {
                    y = jsonReader.nextDouble();
                    break;
                }
                case "z": {
                    z = jsonReader.nextDouble();
                    break;
                }
            }
        }
        jsonReader.endObject();

        return new Location(worldName == null ? null : Bukkit.getWorld(worldName), x, y, z);
    }
}
