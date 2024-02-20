package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
@AllArgsConstructor
final class RelativeShowOrdering implements ShowOrder {

    private final ShowComponent relative;

    private final boolean before;

    @Override
    public int computeIndex(@NotNull Iterable<ShowComponent> components, int length) {
        int index = 1;
        for (ShowComponent component : components) {
            if (component.equals(this.relative)) {
                return Math.max(0, Math.min(length, this.before ? index - 1 : index + 1));
            }

            index++;
        }

        // fallback to end
        return -1;
    }
}
