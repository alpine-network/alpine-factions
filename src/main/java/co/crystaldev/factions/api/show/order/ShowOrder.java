package co.crystaldev.factions.api.show.order;

import co.crystaldev.factions.api.show.component.ShowComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
public interface ShowOrder {

    int computeIndex(@NotNull Iterable<ShowComponent> components, int length);

    @NotNull
    static ShowOrder inserting() {
        return new BasicShowOrdering();
    }

    @NotNull
    static ShowOrder before(@NotNull ShowComponent component) {
        return new RelativeShowOrdering(component, true);
    }

    @NotNull
    static ShowOrder after(@NotNull ShowComponent component) {
        return new RelativeShowOrdering(component, false);
    }

    @NotNull
    static ShowOrder fixed(int index) {
        return new FixedShowOrdering(index);
    }
}
