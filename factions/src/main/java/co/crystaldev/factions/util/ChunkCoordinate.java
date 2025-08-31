/*
 * This file is part of AlpineFactions - https://github.com/alpine-network/alpine-factions
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.factions.util;

import lombok.Data;

/**
 * @since 0.1.0
 */
@Data(staticConstructor = "of")
public final class ChunkCoordinate {
    private final int x;
    private final int z;
}
