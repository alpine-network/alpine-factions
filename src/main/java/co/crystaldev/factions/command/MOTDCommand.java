package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions motd")
@Description("Modify your faction's message of the day.")
final class MOTDCommand extends FactionsCommand {
    public MOTDCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void set(
            @Context Player player,
            @Join("name") String motd
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.get().factions().findOrDefault(player);

        // ensure the user has permission
        if (!faction.isPermitted(player, Permissions.MODIFY_MOTD)) {
            FactionHelper.missingPermission(player, faction, "modify motd");
            return;
        }

        // set the motd
        Component formatted = ComponentHelper.legacy(motd);
        faction.setMotd(formatted);

        // notify the faction
        FactionHelper.broadcast(faction, player, observer -> {
            return config.motd.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),
                    "motd", formatted);
        });
    }

    @Execute
    public void view(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.get().factions().findOrDefault(player);

        Component motd = Optional.ofNullable(faction.getMotd()).orElse(Faction.DEFAULT_MOTD);
        Component title = config.motdTitle.build(
                "faction", FactionHelper.formatRelational(player, faction, faction.getName()),
                "faction_name", faction.getName());

        Messaging.send(player, Components.joinNewLines(Formatting.title(title), motd));
    }

    @Execute(name = "clear")
    public void clear(@Context Player player) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.get().factions().findOrDefault(player);

        // ensure the user has permission
        if (!faction.isPermitted(player, Permissions.MODIFY_MOTD)) {
            FactionHelper.missingPermission(player, faction, "modify motd");
            return;
        }

        // remove the motd
        faction.setMotd(null);

        // notify the faction
        FactionHelper.broadcast(faction, player, observer -> {
            return config.motd.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),
                    "motd", Faction.DEFAULT_MOTD);
        });
    }
}
