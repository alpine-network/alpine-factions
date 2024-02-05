package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.config.type.ConfigText;
import de.exlll.configlib.Comment;
import lombok.Getter;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/24/2023
 */
public final class MessageConfig extends AlpineConfig {

    @Getter
    private static MessageConfig instance;
    { instance = this; }

    @Override
    public String getFileName() {
        return "message_config.yml";
    }

    @Comment({
            "Alpine Factions v{{ pluginVersion }}",
            "Developed by Crystal Development, LLC.",
            ""
    })
    public ConfigText prefix = ConfigText.of("<info>AlpineFactions</info> <separator>»</separator><text>");

    public ConfigText errorPrefix = ConfigText.of("<error>AlpineFactions</error> <separator>»</separator><text>");

    public ConfigText alphanumeric = ConfigText.of("%error_prefix% Your input must be alphanumeric.");

    @Comment({
            "",
            "| Create"
    })
    public ConfigText create = ConfigText.of("%prefix% Created a new faction named <highlight>%faction_name%</highlight>.");

    public ConfigText factionWithName = ConfigText.of("%error_prefix% A faction with the name <error_highlight>%faction_name%</error_highlight> already exists.");

    public ConfigText alreadyInFaction = ConfigText.of("%error_prefix% You are already in a faction.");

    public ConfigText nameTooShort = ConfigText.of("%error_prefix% Your faction name can't be shorter than <error_highlight>%length% characters</error_highlight>");

    public ConfigText nameTooLong = ConfigText.of("%error_prefix% Your faction name can't be longer than <error_highlight>%length% characters</error_highlight>");
}
