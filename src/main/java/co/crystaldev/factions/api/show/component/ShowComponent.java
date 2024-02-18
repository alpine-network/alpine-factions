package co.crystaldev.factions.api.show.component;

import co.crystaldev.factions.api.show.ShowContext;
import co.crystaldev.factions.api.show.order.ShowOrder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/15/2024
 */
public interface ShowComponent {

    @NotNull
    ShowOrder getOrdering();

    @NotNull
    Component buildComponent(@NotNull ShowContext ctx);

    default boolean isVisible(@NotNull ShowContext ctx) {
        return true;
    }

    @NotNull
    static ShowComponent create(
            @NotNull ShowOrder order,
            @NotNull Function<ShowContext, @NotNull Component> componentFunction,
            @Nullable Predicate<ShowContext> visibilityPredicate
    ) {
        return new FunctionalShowComponent(order, componentFunction, visibilityPredicate);
    }

    @NotNull
    static ShowComponent create(
            @NotNull ShowOrder order,
            @NotNull Function<ShowContext, @NotNull Component> componentPredicate
    ) {
        return new FunctionalShowComponent(order, componentPredicate, null);
    }
}
