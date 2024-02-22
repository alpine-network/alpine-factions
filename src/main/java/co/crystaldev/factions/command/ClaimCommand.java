package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.handler.player.AutoClaimState;
import co.crystaldev.factions.handler.player.PlayerState;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.claims.ClaimType;
import co.crystaldev.factions.util.claims.Claiming;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/06/2024
 */
@Command(name = "factions claim")
@Description("Claim faction territory.")
public final class ClaimCommand extends FactionsCommand {
    public ClaimCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("type") @Key(Args.CLAIM_TYPE) ClaimType type,
            @Arg("radius") Optional<Integer> rad,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        FactionAccessor factions = Accessors.factions();
        Faction claimingFaction = faction.orElse(factions.find(player));
        Faction actingFaction = faction.orElse(factions.findOrDefault(player));
        Claiming.mode(player, actingFaction, claimingFaction, type, Math.max(rad.orElse(1), 1));
    }

    @Execute(name = "fill", aliases = "f") @Async
    public void fill(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        FactionAccessor factions = Accessors.factions();
        Faction claimingFaction = faction.orElse(factions.find(player));
        Faction actingFaction = faction.orElse(factions.findOrDefault(player));
        Claiming.fill(player, actingFaction, claimingFaction);
    }

    @Execute(name = "one", aliases = "o")
    public void one(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        FactionAccessor factions = Accessors.factions();
        Faction claimingFaction = faction.orElse(factions.find(player));
        Faction actingFaction = faction.orElse(factions.findOrDefault(player));
        Claiming.one(player, actingFaction, claimingFaction);
    }

    @Execute(name = "auto", aliases = "a")
    public void auto(
            @Context Player player,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        Faction claimingFaction = faction.orElse(Accessors.factions().findOrDefault(player));

        PlayerState state = PlayerHandler.getInstance().getPlayer(player);
        AutoClaimState autoClaim = state.getAutoClaimState();
        autoClaim.toggle(claimingFaction);

        if (autoClaim.isEnabled()) {
            config.enableAutoClaim.send(player,
                    "faction", FactionHelper.formatRelational(player, claimingFaction),
                    "faction_name", claimingFaction.getName());

            // attempt to claim the chunk the player is standing in
            this.one(player, Optional.of(claimingFaction));
        }
        else {
            config.disableAutoSetting.send(player);
        }
    }
}
