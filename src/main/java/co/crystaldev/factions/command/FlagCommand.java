package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FlagAdapter;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;
import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/17/2024
 */
@Command(name = "factions flag")
@Description("Modify faction flags.")
public final class FlagCommand extends FactionsCommand {
    public FlagCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("flag") @Key(Args.FACTION_FLAG) FactionFlag<?> flag,
            @Arg("value") String value,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FlagAdapter<?> adapter = flag.getAdapter();
        Object resolvedState = adapter.deserialize(value);

        if (resolvedState == null) {
            config.invalidFlagValue.send(sender, "flag", flag.getName());
            return;
        }

        Faction resolvedFaction = faction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));
        if (!resolvedFaction.isPermitted(sender, Permissions.MODIFY_FLAGS)) {
            FactionHelper.missingPermission(sender, resolvedFaction, "modify flags");
            return;
        }

        resolvedFaction.setFlagValue((FactionFlag) flag, resolvedState);

        String stateDescription = flag.getType().equals(Boolean.class)
                ? flag.getStateDescription((Boolean) resolvedState)
                : flag.getStateDescription();
        Messaging.broadcast(resolvedFaction, sender, observer -> {

            Component actor = sender instanceof ConsoleCommandSender
                    ? Component.text("@console")
                    : FactionHelper.formatRelational(observer, resolvedFaction, (OfflinePlayer) sender);

            return config.updatedFlagValue.build(
                    "actor", actor,
                    "actor_name", actor instanceof ConsoleCommandSender ? "@console" : ((OfflinePlayer) sender).getName(),

                    "flag_id", flag.getId(),
                    "flag_name", flag.getName(),
                    "flag_description", flag.getDescription(),
                    "flag_state_description", ComponentHelper.mini(Formatting.formatPlaceholders(stateDescription, resolvedState)),
                    "state", resolvedState
            );
        });
    }

    @Execute(name = "show")
    public void show(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction,
            @Arg("page") Optional<Integer> page
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FlagRegistry registry = AlpineFactions.getInstance().getFlagRegistry();

        Faction resolvedFaction = faction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));
        if (!resolvedFaction.isPermitted(sender, Permissions.MODIFY_FLAGS)) {
            FactionHelper.missingPermission(sender, resolvedFaction, "modify flags");
            return;
        }

        List<FactionFlag> flags = (List) registry.getAll(sender);
        String command = "/f flag show " + resolvedFaction.getName() + " %page%";
        Component title = config.flagStateListTitle.build("faction", FactionHelper.formatRelational(sender, resolvedFaction, false),
                "faction_name", resolvedFaction.getName());
        Component compiledPage = Formatting.page(title, flags, command, page.orElse(1), 10, flag -> {
            if (flag == null) {
                return ComponentHelper.nil();
            }

            Object resolvedState = resolvedFaction.getFlagValueOrDefault(flag);
            String stateDescription = flag.getType().equals(Boolean.class)
                    ? flag.getStateDescription((Boolean) resolvedState)
                    : flag.getStateDescription();

            String state = flag.getAdapter().serialize(resolvedState);
            return config.flagStateListEntry.build(
                    "flag_id", flag.getId(),
                    "flag_name", flag.getName(),
                    "flag_description", flag.getDescription(),
                    "flag_state_description", ComponentHelper.mini(Formatting.formatPlaceholders(stateDescription, state)),
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
        MessageConfig config = MessageConfig.getInstance();
        FlagRegistry registry = AlpineFactions.getInstance().getFlagRegistry();

        List<FactionFlag> flags = (List) registry.getAll(sender);
        String command = "/f flag list %page%";
        Component title = config.flagListTitle.build();
        Component compiledPage = Formatting.page(title, flags, command, page.orElse(1), 10, flag -> {
            if (flag == null) {
                return ComponentHelper.nil();
            }

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
