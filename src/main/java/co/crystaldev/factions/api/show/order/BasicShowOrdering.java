package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
final class BasicShowOrdering implements ShowOrder {
    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        return -1;
    }
}
