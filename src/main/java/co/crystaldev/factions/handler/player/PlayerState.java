package co.crystaldev.factions.handler.player;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.api.player.TerritorialTitleMode;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.AsciiFactionMap;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.claims.Claiming;
import co.crystaldev.factions.config.MessageConfig;
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
 * @since 0.1.0
 */
@RequiredArgsConstructor @Getter
public final class PlayerState {

    private final Player player;

    private final AutoClaimState autoClaimState = new AutoClaimState();

    @Setter
    private boolean autoFactionMap;

    @Setter
    private boolean overriding;

    public void onLogin() {
        Faction faction = Factions.get().getFactions().findOrDefault(this.player);
        Component motd = faction.getMotd();

        if (motd != null) {
            MessageConfig config = MessageConfig.getInstance();
            Component title = config.motdTitle.build(
                    "faction", FactionHelper.formatRelational(this.player, faction, faction.getName()),
                    "faction_name", faction.getName());

            AlpineFactions.schedule(() -> Messaging.send(this.player, Components.joinNewLines(Formatting.title(title), motd)));
        }
    }

    public void onMoveChunk(@NotNull Chunk oldChunk, @NotNull Chunk newChunk) {
        FPlayer state = Factions.get().getPlayers().get(this.player);

        // player has entered into a new faction claim
        ClaimAccessor claims = Factions.get().getClaims();
        if (!claims.isSameClaim(oldChunk, newChunk)) {
            this.displayTerritorialTitle(state, newChunk);
        }

        // now we should attempt to claim/unclaim
        if (this.autoClaimState.isEnabled()) {
            Faction actingFaction = Factions.get().getFactions().findOrDefault(this.player);
            AlpineFactions.schedule(() -> Claiming.one(this.player, actingFaction, this.autoClaimState.getFaction()));
        }

        // now send the minimized faction map
        if (this.autoFactionMap) {
            AlpineFactions.schedule(() -> Messaging.send(this.player, AsciiFactionMap.create(this.player, true)));
        }

        // check the player's access
        boolean isElevated = Optional.ofNullable(claims.getClaim(newChunk)).map(claim -> claim.isAccessed(this.player)).orElse(false);
        boolean wasElevated = Optional.ofNullable(claims.getClaim(oldChunk)).map(claim -> claim.isAccessed(this.player)).orElse(false);
        if (isElevated != wasElevated) {
            MessageConfig config = MessageConfig.getInstance();
            ConfigText message = isElevated ? config.elevatedAccess : config.standardAccess;
            AlpineFactions.schedule(() -> message.send(this.player));
        }
    }

    private void displayTerritorialTitle(@NotNull FPlayer state, @NotNull Chunk chunk) {
        Faction faction = Factions.get().getClaims().getFactionOrDefault(chunk);
        TerritorialTitleMode mode = state.getTerritorialTitleMode();

        Component description = Optional.ofNullable(faction.getDescription()).orElse(Faction.DEFAULT_DESCRIPTION);

        if (mode == TerritorialTitleMode.TITLE) {
            Messaging.title(this.player, FactionHelper.formatRelational(this.player, faction, faction.getName()),
                    Component.text("").color(NamedTextColor.GRAY).append(description));
        }
        else {
            Component desc = Components.joinSpaces(Component.text(faction.getName() + ":"), description);
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
