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
    @NotNull
    public static String placeholders(@Nullable String string, @NotNull Object... placeholders) {
        return co.crystaldev.alpinecore.util.Formatting.placeholders(AlpineFactions.getInstance(), string, placeholders);
    }

    @NotNull
    public static String formatMillis(long millis) {
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

    @NotNull
    private static String getDayOfMonthSuffix(int dayOfMonth) {
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

    @NotNull
    public static Component applyTitlePadding(@NotNull Component component) {
        return co.crystaldev.alpinecore.util.Formatting.applyTitlePadding(AlpineFactions.getInstance(), component);
    }

    @NotNull
    public static Component title(@NotNull Component component) {
        return co.crystaldev.alpinecore.util.Formatting.title(AlpineFactions.getInstance(), component);
    }

    @NotNull
    public static <T> Component elements(@NotNull Collection<T> elements, @NotNull Function<@NotNull T, Component> toComponentFn) {
        return co.crystaldev.alpinecore.util.Formatting.elements(AlpineFactions.getInstance(), elements, toComponentFn);
    }

    @NotNull
    public static <T> Component page(@NotNull Component title, @NotNull Collection<T> elements,
                                     @NotNull String command, int currentPage, int elementsPerPage,
                                     @NotNull Function<@NotNull T, Component> toComponentFn) {
        return co.crystaldev.alpinecore.util.Formatting.page(AlpineFactions.getInstance(), title, elements, command, currentPage, elementsPerPage, toComponentFn);
    }

    @NotNull
    public static Component progress(double progress) {
        return co.crystaldev.alpinecore.util.Formatting.progress(AlpineFactions.getInstance(), progress);
    }
}
