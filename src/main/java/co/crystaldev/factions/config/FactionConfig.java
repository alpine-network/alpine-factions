package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import de.exlll.configlib.Comment;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/24/2023
 */
public final class FactionConfig extends AlpineConfig {

    @Getter
    private static FactionConfig instance;
    { instance = this; }

    @Override
    public String getFileName() {
        return "factions_config.yml";
    }

    @Comment({
            "Alpine Factions v{{ pluginVersion }}",
            "Developed by Crystal Development, LLC.",
            "",
            "The minimum length of a faction name."
    })
    public int minNameLength = 3;

    @Comment({
            "",
            "The maximum length of a faction name."
    })
    public int maxNameLength = 10;

    @Comment({
            "",
            "The member limit for a faction. Faction flags can modify this value."
    })
    public int memberLimit = 30;

    @Comment({
            "",
            "The roster limit for a faction. Faction flags can modify this value."
    })
    public int rosterLimit = 50;

    @Comment({
            "",
            "The maximum power per player."
    })
    public int maxPlayerPower = 100;

    @Comment({
            "",
            "The amount of power a player starts with."
    })
    public int initialPlayerPower = 50;

    @Comment({
            "",
            "The amount of power players should gain in one hour."
    })
    public int powerGainPerHour = 10;

    @Comment({
            "",
            "The maximum amount of claims that can be filled via `/f claim fill`."
    })
    public int maxClaimFillVolume = 1000;

    @Comment({
            "",
            "The maximum distance from the player (in chunks) where land can be claimed."
    })
    public int maxClaimDistance = 30;

    @Comment({
            "",
            "The maximum number of faction alliances for a faction."
    })
    public int maxAlliances = 5;

    @Comment({
            "",
            "The maximum number of faction truces for a faction."
    })
    public int maxTruces = 999;
}
