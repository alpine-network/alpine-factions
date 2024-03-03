package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.event.FactionPermissionUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.PlayerHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/02/2024
 */
@Command(name = "factions permission", aliases = { "factions perm" })
@Description("Manage faction permissions.")
public final class PermissionCommand extends FactionsCommand {
    public PermissionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("permission") @Key(Args.FACTION_PERMISSION) Permission permission,
            @Arg("relational") @Key(Args.RELATIONAL) Relational relational,
            @Arg("value") boolean value,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();

        Faction resolvedFaction = faction.orElse(Accessors.factions().findOrDefault(sender));
        if (!resolvedFaction.isPermitted(sender, Permissions.MODIFY_PERMS)) {
            FactionHelper.missingPermission(sender, resolvedFaction, "modify permissions");
            return;
        }

        // call event
        FactionPermissionUpdateEvent event = AlpineFactions.callEvent(new FactionPermissionUpdateEvent(
                resolvedFaction, sender, permission, relational, value));
        if (event.isCancelled()) {
            config.operationCancelled.send(sender);
            return;
        }

        boolean newValue = event.isValue();
        resolvedFaction.setPermission(permission, newValue, relational);

        FactionHelper.broadcast(resolvedFaction, sender, observer -> {
            return config.updatedPermissionValue.build(
                    "actor", FactionHelper.formatRelational(observer, resolvedFaction, sender),
                    "actor_name", PlayerHelper.getName(sender),

                    "permission_id", permission.getId(),
                    "permission_name", permission.getName(),
                    "permission_description", permission.getDescription(),
                    "relation", relational.getId(),
                    "state", newValue
            );
        });
    }

    @Execute(name = "show")
    public void show(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        PermissionRegistry registry = AlpineFactions.getInstance().getPermissionRegistry();

        Faction resolvedFaction = faction.orElse(Accessors.factions().findOrDefault(sender));
        if (!resolvedFaction.isPermitted(sender, Permissions.MODIFY_PERMS)) {
            FactionHelper.missingPermission(sender, resolvedFaction, "modify permissions");
            return;
        }

        List<Permission> permissions = registry.getAll();
        Component title = config.permissionStateListTitle.build(
                "faction", FactionHelper.formatRelational(sender, resolvedFaction, false),
                "faction_name", resolvedFaction.getName());
        Component compiledTitle = Components.joinNewLines(
                Formatting.appendTitlePadding(title),
                config.permissionRelationStateTitle.build()
        );

        Component elements = Formatting.elements(permissions, permission -> {
            if (permission == null) {
                return ComponentHelper.nil();
            }

            Component relationStates = config.permissionRelationState.build(
                    "enemy", (resolvedFaction.isPermitted(FactionRelation.ENEMY, permission) ? config.permissionYes : config.permissionNo).build(),
                    "neutral", (resolvedFaction.isPermitted(FactionRelation.ENEMY, permission) ? config.permissionYes : config.permissionNo).build(),
                    "truce", (resolvedFaction.isPermitted(FactionRelation.TRUCE, permission) ? config.permissionYes : config.permissionNo).build(),
                    "ally", (resolvedFaction.isPermitted(FactionRelation.ALLY, permission) ? config.permissionYes : config.permissionNo).build(),
                    "recruit", (resolvedFaction.isPermitted(Rank.RECRUIT, permission) ? config.permissionYes : config.permissionNo).build(),
                    "member", (resolvedFaction.isPermitted(Rank.MEMBER, permission) ? config.permissionYes : config.permissionNo).build(),
                    "officer", (resolvedFaction.isPermitted(Rank.OFFICER, permission) ? config.permissionYes : config.permissionNo).build(),
                    "coleader", (resolvedFaction.isPermitted(Rank.COLEADER, permission) ? config.permissionYes : config.permissionNo).build(),
                    "leader", (resolvedFaction.isPermitted(Rank.LEADER, permission) ? config.permissionYes : config.permissionNo).build()
            );

            return config.permissionStateListEntry.build(
                    "permission_id", permission.getId(),
                    "permission_name", permission.getName(),
                    "permission_description", permission.getDescription(),
                    "relation_states", relationStates
            );
        });

        Messaging.send(sender, Components.joinNewLines(compiledTitle, elements));
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page
    ) {
        MessageConfig config = MessageConfig.getInstance();
        PermissionRegistry registry = AlpineFactions.getInstance().getPermissionRegistry();

        List<Permission> flags = registry.getAll();
        String command = "/f permission list %page%";
        Component title = config.permissionListTitle.build();
        Component compiledPage = Formatting.page(title, flags, command, page.orElse(1), 10, flag -> {
            if (flag == null) {
                return ComponentHelper.nil();
            }

            return config.permissionListEntry.build(
                    "permission_id", flag.getId(),
                    "permission_name", flag.getName(),
                    "permission_description", flag.getDescription()
            );
        });

        Messaging.send(sender, compiledPage);
    }
}
