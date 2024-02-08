package co.crystaldev.factions;

import co.crystaldev.alpinecore.util.UuidTypeAdapter;
import co.crystaldev.factions.api.faction.flag.FlagHolder;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.util.ComponentTypeAdapter;
import co.crystaldev.factions.util.tags.ColorwayTagResolver;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class Reference {
    public static final String ID = "{{ mavenArtifact }}";
    public static final String NAME = "{{ pluginName }}";
    public static final String VERSION = "{{ pluginVersion }}";
    public static final MiniMessage MINI_MESSAGE = miniMessage().build();
    public static final MiniMessage STRICT_MINIMESSAGE = miniMessage().strict(true).build();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<FlagHolder<?>>(){}.getType(), new FlagHolder.Adapter())
            .registerTypeAdapter(Claim.class, new Claim.Adapter())
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(UUID.class, new UuidTypeAdapter())
            .disableHtmlEscaping()
            .create();


    @NotNull
    private static MiniMessage.Builder miniMessage() {
        return MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), new ColorwayTagResolver()));
    }
}
