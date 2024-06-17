package co.crystaldev.factions;

import co.crystaldev.alpinecore.util.UuidTypeAdapter;
import co.crystaldev.factions.api.faction.flag.FlagHolder;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.util.adapter.ComponentTypeAdapter;
import co.crystaldev.factions.util.adapter.LocationTypeAdapter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @since 0.1.0
 */
public final class Reference {
    public static final String ID = "{{ mavenArtifact }}";
    public static final String NAME = "{{ pluginName }}";
    public static final String VERSION = "{{ pluginVersion }}";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<FlagHolder<?>>(){}.getType(), new FlagHolder.Adapter())
            .registerTypeAdapter(Claim.class, new Claim.Adapter())
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(UUID.class, new UuidTypeAdapter())
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .disableHtmlEscaping()
            .create();
}
