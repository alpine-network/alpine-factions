package co.crystaldev.factions.api.player;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @since 0.1.0
 */
@AllArgsConstructor @Getter
public enum TerritorialTitleMode {
    @SerializedName("title")      TITLE("Title"),
    @SerializedName("action_bar") ACTION_BAR("Action Bar"),
    @SerializedName("chat")       CHAT("Chat");

    private final String name;
}
