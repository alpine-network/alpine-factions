package co.crystaldev.factions.api.show.component;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.RelationType;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.show.order.ShowOrder;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
@UtilityClass
public final class DefaultShowComponents {

    public static final ShowComponent DESCRIPTION = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> conf().showDesc.build("description", Optional.ofNullable(ctx.getFaction().getDescription()).orElse(Faction.DEFAULT_DESCRIPTION))
    );

    public static final ShowComponent ID = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> conf().showId.build("id", ctx.getFaction().getId()),
            ctx -> PlayerHandler.getInstance().isOverriding(ctx.getSender())
    );

    public static final ShowComponent CREATED = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> conf().showCreated.build("created", Formatting.formatMillis(ctx.getFaction().getCreatedAt())),
            ctx -> !ctx.getFaction().isMinimal()
    );

    public static final ShowComponent JOINING = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                CommandSender sender = ctx.getSender();
                Faction faction = ctx.getFaction();
                boolean canJoin = faction.getFlagValueOrDefault(FactionFlags.OPEN)
                        || sender instanceof OfflinePlayer && faction.isInvited(((OfflinePlayer) sender).getUniqueId());
                return config.showJoinState.build("joining", (canJoin ? config.joinable : config.notJoinable).build());
            },
            ctx -> !ctx.getFaction().isMinimal()
    );

    public static final ShowComponent LAND_AND_POWER = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                Faction faction = ctx.getFaction();
                boolean infinitePower = faction.hasInfinitePower();
                int land = faction.getClaimCount();

                return conf().showLandPower.build("land", faction.getClaimCount(),
                        "power", infinitePower ? land + 1 : faction.getPowerLevel(),
                        "max_power", infinitePower ? land + 1 : faction.getMaxPowerLevel());
            },
            ctx -> !ctx.getFaction().isMinimal()
    );

    public static final ShowComponent ALLIES = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Faction> factions = ctx.getFaction().getRelatedFactions(RelationType.ALLY);
                Component joined = factions.isEmpty()
                        ? config.none.build()
                        : Component.text(factions.stream().map(Faction::getName).collect(Collectors.joining(", ")));

                return config.showAllies.build("allies", joined, "ally_count", factions.size(), "max_allies", FactionConfig.getInstance().maxAlliances);
            },
            ctx -> !ctx.getFaction().isMinimal()
    );

    public static final ShowComponent TRUCES = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Faction> factions = ctx.getFaction().getRelatedFactions(RelationType.TRUCE);
                Component joined = factions.isEmpty()
                        ? config.none.build()
                        : Component.text(factions.stream().map(Faction::getName).collect(Collectors.joining(", ")));

                return config.showTruces.build("truces", joined, "truce_count", factions.size(), "max_truces", FactionConfig.getInstance().maxTruces);
            },
            ctx -> !ctx.getFaction().isMinimal()
    );

    public static final ShowComponent ONLINE_MEMBERS = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Component> members = ctx.getFaction().getOnlineMembers()
                        .stream()
                        .map(v -> FactionHelper.formatRelational(v.getOfflinePlayer(), ctx.getFaction(), v.getOfflinePlayer(), false))
                        .collect(Collectors.toList());
                Component joined = members.isEmpty() ? config.none.build() : ComponentHelper.joinCommas(members);
                Component count = (members.isEmpty() ? config.memberCountOffline : config.memberCountOnline).build(members.size());
                return config.showOnlineMembers.build("members", joined, "count", count);
            }
    );

    public static final ShowComponent OFFLINE_MEMBERS = ShowComponent.create(
            ShowOrder.inserting(),
            ctx -> {
                MessageConfig config = conf();
                Collection<Component> members = ctx.getFaction().getOfflineMembers()
                        .stream()
                        .map(v -> FactionHelper.formatRelational(v.getOfflinePlayer(), ctx.getFaction(), v.getOfflinePlayer(), false))
                        .collect(Collectors.toList());
                Component joined = members.isEmpty() ? config.none.build() : ComponentHelper.joinCommas(members);
                Component count = config.memberCountOffline.build(members.size());
                return config.showOfflineMembers.build("members", joined, "count", count);
            }
    );

    public static final ShowComponent[] VALUES = {
            ID, DESCRIPTION, CREATED, JOINING, LAND_AND_POWER,
            ALLIES, TRUCES, ONLINE_MEMBERS, OFFLINE_MEMBERS
    };

    @NotNull
    private static MessageConfig conf() {
        return MessageConfig.getInstance();
    }
}
