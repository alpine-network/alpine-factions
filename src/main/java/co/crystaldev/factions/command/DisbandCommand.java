package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.1.0
 */
@Command(name = "factions disband")
@Description("Disband your faction.")
final class DisbandCommand extends FactionsCommand {

    private final Map<CommandSender, Long> confirmationMap = new HashMap<>();

    public DisbandCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.FACTION) Faction faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        boolean owner = sender instanceof ConsoleCommandSender
                || PlayerHandler.getInstance().isOverriding(sender)
                || sender instanceof OfflinePlayer && faction.getMemberRankOrDefault(((OfflinePlayer) sender).getUniqueId(), Rank.RECRUIT) == Rank.LEADER;
        if (!owner) {
            FactionHelper.missingPermission(sender, faction, "disband");
            return;
        }

        // ensure the faction can even disband
        if (!faction.canDisband()) {
            config.unableToDisband.send(sender);
            return;
        }

        // require a confirmation from the user
        if (!this.confirmationMap.containsKey(sender) || System.currentTimeMillis() - this.confirmationMap.get(sender) > 10_000L) {
            this.confirmationMap.put(sender, System.currentTimeMillis());
            config.confirm.send(sender);
            return;
        }

        this.confirmationMap.remove(sender);
        faction.disband(sender);
    }
}
