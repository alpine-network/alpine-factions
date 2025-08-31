package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
final class FixedShowOrdering implements ShowOrder {

    private final int index;

    public FixedShowOrdering(int index) {
        this.index = index;
    }

    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        return Math.max(0, Math.min(length - 1, this.index));
    }
}
