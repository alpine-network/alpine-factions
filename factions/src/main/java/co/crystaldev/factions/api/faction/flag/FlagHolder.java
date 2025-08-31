/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.api.faction.flag;

import co.crystaldev.alpinecore.util.ReflectionHelper;
import co.crystaldev.factions.Constants;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @since 0.1.0
 */
public final class FlagHolder<T> {

    private final @NotNull Class<T> type;
    private @NotNull T value;

    public FlagHolder(@NotNull Class<T> type, @NotNull T value) {
        this.type = type;
        this.value = value;
    }

    public @NotNull Class<T> getType() {
        return this.type;
    }

    public @NotNull T getValue() {
        return this.value;
    }

    public void setValue(@NotNull T value) {
        this.value = value;
    }

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
            Constants.GSON.toJson(flagHolder.getValue(), flagHolder.getType(), jsonWriter);

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
                } else if ("value".equals(name)) {
                    value = Constants.GSON.fromJson(jsonReader, type);
                }
            }
            jsonReader.endObject();

            return (type != null && value != null) ? new FlagHolder(type, value) : null;
        }
    }
}
