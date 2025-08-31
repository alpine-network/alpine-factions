/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import de.exlll.configlib.Comment;

/**
 * @since 0.1.0
 */
public final class FactionConfig extends AlpineConfig {


    @Override
    public String getFileName() {
        return "fconfig.yml";
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
            "The maximum length of a member title."
    })
    public int maxTitleLength = 16;

    @Comment({
            "",
            "The member limit for a faction. Faction flags can modify this value."
    })
    public int memberLimit = 30;

    @Comment({
            "",
            "Whether the faction roster is enabled."
    })
    public boolean rosterEnabled = true;

    @Comment({
            "",
            "The roster limit for a faction. Faction flags can modify this value."
    })
    public int rosterLimit = 50;

    @Comment({
            "",
            "The maximum power per faction."
    })
    public int maxFactionPower = 3000;

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
            "The amount of power lost per death."
    })
    public int powerLossPerDeath = -15;

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

    @Comment({
            "",
            "Whether spawners can be mined in all territories."
    })
    public boolean silkSpawnerIgnoreTerritory = true;
}
