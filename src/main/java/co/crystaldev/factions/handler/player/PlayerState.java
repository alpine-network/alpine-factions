package co.crystaldev.factions.handler.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/08/2024
 */
@RequiredArgsConstructor @Getter
public final class PlayerState {

    private final Player player;

    private final AutoClaimState autoClaimState = new AutoClaimState();

    private Chunk lastChunk;

    public void tick() {
        Chunk chunk = this.player.getLocation().getChunk();
        if (!chunk.equals(this.lastChunk)) {
            this.lastChunk = chunk;
            this.movedChunk(chunk);
        }
    }

    private void movedChunk(@NotNull Chunk chunk) {

    }
}
