package co.crystaldev.factions.api.show.component;

import co.crystaldev.factions.api.show.ShowContext;
import co.crystaldev.factions.api.show.order.ShowOrder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
final class FunctionalShowComponent implements ShowComponent {

    private static final Function<ShowContext, Boolean> DEFAULT_VISIBILITY_PREDICATE = ctx -> true;

    private final ShowOrder order;

    private final Function<ShowContext, @NotNull Component> componentPredicate;

    private final Function<ShowContext, Boolean> visibilityPredicate;

    public FunctionalShowComponent(@NotNull ShowOrder order,
                                   @NotNull Function<ShowContext, @NotNull Component> componentPredicate,
                                   @Nullable Function<ShowContext, Boolean> visibilityPredicate) {
        this.order = order;
        this.componentPredicate = componentPredicate;
        this.visibilityPredicate = visibilityPredicate == null ? DEFAULT_VISIBILITY_PREDICATE : visibilityPredicate;
    }

    @Override
    public @NotNull ShowOrder getOrdering() {
        return this.order;
    }

    @Override
    public @NotNull Component buildComponent(@NotNull ShowContext ctx) {
        return this.componentPredicate.apply(ctx);
    }

    @Override
    public boolean isVisible(@NotNull ShowContext ctx) {
        return this.visibilityPredicate.apply(ctx);
    }
}
