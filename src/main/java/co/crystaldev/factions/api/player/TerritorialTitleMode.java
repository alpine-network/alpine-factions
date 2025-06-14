package co.crystaldev.factions.api.player;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public enum TerritorialTitleMode {
    @SerializedName("title")      TITLE("Title"),
    @SerializedName("action_bar") ACTION_BAR("Action Bar"),
    @SerializedName("chat")       CHAT("Chat");

    private final @NotNull String name;

    TerritorialTitleMode(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return this.name;
    }
}
