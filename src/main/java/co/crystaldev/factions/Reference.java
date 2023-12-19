package co.crystaldev.factions;

import co.crystaldev.alpinecore.util.UuidTypeAdapter;
import co.crystaldev.factions.util.ComponentSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class Reference {
    public static final String ID = "{{ mavenArtifact }}";
    public static final String NAME = "{{ pluginName }}";
    public static final String VERSION = "{{ pluginVersion }}";
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final MiniMessage STRICT_MINIMESSAGE = MiniMessage.builder().strict(true).build();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentSerializer())
            .registerTypeAdapter(UUID.class, new UuidTypeAdapter())
            .disableHtmlEscaping()
            .create();
}
