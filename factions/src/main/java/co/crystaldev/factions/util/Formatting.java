/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.util;

import co.crystaldev.factions.AlpineFactions;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class Formatting {

    public static @NotNull String placeholders(@Nullable String string, @NotNull Object... placeholders) {
        return co.crystaldev.alpinecore.util.Formatting.placeholders(AlpineFactions.getInstance(), string, placeholders);
    }

    public static @NotNull String formatMillis(long millis) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(date, now);
        long hours = ChronoUnit.HOURS.between(date, now);
        long minutes = ChronoUnit.MINUTES.between(date, now);
        long seconds = ChronoUnit.SECONDS.between(date, now);

        if (seconds < 60)
            return "A few seconds ago";
        else if (minutes < 60)
            return "A few minutes ago";
        else if (hours < 24)
            return StringHelper.pluralize(hours, "%d hour%s ago");
        else if (days <= 3)
            return StringHelper.pluralize(days, "%d day%s ago");

        return DateTimeFormatter.ofPattern(String.format("MMMM d'%s', yyyy", getDayOfMonthSuffix(date.getDayOfMonth()))).format(date);
    }

    private static @NotNull String getDayOfMonthSuffix(int dayOfMonth) {
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return "th";
        }

        switch (dayOfMonth % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static @NotNull Component applyTitlePadding(@NotNull Component component) {
        return co.crystaldev.alpinecore.util.Formatting.applyTitlePadding(AlpineFactions.getInstance(), component);
    }

    public static @NotNull Component title(@NotNull Component component) {
        return co.crystaldev.alpinecore.util.Formatting.title(AlpineFactions.getInstance(), component);
    }

    public static <T> @NotNull Component elements(@NotNull Collection<T> elements, @NotNull Function<@NotNull T, Component> toComponentFn) {
        return co.crystaldev.alpinecore.util.Formatting.elements(AlpineFactions.getInstance(), elements, toComponentFn);
    }

    public static <T> @NotNull Component page(@NotNull Component title, @NotNull Collection<T> elements,
                                     @NotNull String command, int currentPage, int elementsPerPage,
                                     @NotNull Function<@NotNull T, Component> toComponentFn) {
        return co.crystaldev.alpinecore.util.Formatting.page(AlpineFactions.getInstance(), title, elements, command, currentPage, elementsPerPage, toComponentFn);
    }

    public static @NotNull Component progress(double progress) {
        return co.crystaldev.alpinecore.util.Formatting.progress(AlpineFactions.getInstance(), progress);
    }
}
