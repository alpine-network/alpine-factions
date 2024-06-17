package co.crystaldev.factions.api.faction.flag;

import co.crystaldev.factions.handler.PlayerHandler;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE) @Getter
public final class FactionFlag<T> {
    private final String id;
    private final String name;
    private final String description;
    private final String stateDescriptionA;
    private final String stateDescriptionB;
    private final String permission;
    private final boolean visible;
    private final T defaultState;
    private final Class<T> type;
    private final FlagAdapter<T> adapter;

    @NotNull
    public FlagHolder<T> wrapDefaultValue() {
        return new FlagHolder<>(this.type, this.defaultState);
    }

    @NotNull
    public String getStateDescription() {
        return this.stateDescriptionA;
    }

    @NotNull
    public String getStateDescription(boolean state) {
        return state ? this.stateDescriptionA : this.stateDescriptionB;
    }

    public boolean isPermitted(@NotNull Permissible permissible) {
        if (permissible instanceof ConsoleCommandSender) {
            return true;
        }

        if (permissible instanceof CommandSender && PlayerHandler.getInstance().isOverriding((CommandSender) permissible)) {
            return true;
        }

        return this.permission == null || permissible.hasPermission(this.permission);
    }

    @NotNull
    public static <T> Builder<T> builder(@NotNull String id, @NotNull Class<T> type, @NotNull FlagAdapter<T> adapter) {
        return new Builder<>(id, type, adapter);
    }

    public static final class Builder<T> {
        private final String id;
        private final Class<T> type;
        private final FlagAdapter<T> adapter;
        private String name;
        private String description;
        private String stateDescriptionA;
        private String stateDescriptionB;
        private String permission;
        private boolean visible = true;
        private T defaultState;

        public Builder(@NotNull String id, @NotNull Class<T> type, @NotNull FlagAdapter<T> adapter) {
            this.id = id;
            this.type = type;
            this.adapter = adapter;
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
        public Builder<T> visible(boolean visible) {
            this.visible = visible;
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
                    this.permission, this.visible, this.defaultState, this.type, this.adapter);
        }
    }
}
