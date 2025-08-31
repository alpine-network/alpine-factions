package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
final class BasicShowOrdering implements ShowOrder {
    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        return -1;
    }
}
