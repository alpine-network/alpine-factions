package co.crystaldev.factions.api.show.component;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.show.ShowContext;
import co.crystaldev.factions.api.show.order.ShowOrder;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class ShowComponents {

    public static final Predicate<ShowContext> MINIMAL_VISIBILITY_PREDICATE = ctx -> {
        return PlayerHandler.getInstance().isOverriding(ctx.getSender()) || !ctx.getFaction().isMinimal();
    };

    public static final Predicate<ShowContext> REQUIRES_OVERRIDE_PREDICATE = ctx -> {
        return PlayerHandler.getInstance().isOverriding(ctx.getSender());
    };

    public static final ShowComponent ID = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                return conf().showId.build("id", ctx.getFaction().getId())
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                        .clickEvent(ClickEvent.copyToClipboard(ctx.getFaction().getId()));
            },
            REQUIRES_OVERRIDE_PREDICATE
    );

    public static final ShowComponent DESCRIPTION = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> conf().showDesc.build("description", Optional.ofNullable(ctx.getFaction().getDescription()).orElse(Faction.DEFAULT_DESCRIPTION))
    );

    public static final ShowComponent CREATED = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> conf().showCreated.build("created", Formatting.formatMillis(ctx.getFaction().getCreatedAt())),
            MINIMAL_VISIBILITY_PREDICATE
    );

    public static final ShowComponent JOINING = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                CommandSender sender = ctx.getSender();
                Faction faction = ctx.getFaction();
                boolean canJoin = sender instanceof OfflinePlayer && faction.canJoin(((OfflinePlayer) sender).getUniqueId());
                return config.showJoinState.build("joining", (canJoin ? config.joinable : config.notJoinable).build());
            },
            MINIMAL_VISIBILITY_PREDICATE
    );

    public static final ShowComponent LAND_AND_POWER = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                Faction faction = ctx.getFaction();
                boolean infinite = faction.hasInfinitePower();
                int land = faction.getClaimCount();

                return conf().showLandPower.build("land", faction.getClaimCount(),
                        "power", infinite ? "∞" : faction.getPowerLevel(),
                        "max_power", infinite ? "∞" : faction.getMaxPowerLevel());
            },
            MINIMAL_VISIBILITY_PREDICATE
    );

    public static final ShowComponent ALLIES = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Faction> factions = ctx.getFaction().getRelatedFactions(FactionRelation.ALLY);
                Component joined = factions.isEmpty()
                        ? config.none.build()
                        : Component.text(factions.stream().map(Faction::getName).collect(Collectors.joining(", ")));

                return config.showAllies.build("allies", joined, "ally_count", factions.size(), "max_allies", FactionConfig.getInstance().maxAlliances);
            },
            MINIMAL_VISIBILITY_PREDICATE
    );

    public static final ShowComponent TRUCES = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Faction> factions = ctx.getFaction().getRelatedFactions(FactionRelation.TRUCE);
                Component joined = factions.isEmpty()
                        ? config.none.build()
                        : Component.text(factions.stream().map(Faction::getName).collect(Collectors.joining(", ")));

                return config.showTruces.build("truces", joined, "truce_count", factions.size(), "max_truces", FactionConfig.getInstance().maxTruces);
            },
            MINIMAL_VISIBILITY_PREDICATE
    );

    public static final ShowComponent ONLINE_MEMBERS = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Component> members = ctx.getFaction().getOnlineMembers()
                        .stream()
                        .sorted(Member.COMPARATOR)
                        .map(v -> FactionHelper.formatName(v.getOfflinePlayer()))
                        .collect(Collectors.toList());
                Component joined = members.isEmpty() ? config.none.build() : Components.joinCommas(members);
                Component count = (members.isEmpty() ? config.memberCountOffline : config.memberCountOnline).build(
                        "online_count", members.size(), "member_count", ctx.getFaction().getMemberCount());
                return config.showOnlineMembers.build("members", joined, "count", count);
            }
    );

    public static final ShowComponent OFFLINE_MEMBERS = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Component> members = ctx.getFaction().getOfflineMembers()
                        .stream()
                        .sorted(Member.COMPARATOR)
                        .map(v -> FactionHelper.formatName(v.getOfflinePlayer()))
                        .collect(Collectors.toList());
                Component joined = members.isEmpty() ? config.none.build() : Components.joinCommas(members);
                Component count = config.memberCountOffline.build("online_count", members.size(),
                        "member_count", ctx.getFaction().getMemberCount());
                return config.showOfflineMembers.build("members", joined, "count", count);
            },
            MINIMAL_VISIBILITY_PREDICATE
    );

    public static final ShowComponent[] VALUES = {
            ID, DESCRIPTION, CREATED, JOINING, LAND_AND_POWER,
            ALLIES, TRUCES, ONLINE_MEMBERS, OFFLINE_MEMBERS
    };

    private static @NotNull MessageConfig conf() {
        return MessageConfig.getInstance();
    }
}
