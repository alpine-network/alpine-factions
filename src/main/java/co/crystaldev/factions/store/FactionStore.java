package co.crystaldev.factions.store;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.AlpineStore;
import co.crystaldev.alpinecore.framework.storage.driver.FlatfileDriver;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.faction.member.Rank;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @since 0.1.0
 */
public final class FactionStore extends AlpineStore<String, Faction> implements FactionAccessor {

    private final Map<String, Faction> registeredFactions = new ConcurrentHashMap<>();

    FactionStore(@NotNull AlpinePlugin plugin) {
        super(plugin, FlatfileDriver.<String, Faction>builder()
                .directory(new File(AlpineFactions.getInstance().getDataFolder(), "factions"))
                .gson(Reference.GSON)
                .dataType(Faction.class)
                .build(plugin));

        // load factions into memory
        try {
            Collection<Faction> factions = this.loadAllEntries();
            for (Faction faction : factions) {
                this.registeredFactions.put(faction.getId(), faction);
            }
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

    public void saveFactions() {
        boolean updated = false;
        for (Faction faction : this.registeredFactions.values()) {
            if (faction.isDirty()) {
                this.save(faction);
                faction.setDirty(false);
                updated = true;
            }
        }

        if (updated) {
            this.flush();
        }
    }

    @Override
    public void register(@NotNull Faction faction) {
        Reference.LOGGER.info("Registering faction (name={}, id={})", faction.getName(), faction.getId());

        this.registeredFactions.put(faction.getId(), faction);
        this.put(faction.getId(), faction);
        this.flush(faction.getId());
    }

    @Override
    public void unregister(@NotNull Faction faction) {
        Reference.LOGGER.info("Unregistering faction (name={}, id={})", faction.getName(), faction.getId());

        this.registeredFactions.remove(faction.getId());
        this.remove(faction.getId());
        this.flush(faction.getId());
    }

    @Override
    public void save(@NotNull Faction faction) {
        this.put(faction.getId(), faction);
    }

    @Override
    public @NotNull Collection<Faction> get() {
        return this.registeredFactions.values();
    }

    @Override
    public @NotNull Faction getWilderness() {
        Faction faction = this.getById(Faction.WILDERNESS_ID);
        if (faction != null) {
            return faction;
        }

        // create the faction
        faction = new Faction(Faction.WILDERNESS_ID, "Wilderness");
        faction.setDescription(Component.text("It's dangerous to go alone."));

        // update the flags
        faction.setFlagValue(FactionFlags.PERMANENT, true);
        faction.setFlagValue(FactionFlags.OPEN, true);
        faction.setFlagValue(FactionFlags.MOB_SPAWNING, true);
        faction.setFlagValue(FactionFlags.ANIMAL_SPAWNING, true);
        faction.setFlagValue(FactionFlags.INFINITE_POWER, true);
        faction.setFlagValue(FactionFlags.MINIMAL_VISIBILITY, true);

        // update default perms
        Relational[] relations = {
                FactionRelation.ALLY, FactionRelation.TRUCE, FactionRelation.NEUTRAL, FactionRelation.ENEMY,
                Rank.LEADER, Rank.COLEADER, Rank.OFFICER, Rank.MEMBER, Rank.RECRUIT
        };
        faction.setPermissionBulk(Permissions.BUILD, true, relations);
        faction.setPermissionBulk(Permissions.OPEN_DOORS, true, relations);
        faction.setPermissionBulk(Permissions.USE_PRESSURE_PLATES, true, relations);
        faction.setPermissionBulk(Permissions.USE_SWITCHES, true, relations);
        faction.setPermissionBulk(Permissions.ACCESS_HOME, true, relations);
        faction.setPermissionBulk(Permissions.BANK_DEPOSIT, true);

        // register the faction
        this.register(faction);
        return faction;
    }

    @Override
    public @NotNull Faction getSafeZone() {
        Faction faction = this.getById(Faction.SAFEZONE_ID);
        if (faction != null) {
            return faction;
        }

        // create the faction
        faction = new Faction(Faction.SAFEZONE_ID, "SafeZone");
        faction.setDescription(Component.text("Free from PvP and monsters."));

        // update the flags
        faction.setFlagValue(FactionFlags.PERMANENT, true);
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
                Rank.LEADER, Rank.COLEADER, Rank.OFFICER
        };
        faction.setPermissionBulk(Permissions.BUILD, false, relations);
        faction.setPermissionBulk(Permissions.OPEN_DOORS, false, relations);
        faction.setPermissionBulk(Permissions.USE_PRESSURE_PLATES, false, relations);
        faction.setPermissionBulk(Permissions.USE_SWITCHES, false, relations);
        faction.setPermissionBulk(Permissions.ACCESS_HOME, false, relations);

        // register the faction
        this.register(faction);
        return faction;
    }

    @Override
    public @Nullable Faction getById(@NotNull String id) {
        return this.registeredFactions.get(id);
    }

    @Override
    public @Nullable Faction find(@NotNull Predicate<Faction> factionPredicate) {
        for (Faction value : this.registeredFactions.values()) {
            if (factionPredicate.test(value)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public @NotNull Faction getWarZone() {
        Faction faction = this.getById(Faction.WARZONE_ID);
        if (faction != null) {
            return faction;
        }

        // create the faction
        faction = new Faction(Faction.WARZONE_ID, "WarZone");
        faction.setDescription(Component.text("Not the safest place to be."));

        // update the flags
        faction.setFlagValue(FactionFlags.PERMANENT, true);
        faction.setFlagValue(FactionFlags.EXPLOSIONS, false);
        faction.setFlagValue(FactionFlags.FIRE_SPREAD, false);
        faction.setFlagValue(FactionFlags.MOB_GRIEFING, false);
        faction.setFlagValue(FactionFlags.MOB_SPAWNING, false);
        faction.setFlagValue(FactionFlags.ANIMAL_SPAWNING, false);
        faction.setFlagValue(FactionFlags.INFINITE_POWER, true);
        faction.setFlagValue(FactionFlags.MINIMAL_VISIBILITY, true);

        // update default perms
        Relational[] relations = {
                Rank.LEADER, Rank.COLEADER, Rank.OFFICER
        };
        faction.setPermissionBulk(Permissions.BUILD, false, relations);
        faction.setPermissionBulk(Permissions.OPEN_DOORS, false, relations);
        faction.setPermissionBulk(Permissions.USE_PRESSURE_PLATES, false, relations);
        faction.setPermissionBulk(Permissions.USE_SWITCHES, false, relations);
        faction.setPermissionBulk(Permissions.ACCESS_HOME, false, relations);

        // register the faction
        this.register(faction);
        return faction;
    }
}
