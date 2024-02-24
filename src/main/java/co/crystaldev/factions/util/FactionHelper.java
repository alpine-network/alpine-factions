package co.crystaldev.factions.util;

import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.StyleConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/05/2024
 */
@UtilityClass
public final class FactionHelper {

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull Component component) {
        if (faction == null) {
            return ComponentHelper.nil();
        }

        StyleConfig config = StyleConfig.getInstance();
        Faction self = findFactionOrDefault(viewer);

        FactionRelation relation = self.relationTo(faction);

        Component name = ComponentHelper.stylize(config.relationalStyles.get(relation), component);
        return ComponentHelper.stylize(config.factionNameStyles.get(faction.getName()), name, true);
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull String component) {
        return formatRelational(viewer, faction, Component.text(component));
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull ServerOperator other, boolean checkEquals) {
        if (faction == null) {
            return ComponentHelper.nil();
        }

        // if player is console, do not bother parsing
        if (other instanceof ConsoleCommandSender) {
            return Component.text("@console").color(NamedTextColor.YELLOW);
        }

        // if it was a non-player object, do not bother parsing
        if (!(other instanceof OfflinePlayer)) {
            return Component.text(other.toString());
        }

        OfflinePlayer player = (OfflinePlayer) other;

        // player is not a member of this faction, just colorize their name without a rank or title
        if (!faction.isWilderness() && !faction.isMember(player.getUniqueId())) {
            return formatRelational(viewer, faction, player.getName());
        }

        if (checkEquals && viewer.equals(player)) {
            // do not display full name, title, and rank for yourself

            return formatRelational(viewer, faction, "You");
        }
        else if (viewer instanceof OfflinePlayer && faction.isMember(((OfflinePlayer) viewer).getUniqueId())) {
            // both the viewer and player are in the same faction, display their title instead of their faction

            Member member = faction.getMember(player);
            return formatRelational(viewer, faction, ComponentHelper.join(
                    Component.text(member.getRank().getPrefix()),
                    member.getTitle(),
                    member.hasTitle() ? Component.space() : Component.empty(),
                    Component.text(player.getName())
            ));
        }
        else {
            // viewer and player are in different factions, display their faction name and rank only

            Member member = faction.getMember(player);
            Faction playerFaction = findFactionOrDefault(player);

            return formatRelational(viewer, faction, ComponentHelper.join(
                    Component.text(member.getRank().getPrefix()),
                    Component.text(playerFaction.getName()),
                    Component.space(),
                    Component.text(player.getName())
            ));
        }
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull ServerOperator other) {
        return formatRelational(viewer, faction, other, true);
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction) {
        Faction playerFaction = findFaction(viewer);
        String factionName = Objects.equals(faction, playerFaction) ? "Your faction" : (faction == null ? "< null >" : faction.getName());
        return formatRelational(viewer, faction, factionName);
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, boolean checkEquals) {
        if (checkEquals) {
            return formatRelational(viewer, faction);
        }

        String factionName = faction == null ? "< null >" : faction.getName();
        return formatRelational(viewer, faction, factionName);
    }

    @NotNull
    public static Component formatName(@NotNull ServerOperator operator) {
        if (!(operator instanceof OfflinePlayer)) {
            return Component.text(PlayerHelper.getName(operator));
        }

        OfflinePlayer player = (OfflinePlayer) operator;
        Faction faction = Accessors.factions().findOrDefault(operator);
        Member member = faction.getMember(player.getUniqueId());

        return ComponentHelper.join(
                Component.text(member.getRank().getPrefix()),
                member.getTitle(),
                member.hasTitle() ? Component.space() : Component.empty(),
                Component.text(player.getName())
        );
    }

    public static void missingPermission(@NotNull CommandSender player, @NotNull Faction faction, @NotNull String action) {
        MessageConfig.getInstance().missingFactionPerm.send(player,
                "action", action,
                "faction", formatRelational(player, faction),
                "faction_name", faction.getName()
        );
    }

    public static boolean isPermitted(@NotNull Player player, @NotNull Chunk chunk, @NotNull Permission permission, @NotNull String action) {
        ClaimAccessor claims = Accessors.claims();
        if (!claims.isClaimed(chunk)) {
            return true;
        }

        Claim claim = claims.getClaim(chunk);
        if (claim != null && !claim.isPermitted(player, permission)) {
            FactionHelper.missingPermission(player, claim.getFaction(), action);
            return false;
        }

        return true;
    }

    @Nullable
    private static Faction findFaction(@NotNull ServerOperator sender) {
        FactionAccessor factions = Accessors.factions();
        return sender instanceof OfflinePlayer ? factions.find((OfflinePlayer) sender) : factions.getWilderness();
    }

    @NotNull
    private static Faction findFactionOrDefault(@NotNull ServerOperator sender) {
        FactionAccessor factions = Accessors.factions();
        return sender instanceof OfflinePlayer ? factions.findOrDefault((OfflinePlayer) sender) : factions.getWilderness();
    }
}
