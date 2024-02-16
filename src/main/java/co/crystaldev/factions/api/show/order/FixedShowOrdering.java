package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
@AllArgsConstructor
final class FixedShowOrdering implements ShowOrder {

    private final int index;

    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        return Math.max(0, Math.min(length - 1, this.index));
    }
}
