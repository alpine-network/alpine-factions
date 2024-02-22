package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Messaging;
import co.crystaldev.factions.util.PlayerHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/19/2024
 */
@Command(name = "factions title")
@Description("Modify a member title.")
public final class TitleCommand extends FactionsCommand {
    public TitleCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("title") String title
    ) {
        this.setTitle(sender, other, ComponentHelper.legacy(title));
    }

    @Execute(name = "clear")
    public void clear(
            @Context CommandSender sender,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other
    ) {
        this.setTitle(sender, other, null);
    }

    private void setTitle(@NotNull CommandSender sender, @NotNull OfflinePlayer other, @Nullable Component title) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = factions.findOrDefault(other);
        Faction actingFaction = factions.findOrDefault(sender);

        if (faction.isWilderness()) {
            config.playerNotInFaction.send(sender, "player", FactionHelper.formatRelational(sender, faction, other));
            return;
        }

        if (!faction.isPermitted(sender, Permissions.MODIFY_TITLE)) {
            FactionHelper.missingPermission(sender, faction, "modify titles");
            return;
        }

        faction.wrapMember(other.getUniqueId(), member -> {
            member.setTitle(title);

            Messaging.broadcast(faction, sender, observer -> config.titleChange.build(
                    "actor", FactionHelper.formatRelational(observer, actingFaction, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "player", FactionHelper.formatRelational(observer, faction, other, false),
                    "player_name", other.getName(),
                    "title", title == null ? "none" : title
            ));
        });
    }
}
