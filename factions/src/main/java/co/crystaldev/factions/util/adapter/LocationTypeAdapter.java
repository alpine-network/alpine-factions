/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.util.adapter;

import co.crystaldev.factions.Constants;
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

        JsonObject obj = Constants.GSON.fromJson(jsonReader, JsonObject.class);
        String worldName = obj.has("world") ? obj.get("world").getAsString() : null;
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        float yaw = obj.get("yaw").getAsFloat();
        float pitch = obj.get("pitch").getAsFloat();

        return new Location(worldName == null ? null : Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
