package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.framework.teleport.TeleportTask;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.FactionWarpUpdateEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.Warp;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import co.crystaldev.factions.util.PermissionHelper;
import co.crystaldev.factions.util.RelationHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.shortcut.Shortcut;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @since 0.1.0
 */
@Command(name = "factions warp")
@Description("Warp to the faction warp.")
final class WarpCommand extends AlpineCommand {
    public WarpCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void teleport(
            @Context Player player,
            @Arg("warp") Warp warp,
            @Arg("password") @Key(Args.ALPHANUMERIC) Optional<String> password
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = factions.findOrDefault(player);

        // ensure player has access to warp
        boolean permitted = PermissionHelper.checkPermissionAndNotify(player,
                Permissions.ACCESS_WARP, "access warps");
        if (!permitted) {
            return;
        }

        // if password is present, check for it
        if (warp.hasPassword() && !(warp.isPasswordCorrect(password.orElse(null))) && faction.getMember(player) != faction.getOwner()) {
            config.warpInvalidPassword.send(player,
                    "faction", RelationHelper.formatFactionName(player, faction),
                    "faction_name", faction.getName(),
                    "warp", warp);
            return;
        }

        Location warpLocation = warp.getLocation();

        // ensure the faction has a warp (only called if target faction is defined)
        if (warpLocation == null) {
            config.noWarp.send(player,
                    "faction", RelationHelper.formatFactionName(player, faction),
                    "faction_name", faction.getName(),
                    "warp", warp);
            return;
        }

        // ensure warp is still in the faction's own territory
        if (!Factions.claims().getFactionOrDefault(warpLocation).equals(faction)) {
            FactionWarpUpdateEvent event = AlpineFactions.callEvent(new FactionWarpUpdateEvent(faction, player, warp, null));
            if (!event.isCancelled()) {
                // delete warp and notify
                faction.delWarp(warp);
                faction.audience().sendMessage(config.unsetWarp.build(
                        "warp", warp.getName()));
                return;
            }
        }

        // teleport the player
        TeleportTask.builder(player, warpLocation)
                .delay(5, TimeUnit.SECONDS)
                .onApply(ctx -> {
                    ConfigText message = ctx.isInstant() ? config.warpInstant : config.warp;
                    ctx.message(message.build(
                            "faction", RelationHelper.formatLiteralFactionName(player, faction),
                            "faction_name", faction.getName(),
                            "warp", warp.getName(),
                            "seconds", ctx.timeUntilTeleport(TimeUnit.SECONDS)));
                })
                .initiate(this.plugin);
    }

    @Execute(name = "add")
    @Shortcut({ "factions setwarp" })
    public void add(
            @Context Player player,
            @Arg("name") @Key(Args.ALPHANUMERIC) String warpName,
            @Arg("password") @Key(Args.ALPHANUMERIC) Optional<String> password
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Location location = player.getLocation();
        Faction faction = Factions.claims().getFactionOrDefault(location);
        Faction selfFaction = Factions.registry().findOrDefault(player);

        // ensure claim is not wilderness and player's faction
        if (faction.isWilderness() || !faction.isMember(player.getUniqueId())) {
            config.outsideTerritory.send(player,
                    "faction", RelationHelper.formatLiteralFactionName(player, selfFaction),
                    "faction_name", faction.getName());
            return;
        }

        // ensure player can modify warps
        boolean permitted = PermissionHelper.checkPermissionAndNotify(player,
                Permissions.MODIFY_WARP, "modify warps");
        if (!permitted) {
            return;
        }

        // call warp update event with new warp
        Warp oldWarp = faction.getWarpByName(warpName);
        Warp warp = Warp.of(warpName, location, password.orElse(null));

        // oldWarp is null, if non-existent
        FactionWarpUpdateEvent event = AlpineFactions.callEvent(new FactionWarpUpdateEvent(selfFaction, player, oldWarp, warp));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // set the faction warp
        faction.addWarp(warp);

        // broadcast set warp to faction
        FactionHelper.broadcast(faction, observer -> {
            Component locComponent = warp.hasPassword() && faction.getMember(observer) != faction.getOwner()
                    ? config.warpHidden.build() : config.warpLocation.build(
                    "world", location.getWorld().getName(),
                    "x", location.getBlockX(),
                    "y", location.getBlockY(),
                    "z", location.getBlockZ());

            return config.setWarp.build(
                    "actor", RelationHelper.formatPlayerName(observer, player),
                    "actor_name", player.getName(),
                    "warp", warpName,
                    "location", locComponent);
        });
    }

    @Execute(name = "remove")
    @Shortcut({ "factions delwarp" })
    public void remove(
            @Context Player player,
            @Arg("warp") Warp warp,
            @Arg("password") @Key(Args.ALPHANUMERIC) Optional<String> password
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.registry().findOrDefault(player);
        Location warpLocation = warp.getLocation();

        // ensure player can modify warps
        boolean permitted = PermissionHelper.checkPermissionAndNotify(player, faction,
                Permissions.MODIFY_WARP, "modify warps");
        if (!permitted) {
            return;
        }

        // if password is present, check for it
        if (warp.hasPassword() && !(warp.isPasswordCorrect(password.orElse(null))) && faction.getMember(player) != faction.getOwner()) {
            config.warpInvalidPassword.send(player,
                    "faction", RelationHelper.formatFactionName(player, faction),
                    "faction_name", faction.getName(),
                    "warp", warp);
            return;
        }

        // call warp update event
        FactionWarpUpdateEvent event = AlpineFactions.callEvent(new FactionWarpUpdateEvent(faction, player, warp, null));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // broadcast warp deletion to faction
        FactionHelper.broadcast(faction, observer -> {
            Component location = warp.hasPassword() && faction.getMember(observer) != faction.getOwner()
                    ? config.warpHidden.build() : config.warpLocation.build(
                    "world", warpLocation.getWorld().getName(),
                    "x", warpLocation.getBlockX(),
                    "y", warpLocation.getBlockY(),
                    "z", warpLocation.getBlockZ());

            return config.delWarp.build(
                    "actor", RelationHelper.formatPlayerName(observer, player),
                    "actor_name", player.getName(),
                    "warp", warp.getName(),
                    "location", location);
        });

        // delete warp from faction
        faction.delWarp(warp);
    }

    @Execute(name = "list")
    @Shortcut({ "factions listwarps", "factions warps" })
    public void list(
            @Context Player player,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.registry();
        Faction faction = targetFaction.orElse(factions.findOrDefault(player));

        // ensure player has access to warps
        boolean permitted = PermissionHelper.checkPermissionAndNotify(player, faction,
                Permissions.ACCESS_WARP, "access warps");
        if (!permitted) {
            return;
        }

        Component title = config.warpListTitle.build(
                "faction", RelationHelper.formatLiteralFactionName(player, faction),
                "faction_name", faction.getName()
        );

        String command = "/f warp list %page% " + faction.getName();

        Messaging.send(player, Formatting.page(title, faction.getWarps(), command, page.orElse(1), 10, warp -> {
            Location warpLocation = warp.getLocation();

            Component location = warp.hasPassword() && faction.getMember(player) != faction.getOwner()
                    ? config.warpHidden.build() : config.warpLocation.build(
                    "world", warpLocation.getWorld().getName(),
                    "x", warpLocation.getBlockX(),
                    "y", warpLocation.getBlockY(),
                    "z", warpLocation.getBlockZ());

            return config.warpListEntry.build(
                    "warp", warp.getName(),
                    "status", warp.hasPassword() ? config.warpHasPassword.build() : config.warpNoPassword.build(),
                    "location", location
            );
        }));
    }
}
