/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.config.type;

import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.util.ComponentRateLimiter;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
@NoArgsConstructor
@Configuration @SerializeWith(serializer = ConfigText.Serializer.class)
public final class ConfigText extends ConfigMessage {

    private ConfigText(@NotNull List<String> message) {
        super(message);
    }

    private ConfigText(@NotNull String message) {
        super(message);
    }

    public @NotNull Component build(@NotNull Object... placeholders) {
        return super.build(AlpineFactions.getInstance(), placeholders);
    }

    public @NotNull String buildString(@NotNull Object... placeholders) {
        return super.buildString(AlpineFactions.getInstance(), placeholders);
    }

    public void send(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Messaging.send(sender, this.build(placeholders));
    }

    public void send(@NotNull Collection<CommandSender> senders, @NotNull Object... placeholders) {
        senders.forEach(sender -> this.send(sender, placeholders));
    }

    public void rateLimitedSend(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Component component = this.build(placeholders);
        if (sender instanceof Player && ComponentRateLimiter.getInstance().isLimited((Player) sender, component)) {
            return;
        }
        Messaging.send(sender, component);
    }

    public void rateLimitedSend(@NotNull Collection<CommandSender> senders, @NotNull Object... placeholders) {
        senders.forEach(sender -> this.rateLimitedSend(sender, placeholders));
    }

    public void sendActionBar(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Messaging.actionBar(sender, this.build(placeholders));
    }

    public static @NotNull ConfigText of(@NotNull String component) {
        return new ConfigText(Collections.singletonList(component));
    }

    public static @NotNull ConfigText of(@NotNull String... component) {
        return new ConfigText(Arrays.asList(component));
    }

    public static class Serializer implements de.exlll.configlib.Serializer<ConfigText, Object> {
        @Override
        public Object serialize(ConfigText element) {
            return String.join("\n", element.message);
        }

        @Override
        public ConfigText deserialize(Object element) {
            if (element instanceof Map) {
                Object value = ((Map) element).get("message");
                List<String> message = value instanceof Collection<?>
                        ? new LinkedList<>((Collection<String>) value)
                        : Collections.singletonList(value.toString());
                return new ConfigText(message);
            }
            else if (element instanceof List) {
                return new ConfigText(((List<?>) element).stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));
            }
            else if (element instanceof String) {
                return new ConfigText((String) element);
            }
            else {
                return new ConfigText(element.toString());
            }
        }
    }
}
