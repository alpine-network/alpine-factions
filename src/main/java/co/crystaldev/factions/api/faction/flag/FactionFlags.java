package co.crystaldev.factions.api.faction.flag;

import co.crystaldev.factions.PermissionNodes;
import lombok.experimental.UtilityClass;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class FactionFlags {

    public static final FactionFlag<Boolean> OPEN =
            FactionFlag.builder("open", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Open")
                    .description("Determines if the faction can be joined without an invite.")
                    .stateDescription(
                            "No invite required to join.",
                            "An invite is required to join."
                    )
                    .defaultState(false)
                    .build();

    public static final FactionFlag<Boolean> EXPLOSIONS =
            FactionFlag.builder("explosions", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Explosions")
                    .description("Controls whether explosions are allowed in the faction's territory.")
                    .stateDescription(
                            "Explosions can occur in this territory.",
                            "No explosions can occur in this territory."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(true)
                    .build();

    public static final FactionFlag<Boolean> FIRE_SPREAD =
            FactionFlag.builder("fire_spread", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Fire Spread")
                    .description("Controls whether fire spreads within the faction's territory.")
                    .stateDescription(
                            "Fire can spread in this territory.",
                            "Fire cannot spread in this territory."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(true)
                    .build();

    public static final FactionFlag<Boolean> MOB_GRIEFING =
            FactionFlag.builder("mob_griefing", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Mob Griefing")
                    .description("Determines if mobs can cause damage in the faction's territory.")
                    .stateDescription(
                            "Mobs can cause damage to territory.",
                            "Mobs cannot cause damage to territory."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(true)
                    .build();

    public static final FactionFlag<Boolean> COMBAT =
            FactionFlag.builder("combat", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Combat")
                    .description("Determines if players can engage in PvP within the faction's territory.")
                    .stateDescription(
                            "Players can engage in combat.",
                            "Combat between players is not allowed."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(true)
                    .build();

    public static final FactionFlag<Boolean> MOB_SPAWNING =
            FactionFlag.builder("mob_spawning", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Mob Spawning")
                    .description("Controls the spawning of monsters within the faction's territory.")
                    .stateDescription(
                            "Monsters are allowed to spawn in this territory.",
                            "Monsters are not allowed to spawn in this territory."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(false)
                    .build();

    public static final FactionFlag<Boolean> ANIMAL_SPAWNING =
            FactionFlag.builder("animal_spawning", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Animal Spawning")
                    .description("Controls the spawning of animals within the faction's territory.")
                    .stateDescription(
                            "Animals are allowed to spawn in this territory.",
                            "Animals are not allowed to spawn in this territory."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(false)
                    .build();

    public static final FactionFlag<Boolean> POWER_LOSS =
            FactionFlag.builder("power_loss", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Power Loss")
                    .description("Determines whether faction members lose power upon death.")
                    .stateDescription(
                            "Members lose power upon death.",
                            "Members do not lose power upon death."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(true)
                    .build();

    public static final FactionFlag<Boolean> POWER_GAIN =
            FactionFlag.builder("power_gain", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Power Gain")
                    .description("Determines whether faction members gain power in the faction's territory.")
                    .stateDescription(
                            "Members can gain power in this territory.",
                            "Members cannot gain power in this territory."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(true)
                    .build();

    public static final FactionFlag<Boolean> INFINITE_POWER =
            FactionFlag.builder("inf_power", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Infinite Power")
                    .description("Determines if the faction possesses infinite power.")
                    .stateDescription(
                            "The faction has an unlimited power supply.",
                            "The faction's power operates as usual."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(false)
                    .build();

    public static final FactionFlag<Long> POWER_MODIFIER =
            FactionFlag.builder("power_mod", Long.class, FlagAdapters.LONG)
                    .name("Power Modifier")
                    .description("Defines the power modifier.")
                    .stateDescription(
                            "Faction power level is modified by %value% points."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(0L)
                    .build();

    public static final FactionFlag<Integer> MEMBER_LIMIT_MODIFIER =
            FactionFlag.builder("member_limit_mod", Integer.class, FlagAdapters.INTEGER)
                    .name("Member Limit Modifier")
                    .description("Defines the member limit modifier.")
                    .stateDescription(
                            "Faction member limit is modified by %value% members."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(0)
                    .build();

    public static final FactionFlag<Integer> ROSTER_LIMIT_MODIFIER =
            FactionFlag.builder("roster_limit_mod", Integer.class, FlagAdapters.INTEGER)
                    .name("Roster Limit Modifier")
                    .description("Defines the roster limit modifier.")
                    .stateDescription(
                            "Faction roster limit is modified by %value% members."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(0)
                    .build();

    public static final FactionFlag<Boolean> MINIMAL_VISIBILITY =
            FactionFlag.builder("minimal", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Minimal")
                    .description("Should only essential faction information be displayed?")
                    .stateDescription(
                            "Only essential information will be displayed.",
                            "No faction information will be withheld."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(false)
                    .visible(false)
                    .build();

    public static final FactionFlag<Boolean> PERMANENT =
            FactionFlag.builder("permanent", Boolean.class, FlagAdapters.BOOLEAN)
                    .name("Permanent")
                    .description("Is this faction permanent?")
                    .stateDescription(
                            "This faction cannot be disbanded.",
                            "This faction can be disbanded."
                    )
                    .permission(PermissionNodes.ADMIN)
                    .defaultState(false)
                    .visible(false)
                    .build();

    public static final FactionFlag<?>[] VALUES = {
            OPEN,
            EXPLOSIONS,
            FIRE_SPREAD,
            MOB_GRIEFING,
            COMBAT,
            MOB_SPAWNING,
            ANIMAL_SPAWNING,
            POWER_LOSS,
            POWER_GAIN,
            INFINITE_POWER,
            POWER_MODIFIER,
            MEMBER_LIMIT_MODIFIER,
            ROSTER_LIMIT_MODIFIER,
            MINIMAL_VISIBILITY,
            PERMANENT
    };
}
