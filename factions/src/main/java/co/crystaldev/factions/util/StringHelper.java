package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class StringHelper {
    public static @NotNull String repeat(@NotNull String string, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    public static @NotNull String pluralize(@NotNull Number num, @NotNull String word) {
        String pluralized = num.intValue() == 1 ? "" : "s";
        String str = word.contains("%s") ? word.replace("%s", pluralized) : word + pluralized;
        if (str.contains("%d")) {
            str = String.format(str, num);
        }
        return str;
    }
}
