package co.crystaldev.factions.handler.player;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.api.player.TerritorialTitleMode;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.AsciiFactionMap;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.claims.Claiming;
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
        Faction faction = Factions.get().factions().findOrDefault(this.player);
        Component motd = faction.getMotd();

        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
        if (!faction.isWilderness()) {
            // Notify the faction the player has joined
            FactionHelper.broadcast(faction, this.player, observer -> {
                return config.login.build(
                        "player", FactionHelper.formatRelational(observer, faction, this.player));
            });
        }


        if (motd != null) {
            Component title = config.motdTitle.build(
                    "faction", FactionHelper.formatRelational(this.player, faction, faction.getName()),
                    "faction_name", faction.getName());

            AlpineFactions.schedule(() -> Messaging.send(this.player, Components.joinNewLines(Formatting.title(title), motd)));
        }
    }

    public void onLogout() {
        Faction faction = Factions.get().factions().findOrDefault(this.player);

        if (!faction.isWilderness()) {
            MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
            // Notify the faction the player has left
            FactionHelper.broadcast(faction, this.player, observer -> {
                return config.logout.build(
                        "player", FactionHelper.formatRelational(observer, faction, this.player));
            });
        }
    }

    public void onMoveChunk(@NotNull Chunk oldChunk, @NotNull Chunk newChunk) {
        FPlayer state = Factions.get().players().get(this.player);

        // player has entered into a new faction claim
        ClaimAccessor claims = Factions.get().claims();
        if (!claims.isSameClaim(oldChunk, newChunk)) {
            this.displayTerritorialTitle(state, newChunk);
        }

        // now we should attempt to claim/unclaim
        if (this.autoClaimState.isEnabled()) {
            Faction actingFaction = Factions.get().factions().findOrDefault(this.player);
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
            MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);
            ConfigText message = isElevated ? config.elevatedAccess : config.standardAccess;
            AlpineFactions.schedule(() -> message.send(this.player));
        }
    }

    private void displayTerritorialTitle(@NotNull FPlayer state, @NotNull Chunk chunk) {
        Faction faction = Factions.get().claims().getFactionOrDefault(chunk);
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
