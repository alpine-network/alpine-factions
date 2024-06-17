package co.crystaldev.factions.api.show;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.show.component.ShowComponent;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.Formatting;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
public final class ShowFormatter {

    private final List<ShowComponent> components = new LinkedList<>();

    public void register(@NotNull ShowComponent... components) {
        for (ShowComponent component : components) {
            this.addComponent(component);
        }
    }

    @NotNull @ApiStatus.Internal
    public Component build(@NotNull CommandSender sender, @NotNull Faction faction, @NotNull Faction senderFaction) {
        MessageConfig config = MessageConfig.getInstance();
        boolean overriding = PlayerHandler.getInstance().isOverriding(sender);

        ShowContext context = new ShowContext(sender, faction, senderFaction);
        Set<Component> components = this.components.stream()
                .filter(v -> overriding || v.isVisible(context))
                .map(v -> v.buildComponent(context))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Component title = config.showTitle.build("faction", FactionHelper.formatRelational(sender, faction, false),
                "faction_name", faction.getName());
        return Components.joinNewLines(Formatting.title(title), Components.joinNewLines(components));
    }

    private void addComponent(@NotNull ShowComponent component) {
        // if the component is already registered, remove and re-add it
        this.components.remove(component);

        int length = this.components.size();
        int index = component.getOrdering().computeIndex(this.components, length);

        // negative number was input, insert at the end
        if (index < 0) {
            this.components.add(component);
            return;
        }

        this.components.add(Math.min(length, index), component);
    }
}
