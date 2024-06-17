package co.crystaldev.factions.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.StyleConfig;
import co.crystaldev.factions.util.FactionHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class PlaceholderAPIIntegration extends AlpineIntegration {
    PlaceholderAPIIntegration(AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean shouldActivate() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @Override
    protected @NotNull Class<? extends AlpineIntegrationEngine> getEngineClass() {
        return Engine.class;
    }

    public static final class Engine extends AlpineIntegrationEngine {
        Engine(AlpinePlugin plugin) {
            super(plugin);
            new Expansion().register();
        }
    }

    private static final class Expansion extends PlaceholderExpansion implements Relational {

        @Override
        public @NotNull String getIdentifier() {
            return Reference.ID;
        }

        @Override
        public @NotNull String getVersion() {
            return Reference.VERSION;
        }

        @Override
        public @NotNull String getAuthor() {
            return "BestBearr";
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
            return this.onPlaceholderRequest(player, null, params);
        }

        @Override
        public String onPlaceholderRequest(Player one, Player two, String identifier) {
            FactionAccessor factions = Factions.get().getFactions();

            Player subject = two == null ? one : two;
            Faction selfFaction = factions.findOrDefault(one);
            Faction faction = two == null ? factions.findOrDefault(one) : factions.findOrDefault(two);
            FPlayer playerState = Factions.get().getPlayers().get(subject);
            Member member = faction.getMember(subject.getUniqueId());

            switch (identifier) {
                case "faction":
                    return legacy(FactionHelper.formatRelational(one, faction, false));
                case "factionname":
                    return faction.getName();
                case "relationalusername":
                    if (two == null) {
                        return null;
                    }
                    return legacy(FactionHelper.formatRelational(one, faction, two.getName()));
                case "relation":
                    if (two == null) {
                        return null;
                    }
                    FactionRelation relation = selfFaction.relationTo(faction);
                    Component style = Components.stylize(StyleConfig.getInstance().relationalStyles.get(relation), Component.text(""));
                    return legacy(style);
                case "power":
                    return String.valueOf(playerState.getPowerLevel());
                case "maxpower":
                    return String.valueOf(playerState.getMaxPower());
                case "powerboost":
                    return String.valueOf(playerState.getPowerBoost());
                case "factionpower":
                    return String.valueOf(faction.getPowerLevel());
                case "factionmaxpower":
                    return String.valueOf(faction.getMaxPowerLevel());
                case "factionpowerboost":
                    return String.valueOf(faction.getFlagValueOrDefault(FactionFlags.POWER_MODIFIER));
                case "title":
                    return member == null ? "" : legacy(member.getTitle());
                case "rank":
                    return (member == null ? Rank.getDefault() : member.getRank()).getId();
                case "claims":
                    return String.valueOf(faction.getClaimCount());
                case "worldclaims":
                    return String.valueOf(faction.getClaimCount(subject.getWorld()));
                case "onlinemembers":
                    return String.valueOf(faction.countOnlineMembers());
                case "offlinemembers":
                    return String.valueOf(faction.countOfflineMembers());
                case "allmembers":
                    return String.valueOf(faction.countMembers());
                case "totalmembers":
                    return String.valueOf(faction.countTotalMembers());
                default:
                    return null;
            }
        }

        @NotNull
        private static String legacy(@NotNull Component component) {
            return LegacyComponentSerializer.legacySection().serialize(component);
        }
    }
}
