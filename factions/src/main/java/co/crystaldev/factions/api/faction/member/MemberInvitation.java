package co.crystaldev.factions.api.faction.member;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * @since 0.1.0
 */
public final class MemberInvitation {
    @SerializedName("player")
    private final @NotNull UUID playerId;

    @SerializedName("inviter")
    private final @NotNull UUID inviterId;

    public MemberInvitation(@NotNull UUID playerId, @NotNull UUID inviterId) {
        this.playerId = playerId;
        this.inviterId = inviterId;
    }

    public @NotNull UUID getPlayerId() {
        return this.playerId;
    }

    public @NotNull UUID getInviterId() {
        return this.inviterId;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MemberInvitation)) return false;
        MemberInvitation that = (MemberInvitation) object;
        return Objects.equals(this.getPlayerId(), that.getPlayerId()) &&
                Objects.equals(this.getInviterId(), that.getInviterId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPlayerId(), this.getInviterId());
    }

    @Override
    public String toString() {
        return "MemberInvitation(playerId=" + this.getPlayerId() + ", inviterId=" + this.getInviterId() + ")";
    }
}
