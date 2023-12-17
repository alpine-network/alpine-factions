package co.crystaldev.factions.api.faction;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/15/2023
 */
public final class FactionFlags {

    public static final FactionFlag OPEN = FactionFlag.builder("open")
            .name("Open")
            .description("Determines if the faction can be joined without an invite.")
            .stateDescription(
                    "Open to all: No invite required to join.",
                    "Invite only: An invite is required to join."
            )
            .defaultState(false)
            .build();

    public static final FactionFlag EXPLOSIONS = FactionFlag.builder("explosions")
            .name("Explosions")
            .description("Controls whether explosions are allowed in the faction's territory.")
            .stateDescription(
                    "Explosions allowed: Explosions can occur in this territory.",
                    "Explosions prohibited: No explosions can occur in this territory."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(true)
            .build();

    public static final FactionFlag FIRE_SPREAD = FactionFlag.builder("fire_spread")
            .name("Fire Spread")
            .description("Controls whether fire can spread within the faction's territory.")
            .stateDescription(
                    "Fire spread enabled: Fire can spread in this territory.",
                    "Fire spread disabled: Fire cannot spread in this territory."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(true)
            .build();

    public static final FactionFlag MOB_GRIEFING = FactionFlag.builder("mob_griefing")
            .name("Mob Griefing")
            .description("Determines if mobs like Endermen and Zombies can cause damage in the territory.")
            .stateDescription(
                    "Mob griefing enabled: Endermen and Zombies can cause damage.",
                    "Mob griefing disabled: Endermen and Zombies cannot cause damage."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(true)
            .build();

    public static final FactionFlag COMBAT = FactionFlag.builder("combat")
            .name("Combat")
            .description("Determines if players can engage in PvP combat within the faction's territory.")
            .stateDescription(
                    "PvP enabled: Players can engage in combat.",
                    "PvP disabled: Combat between players is not allowed."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(true)
            .build();

    public static final FactionFlag MOB_SPAWNING = FactionFlag.builder("mob_spawning")
            .name("Mob Spawning")
            .description("Controls the spawning of monsters within the faction's territory.")
            .stateDescription(
                    "Monsters can spawn: Monsters are allowed to spawn in this territory.",
                    "No monster spawning: Monsters are not allowed to spawn in this territory."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(false)
            .build();

    public static final FactionFlag ANIMAL_SPAWNING = FactionFlag.builder("animal_spawning")
            .name("Animal Spawning")
            .description("Controls the spawning of animals within the faction's territory.")
            .stateDescription(
                    "Animals can spawn: Animals are allowed to spawn in this territory.",
                    "No animal spawning: Animals are not allowed to spawn in this territory."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(false)
            .build();

    public static final FactionFlag POWER_LOSS = FactionFlag.builder("power_loss")
            .name("Power Loss")
            .description("Determines if faction members lose power upon death.")
            .stateDescription(
                    "Power loss on death: Members lose power upon death.",
                    "No power loss on death: Members do not lose power upon death."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(true)
            .build();

    public static final FactionFlag POWER_GAIN = FactionFlag.builder("power_gain")
            .name("Power Gain")
            .description("Determines if faction members can gain power in the faction's territory.")
            .stateDescription(
                    "Power gain enabled: Members can gain power in this territory.",
                    "No power gain: Members cannot gain power in this territory."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(true)
            .build();

    public static final FactionFlag INFINITE_POWER = FactionFlag.builder("inf_power")
            .name("Infinite Power")
            .description("Determines if the faction possesses infinite power.")
            .stateDescription(
                    "Infinite power: The faction has an unlimited power supply.",
                    "Standard power: The faction's power operates as usual."
            )
            .permission("alpinefactions.faction.admin")
            .defaultState(false)
            .build();

    public static final FactionFlag[] VALUES = {
            OPEN,
            EXPLOSIONS,
            FIRE_SPREAD,
            MOB_GRIEFING,
            COMBAT,
            MOB_SPAWNING,
            ANIMAL_SPAWNING,
            POWER_LOSS,
            POWER_GAIN,
            INFINITE_POWER
    };
}
