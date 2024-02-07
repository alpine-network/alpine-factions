package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.command.claiming.ClaimType;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.ComponentHelper;
import de.exlll.configlib.Comment;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

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

    public ConfigText alphanumeric = ConfigText.of("%error_prefix% Your input must be alphanumeric");

    public ConfigText missingFactionPerm = ConfigText.of("%error_prefix% <error_highlight>%faction%</error_highlight> does not allow you to <error_highlight>%action%</error_highlight>");

    public ConfigText unknownFaction = ConfigText.of("%error_prefix% No faction or player was found with the name <error_highlight>%value%</error_highlight>");

    @Comment({
            "",
            "| Name"
    })
    public ConfigText rename = ConfigText.of("%prefix% %player% set the faction name to <highlight>%faction_name%</highlight>");

    public ConfigText factionWithName = ConfigText.of("%error_prefix% A faction with the name <error_highlight>%faction_name%</error_highlight> already exists");

    public ConfigText factionNameUnchanged = ConfigText.of("%error_prefix% The new name must differ from the existing name");

    public ConfigText nameTooShort = ConfigText.of("%error_prefix% Your faction name can't be shorter than <error_highlight>%length% characters</error_highlight>");

    public ConfigText nameTooLong = ConfigText.of("%error_prefix% Your faction name can't be longer than <error_highlight>%length% characters</error_highlight>");

    @Comment({
            "",
            "| Create/Join"
    })
    public ConfigText create = ConfigText.of("%prefix% You created a new faction named <highlight>%faction_name%</highlight>");

    public ConfigText join = ConfigText.of("%prefix% You joined <highlight>%faction_name%</highlight>");

    public ConfigText memberJoin = ConfigText.of("%prefix% %player% joined the faction");

    public ConfigText alreadyInFaction = ConfigText.of("%error_prefix% You must leave your current faction to create a new faction");

    @Comment({
            "",
            "| Land Claiming"
    })
    public ConfigText landClaim = ConfigText.of(ComponentHelper.joinNewLines(
            Component.text("%prefix% <highlight>%player%</highlight> claimed <highlight>%amount% chunks</highlight> near " +
                    "<emphasis>%world%</emphasis> <highlight>%chunk_x% %chunk_z%</highlight> using %type%"),
            Component.text("   %old_faction% <emphasis>-></emphasis> %new_faction%")
    ));

    public ConfigText landClaimSingle = ConfigText.of(ComponentHelper.joinNewLines(
            Component.text("%prefix% <highlight>%player%</highlight> claimed <highlight>%amount% chunk</highlight> near " +
                    "<emphasis>%world%</emphasis> <highlight>%chunk_x% %chunk_z%</highlight> using %type%"),
            Component.text("   %old_faction% <emphasis>-></emphasis> %new_faction%")
    ));

    public ConfigText landOwned = ConfigText.of("%prefix% This land is already owned by <highlight>%faction%</highlight>");

    public ConfigText fillLimit = ConfigText.of("%error_prefix% Reached the fill limit of <error_highlight>%limit% chunks</error_highlight>");

    public Map<ClaimType, ConfigText> claimTypes = new HashMap<>();
    {
        this.claimTypes.put(ClaimType.SQUARE, ConfigText.of("square"));
        this.claimTypes.put(ClaimType.CIRCLE, ConfigText.of("circle"));
        this.claimTypes.put(ClaimType.LINE, ConfigText.of("line"));
    }
}
