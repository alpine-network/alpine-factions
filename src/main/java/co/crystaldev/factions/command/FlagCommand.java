package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.registry.FlagRegistry;
import co.crystaldev.factions.api.event.FactionFlagUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FlagAdapter;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.*;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

/**
 * @since 0.1.0
 */
@Command(name = "factions flag")
@Description("Modify faction flags.")
final class FlagCommand extends AlpineCommand {
    public FlagCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("flag") FactionFlag flag,
            @Arg("value") String value,
            @Arg("faction") Optional<Faction> faction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FlagAdapter<?> adapter = flag.getAdapter();
        Object resolvedValue = adapter.deserialize(value);

        if (resolvedValue == null) {
            config.invalidFlagValue.send(sender, "flag", flag.getName());
            return;
        }

        Faction resolvedFaction = faction.orElse(Factions.registry().findOrDefault(sender));
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, resolvedFaction,
                Permissions.MODIFY_FLAGS, "modify flags");
        if (!permitted) {
            return;
        }

        // call event
        FactionFlagUpdateEvent event = AlpineFactions.callEvent(new FactionFlagUpdateEvent(resolvedFaction, sender, flag, resolvedValue));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        Object newValue = event.getValue();
        resolvedFaction.setFlagValue(flag, newValue);

        String stateDescription = flag.getType().equals(Boolean.class)
                ? flag.getStateDescription((Boolean) newValue)
                : flag.getStateDescription();
        FactionHelper.broadcast(resolvedFaction, sender, observer -> {
            return config.updatedFlagValue.build(
                    "actor", RelationHelper.formatPlayerName(observer, sender),
                    "actor_name", PlayerHelper.getName(sender),

                    "flag_id", flag.getId(),
                    "flag_name", flag.getName(),
                    "flag_description", flag.getDescription(),
                    "flag_state_description", ComponentHelper.mini(Formatting.placeholders(stateDescription, "value", newValue)),
                    "state", newValue
            );
        });
    }

    @Execute(name = "show")
    public void show(
            @Context CommandSender sender,
            @Arg("faction") Optional<Faction> faction,
            @Arg("page") Optional<Integer> page
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FlagRegistry registry = AlpineFactions.getInstance().flags();

        Faction resolvedFaction = faction.orElse(Factions.registry().findOrDefault(sender));
        boolean permitted = PermissionHelper.checkPermissionAndNotify(sender, resolvedFaction,
                Permissions.MODIFY_FLAGS, "modify flags");
        if (!permitted) {
            return;
        }

        List<FactionFlag> flags = (List) registry.getAll(sender);
        String command = "/f flag show " + resolvedFaction.getName() + " %page%";
        Component title = config.flagStateListTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(sender, resolvedFaction),
                "faction_name", resolvedFaction.getName());
        Component compiledPage = Formatting.page(title, flags, command, page.orElse(1), 10, flag -> {
            Object resolvedState = resolvedFaction.getFlagValueOrDefault(flag);
            String stateDescription = flag.getType().equals(Boolean.class)
                    ? flag.getStateDescription((Boolean) resolvedState)
                    : flag.getStateDescription();

            String state = flag.getAdapter().serialize(resolvedState);
            return config.flagStateListEntry.build(
                    "flag_id", flag.getId(),
                    "flag_name", flag.getName(),
                    "flag_description", flag.getDescription(),
                    "flag_state_description", ComponentHelper.mini(Formatting.placeholders(stateDescription, state)),
                    "state", state
            );
        });

        Messaging.send(sender, compiledPage);
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FlagRegistry registry = AlpineFactions.getInstance().flags();

        List<FactionFlag> flags = (List) registry.getAll(sender);
        String command = "/f flag list %page%";
        Component title = config.flagListTitle.build();
        Component compiledPage = Formatting.page(title, flags, command, page.orElse(1), 10, flag -> {
            return config.flagListEntry.build(
                    "flag_id", flag.getId(),
                    "flag_name", flag.getName(),
                    "flag_description", flag.getDescription(),
                    "default_state", flag.getAdapter().serialize(flag.getDefaultState())
            );
        });

        Messaging.send(sender, compiledPage);
    }
}
