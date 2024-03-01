package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import com.cryptomorin.xseries.XSound;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/20/2024
 */
@Command(name = "factions alert")
@Description("Notify the faction.")
public final class AlertCommand extends FactionsCommand {
    public AlertCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context Player player,
            @Join("message") String message
    ) {
        MessageConfig config = MessageConfig.getInstance();
        Faction faction = Accessors.factions().findOrDefault(player);

        if (!faction.isPermitted(player, Permissions.ALERT)) {
            FactionHelper.missingPermission(player, faction, "alert");
            return;
        }

        Component parsedAlert = ComponentHelper.legacy(message);

        // broadcast chat message
        FactionHelper.broadcast(faction, observer -> {
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(observer);

            return config.alertMessage.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player, false),
                    "actor_name", player.getName(),
                    "alert", parsedAlert
            );
        });

        // broadcast title
        Component title = config.alertTitle.build("alert", parsedAlert);
        for (Member member : faction.getMembers()) {
            Player observer = member.getPlayer();
            if (observer != null) {
                Component subtitle = config.alertSubtitle.build(
                        "actor", FactionHelper.formatRelational(observer, faction, player, false),
                        "actor_name", player.getName(),
                        "alert", parsedAlert
                );
                Messaging.title(observer, title, subtitle);
            }
        }
    }
}
