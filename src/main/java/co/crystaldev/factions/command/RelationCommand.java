package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.RelatedFaction;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.Args;
import co.crystaldev.factions.command.framework.FactionsCommand;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.config.StyleConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.store.FactionStore;
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
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/20/2024
 */
@Command(name = "factions relation")
@Description("Manage faction relations.")
public final class RelationCommand extends FactionsCommand {
    public RelationCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute(name = "set")
    public void set(
            @Context CommandSender sender,
            @Arg("target_faction") @Key(Args.FACTION) Faction target,
            @Arg("relation") @Key(Args.FACTION_RELATION) FactionRelation relation,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> faction
    ) {
        setRelation(sender, target, faction.orElse(FactionStore.getInstance().findFactionOrDefault(sender)), relation);
    }

    @Execute(name = "list")
    public void list(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        Faction target = targetFaction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));

        if (!target.isPermitted(sender, Permissions.MODIFY_RELATIONS)) {
            FactionHelper.missingPermission(sender, target, "manage relations");
            return;
        }

        String command = "/f relation list %page% " + target.getName();
        Component compiledPage = Formatting.page(config.relationListTitle.build(), target.getRelatedFactions(),
                command, page.orElse(1), 10, entry -> relatedFactionToEntry(sender, entry));
        Messaging.send(sender, compiledPage);
    }

    @Execute(name = "wishes")
    public void wishes(
            @Context CommandSender sender,
            @Arg("page") Optional<Integer> page,
            @Arg("faction") @Key(Args.FACTION) Optional<Faction> targetFaction
    ) {
        MessageConfig config = MessageConfig.getInstance();
        Faction target = targetFaction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));

        if (!target.isPermitted(sender, Permissions.MODIFY_RELATIONS)) {
            FactionHelper.missingPermission(sender, target, "manage relations");
            return;
        }

        String command = "/f relation list %page% " + target.getName();
        Component compiledPage = Formatting.page(config.relationWishListTitle.build(), target.getRelationWishes(),
                command, page.orElse(1), 10, entry -> relatedFactionToEntry(sender, entry));
        Messaging.send(sender, compiledPage);
    }

    @NotNull
    private static Component relatedFactionToEntry(@NotNull CommandSender sender, @Nullable RelatedFaction entry) {
        if (entry == null) {
            return ComponentHelper.nil();
        }

        Faction faction = entry.getFaction();
        FactionRelation relation = entry.getRelation();

        return MessageConfig.getInstance().relationListEntry.build(
                "faction", FactionHelper.formatRelational(sender, faction, false),
                "faction_name", faction.getName(),
                "relation", ComponentHelper.stylize(StyleConfig.getInstance().relationalStyles.get(relation), Component.text(relation.name().toLowerCase())),
                "relation_name", relation.name().toLowerCase()
        );
    }

    private static void setRelation(@NotNull CommandSender sender, @NotNull Faction targetFaction,
                                    @NotNull Faction actingFaction, @NotNull FactionRelation relation) {
        MessageConfig config = MessageConfig.getInstance();

        if (!actingFaction.isPermitted(sender, Permissions.MODIFY_RELATIONS)) {
            FactionHelper.missingPermission(sender, actingFaction, "manage relations");
            return;
        }

        if (actingFaction.equals(targetFaction)) {
            config.relationSelf.send(sender,
                    "faction", FactionHelper.formatRelational(sender, targetFaction, false),
                    "faction_name", targetFaction.getName());
            return;
        }

        if (actingFaction.isRelation(targetFaction, relation)) {
            config.alreadyRelation.send(sender,
                    "faction", FactionHelper.formatRelational(sender, targetFaction, false),
                    "faction_name", targetFaction.getName());
            return;
        }

        // set the relation wish internally
        actingFaction.setRelation(targetFaction, relation);

        // if override mode is enabled, force set this relation
        if (PlayerHandler.getInstance().isOverriding(sender)) {
            targetFaction.setRelation(actingFaction, relation);
        }

        // notify the target faction
        boolean enemy = relation == FactionRelation.ENEMY;
        boolean wish = !targetFaction.isRelation(actingFaction, relation) && !enemy;
        ConfigText targetMessage = (wish ? config.relationWishes : config.relationDeclarations).get(relation);
        Messaging.broadcast(targetFaction, observer -> {
            return targetMessage.build(
                    "faction", FactionHelper.formatRelational(observer, actingFaction, false),
                    "faction_name", actingFaction.getName()
            );
        });

        // notify the declaring faction
        ConfigText actingMessage = (wish ? config.relationRequest : config.relationDeclarations).get(relation);
        Messaging.broadcast(actingFaction, sender, observer -> {
            return actingMessage.build(
                    "faction", FactionHelper.formatRelational(observer, targetFaction, false),
                    "faction_name", targetFaction.getName()
            );
        });
    }

    @Command(name = "factions neutral")
    @Description("Declare neutrality between factions.")
    public static final class Neutral extends FactionsCommand {
        public Neutral(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") @Key(Args.FACTION) Faction targetFaction,
                @Arg("faction") @Key(Args.FACTION) Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.NEUTRAL);
        }
    }

    @Command(name = "factions enemy")
    @Description("Declare a faction as an enemy.")
    public static final class Enemy extends FactionsCommand {
        public Enemy(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") @Key(Args.FACTION) Faction targetFaction,
                @Arg("faction") @Key(Args.FACTION) Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.ENEMY);
        }
    }

    @Command(name = "factions ally")
    @Description("Declare an alliance between factions.")
    public static final class Ally extends FactionsCommand {
        public Ally(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") @Key(Args.FACTION) Faction targetFaction,
                @Arg("faction") @Key(Args.FACTION) Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.ALLY);
        }
    }

    @Command(name = "factions truce")
    @Description("Declare a truce between factions.")
    public static final class Truce extends FactionsCommand {
        public Truce(AlpinePlugin plugin) {
            super(plugin);
        }

        @Execute
        public void execute(
                @Context CommandSender sender,
                @Arg("target_faction") @Key(Args.FACTION) Faction targetFaction,
                @Arg("faction") @Key(Args.FACTION) Optional<Faction> actingFaction
        ) {
            Faction acting = actingFaction.orElse(FactionStore.getInstance().findFactionOrDefault(sender));
            setRelation(sender, targetFaction, acting, FactionRelation.TRUCE);
        }
    }
}
