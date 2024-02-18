package co.crystaldev.factions.api.faction.flag;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/17/2024
 */
@UtilityClass
public final class FlagAdapters {

    private static final DecimalFormat DECIMAL_FORMAT_FP = new DecimalFormat("#,###.##");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    public static final FlagAdapter<String> STRING = argument -> argument.isEmpty() ? null : argument;

    public static final FlagAdapter<Boolean> BOOLEAN = argument ->
            argument.equalsIgnoreCase("true") || argument.equalsIgnoreCase("yes");

    public static final FlagAdapter<Double> DOUBLE = new FlagAdapter<Double>() {
        @Override
        public @NotNull String serialize(@NotNull Double value) {
            return DECIMAL_FORMAT_FP.format(value);
        }

        @Override
        public @Nullable Double deserialize(@NotNull String argument) {
            return NumberUtils.isNumber(argument) ? Double.parseDouble(argument) : null;
        }
    };

    public static final FlagAdapter<Float> FLOAT = new FlagAdapter<Float>() {
        @Override
        public @NotNull String serialize(@NotNull Float value) {
            return DECIMAL_FORMAT_FP.format(value);
        }

        @Override
        public @Nullable Float deserialize(@NotNull String argument) {
            return NumberUtils.isNumber(argument) ? Float.parseFloat(argument) : null;
        }
    };

    public static final FlagAdapter<Integer> INTEGER = new FlagAdapter<Integer>() {
        @Override
        public @NotNull String serialize(@NotNull Integer value) {
            return DECIMAL_FORMAT.format(value);
        }

        @Override
        public @Nullable Integer deserialize(@NotNull String argument) {
            return NumberUtils.isNumber(argument) ? Integer.parseInt(argument) : null;
        }
    };

    public static final FlagAdapter<Long> LONG = new FlagAdapter<Long>() {
        @Override
        public @NotNull String serialize(@NotNull Long value) {
            return DECIMAL_FORMAT.format(value);
        }

        @Override
        public @Nullable Long deserialize(@NotNull String argument) {
            return NumberUtils.isNumber(argument) ? Long.parseLong(argument) : null;
        }
    };

    public static final FlagAdapter<Short> SHORT = new FlagAdapter<Short>() {
        @Override
        public @NotNull String serialize(@NotNull Short value) {
            return DECIMAL_FORMAT.format(value);
        }

        @Override
        public @Nullable Short deserialize(@NotNull String argument) {
            return NumberUtils.isNumber(argument) ? Short.parseShort(argument) : null;
        }
    };

    public static final FlagAdapter<Byte> BYTE = new FlagAdapter<Byte>() {
        @Override
        public @NotNull String serialize(@NotNull Byte value) {
            return DECIMAL_FORMAT.format(value);
        }

        @Override
        public @Nullable Byte deserialize(@NotNull String argument) {
            return NumberUtils.isNumber(argument) ? Byte.parseByte(argument) : null;
        }
    };
}
