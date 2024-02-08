package co.crystaldev.factions.util;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.RelationType;
import co.crystaldev.factions.api.member.Member;
import co.crystaldev.factions.StyleConfig;
import co.crystaldev.factions.store.FactionStore;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
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
    public static Component formatRelational(@NotNull Player viewer, @Nullable Faction faction, @NotNull Component component) {
        if (faction == null) {
            return Component.text("< null >");
        }

        StyleConfig config = StyleConfig.getInstance();
        FactionStore store = FactionStore.getInstance();
        Faction self = store.findFactionOrDefault(viewer);

        RelationType relationType = self.relationTo(faction);

        Component name = ComponentHelper.stylize(config.relationalStyles.get(relationType), component);
        return ComponentHelper.stylize(config.factionNameStyles.get(faction.getName()), name, true);
    }

    @NotNull
    public static Component formatRelational(@NotNull Player viewer, @Nullable Faction faction, @NotNull String component) {
        return formatRelational(viewer, faction, Component.text(component));
    }

    @NotNull
    public static Component formatRelational(@NotNull Player viewer, @Nullable Faction faction, @NotNull Player player) {
        if (faction == null) {
            return Component.text("< null >");
        }

        if (!faction.isMember(player.getUniqueId())) {
            return formatRelational(viewer, faction, player.getName());
        }

        if (viewer.equals(player)) {
            return formatRelational(viewer, faction, "You");
        }
        else {
            Member member = faction.getMember(player);
            return formatRelational(viewer, faction, ComponentHelper.join(
                    Component.text(member.getRank().getPrefix()),
                    member.getTitle(),
                    member.hasTitle() ? Component.space() : Component.empty(),
                    Component.text(player.getName())
            ));
        }
    }

    @NotNull
    public static Component formatRelational(@NotNull Player viewer, @Nullable Faction faction) {
        Faction playerFaction = FactionStore.getInstance().findFaction(viewer);
        String factionName = Objects.equals(faction, playerFaction) ? "Your faction" : (faction == null ? "< null >" : faction.getName());
        return formatRelational(viewer, faction, factionName);
    }
}
