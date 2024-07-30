package co.crystaldev.factions.api.show.component;

import co.crystaldev.factions.api.show.ShowContext;
import co.crystaldev.factions.api.show.order.ShowOrder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @since 0.1.0
 */
public interface ShowComponent {

    @NotNull ShowOrder getOrdering();

    @NotNull Component buildComponent(@NotNull ShowContext ctx);

    default boolean isVisible(@NotNull ShowContext ctx) {
        return true;
    }

    static @NotNull ShowComponent create(
            @NotNull ShowOrder order,
            @NotNull Function<ShowContext, @NotNull Component> componentFunction,
            @Nullable Predicate<ShowContext> visibilityPredicate
    ) {
        return new FunctionalShowComponent(order, componentFunction, visibilityPredicate);
    }

    static @NotNull ShowComponent create(
            @NotNull ShowOrder order,
            @NotNull Function<ShowContext, @NotNull Component> componentPredicate
    ) {
        return new FunctionalShowComponent(order, componentPredicate, null);
    }
}
