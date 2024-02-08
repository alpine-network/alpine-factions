package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.config.type.ConfigText;
import co.crystaldev.factions.util.ComponentHelper;
import de.exlll.configlib.Comment;
import lombok.Getter;
import net.kyori.adventure.text.Component;

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
    public ConfigText prefix = ConfigText.of("<info>Factions</info> <separator>»</separator><text>");

    public ConfigText errorPrefix = ConfigText.of("<error>Factions</error> <separator>»</separator><text>");

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
    public ConfigText landClaim = ConfigText.of(
            "%prefix% <highlight>%player%</highlight> %claim_type% <highlight>%amount% chunks</highlight> near <emphasis>(%world% %chunk_x% %chunk_z%)</emphasis> using %type%",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimSingle = ConfigText.of(
            "%prefix% <highlight>%player%</highlight> %claim_type% <highlight>%amount% chunk</highlight> at <emphasis>(%world% %chunk_x%, %chunk_z%)</emphasis>",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText claimed = ConfigText.of("claimed");

    public ConfigText pillaged = ConfigText.of("pillaged");

    public ConfigText conquered = ConfigText.of("conquered");

    public ConfigText conquerFail = ConfigText.of("%error_prefix% <error_highlight>%faction%</error_highlight> owns this land and is strong enough to keep it");

    public ConfigText landOwned = ConfigText.of("%prefix% This land is already owned by <highlight>%faction%</highlight>");

    public ConfigText notEnoughPower = ConfigText.of("%error_prefix% <error_highlight>%faction%</error_highlight> does not have enough power to claim this land");

    public ConfigText fillLimit = ConfigText.of("%error_prefix% Reached the fill limit of <error_highlight>%limit% chunks</error_highlight>");

    public ConfigText claimTooFar = ConfigText.of("%error_prefix% Unable to claim land too far away.");
}
