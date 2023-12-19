package co.crystaldev.factions;

import co.crystaldev.alpinecore.AlpinePlugin;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
public final class AlpineFactions extends AlpinePlugin {

    @Getter
    private static AlpineFactions instance;
    { instance = this; }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }
}
