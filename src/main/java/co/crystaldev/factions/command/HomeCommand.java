package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.teleport.TeleportTask;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.event.FactionHomeUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @since 0.1.0
 */
@Command(name = "factions home")
@Description("Warp to the faction home.")
final class HomeCommand extends FactionsCommand {
    public HomeCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Faction faction = targetFaction.orElse(Factions.get().factions().findOrDefault(player));
        if (!faction.isPermitted(player, Permissions.ACCESS_HOME)) {
            FactionHelper.missingPermission(player, faction, "access home");
            return;
        }

        // ensure the faction has a home
        Location home = faction.getHome();
        if (home == null) {
            config.noHome.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure home is still in the faction's own territory
        if (!Factions.get().claims().getFactionOrDefault(home).equals(faction)) {
            FactionHomeUpdateEvent event = AlpineFactions.callEvent(new FactionHomeUpdateEvent(faction, player, null));
            if (!event.isCancelled()) {
                faction.setHome(event.getLocation());
                if (event.getLocation() == null) {
                    FactionHelper.broadcast(faction, config.unsetHome.build());
                }
                return;
            }
        }

        TeleportTask.builder(player, home)
                .delay(5, TimeUnit.SECONDS)
                .onApply(ctx -> {
                    ConfigText message = ctx.isInstant() ? config.homeInstant : config.home;
                    ctx.message(message.build(
                            "faction", FactionHelper.formatRelational(player, faction, false),
                            "faction_name", faction.getName(),
                            "seconds", ctx.timeUntilTeleport(TimeUnit.SECONDS)));
                })
                .initiate(this.plugin);
    }
}
