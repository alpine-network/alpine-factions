package co.crystaldev.factions.util;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.RelationType;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.StyleConfig;
import co.crystaldev.factions.store.FactionStore;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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

    public static boolean equals(@Nullable Faction a, @Nullable Faction b) {
        return (a == b) || (a != null && a.equals(b)) || (b != null && b.equals(a));
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull Component component) {
        if (faction == null) {
            return Component.text("< null >");
        }

        StyleConfig config = StyleConfig.getInstance();
        Faction self = findFactionOrDefault(viewer);

        RelationType relationType = self.relationTo(faction);

        Component name = ComponentHelper.stylize(config.relationalStyles.get(relationType), component);
        return ComponentHelper.stylize(config.factionNameStyles.get(faction.getName()), name, true);
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull String component) {
        return formatRelational(viewer, faction, Component.text(component));
    }

    @NotNull
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull OfflinePlayer player, boolean checkEquals) {
        if (faction == null) {
            return Component.text("< null >");
        }

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
    public static Component formatRelational(@NotNull ServerOperator viewer, @Nullable Faction faction, @NotNull OfflinePlayer player) {
        return formatRelational(viewer, faction, player, true);
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

    public static void missingPermission(@NotNull CommandSender player, @NotNull Faction faction, @NotNull String action) {
        MessageConfig.getInstance().missingFactionPerm.send(player,
                "action", action,
                "faction", FactionHelper.formatRelational(player, faction),
                "faction_name", faction.getName()
        );
    }

    @Nullable
    private static Faction findFaction(@NotNull ServerOperator sender) {
        FactionStore store = FactionStore.getInstance();
        return sender instanceof OfflinePlayer ? store.findFaction((OfflinePlayer) sender) : store.getWilderness();
    }

    @NotNull
    private static Faction findFactionOrDefault(@NotNull ServerOperator sender) {
        FactionStore store = FactionStore.getInstance();
        return sender instanceof OfflinePlayer ? store.findFactionOrDefault((OfflinePlayer) sender) : store.getWilderness();
    }
}
