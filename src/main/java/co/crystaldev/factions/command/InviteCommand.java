package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.MemberInvitation;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/12/2024
 */
@Command(name = "factions invite", aliases = "factions inv")
@Description("Manage faction membership invitations.")
public final class InviteCommand extends FactionsCommand {
    public InviteCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "add")
    public void add(
            @Context Player player,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer invitee
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = factions.findOrDefault(player);
        Faction inviteeFaction = factions.findOrDefault(invitee);

        if (!faction.isPermitted(player, Permissions.INVITE_MEMBERS)) {
            FactionHelper.missingPermission(player, faction, "invite members");
            return;
        }

        // you can't invite an existing member!
        if (faction.isMember(invitee.getUniqueId())) {
            config.inviteFail.send(player,
                    "player", FactionHelper.formatRelational(player, faction, invitee),
                    "player_name", invitee.getName(),
                    "faction", FactionHelper.formatRelational(player, faction),
                    "faction_name", faction.getName());
            return;
        }

        // invite the member
        boolean previouslyInvited = faction.isInvited(invitee.getUniqueId());
        faction.addInvitation(invitee.getUniqueId(), player.getUniqueId());

        // notify the faction
        Messaging.broadcast(faction, observer -> {
            if (previouslyInvited && !player.equals(observer)) {
                // if the member was already invited, do not send broadcast to the
                // rest of the faction, only to the sender
                return null;
            }

            return config.invite.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),

                    "invitee", FactionHelper.formatRelational(observer, inviteeFaction, invitee),
                    "invitee_name", invitee.getName()
            );
        });

        // notify the invitee
        Messaging.attemptSend(invitee, config.invited.build(
                "actor", FactionHelper.formatRelational(invitee, faction, player),
                "actor_name", player.getName(),

                "faction", FactionHelper.formatRelational(invitee, faction, false),
                "faction_name", faction.getName()
        ));
    }

    @Execute(name = "remove")
    public void remove(
            @Context Player player,
            @Arg("player") @Key(Args.OFFLINE_PLAYER) OfflinePlayer invitee
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction faction = factions.findOrDefault(player);
        Faction inviteeFaction = factions.findOrDefault(invitee);

        if (!faction.isPermitted(player, Permissions.INVITE_MEMBERS)) {
            FactionHelper.missingPermission(player, faction, "invite members");
            return;
        }

        // remove the invite
        faction.removeInvitation(invitee.getUniqueId());

        // notify the faction
        Messaging.broadcast(faction, observer -> {
            return config.inviteRevoke.build(
                    "actor", FactionHelper.formatRelational(observer, faction, player),
                    "actor_name", player.getName(),

                    "invitee", FactionHelper.formatRelational(observer, inviteeFaction, invitee),
                    "invitee_name", invitee.getName()
            );
        });
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionAccessor factions = Accessors.factions();
        Faction resolvedFaction = faction.orElse(factions.findOrDefault(sender));
        Set<MemberInvitation> invitations = resolvedFaction.getInvitations();

        if (!resolvedFaction.isPermitted(sender, Permissions.INVITE_MEMBERS)) {
            FactionHelper.missingPermission(sender, resolvedFaction, "invite members");
            return;
        }

        Component title = config.inviteListTitle.build(
                "faction", FactionHelper.formatRelational(sender, resolvedFaction, false),
                "faction_name", resolvedFaction.getName()
        );
        String command = "/f invite list %page% " + resolvedFaction.getName();
        Messaging.send(sender, Formatting.page(title, invitations, command, page.orElse(1), 10, invite -> {
            if (invite == null) {
                return ComponentHelper.nil();
            }

            OfflinePlayer invitee = Bukkit.getOfflinePlayer(invite.getPlayerId());
            OfflinePlayer inviter = Bukkit.getOfflinePlayer(invite.getInviterId());
            Faction inviteeFaction = factions.findOrDefault(invite.getPlayerId());
            Faction inviterFaction = factions.findOrDefault(invite.getInviterId());

            return config.inviteListEntry.build(
                    "player", FactionHelper.formatRelational(sender, inviteeFaction, invitee),
                    "player_name", invitee.getName(),

                    "inviter", FactionHelper.formatRelational(sender, inviterFaction, inviter),
                    "inviter_name", inviter.getName()
            );
        }));
    }
}
