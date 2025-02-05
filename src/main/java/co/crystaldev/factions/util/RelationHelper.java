package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.config.StyleConfig;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.3.0
 */
@UtilityClass
public final class RelationHelper {

    private static final Component NULL_FACTION_COMPONENT = Component.text("< null faction >");
    private static final Component NULL_PLAYER_COMPONENT = Component.text("< null participator >");
    private static final Component FACTION_COMPONENT = Component.text("your faction");
    private static final Component PLAYER_COMPONENT = Component.text("you");
    private static final Component OPERATOR_COMPONENT = Component.text("@operator").color(NamedTextColor.YELLOW);

    // region Player

    /**
     * (BestBearr, BestBearr) -> <green>you
     * (BestBearr, xPenguinx) -> <red>**Karma xPenguinx
     */
    public static @NotNull Component formatPlayerName(
            @NotNull ServerOperator observer,
            @NotNull ServerOperator player
    ) {
        if (observer.equals(player)) {
            return applyStyle(PLAYER_COMPONENT, null, FactionRelation.SELF);
        }
        else {
            return formatLiteralPlayerName(observer, player);
        }
    }

    /**
     * (BestBearr, BestBearr) -> <green>***[Leader] BestBearr
     * (BestBearr, xPenguinx) -> <red>**Karma xPenguinx
     */
    public static @NotNull Component formatLiteralPlayerName(
            @NotNull ServerOperator observer,
            @NotNull ServerOperator player
    ) {
        Faction observerFaction = getFaction(observer);
        Faction faction = getFaction(player);
        Component rankPrefix = Component.text(getRank(player, faction).getPrefix());
        Component playerName = getPlayerName(player);

        FactionRelation relation = observerFaction.relationTo(faction);
        if (relation == FactionRelation.SELF) {
            Component title = getTitle(player, faction);
            Component prefix = title == null ? rankPrefix : Components.join(rankPrefix, title, Component.space());

            return applyStyle(Components.join(prefix, playerName), faction, relation);
        }
        else {
            Component prefix = Components.join(rankPrefix, getFactionName(faction));
            return applyStyle(Components.join(prefix, Component.space(), playerName), faction, relation);
        }
    }

    /**
     * (BestBearr) -> ***[Leader] BestBearr
     */
    public static @NotNull Component formatPlayerName(@NotNull ServerOperator observer) {
        Faction faction = getFaction(observer);
        Component playerName = getPlayerName(observer);

        Component title = getTitle(observer, faction);
        Component rankPrefix = Component.text(getRank(observer, faction).getPrefix());
        Component prefix = title == null ? rankPrefix : Components.join(rankPrefix, title, Component.space());

        return Components.join(prefix, playerName);
    }

    /**
     * (BestBearr, BestBearr) -> <green>BestBearr
     * (BestBearr, xPenguinx) -> <red>xPenguinx
     */
    public static @NotNull Component formatMinimalPlayerName(
            @NotNull ServerOperator observer,
            @NotNull ServerOperator player
    ) {
        Faction observerFaction = getFaction(observer);
        Faction faction = getFaction(player);
        FactionRelation relation = observerFaction.relationTo(faction);
        return applyStyle(getPlayerName(player), faction, relation);
    }

    public static @NotNull Component getPlayerName(@Nullable ServerOperator player) {
        if (player instanceof OfflinePlayer) {
            return Component.text(((OfflinePlayer) player).getName());
        }
        else if (player instanceof ConsoleCommandSender) {
            return OPERATOR_COMPONENT;
        }
        else {
            return NULL_PLAYER_COMPONENT;
        }
    }

    // endregion Player



    // region Faction

    /**
     * (Tide, Tide) -> <green>your faction
     * (Tide, Karma) -> <red>Karma
     */
    public static @NotNull Component formatFactionName(
            @NotNull Faction observer,
            @NotNull Object factionOrPlayer
    ) {
        Faction other = getFaction(factionOrPlayer);
        FactionRelation relation = observer.relationTo(other);
        Component name = relation == FactionRelation.SELF && !observer.isWilderness()
                ? FACTION_COMPONENT : getFactionName(other);
        return applyStyle(name, other, relation);
    }

    /**
     * (Tide, Tide) -> <green>Tide
     * (Tide, Karma) -> <red>Karma
     */
    public static @NotNull Component formatLiteralFactionName(
            @NotNull Faction observer,
            @NotNull Object factionOrPlayer
    ) {
        Faction other = getFaction(factionOrPlayer);
        FactionRelation relation = observer.relationTo(other);
        return applyStyle(getFactionName(other), other, relation);
    }

    /**
     * (BestBearr, Tide) -> <green>your faction
     * (BestBearr, Karma) -> <red>Karma
     */
    public static @NotNull Component formatFactionName(
            @NotNull ServerOperator observer,
            @NotNull Object factionOrPlayer
    ) {
        return formatFactionName(getFaction(observer), factionOrPlayer);
    }

    /**
     * (BestBearr, Tide) -> <green>Tide
     * (BestBearr, Karma) -> <red>Karma
     */
    public static @NotNull Component formatLiteralFactionName(
            @NotNull ServerOperator observer,
            @NotNull Object factionOrPlayer
    ) {
        return formatLiteralFactionName(getFaction(observer), factionOrPlayer);
    }

    public static @NotNull Component getFactionName(@Nullable Object factionOrPlayer) {

        if (factionOrPlayer == null) {
            return NULL_FACTION_COMPONENT;
        }

        Faction faction = getFaction(factionOrPlayer);

        StyleConfig config = AlpineFactions.getInstance().getConfiguration(StyleConfig.class);
        String style = config.factionNameStyles.get(faction.getName());
        if (style != null) {
            return Components.stylize(style, Component.text(faction.getName()));
        }
        else {
            return Component.text(faction.getName());
        }
    }

    public static @NotNull Component formatComponent(
            @NotNull Faction observer,
            @NotNull Object factionOrPlayer,
            @NotNull Component component
    ) {
        Faction observerFaction = getFaction(observer);
        Faction otherFaction = getFaction(factionOrPlayer);
        FactionRelation relation = observerFaction.relationTo(otherFaction);
        return applyStyle(component, otherFaction, relation);
    }

    public static @NotNull Component formatComponent(
            @NotNull Faction observer,
            @NotNull Object factionOrPlayer,
            @NotNull String component
    ) {
        return formatComponent(observer, factionOrPlayer, Component.text(component));
    }

    public static @NotNull Component formatComponent(
            @NotNull ServerOperator observer,
            @NotNull Object factionOrPlayer,
            @NotNull Component component
    ) {
        Faction observerFaction = getFaction(observer);
        Faction otherFaction = getFaction(factionOrPlayer);
        FactionRelation relation = observerFaction.relationTo(otherFaction);
        return applyStyle(component, otherFaction, relation);
    }

    public static @NotNull Component formatComponent(
            @NotNull ServerOperator observer,
            @NotNull Object factionOrPlayer,
            @NotNull String component
    ) {
        return formatComponent(observer, factionOrPlayer, Component.text(component));
    }

    // endregion Faction

    private static @NotNull Rank getRank(@NotNull ServerOperator player, @NotNull Faction faction) {
        if (!(player instanceof OfflinePlayer) || faction.isWilderness()) {
            return Rank.getDefault();
        }
        else {
            return faction.getMemberRankOrDefault(((OfflinePlayer) player).getUniqueId());
        }
    }

    private static @Nullable Component getTitle(@NotNull ServerOperator player, @NotNull Faction faction) {
        if (!(player instanceof OfflinePlayer) || faction.isWilderness()) {
            return null;
        }
        else {
            Member member = faction.getMember((OfflinePlayer) player);
            return member.hasTitle() ? member.getTitle() : null;
        }
    }

    private static @NotNull Component applyStyle(@NotNull Component value, @Nullable Faction faction, @NotNull FactionRelation relation) {
        StyleConfig config = AlpineFactions.getInstance().getConfiguration(StyleConfig.class);
        String nameStyle = config.factionNameStyles.get(faction == null ? "" : faction.getName());
        if (nameStyle != null) {
            return Components.stylize(nameStyle, value);
        }
        else {
            String style = config.relationalStyles.get(relation);
            return Components.stylize(style, value);
        }
    }

    private static @NotNull Faction getFaction(@Nullable Object relational) {
        Factions factions = Factions.get();
        if (relational == null) {
            return factions.factions().getWilderness();
        }
        else if (relational instanceof Faction) {
            return (Faction) relational;
        }
        else if (relational instanceof OfflinePlayer) {
            return factions.factions().findOrDefault((OfflinePlayer) relational);
        }
        else if (relational instanceof String) {
            return factions.factions().getByIdOrDefault((String) relational);
        }
        else {
            return factions.factions().getWilderness();
        }
    }
}
