package co.crystaldev.factions.api.faction.member;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.UUID;

/**
 * @since 0.1.0
 */
@Data
public final class MemberInvitation {
    @SerializedName("player")
    private final UUID playerId;
    @SerializedName("inviter")
    private final UUID inviterId;
}
