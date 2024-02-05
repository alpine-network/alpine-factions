package co.crystaldev.factions.config.type;

import co.crystaldev.factions.Reference;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.Messaging;
import de.exlll.configlib.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
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
@Configuration
public final class ConfigText {

    public List<String> message;

    @NotNull
    public Component build() {
        MessageConfig conf = MessageConfig.getInstance();
        String message = Formatting.formatPlaceholders(String.join("\n", this.message),
                "prefix", conf.prefix.buildWithoutPlaceholders(),
                "error_prefix", conf.errorPrefix.buildWithoutPlaceholders());
        return Reference.MINI_MESSAGE.deserialize(message);
    }

    @NotNull
    public Component build(@NotNull Object... placeholders) {
        MessageConfig conf = MessageConfig.getInstance();
        String message = Formatting.formatPlaceholders(String.join("\n", this.message),
                "prefix", conf.prefix.buildWithoutPlaceholders(),
                "error_prefix", conf.errorPrefix.buildWithoutPlaceholders());
        message = Formatting.formatPlaceholders(message, placeholders);
        return Reference.MINI_MESSAGE.deserialize(message);
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
        return Reference.MINI_MESSAGE.deserialize(String.join("\n", this.message));
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
    public static ConfigText of(@NotNull Component component) {
        return builder().message(component).build();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String message;

        @NotNull
        public Builder message(@NotNull String message) {
            this.message = message;
            return this;
        }

        @NotNull
        public Builder message(@NotNull Component component) {
            this.message = Reference.MINI_MESSAGE.serialize(component.compact());
            return this;
        }

        @NotNull
        public Builder message(@NotNull Component... components) {
            this.message = Reference.MINI_MESSAGE.serialize(Component.join(JoinConfiguration.noSeparators(), components).compact());
            return this;
        }

        @NotNull
        public Builder lines(@NotNull Component... components) {
            this.message = Reference.MINI_MESSAGE.serialize(Component.join(JoinConfiguration.newlines(), components).compact());
            return this;
        }

        @NotNull
        public ConfigText build() {
            return new ConfigText(Arrays.asList(this.message.replace("\r", "").split("(\n|<br>)")));
        }
    }
}
