package co.crystaldev.factions.api.faction;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/15/2023
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE) @Getter
public final class FactionFlag<T> {
    private final String id;
    private final String name;
    private final String description;
    private final String stateDescriptionA;
    private final String stateDescriptionB;
    private final String permission;
    private final T defaultState;
    private final Class<T> type;

    @NotNull
    public FactionFlagValue<T> wrapDefaultValue() {
        return new FactionFlagValue<>(this.type, this.defaultState);
    }

    @NotNull
    public static <T> Builder<T> builder(@NotNull String id, @NotNull Class<T> type) {
        return new Builder<>(id, type);
    }

    public static final class Builder<T> {
        private final String id;
        private final Class<T> type;
        private String name;
        private String description;
        private String stateDescriptionA;
        private String stateDescriptionB;
        private String permission;
        private T defaultState;

        public Builder(@NotNull String id, @NotNull Class<T> type) {
            this.id = id;
            this.type = type;
        }

        @NotNull
        public Builder<T> name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder<T> description(@NotNull String description) {
            this.description = description;
            return this;
        }

        @NotNull
        public Builder<T> stateDescription(@NotNull String stateDescriptionA, @NotNull String stateDescriptionB) {
            this.stateDescriptionA = stateDescriptionA;
            this.stateDescriptionB = stateDescriptionB;
            return this;
        }

        @NotNull
        public Builder<T> stateDescription(@NotNull String stateDescription) {
            this.stateDescriptionA = stateDescription;
            return this;
        }

        @NotNull
        public Builder<T> permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public Builder<T> defaultState(@NotNull T state) {
            this.defaultState = state;
            return this;
        }

        @NotNull
        public FactionFlag<T> build() {
            Preconditions.checkNotNull(this.name, "name must not be null");
            Preconditions.checkNotNull(this.description, "description must not be null");
            Preconditions.checkNotNull(this.stateDescriptionA, "stateDescription must not be null");
            return new FactionFlag<>(this.id, this.name, this.description, this.stateDescriptionA, this.stateDescriptionB,
                    this.permission, this.defaultState, this.type);
        }
    }
}
