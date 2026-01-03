/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.MappedMaterial;
import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class MaterialMapping {

    public static final MappedMaterial BUCKETS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().contains("BUCKET")))
            .build();

    public static final MappedMaterial CANDLES = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("CANDLE")))
            .build();

    public static final MappedMaterial FLOWER_POTS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().startsWith("POTTED_")))
            .add(XMaterial.FLOWER_POT)
            .build();

    public static final MappedMaterial STRIPPED_LOGS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().startsWith("STRIPPED_")))
            .build();

    public static final MappedMaterial SIGNS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("_SIGN")))
            .build();

    public static final MappedMaterial BEDS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("_BED")))
            .build();

    public static final MappedMaterial DOORS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("_DOOR")))
            .add(types -> types.filter(m -> m.name().endsWith("_TRAPDOOR")))
            .add(types -> types.filter(m -> m.name().endsWith("_FENCE_GATE")))
            .build();

    public static final MappedMaterial SWITCHES = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("_BUTTON")))
            .add(XMaterial.LEVER)
            .build();

    public static final MappedMaterial PRESSURE_PLATES = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("_PRESSURE_PLATE")))
            .build();

    public static final MappedMaterial SPAWNERS = MappedMaterial.of(
            XMaterial.SPAWNER,
            XMaterial.TRIAL_SPAWNER
    );

    public static final MappedMaterial MATERIAL_EDIT_ON_INTERACT = MappedMaterial.of(
            XMaterial.NOTE_BLOCK,
            XMaterial.CHISELED_BOOKSHELF,
            XMaterial.JUKEBOX,
            XMaterial.COMPOSTER,
            XMaterial.BELL,
            XMaterial.DECORATED_POT,
            XMaterial.CAMPFIRE,
            XMaterial.SOUL_CAMPFIRE,
            XMaterial.DRAGON_EGG,
            XMaterial.LECTERN,

            XMaterial.REPEATER,
            XMaterial.DAYLIGHT_DETECTOR,
            XMaterial.COMPARATOR,
            XMaterial.COPPER_BULB
    );

    public static final MappedMaterial CONTAINERS = MappedMaterial.builder()
            .add(types -> types.filter(m -> m.name().endsWith("SHULKER_BOX"))) // MC 1.9
            .add(types -> types.filter(m -> m.name().endsWith("COPPER_CHEST"))) // MC 1.21.9
            .add(types -> types.filter(m -> m.name().endsWith("_SHELF"))) // MC 1.21.9
            .add(
                    XMaterial.CHEST, // MC Legacy
                    XMaterial.TRAPPED_CHEST, // MC Legacy
                    XMaterial.FURNACE, // MC Legacy
                    XMaterial.JUKEBOX, // MC Legacy
                    XMaterial.DISPENSER, // MC B1.8
                    XMaterial.BREWING_STAND, // MC 1.0
                    XMaterial.ENCHANTING_TABLE, // MC 1.0
                    XMaterial.ANVIL, // MC 1.4
                    XMaterial.CHIPPED_ANVIL, // MC 1.4
                    XMaterial.DAMAGED_ANVIL, // MC 1.4
                    XMaterial.BEACON, // MC 1.4
                    XMaterial.HOPPER, // MC 1.5
                    XMaterial.DROPPER, // MC 1.5
                    XMaterial.BARREL, // MC 1.14
                    XMaterial.CARTOGRAPHY_TABLE, // MC 1.14
                    XMaterial.SMITHING_TABLE, // MC 1.14
                    XMaterial.LOOM, // MC 1.14
                    XMaterial.GRINDSTONE, // MC 1.14
                    XMaterial.LECTERN, // MC 1.14
                    XMaterial.BLAST_FURNACE, // MC 1.14
                    XMaterial.SMOKER, // MC 1.14
                    XMaterial.CHISELED_BOOKSHELF, // MC 1.20
                    XMaterial.CRAFTER // MC 1.21
            )
            .build();

    // https://github.com/MassiveCraft/Factions/blob/master/src/com/massivecraft/factions/util/EnumerationUtil.java
    public static final MappedMaterial MATERIAL_EDIT_TOOLS = MappedMaterial.builder()
            .add(
                    XMaterial.FIRE_CHARGE,
                    XMaterial.FLINT_AND_STEEL,
                    XMaterial.ARMOR_STAND,
                    XMaterial.END_CRYSTAL,
                    XMaterial.CHEST,
                    XMaterial.BONE_MEAL
            )
            .add(types -> types.filter(m -> m.name().endsWith("_DOOR")))
            .add(SIGNS)
            .build();

    public static final MappedMaterial CAULDRONS = MappedMaterial.of(
            XMaterial.CAULDRON,
            XMaterial.WATER_CAULDRON,
            XMaterial.LAVA_CAULDRON,
            XMaterial.POWDER_SNOW_CAULDRON
    );
}
