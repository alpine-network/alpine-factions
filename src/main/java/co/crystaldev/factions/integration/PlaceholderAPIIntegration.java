package co.crystaldev.factions.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.AlpineFactions;
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
import co.crystaldev.factions.util.RelationHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @since 0.1.0
 */
public final class PlaceholderAPIIntegration extends AlpineIntegration {
    PlaceholderAPIIntegration(@NotNull AlpinePlugin plugin) {
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

        private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");

        private static final List<String> PLACEHOLDERS = Arrays.asList(
                "%alpinefactions_factiondisplayname%",  // REL - Formats the player's faction name
                "%alpinefactions_faction%",             // The player's faction name
                "%alpinefactions_factionname%",         // The player's non-stylized faction name
                "%alpinefactions_relationalusername%",  // REL - The player's IGN formatted relationally
                "%alpinefactions_relation%",            // REL - The formatting for the faction relation
                "%alpinefactions_relationname%",        // REL - The name of the relation with the other faction
                "%alpinefactions_power%",               // The player's power level
                "%alpinefactions_maxpower%",            // The player's maximum power level
                "%alpinefactions_powerboost%",          // The player's power boost modifier
                "%alpinefactions_factionpower%",        // The power level of the player's faction
                "%alpinefactions_factionmaxpower%",     // The max power level for the player's faction
                "%alpinefactions_factionpowerboost%",   // The power boost modifier for the player's faction
                "%alpinefactions_title%",               // The player's current faction title
                "%alpinefactions_rank%",                // The player's rank in the faction
                "%alpinefactions_claims%",              // The total number of claimed chunks the faction has
                "%alpinefactions_worldclaims%",         // The total number of claimed chunks the faction has in the world
                "%alpinefactions_onlinemembers%",       // The number of online members in the player's faction
                "%alpinefactions_offlinemembers%",      // The number of offline members in the player's faction
                "%alpinefactions_allmembers%",          // The number of members in the player's faction
                "%alpinefactions_totalmembers%"         // The number of members in the player's faction roster
        );

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
            return "Crystal Development, LLC.";
        }

        @Override
        public @NotNull List<String> getPlaceholders() {
            return PLACEHOLDERS;
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player primary, @NotNull String identifier) {
            FactionAccessor factions = Factions.registry();

            Faction primaryFaction = factions.findOrDefault(primary);
            FPlayer primaryState = Factions.players().get(primary);
            Member primaryMember = primaryFaction.getMember(primary.getUniqueId());

            switch (identifier) {
                case "faction":
                    return legacy(RelationHelper.formatLiteralFactionName(primary, primaryFaction));
                case "factionname":
                    return primaryFaction.getName();
                case "relation":
                    StyleConfig config = AlpineFactions.getInstance().getConfiguration(StyleConfig.class);
                    String style = config.relationalStyles.get(FactionRelation.SELF);
                    String legacy = legacy(Components.stylize(style, Component.text("-")));
                    return legacy.substring(0, legacy.length() - 1);
                case "relationname":
                    return "self";
                case "power":
                    return NUMBER_FORMAT.format(primaryState.getPowerLevel());
                case "maxpower":
                    return NUMBER_FORMAT.format(primaryState.getMaxPower());
                case "powerboost":
                    return NUMBER_FORMAT.format(primaryState.getPowerBoost());
                case "factionpower":
                    return NUMBER_FORMAT.format(primaryFaction.getPowerLevel());
                case "factionmaxpower":
                    return NUMBER_FORMAT.format(primaryFaction.getMaxPowerLevel());
                case "factionpowerboost":
                    return NUMBER_FORMAT.format(primaryFaction.getFlagValueOrDefault(FactionFlags.POWER_MODIFIER));
                case "title":
                    return primaryMember == null ? "" : legacy(primaryMember.getTitle());
                case "rank":
                    return (primaryMember == null ? Rank.getDefault() : primaryMember.getRank()).getId();
                case "rankprefix":
                    return (primaryMember == null ? Rank.getDefault() : primaryMember.getRank()).getPrefix();
                case "claims":
                    return NUMBER_FORMAT.format(primaryFaction.getClaimCount());
                case "worldclaims":
                    return NUMBER_FORMAT.format(primaryFaction.getClaimCount(primary.getWorld()));
                case "onlinemembers":
                    return NUMBER_FORMAT.format(primaryFaction.countOnlineMembers());
                case "offlinemembers":
                    return NUMBER_FORMAT.format(primaryFaction.countOfflineMembers());
                case "allmembers":
                    return NUMBER_FORMAT.format(primaryFaction.countMembers());
                case "totalmembers":
                    return NUMBER_FORMAT.format(primaryFaction.countTotalMembers());
                default:
                    return null;
            }
        }

        @Override
        public String onPlaceholderRequest(Player primary, Player relational, String identifier) {
            FactionAccessor factions = Factions.registry();
            Faction primaryFaction = factions.findOrDefault(primary);
            Faction relationalFaction = factions.findOrDefault(relational);

            switch (identifier) {
                case "factiondisplayname":
                    Member member = primaryFaction.getMember(primary.getUniqueId());
                    String prefix = primaryFaction.isWilderness() ? ""
                            : (member == null ? Rank.getDefault() : member.getRank()).getPrefix();
                    return legacy(RelationHelper.formatComponent(relationalFaction, primaryFaction,
                            prefix + primaryFaction.getName()));
                case "faction":
                    return legacy(RelationHelper.formatLiteralFactionName(relationalFaction, primaryFaction));
                case "relationalusername":
                    return legacy(RelationHelper.formatComponent(primary, relationalFaction, relational.getName()));
                case "relation":
                    StyleConfig config = AlpineFactions.getInstance().getConfiguration(StyleConfig.class);
                    FactionRelation relation = primaryFaction.relationTo(relationalFaction);
                    String style = config.relationalStyles.get(relation);

                    // Apply styling to a template string so we can extract it
                    String legacy = legacy(Components.stylize(style, Component.text("-")));

                    // Remove the template string
                    return legacy.substring(0, legacy.length() - 1);
                case "relationname":
                    return primaryFaction.relationTo(relationalFaction).getId();
                default:
                    // format using other placeholders
                    return this.onPlaceholderRequest(primary, identifier);
            }
        }

        private static @NotNull String legacy(@NotNull Component component) {
            return LegacyComponentSerializer.legacySection().serialize(component);
        }
    }
}
