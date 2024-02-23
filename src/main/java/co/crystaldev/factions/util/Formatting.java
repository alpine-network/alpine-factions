package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.config.MessageConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
@UtilityClass
public final class Formatting {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private static final int TITLE_LENGTH = 54;

    @NotNull
    public static String formatPlaceholders(@Nullable String string, @NotNull Object... placeholders) {
        if (string == null)
            return "";
        if (placeholders.length == 0)
            return string;

        if (placeholders.length == 1) {
            // Replace all placeholders with given value
            string = string.replaceAll("%\\w+%", placeholders[0].toString());
        }
        else {
            for (int i = 0; i < (placeholders.length / 2) * 2; i += 2) {
                String placeholder = (String) placeholders[i];
                Object rawReplacer = placeholders[i + 1];
                String formattedReplacer;

                if (rawReplacer instanceof Float || rawReplacer instanceof Double) formattedReplacer = DECIMAL_FORMAT.format(rawReplacer);
                else if (rawReplacer instanceof Boolean) formattedReplacer = (Boolean) rawReplacer ? "True" : "False";
                else if (rawReplacer instanceof Component) formattedReplacer = Reference.STRICT_MINIMESSAGE.serialize(((Component) rawReplacer).append(Components.reset()));
                else formattedReplacer = rawReplacer.toString();

                string = string.replaceAll("%" + placeholder + "%", formattedReplacer);
            }
        }

        return string;
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
    public static Component appendTitlePadding(@NotNull Component component) {
        MessageConfig config = MessageConfig.getInstance();
        int paddingLength = Math.max(4, (TITLE_LENGTH - ComponentHelper.length(component)) / 2);
        String paddingString = StringHelper.repeat(config.paddingCharacter, paddingLength);
        Component padding = ComponentHelper.stylize(config.paddingStyle, Component.text(paddingString));
        return ComponentHelper.join(padding, component, padding);
    }

    @NotNull
    public static Component title(@NotNull Component component) {
        MessageConfig config = MessageConfig.getInstance();
        component = config.titleFormat.build("content", component);
        return config.titleUsesPadding ? appendTitlePadding(component) : component;
    }

    @NotNull
    public static <T> Component page(@NotNull Component title, @NotNull Collection<T> elements,
                                     @NotNull String command, int currentPage, int elementsPerPage,
                                     @NotNull Function<@Nullable T, Component> toComponentFn) {
        MessageConfig config = MessageConfig.getInstance();
        List<Component> pageElements = new LinkedList<>();
        int totalPages = (int) Math.ceil(elementsPerPage / (double) elementsPerPage);

        // needs to be non-zero based
        int humanPage = currentPage;
        currentPage--;

        // collect page elements
        int index = 0;
        for (T element : elements) {
            if (pageElements.size() >= elementsPerPage) {
                break;
            }

            if (index >= currentPage * elementsPerPage) {
                pageElements.add(toComponentFn.apply(element));
            }

            index++;
        }

        // ensure there is data to display
        if (pageElements.isEmpty()) {
            return config.noPages.build();
        }

        // create the title
        Component previous = currentPage == 0
                ? config.previousDisabled.build()
                : ComponentHelper.events(config.previous.build(), Formatting.formatPlaceholders(command, humanPage - 1));
        Component next = currentPage == totalPages - 1
                ? config.nextDisabled.build()
                : ComponentHelper.events(config.next.build(), Formatting.formatPlaceholders(command, humanPage + 1));

        // build the title
        Component center = config.paginatorTitleFormat.build(
                "content", title,
                "page", humanPage,
                "max_pages", totalPages,
                "previous", previous,
                "next", next
        );

        // build the component
        return ComponentHelper.joinNewLines(
                appendTitlePadding(center),
                ComponentHelper.joinNewLines(pageElements)
        );
    }

    @NotNull
    public static <T> Component page(@NotNull Component title, @NotNull Collection<T> elements,
                                     @NotNull String command, int currentPage, int elementsPerPage) {
        return page(title, elements, command, currentPage, elementsPerPage, Formatting::asComponent);
    }

    @NotNull
    public static Component progress(double progress) {
        progress = Math.max(0.0, Math.min(1.0, progress));

        MessageConfig config = MessageConfig.getInstance();

        int fillLength = (int) (config.progressLength * progress);
        Component progressComponent = ComponentHelper.join(
                ComponentHelper.stylize(config.progressIndicatorStyle, Component.text(StringHelper.repeat(config.progressIndicatorCharacter, fillLength))),
                ComponentHelper.stylize(config.progressRemainingStyle, Component.text(StringHelper.repeat(config.progressRemainingCharacter, config.progressLength - fillLength)))
        );

        return config.progressBarFormat.build("progress", progressComponent);
    }

    @NotNull
    private static Component asComponent(@Nullable Object obj) {
        if (obj == null) {
            return ComponentHelper.nil();
        }

        if (obj instanceof ComponentLike) {
            return ((ComponentLike) obj).asComponent();
        }

        return Component.text(obj.toString());
    }
}
