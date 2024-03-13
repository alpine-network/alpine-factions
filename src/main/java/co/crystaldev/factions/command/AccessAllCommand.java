package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.ChunkAccessUpdateEvent;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.ClaimedChunk;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.FactionHelper;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/10/2024
 */
@Command(name = "factions accessall")
@Description("Manage chunk access.")
public final class AccessAllCommand extends FactionsCommand {
    public AccessAllCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "player", aliases = "p")
    public void player(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.OFFLINE_PLAYER) OfflinePlayer other,
            @Arg("access") boolean access,
            @Arg("target_faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        FactionAccessor factions = Accessors.factions();
        Faction target = targetFaction.orElse(factions.findOrDefault(sender));
        if (target.isWilderness()) {
            FactionHelper.missingPermission(sender, target, "grant access");
            return;
        }

        Faction otherFaction = factions.findOrDefault(other);
        setAccess(sender, other, access, target, FactionHelper.formatRelational(sender, otherFaction, other, false),
                Component.text(other.getName()));
    }

    @Execute(name = "faction", aliases = "f")
    public void faction(
            @Context CommandSender sender,
            @Arg("faction") @Key(Args.FACTION) Faction faction,
            @Arg("access") boolean access,
            @Arg("target_faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        Faction target = targetFaction.orElse(Accessors.factions().findOrDefault(sender));
        if (target.isWilderness()) {
            FactionHelper.missingPermission(sender, target, "grant access");
            return;
        }

        setAccess(sender, faction, access, target, FactionHelper.formatRelational(sender, faction, false),
                Component.text(faction.getName()));
    }

    private static void setAccess(@NotNull CommandSender sender, @NotNull Object subject, boolean access,
                                  @NotNull Faction targetFaction, @NotNull Component formattedSubject,
                                  @NotNull Component subjectName) {
        MessageConfig config = MessageConfig.getInstance();
        ClaimAccessor claims = Accessors.claims();

        // ensure the player has permission for the faction
        if (!targetFaction.isPermitted(sender, Permissions.MODIFY_ACCESS)) {
            FactionHelper.missingPermission(sender, targetFaction, "grant access");
            return;
        }

        List<ClaimedChunk> chunks = claims.getClaims(targetFaction);

        ChunkAccessUpdateEvent event = AlpineFactions.callEvent(new ChunkAccessUpdateEvent(targetFaction, sender, chunks, subject));
        if (event.isCancelled() || chunks.isEmpty()) {
            config.operationCancelled.send(sender);
            return;
        }

        if (chunks.size() == 1) {
            // handle a single chunk

            // set access
            ClaimedChunk chunk = chunks.get(0);
            setAccess(chunk.getClaim(), subject, access);
            claims.save(chunk.getWorld(), chunk.getChunkX(), chunk.getChunkZ());

            // notify
            ConfigText message = access ? config.accessGrantedSingle : config.accessRevokedSingle;
            message.send(sender,
                    "subject", formattedSubject,
                    "subject_name", subjectName);
        }
        else {
            // handle multiple chunks

            // set access
            String owningFaction = targetFaction.getId();
            for (ClaimedChunk chunk : chunks) {
                Claim claim = chunk.getClaim();
                if (claim == null || !Objects.equals(claim.getFactionId(), owningFaction)) {
                    continue;
                }

                // set access
                setAccess(claim, subject, access);
                claims.save(chunk.getWorld(), chunk.getChunkX(), chunk.getChunkZ());
            }

            // notify
            ConfigText message = access ? config.accessGrantedAll : config.accessRevokedAll;
            message.send(sender,
                    "subject", formattedSubject,
                    "subject_name", subjectName,
                    "amount", chunks.size());
        }
    }

    private static void setAccess(@NotNull Claim claim, @NotNull Object subject, boolean access) {
        if (subject instanceof Faction) {
            claim.setAccess((Faction) subject, access);
        }
        else if (subject instanceof OfflinePlayer) {
            claim.setAccess((OfflinePlayer) subject, access);
        }
    }
}
