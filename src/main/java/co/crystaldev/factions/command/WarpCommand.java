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

    // TODO:
    //  - change way tabbing works
    //    - /f warp <tab> shows current warps and subcommands
    //    - Argument resolver won't let you tab other faction warps
    //  - add subcommands (e.g. setwarp, delwarp)

    @Execute
    public void teleport(
            @Context Player player,
            @Arg("warp") @Key(Args.WARP) String warp,
            @Arg("password") @Key(Args.ALPHANUMERIC) Optional<String> password
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.get().factions();
        Faction faction = factions.findOrDefault(player);
        Warp factionWarp = faction.getWarp(warp);

        // ensure player has access to warp
        if (!faction.isPermitted(player, Permissions.ACCESS_WARP)) {
            FactionHelper.missingPermission(player, faction, "access warp");
            return;
        }

        // if password is present, check for it
        if (factionWarp.hasPassword() && !(factionWarp.isPasswordCorrect(password)) && faction.getMember(player) != faction.getOwner()) {
            config.warpInvalidPassword.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName(),
                    "warp", warp);
            return;
        }

        Location warpLocation = factionWarp.getLocation();

        // ensure the faction has a warp (only called if target faction is defined)
        if (warpLocation == null) {
            config.noWarp.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName(),
                    "warp", warp);
            return;
        }

        // ensure warp is still in the faction's own territory
        if (!Factions.get().claims().getFactionOrDefault(warpLocation).equals(faction)) {
            FactionWarpUpdateEvent event = AlpineFactions.callEvent(new FactionWarpUpdateEvent(faction, player, warp, null, null));
            if (!event.isCancelled()) {
                // delete warp and notify
                faction.delWarp(warp);
                FactionHelper.broadcast(faction, config.unsetWarp.build(
                            "warp", warp));
                return;
            }
        }

        // teleport the player
        TeleportTask.builder(player, warpLocation)
                .delay(5, TimeUnit.SECONDS)
                .onApply(ctx -> {
                    ConfigText message = ctx.isInstant() ? config.warpInstant : config.warp;
                    ctx.message(message.build(
                            "faction", FactionHelper.formatRelational(player, faction, false),
                            "faction_name", faction.getName(),
                            "warp", warp,
                            "seconds", ctx.timeUntilTeleport(TimeUnit.SECONDS)));
                })
                .initiate(this.plugin);
    }

    @Execute(name = "add")
    @Shortcut({ "factions setwarp" })
    public void add(
            @Context Player player,
            @Arg("name") @Key(Args.ALPHANUMERIC) String name,
            @Arg("password") @Key(Args.ALPHANUMERIC) Optional<String> password
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);

        Location warpLocation = player.getLocation();
        Faction faction = Factions.get().claims().getFactionOrDefault(warpLocation);
        Faction selfFaction = Factions.get().factions().findOrDefault(player);

        // ensure claim is not wilderness and player's faction
        if (faction.isWilderness() || !faction.isMember(player.getUniqueId())) {
            config.outsideTerritory.send(player,
                    "faction", FactionHelper.formatRelational(player, selfFaction, false),
                    "faction_name", faction.getName());
            return;
        }

        // ensure player can modify warps
        if (!faction.isPermitted(player, Permissions.MODIFY_WARP)) {
            FactionHelper.missingPermission(player, faction, "modify warp");
            return;
        }

        // call warp update event with new warp
        FactionWarpUpdateEvent event = AlpineFactions.callEvent(new FactionWarpUpdateEvent(selfFaction, player, name, password.orElse(null), warpLocation));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // set the faction warp
        faction.addWarp(name, new Warp(warpLocation, password.orElse(null), System.currentTimeMillis()));

        Warp factionWarp = faction.getWarp(name);

        // broadcast set warp to faction
        FactionHelper.broadcast(faction, observer -> {
            Component location = factionWarp.hasPassword() && faction.getMember(observer) != faction.getOwner()
                    ? config.warpHidden.build() : config.warpLocation.build(
                    "world", warpLocation.getWorld().getName(),
                    "x", warpLocation.getBlockX(),
                    "y", warpLocation.getBlockY(),
                    "z", warpLocation.getBlockZ());

            return config.setWarp.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),
                    "warp", name,
                    "location", location);
        });
    }

    @Execute(name = "remove")
    @Shortcut({ "factions delwarp" })
    public void remove(
            @Context Player player,
            @Arg("name") @Key(Args.WARP) String warp,
            @Arg("password") @Key(Args.ALPHANUMERIC) Optional<String> password
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        Faction faction = Factions.get().factions().findOrDefault(player);
        Warp factionWarp = faction.getWarp(warp);
        Location warpLocation = factionWarp.getLocation();

        // ensure player can modify warps
        if (!faction.isPermitted(player, Permissions.MODIFY_WARP)) {
            FactionHelper.missingPermission(player, faction, "modify warp");
            return;
        }

        // if password is present, check for it
        if (factionWarp.hasPassword() && !(factionWarp.isPasswordCorrect(password)) && faction.getMember(player) != faction.getOwner()) {
            config.warpInvalidPassword.send(player,
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName(),
                    "warp", warp);
            return;
        }

        // call warp update event
        FactionWarpUpdateEvent event = AlpineFactions.callEvent(new FactionWarpUpdateEvent(faction, player, warp, null,null));
        if (event.isCancelled()) {
            config.operationCancelled.send(player);
            return;
        }

        // broadcast warp deletion to faction
        FactionHelper.broadcast(faction, observer -> {
            Component location = factionWarp.hasPassword() && faction.getMember(observer) != faction.getOwner()
                    ? config.warpHidden.build() : config.warpLocation.build(
                    "world", warpLocation.getWorld().getName(),
                    "x", warpLocation.getBlockX(),
                    "y", warpLocation.getBlockY(),
                    "z", warpLocation.getBlockZ());

            return config.delWarp.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),
                    "warp", warp,
                    "location", location);
        });

        // delete warp from faction
        faction.delWarp(warp);
    }

    @Execute(name = "list")
    @Shortcut({ "factions listwarps" })
    public void list(
            @Context Player player,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") Optional<Faction> targetFaction
    ) {
        MessageConfig config = this.plugin.getConfiguration(MessageConfig.class);
        FactionAccessor factions = Factions.get().factions();
        Faction faction = targetFaction.orElse(factions.findOrDefault(player));

        Component title = config.warpListTitle.build(
                "faction", FactionHelper.formatRelational(player, faction, false),
                "faction_name", faction.getName()
        );

        String command = "/f warp list %page% " + faction.getName();

        Messaging.send(player, Formatting.page(title, faction.getWarps(), command, page.orElse(1), 10, warp -> {
            Warp factionWarp = faction.getWarp(warp);
            Location warpLocation = factionWarp.getLocation();

            Component location = factionWarp.hasPassword() && faction.getMember(player) != faction.getOwner()
                    ? config.warpHidden.build() : config.warpLocation.build(
                    "world", warpLocation.getWorld().getName(),
                    "x", warpLocation.getBlockX(),
                    "y", warpLocation.getBlockY(),
                    "z", warpLocation.getBlockZ());

            return config.warpListEntry.build(
                    "warp", warp,
                    "status", factionWarp.hasPassword() ? config.warpHasPassword.build() : config.warpNoPassword.build(),
                    "location", location
            );
        }));
    }
}
