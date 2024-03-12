package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.event.FactionDescriptionUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/10/2024
 */
@Command(name = "factions description", aliases = "factions desc")
@Description("Modify your faction's description.")
public final class DescriptionCommand extends FactionsCommand {
    public DescriptionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(
            @Context CommandSender sender,
            @Join("name") String description
    ) {
        set(Accessors.factions().findOrDefault(sender), sender, Component.text(description));
    }

    @Execute(name = "clear")
    public void clear(@Context CommandSender sender) {
        set(Accessors.factions().findOrDefault(sender), sender, null);
    }

    private static void set(@NotNull Faction faction, @NotNull CommandSender sender, @Nullable Component description) {
        MessageConfig config = MessageConfig.getInstance();

        // ensure the user has permission
        if (!faction.isPermitted(sender, Permissions.MODIFY_DESCRIPTION)) {
            FactionHelper.missingPermission(sender, faction, "modify description");
            return;
        }

        // call event
        FactionDescriptionUpdateEvent event = AlpineFactions.callEvent(new FactionDescriptionUpdateEvent(faction, sender, description));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        // change the faction description
        Component newDescription = event.getDescription();
        faction.setDescription(newDescription);

        // notify the faction
        FactionHelper.broadcast(faction, sender, observer -> {
            return config.description.build(
                    "actor", FactionHelper.formatRelational(observer, faction, sender),
                    "actor_name", PlayerHelper.getName(sender),
                    "description", newDescription == null ? Faction.DEFAULT_DESCRIPTION : newDescription);
        });
    }
}
