package co.crystaldev.factions.config.type;

import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.Formatting;
import de.exlll.configlib.SerializeWith;
import de.exlll.configlib.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 11/14/2023
 */
@Getter @AllArgsConstructor @NoArgsConstructor
@SerializeWith(serializer = ConfigText.ConfigTextSerializer.class)
public final class ConfigText {

    private List<String> message;

    @NotNull
    public Component build() {
        MessageConfig conf = MessageConfig.getInstance();
        String message = Formatting.formatPlaceholders(String.join("\n", this.message),
                "prefix", conf.prefix.buildWithoutPlaceholders(),
                "error_prefix", conf.errorPrefix.buildWithoutPlaceholders());
        return ComponentHelper.mini(message);
    }

    @NotNull
    public Component build(@NotNull Object... placeholders) {
        MessageConfig conf = MessageConfig.getInstance();
        String message = Formatting.formatPlaceholders(String.join("\n", this.message),
                "prefix", conf.prefix.buildWithoutPlaceholders(),
                "error_prefix", conf.errorPrefix.buildWithoutPlaceholders());
        message = Formatting.formatPlaceholders(message, placeholders);
        return ComponentHelper.mini(message);
    }

    @NotNull
    public String buildString() {
        MessageConfig conf = MessageConfig.getInstance();
        return Formatting.formatPlaceholders(String.join("\n", this.message),
                "prefix", conf.prefix.buildWithoutPlaceholders(),
                "error_prefix", conf.errorPrefix.buildWithoutPlaceholders());
    }

    @NotNull
    public String buildString(@NotNull Object... placeholders) {
        MessageConfig conf = MessageConfig.getInstance();
        String message = Formatting.formatPlaceholders(String.join("\n", this.message),
                "prefix", conf.prefix.buildWithoutPlaceholders(),
                "error_prefix", conf.errorPrefix.buildWithoutPlaceholders());
        return Formatting.formatPlaceholders(message, placeholders);
    }

    @NotNull
    private Component buildWithoutPlaceholders() {
        return ComponentHelper.mini(String.join("\n", this.message));
    }

    public void send(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Messaging.send(sender, this.build(placeholders));
    }

    public void send(@NotNull Collection<CommandSender> senders, @NotNull Object... placeholders) {
        senders.forEach(sender -> this.send(sender, placeholders));
    }

    public void sendActionBar(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Messaging.wrap(sender).sendActionBar(this.build(placeholders));
    }

    @NotNull
    public static ConfigText of(@NotNull String component) {
        return new ConfigText(Collections.singletonList(component));
    }

    @NotNull
    public static ConfigText of(@NotNull String... components) {
        return new ConfigText(Arrays.asList(components));
    }

    @NotNull
    public static ConfigText of(@NotNull Component component) {
        String message = ComponentHelper.mini(component);
        return new ConfigText(Arrays.asList(message.replace("\r", "").split("(\n|<br>)")));
    }

    public static final class ConfigTextSerializer implements Serializer<ConfigText, String> {
        @Override
        public String serialize(ConfigText element) {
            return String.join("\n", element.message);
        }

        @Override
        public ConfigText deserialize(String element) {
            return new ConfigText(Arrays.asList(element.split("(<br>|\n|\r)")));
        }
    }
}
