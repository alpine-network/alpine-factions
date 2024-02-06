package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.faction.Faction;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/17/2023
 */
public final class FactionStore extends AlpineStore<UUID, Faction> {

    @Getter
    private static FactionStore instance;
    { instance = this; }

    private final Set<Faction> registeredFactions = new HashSet<>();

    FactionStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<UUID, Faction>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "factions"))
                .gson(Reference.GSON)
                .dataType(Faction.class)
                .build());
        try {
            this.registeredFactions.addAll(this.loadAllEntries());
        }
        catch (Throwable ex) {
            Reference.LOGGER.error("Unable to load factions", ex);
            throw new RuntimeException(ex);
        }
    }

    @NotNull
    public Collection<Faction> getAllFactions() {
        return this.registeredFactions;
    }

    @Nullable
    public Faction findFaction(@NotNull UUID member) {
        for (Faction faction : this.registeredFactions) {
            if (faction.isMember(member))
                return faction;
        }
        return null;
    }

    @Nullable
    public Faction findFaction(@NotNull OfflinePlayer member) {
        for (Faction faction : this.registeredFactions) {
            if (faction.isMember(member.getUniqueId()))
                return faction;
        }
        return null;
    }

    @Nullable
    public Faction findFaction(@NotNull String name) {
        for (Faction faction : this.registeredFactions) {
            if (name.equalsIgnoreCase(faction.getName())) {
                return faction;
            }
        }
        return null;
    }

    @Nullable
    public Faction getFaction(@NotNull UUID id) {
        for (Faction faction : this.registeredFactions) {
            if (faction.getId().equals(id)) {
                return faction;
            }
        }
        return null;
    }

    public void saveFaction(@NotNull Faction faction) {
        this.put(faction.getId(), faction);
    }

    public void registerFaction(@NotNull Faction faction) {
        this.put(faction.getId(), faction);
        this.registeredFactions.add(faction);
    }

    public void unregisterFaction(@NotNull Faction faction) {
        this.remove(faction.getId());
        this.registeredFactions.remove(faction);
    }

    public void saveFactions() {
        boolean updated = false;
        for (Faction faction : this.registeredFactions) {
            if (faction.isDirty()) {
                this.saveFaction(faction);
                faction.setDirty(false);
                updated = true;
            }
        }

        if (updated) {
            this.flush();
        }
    }
}
