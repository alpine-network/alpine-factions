package co.crystaldev.factions.api.player;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/10/2024
 */
@AllArgsConstructor @Getter
public enum TerritorialTitleMode {
    @SerializedName("title")      TITLE("Title"),
    @SerializedName("action_bar") ACTION_BAR("Action Bar"),
    @SerializedName("chat")       CHAT("Chat");

    private final String name;
}
