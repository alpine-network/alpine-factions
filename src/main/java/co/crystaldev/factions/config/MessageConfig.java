package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/24/2023
 */
public final class MessageConfig extends AlpineConfig {

    @Getter
    private static MessageConfig instance;
    { instance = this; }


}
