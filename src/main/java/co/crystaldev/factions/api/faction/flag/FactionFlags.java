package co.crystaldev.factions.api.faction.flag;

import co.crystaldev.factions.PermissionNodes;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/15/2023
 */
public final class FactionFlags {

    public static final FactionFlag<Boolean> OPEN = FactionFlag.builder("open", boolean.class)
            .name("Open")
            .description("Determines if the faction can be joined without an invite.")
            .stateDescription(
                    "Open to all: No invite required to join.",
                    "Invite only: An invite is required to join."
            )
            .defaultState(false)
            .build();

    public static final FactionFlag<Boolean> EXPLOSIONS = FactionFlag.<Boolean>builder("explosions", boolean.class)
            .name("Explosions")
            .description("Controls whether explosions are allowed in the faction's territory.")
            .stateDescription(
                    "Explosions allowed: Explosions can occur in this territory.",
                    "Explosions prohibited: No explosions can occur in this territory."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(true)
            .build();

    public static final FactionFlag<Boolean> FIRE_SPREAD = FactionFlag.<Boolean>builder("fire_spread", boolean.class)
            .name("Fire Spread")
            .description("Controls whether fire can spread within the faction's territory.")
            .stateDescription(
                    "Fire spread enabled: Fire can spread in this territory.",
                    "Fire spread disabled: Fire cannot spread in this territory."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(true)
            .build();

    public static final FactionFlag<Boolean> MOB_GRIEFING = FactionFlag.<Boolean>builder("mob_griefing", boolean.class)
            .name("Mob Griefing")
            .description("Determines if mobs like Endermen and Zombies can cause damage in the territory.")
            .stateDescription(
                    "Mob griefing enabled: Endermen and Zombies can cause damage.",
                    "Mob griefing disabled: Endermen and Zombies cannot cause damage."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(true)
            .build();

    public static final FactionFlag<Boolean> COMBAT = FactionFlag.builder("combat", boolean.class)
            .name("Combat")
            .description("Determines if players can engage in PvP combat within the faction's territory.")
            .stateDescription(
                    "PvP enabled: Players can engage in combat.",
                    "PvP disabled: Combat between players is not allowed."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(true)
            .build();

    public static final FactionFlag<Boolean> MOB_SPAWNING = FactionFlag.builder("mob_spawning", boolean.class)
            .name("Mob Spawning")
            .description("Controls the spawning of monsters within the faction's territory.")
            .stateDescription(
                    "Monsters can spawn: Monsters are allowed to spawn in this territory.",
                    "No monster spawning: Monsters are not allowed to spawn in this territory."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(false)
            .build();

    public static final FactionFlag<Boolean> ANIMAL_SPAWNING = FactionFlag.builder("animal_spawning", boolean.class)
            .name("Animal Spawning")
            .description("Controls the spawning of animals within the faction's territory.")
            .stateDescription(
                    "Animals can spawn: Animals are allowed to spawn in this territory.",
                    "No animal spawning: Animals are not allowed to spawn in this territory."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(false)
            .build();

    public static final FactionFlag<Boolean> POWER_LOSS = FactionFlag.builder("power_loss", boolean.class)
            .name("Power Loss")
            .description("Determines if faction members lose power upon death.")
            .stateDescription(
                    "Power loss on death: Members lose power upon death.",
                    "No power loss on death: Members do not lose power upon death."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(true)
            .build();

    public static final FactionFlag<Boolean> POWER_GAIN = FactionFlag.builder("power_gain", boolean.class)
            .name("Power Gain")
            .description("Determines if faction members can gain power in the faction's territory.")
            .stateDescription(
                    "Power gain enabled: Members can gain power in this territory.",
                    "No power gain: Members cannot gain power in this territory."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(true)
            .build();

    public static final FactionFlag<Boolean> INFINITE_POWER = FactionFlag.builder("inf_power", boolean.class)
            .name("Infinite Power")
            .description("Determines if the faction possesses infinite power.")
            .stateDescription(
                    "Infinite power: The faction has an unlimited power supply.",
                    "Standard power: The faction's power operates as usual."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(false)
            .build();

    public static final FactionFlag<Integer> POWER_MODIFIER = FactionFlag.builder("power_mod", int.class)
            .name("Power Modifier")
            .description("Defines the power modifier.")
            .stateDescription(
                    "Faction power level is modified by %value% points."
            )
            .permission(PermissionNodes.ADMIN)
            .defaultState(0)
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
            POWER_MODIFIER
    };
}
