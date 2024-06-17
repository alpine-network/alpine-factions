package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @since 0.1.0
 */
@Command(name = "factions status")
@Description("View the status of your faction's members.")
final class StatusCommand extends FactionsCommand {
    public StatusCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "faction")
    public void faction(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction,
            @Arg("page") Optional<Integer> page
    ) {
        MessageConfig config = MessageConfig.getInstance();

        FactionAccessor factions = Factions.get().getFactions();
        Faction target = targetFaction.orElse(factions.findOrDefault(sender));
        Faction senderFaction = factions.findOrDefault(sender);

        if (!PlayerHandler.getInstance().isOverriding(sender) && !Objects.equals(target, senderFaction)) {
            FactionHelper.missingPermission(sender, target, "status");
            return;
        }

        List<Member> members = new LinkedList<>(target.getMembers());
        members.sort(Comparator.comparing(Member::isOnline));

        Component title = config.factionStatusTitle.build("faction", FactionHelper.formatRelational(sender, target, false),
                "faction_name", target.getName());
        String command = "/f status " + target.getName() + " %page%";
        Component compiledPage = Formatting.page(title, members, command, page.orElse(1), 10, member -> {
            OfflinePlayer player = member.getOfflinePlayer();
            FPlayer state = Factions.get().getPlayers().get(player);

            ConfigText text = member.isOnline() ? config.factionStatusOnlineEntry : config.factionStatusOfflineEntry;
            return text.build(
                    "player", FactionHelper.formatRelational(sender, target, player, false),
                    "player_name", player.getName(),
                    "power_boost", state.getPowerBoost(),
                    "power", state.getEffectivePower(),
                    "maxpower", state.getMaxPower()
            );
        });
        Messaging.send(sender, compiledPage);
    }

    @Execute(name = "member")
    public void member(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) Optional<OfflinePlayer> targetPlayer
    ) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        FactionConfig factionConfig = FactionConfig.getInstance();

        OfflinePlayer target = targetPlayer.orElse(sender instanceof Player ? (Player) sender : null);
        if (target == null) {
            return;
        }

        FPlayer state = Factions.get().getPlayers().get(target);
        Faction faction = Factions.get().getFactions().findOrDefault(target);
        Component title = messageConfig.memberStatusTitle.build(
                "player", FactionHelper.formatRelational(sender, faction, target, false),
                "player_name", target.getName());
        Component body = messageConfig.memberStatusBody.build(
                "power_indicator", Formatting.progress(state.getEffectivePower() / (double) factionConfig.maxPlayerPower),
                "power", state.getEffectivePower(),
                "maxpower", factionConfig.maxPlayerPower + state.getPowerBoost(),
                "power_per_hour", factionConfig.powerGainPerHour,
                "power_per_death", factionConfig.powerLossPerDeath
        );

        Messaging.send(sender, Components.joinNewLines(Formatting.title(title), body));
    }
}
