package co.crystaldev.factions.config.type;

import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.SerializeWith;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 11/14/2023
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

    @NotNull
    public Component build(@NotNull Object... placeholders) {
        return super.build(AlpineFactions.getInstance(), placeholders);
    }

    @NotNull
    public String buildString(@NotNull Object... placeholders) {
        return super.buildString(AlpineFactions.getInstance(), placeholders);
    }

    public void send(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Messaging.send(sender, this.build(placeholders));
    }

    public void send(@NotNull Collection<CommandSender> senders, @NotNull Object... placeholders) {
        senders.forEach(sender -> this.send(sender, placeholders));
    }

    public void sendActionBar(@NotNull CommandSender sender, @NotNull Object... placeholders) {
        Messaging.actionBar(sender, this.build(placeholders));
    }

    @NotNull
    public static ConfigText of(@NotNull String component) {
        return new ConfigText(Collections.singletonList(component));
    }

    @NotNull
    public static ConfigText of(@NotNull String... component) {
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
