package co.crystaldev.factions.api.member;

import lombok.Data;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@Data
public final class Member {
    private final UUID id;
    private final Rank rank;
    private Component title;
    private final long joinedAt = System.currentTimeMillis();
}
