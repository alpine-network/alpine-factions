package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.RelationType;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.member.Rank;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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
public final class FactionStore extends AlpineStore<String, Faction> {

    @Getter
    private static FactionStore instance;
    { instance = this; }

    private static final String WILDERNESS_ID = "factions_wilderness";
    private static final String SAFEZONE_ID = "factions_safezone";
    private static final String WARZONE_ID = "factions_warzone";

    private final Set<Faction> registeredFactions = new HashSet<>();

    FactionStore(AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<String, Faction>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "factions"))
                .gson(Reference.GSON)
                .dataType(Faction.class)
                .build());

        // load factions into memory
        try {
            this.registeredFactions.addAll(this.loadAllEntries());
        }
        catch (Throwable ex) {
            Reference.LOGGER.error("Unable to load factions", ex);
            throw new RuntimeException(ex);
        }

        // ensure default factions exist
        this.getWilderness();
        this.getSafeZone();
        this.getWarZone();
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

    @NotNull
    public Faction findFactionOrDefault(@NotNull UUID member) {
        for (Faction faction : this.registeredFactions) {
            if (faction.isMember(member))
                return faction;
        }
        return this.getWilderness();
    }

    @Nullable
    public Faction findFaction(@NotNull OfflinePlayer member) {
        for (Faction faction : this.registeredFactions) {
            if (faction.isMember(member.getUniqueId()))
                return faction;
        }
        return null;
    }

    @NotNull
    public Faction findFactionOrDefault(@NotNull OfflinePlayer member) {
        for (Faction faction : this.registeredFactions) {
            if (faction.isMember(member.getUniqueId()))
                return faction;
        }
        return this.getWilderness();
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
    public Faction getFaction(@NotNull String id) {
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

    @NotNull
    public Faction getWilderness() {
        Faction faction = this.getFaction(WILDERNESS_ID);
        if (faction != null) {
            return faction;
        }

        // create the faction
        faction = new Faction(WILDERNESS_ID, "Wilderness");
        faction.setDescription(Component.text("It's dangerous to go alone."));

        // update the flags
        faction.setFlagValue(FactionFlags.OPEN, true);
        faction.setFlagValue(FactionFlags.MOB_SPAWNING, true);
        faction.setFlagValue(FactionFlags.ANIMAL_SPAWNING, true);
        faction.setFlagValue(FactionFlags.INFINITE_POWER, true);
        faction.setFlagValue(FactionFlags.MINIMAL_VISIBILITY, true);

        // update default perms
        Relational[] relations = {
                RelationType.ALLY, RelationType.TRUCE, RelationType.NEUTRAL, RelationType.ENEMY,
                Rank.LEADER, Rank.COLEADER, Rank.MOD, Rank.MEMBER, Rank.RECRUIT
        };
        faction.setPermissionBulk(Permissions.BUILD, true, relations);
        faction.setPermissionBulk(Permissions.OPEN_DOORS, true, relations);
        faction.setPermissionBulk(Permissions.TRIGGER_PRESSURE_PLATES, true, relations);
        faction.setPermissionBulk(Permissions.USE_SWITCHES, true, relations);
        faction.setPermissionBulk(Permissions.ACCESS_HOME, true, relations);
        faction.setPermissionBulk(Permissions.BANK_DEPOSIT, true);

        // register the faction
        this.registerFaction(faction);
        return faction;
    }

    @NotNull
    public Faction getSafeZone() {
        Faction faction = this.getFaction(SAFEZONE_ID);
        if (faction != null) {
            return faction;
        }

        // create the faction
        faction = new Faction(SAFEZONE_ID, "SafeZone");
        faction.setDescription(Component.text("Free from PvP and monsters."));

        // update the flags
        faction.setFlagValue(FactionFlags.EXPLOSIONS, false);
        faction.setFlagValue(FactionFlags.FIRE_SPREAD, false);
        faction.setFlagValue(FactionFlags.MOB_GRIEFING, false);
        faction.setFlagValue(FactionFlags.COMBAT, false);
        faction.setFlagValue(FactionFlags.MOB_SPAWNING, false);
        faction.setFlagValue(FactionFlags.ANIMAL_SPAWNING, false);
        faction.setFlagValue(FactionFlags.INFINITE_POWER, true);
        faction.setFlagValue(FactionFlags.MINIMAL_VISIBILITY, true);

        // update default perms
        Relational[] relations = {
                Rank.LEADER, Rank.COLEADER, Rank.MOD
        };
        faction.setPermissionBulk(Permissions.BUILD, false, relations);
        faction.setPermissionBulk(Permissions.OPEN_DOORS, false, relations);
        faction.setPermissionBulk(Permissions.TRIGGER_PRESSURE_PLATES, false, relations);
        faction.setPermissionBulk(Permissions.USE_SWITCHES, false, relations);
        faction.setPermissionBulk(Permissions.ACCESS_HOME, false, relations);

        // register the faction
        this.registerFaction(faction);
        return faction;
    }

    @NotNull
    public Faction getWarZone() {
        Faction faction = this.getFaction(WARZONE_ID);
        if (faction != null) {
            return faction;
        }

        // create the faction
        faction = new Faction(WARZONE_ID, "WarZone");
        faction.setDescription(Component.text("Not the safest place to be."));

        // update the flags
        faction.setFlagValue(FactionFlags.EXPLOSIONS, false);
        faction.setFlagValue(FactionFlags.FIRE_SPREAD, false);
        faction.setFlagValue(FactionFlags.MOB_GRIEFING, false);
        faction.setFlagValue(FactionFlags.MOB_SPAWNING, false);
        faction.setFlagValue(FactionFlags.ANIMAL_SPAWNING, false);
        faction.setFlagValue(FactionFlags.INFINITE_POWER, true);
        faction.setFlagValue(FactionFlags.MINIMAL_VISIBILITY, true);

        // update default perms
        Relational[] relations = {
                Rank.LEADER, Rank.COLEADER, Rank.MOD
        };
        faction.setPermissionBulk(Permissions.BUILD, false, relations);
        faction.setPermissionBulk(Permissions.OPEN_DOORS, false, relations);
        faction.setPermissionBulk(Permissions.TRIGGER_PRESSURE_PLATES, false, relations);
        faction.setPermissionBulk(Permissions.USE_SWITCHES, false, relations);
        faction.setPermissionBulk(Permissions.ACCESS_HOME, false, relations);

        // register the faction
        this.registerFaction(faction);
        return faction;
    }
}
