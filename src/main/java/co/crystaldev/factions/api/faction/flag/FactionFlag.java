package co.crystaldev.factions.api.faction.flag;

import co.crystaldev.factions.handler.PlayerHandler;
import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
public final class FactionFlag<T> {

    private final @NotNull String id;
    private final @NotNull String name;
    private final @NotNull String description;
    private final @NotNull String stateDescriptionA;
    private final @Nullable String stateDescriptionB;
    private final @Nullable String permission;
    private final boolean visible;
    private final @NotNull T defaultState;
    private final @NotNull Class<T> type;
    private final @NotNull FlagAdapter<T> adapter;

    FactionFlag(@NotNull String id, @NotNull String name, @NotNull String description,
                @NotNull String stateDescriptionA, @Nullable String stateDescriptionB,
                @Nullable String permission, boolean visible, @NotNull T defaultState,
                @NotNull Class<T> type, @NotNull FlagAdapter<T> adapter) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stateDescriptionA = stateDescriptionA;
        this.stateDescriptionB = stateDescriptionB;
        this.permission = permission;
        this.visible = visible;
        this.defaultState = defaultState;
        this.type = type;
        this.adapter = adapter;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getDescription() {
        return this.description;
    }

    public @NotNull String getStateDescription() {
        return this.stateDescriptionA;
    }

    public @NotNull String getStateDescription(boolean state) {
        return state ? this.stateDescriptionA : this.stateDescriptionB;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public @NotNull T getDefaultState() {
        return this.defaultState;
    }

    public @NotNull Class<T> getType() {
        return this.type;
    }

    public @NotNull FlagAdapter<T> getAdapter() {
        return this.adapter;
    }

    public @NotNull FlagHolder<T> wrapDefaultValue() {
        return new FlagHolder<>(this.type, this.defaultState);
    }

    public @Nullable String getPermission() {
        return this.permission;
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

    public static <T> @NotNull Builder<T> builder(@NotNull String id, @NotNull Class<T> type, @NotNull FlagAdapter<T> adapter) {
        return new Builder<>(id, type, adapter);
    }

    /**
     * @since 0.1.0
     */
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

        public @NotNull Builder<T> name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public @NotNull Builder<T> description(@NotNull String description) {
            this.description = description;
            return this;
        }

        public @NotNull Builder<T> stateDescription(@NotNull String stateDescriptionA, @NotNull String stateDescriptionB) {
            this.stateDescriptionA = stateDescriptionA;
            this.stateDescriptionB = stateDescriptionB;
            return this;
        }

        public @NotNull Builder<T> stateDescription(@NotNull String stateDescription) {
            this.stateDescriptionA = stateDescription;
            return this;
        }

        public @NotNull Builder<T> permission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        public @NotNull Builder<T> visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public @NotNull Builder<T> defaultState(@NotNull T state) {
            this.defaultState = state;
            return this;
        }

        public @NotNull FactionFlag<T> build() {
            Preconditions.checkNotNull(this.name, "name must not be null");
            Preconditions.checkNotNull(this.description, "description must not be null");
            Preconditions.checkNotNull(this.stateDescriptionA, "stateDescription must not be null");
            return new FactionFlag<>(this.id, this.name, this.description, this.stateDescriptionA, this.stateDescriptionB,
                    this.permission, this.visible, this.defaultState, this.type, this.adapter);
        }
    }
}
