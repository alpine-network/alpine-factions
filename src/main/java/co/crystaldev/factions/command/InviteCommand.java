package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.MemberInvitation;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.FactionArgumentResolver;
import co.crystaldev.factions.command.argument.OfflinePlayerArgumentResolver;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.FactionStore;
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
            @Arg("player") @Key(OfflinePlayerArgumentResolver.KEY) OfflinePlayer invitee
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionStore store = FactionStore.getInstance();
        Faction faction = store.findFactionOrDefault(player);
        Faction inviteeFaction = store.findFactionOrDefault(invitee);

        if (!faction.isPermitted(player, Permissions.INVITE_MEMBERS)) {
            FactionHelper.missingPermission(player, faction, "invite members");
            return;
        }

        // invite the member
        boolean previouslyInvited = faction.isInvited(invitee.getUniqueId());
        faction.addInvitation(invitee.getUniqueId(), player.getUniqueId());

        // notify the faction
        Messaging.broadcast(faction, pl -> {
            if (previouslyInvited && !player.equals(pl)) {
                return null;
            }

            return config.invite.build(
                    "player", FactionHelper.formatRelational(pl, faction, player),
                    "player_name", player.getName(),

                    "invitee", FactionHelper.formatRelational(pl, inviteeFaction, invitee),
                    "invitee_name", invitee.getName()
            );
        });

        // notify the invitee
        Messaging.attemptSend(invitee, config.invited.build(
                "player", FactionHelper.formatRelational(invitee, faction, player),
                "player_name", player.getName(),

                "faction", FactionHelper.formatRelational(invitee, faction, false),
                "faction_name", faction.getName()
        ));
    }

    @Execute(name = "remove")
    public void remove(
            @Context Player player,
            @Arg("player") @Key(OfflinePlayerArgumentResolver.KEY) OfflinePlayer invitee
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionStore store = FactionStore.getInstance();
        Faction faction = store.findFactionOrDefault(player);
        Faction inviteeFaction = store.findFactionOrDefault(invitee);

        if (!faction.isPermitted(player, Permissions.INVITE_MEMBERS)) {
            FactionHelper.missingPermission(player, faction, "invite members");
            return;
        }

        // remove the invite
        faction.removeInvitation(invitee.getUniqueId());

        // notify the faction
        Messaging.broadcast(faction, pl -> {
            return config.inviteRevoke.build(
                    "player", FactionHelper.formatRelational(pl, faction, player),
                    "player_name", player.getName(),

                    "invitee", FactionHelper.formatRelational(pl, inviteeFaction, invitee),
                    "invitee_name", invitee.getName()
            );
        });
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") @Key(FactionArgumentResolver.KEY) Optional<Faction> faction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        FactionStore store = FactionStore.getInstance();
        Faction resolvedFaction = faction.orElse(store.findFactionOrDefault(sender));
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
                return Component.text("< null >");
            }

            OfflinePlayer invitee = Bukkit.getOfflinePlayer(invite.getPlayerId());
            OfflinePlayer inviter = Bukkit.getOfflinePlayer(invite.getInviterId());
            Faction inviteeFaction = store.findFactionOrDefault(invite.getPlayerId());
            Faction inviterFaction = store.findFactionOrDefault(invite.getInviterId());

            return config.inviteListEntry.build(
                    "player", FactionHelper.formatRelational(sender, inviteeFaction, invitee),
                    "player_name", invitee.getName(),

                    "inviter", FactionHelper.formatRelational(sender, inviterFaction, inviter),
                    "inviter_name", inviter.getName()
            );
        }));
    }
}
