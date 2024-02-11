package co.crystaldev.factions.handler.player;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.api.player.TerritorialTitleMode;
import co.crystaldev.factions.util.claims.Claiming;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.store.PlayerStore;
import co.crystaldev.factions.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/08/2024
 */
@RequiredArgsConstructor @Getter
public final class PlayerState {

    private final Player player;

    private final AutoClaimState autoClaimState = new AutoClaimState();

    @Setter
    private boolean autoFactionMap;

    public void onLogin() {
        Faction faction = FactionStore.getInstance().findFactionOrDefault(this.player);
        Component motd = faction.getMotd();

        if (motd != null) {
            MessageConfig config = MessageConfig.getInstance();
            Component title = config.motdTitle.build(
                    "faction", FactionHelper.formatRelational(this.player, faction, faction.getName()),
                    "faction_name", faction.getName());

            AlpineFactions.schedule(() -> Messaging.send(this.player, ComponentHelper.joinNewLines(Formatting.title(title), motd)));
        }
    }

    public void onMoveChunk(@NotNull Chunk oldChunk, @NotNull Chunk newChunk) {
        FPlayer state = PlayerStore.getInstance().getPlayer(this.player.getUniqueId());

        // player has entered into a new faction claim
        if (!ClaimStore.getInstance().isSameClaim(oldChunk, newChunk)) {
            this.displayTerritorialTitle(state, newChunk);
        }

        // now we should attempt to claim/unclaim
        if (this.autoClaimState.isEnabled()) {
            Faction actingFaction = FactionStore.getInstance().findFactionOrDefault(this.player);
            AlpineFactions.schedule(() -> Claiming.one(this.player, actingFaction, this.autoClaimState.getFaction()));
        }

        // now send the minimized faction map
        if (this.autoFactionMap) {
            AlpineFactions.schedule(() -> Messaging.send(this.player, AsciiFactionMap.create(this.player, true)));
        }
    }

    private void displayTerritorialTitle(@NotNull FPlayer state, @NotNull Chunk chunk) {
        Faction faction = ClaimStore.getInstance().getFactionOrDefault(chunk);
        TerritorialTitleMode mode = state.getTerritorialTitleMode();

        Component description = Optional.ofNullable(faction.getDescription()).orElse(Faction.DEFAULT_DESCRIPTION);

        if (mode == TerritorialTitleMode.TITLE) {
            Messaging.title(this.player, FactionHelper.formatRelational(this.player, faction, faction.getName()),
                    Component.text("").color(NamedTextColor.GRAY).append(description));
        }
        else {
            Component desc = ComponentHelper.joinSpaces(Component.text(faction.getName() + ":"), description);
            desc = FactionHelper.formatRelational(this.player, faction, desc);

            if (mode == TerritorialTitleMode.ACTION_BAR) {
                Messaging.actionBar(this.player, desc);
            }
            else {
                Messaging.send(this.player, desc);
            }
        }
    }
}
