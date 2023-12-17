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
public final class FactionFlag {
    private final String id;
    private final String name;
    private final String description;
    private final String enabledDescription;
    private final String disabledDescription;
    private final String permission;
    private final boolean defaultState;

    @NotNull
    public static Builder builder(@NotNull String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private String name;
        private String description;
        private String enabledDescription;
        private String disabledDescription;
        private String permission;
        private boolean defaultState;

        public Builder(@NotNull String id) {
            this.id = id;
        }

        @NotNull
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder description(@NotNull String description) {
            this.description = description;
            return this;
        }

        @NotNull
        public Builder stateDescription(@NotNull String enabledDescription, @NotNull String disabledDescription) {
            this.enabledDescription = enabledDescription;
            this.disabledDescription = disabledDescription;
            return this;
        }

        @NotNull
        public Builder permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        @NotNull
        public Builder defaultState(boolean state) {
            this.defaultState = state;
            return this;
        }

        @NotNull
        public FactionFlag build() {
            Preconditions.checkNotNull(this.name, "name must not be null");
            Preconditions.checkNotNull(this.description, "description must not be null");
            Preconditions.checkNotNull(this.enabledDescription, "enabledDescription must not be null");
            Preconditions.checkNotNull(this.disabledDescription, "disabledDescription must not be null");
            return new FactionFlag(this.id, this.name, this.description, this.enabledDescription, this.disabledDescription, this.permission, this.defaultState);
        }
    }
}
