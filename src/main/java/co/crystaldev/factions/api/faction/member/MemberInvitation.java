package co.crystaldev.factions.api.faction.member;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.UUID;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/12/2024
 */
@Data
public final class MemberInvitation {
    @SerializedName("player")
    private final UUID playerId;
    @SerializedName("inviter")
    private final UUID inviterId;
}
