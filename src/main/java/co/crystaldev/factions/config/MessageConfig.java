package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.config.type.ConfigText;
import de.exlll.configlib.Comment;
import lombok.Getter;

import java.util.LinkedHashMap;

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

    public ConfigText operationCancelled = ConfigText.of("%error_prefix% This operation was cancelled");

    public ConfigText missingFactionPerm = ConfigText.of("%error_prefix% <error_highlight>%faction%</error_highlight> does not allow you to <error_highlight>%action%</error_highlight>");

    public ConfigText notInFaction = ConfigText.of("%error_prefix% You are not in a faction");

    public ConfigText playerNotInFaction = ConfigText.of("%error_prefix% %player% is not in a faction");

    public ConfigText unknownFaction = ConfigText.of("%error_prefix% No faction or player was found with the name <error_highlight>%value%</error_highlight>");

    public ConfigText unknownPlayer = ConfigText.of("%error_prefix% No player was found with the name <error_highlight>%player_name%</error_highlight>");

    public ConfigText none = ConfigText.of("<gray>None</gray>");

    public ConfigText hidden = ConfigText.of("<gray>Hidden</gray>");

    @Comment({
            "",
            "| Formatting"
    })
    public ConfigText noPages = ConfigText.of("%error_prefix% No pages available to display");

    public ConfigText titleFormat = ConfigText.of("<dark_gray><</dark_gray> %content% <dark_gray>></dark_gray>");

    public ConfigText paginatorTitleFormat = ConfigText.of("<dark_gray><</dark_gray> %content% <separator>|</separator> %previous% %page%/%max_pages% %next% <dark_gray>></dark_gray>");

    public ConfigText previous = ConfigText.of("<bracket>[</bracket><emphasis><</emphasis><bracket>]</bracket>");

    public ConfigText next = ConfigText.of("<bracket>[</bracket><emphasis>></emphasis><bracket>]</bracket>");

    public ConfigText previousDisabled = ConfigText.of("<emphasis>[<]</emphasis>");

    public ConfigText nextDisabled = ConfigText.of("<emphasis>[>]</emphasis>");

    public boolean titleUsesPadding = true;

    public String paddingCharacter = "-";

    public String paddingStyle = "dark_gray strikethrough";

    @Comment({
            "",
            "| Faction Metadata"
    })
    public ConfigText rename = ConfigText.of("%prefix% %actor% set the faction name to <highlight>%faction_name%</highlight>");

    public ConfigText factionWithName = ConfigText.of("%error_prefix% A faction with the name <error_highlight>%faction_name%</error_highlight> already exists");

    public ConfigText factionNameUnchanged = ConfigText.of("%error_prefix% The new name must differ from the existing name");

    public ConfigText nameTooShort = ConfigText.of("%error_prefix% Your faction name can't be shorter than <error_highlight>%length% characters</error_highlight>");

    public ConfigText nameTooLong = ConfigText.of("%error_prefix% Your faction name can't be longer than <error_highlight>%length% characters</error_highlight>");

    public ConfigText description = ConfigText.of("%prefix% %actor% set the faction's description to:<br>%description%");

    public ConfigText motd = ConfigText.of("%prefix% %actor% set the faction's message of the day to:<br>%motd%");

    public ConfigText motdTitle = ConfigText.of("%faction% <separator>|</separator> Message of the Day");

    @Comment({
            "",
            "| Join"
    })
    public ConfigText join = ConfigText.of("%prefix% You joined <highlight>%faction_name%</highlight>");

    public ConfigText forceJoin = ConfigText.of("%prefix% %actor% added you to %faction%");

    public ConfigText memberJoin = ConfigText.of("%prefix% %player% joined the faction");

    public ConfigText memberForceJoin = ConfigText.of("%prefix% %inviter% added %player% to the faction");

    public ConfigText attemptedMemberJoin = ConfigText.of("%prefix% %player% attempted to join the faction");

    public ConfigText fullFaction = ConfigText.of("%error_prefix% %faction% has reached its member limit of <error_highlight>%limit% members</error_highlight>");

    public ConfigText alreadyInFaction = ConfigText.of("%error_prefix% You must leave your current faction first");

    public ConfigText playerAlreadyInFaction = ConfigText.of("%error_prefix% %player% is already in a faction");

    @Comment({
            "",
            "| Leave"
    })
    public ConfigText leave = ConfigText.of("%prefix% You left <highlight>%faction_name%</highlight>");

    public ConfigText memberLeave = ConfigText.of("%prefix% %player% left the faction");

    public ConfigText promoteLeader = ConfigText.of("%error_prefix% You must promote a new leader first");

    public ConfigText kick = ConfigText.of("%prefix% %actor% kicked %player% from the faction");

    public ConfigText kicked = ConfigText.of("%prefix% %actor% kicked you from %faction%");

    public ConfigText cantKick = ConfigText.of("%error_prefix% You can't kick %player% from the faction");

    @Comment({
            "",
            "| Invite"
    })
    public ConfigText notInvited = ConfigText.of("%error_prefix% You are not invited to %faction%");

    public ConfigText playerNotInvited = ConfigText.of("%error_prefix% %player% is not invited to %faction%");

    public ConfigText inviteFail = ConfigText.of("%error_prefix% %player% is already a member of %faction%");

    public ConfigText invite = ConfigText.of("%prefix% %actor% invited %invitee% to your faction");

    public ConfigText invited = ConfigText.of("%prefix% %actor% invited you to %faction%");

    public ConfigText inviteRevoke = ConfigText.of("%prefix% %actor% revoked the invitation of %invitee%");

    public ConfigText inviteListTitle = ConfigText.of("%faction% <separator>|</separator> Invitations");

    public ConfigText inviteListEntry = ConfigText.of("%player% <separator>»</separator> Invited by %inviter%");

    @Comment({
            "",
            "| Show"
    })
    public ConfigText showTitle = ConfigText.of("%faction% <separator>|</separator> Faction Info");

    public boolean showTitleUsesPadding = true;

    @SuppressWarnings("unused")
    public ConfigText genericShowEntry = ConfigText.of("<emphasis>%name%:</emphasis> <highlight>%value%</highlight>");

    public ConfigText showDesc = ConfigText.of("<emphasis>Description: \"%description%\"");

    public ConfigText showId = ConfigText.of("<hover:show_text:Copy To Clipboard><click:copy_to_clipboard:%id%><emphasis>Faction ID: <highlight>%id%</highlight>");

    public ConfigText showCreated = ConfigText.of("<emphasis>Created:</emphasis> <highlight>%created%");

    public ConfigText showJoinState = ConfigText.of("<emphasis>Joining:</emphasis> %joining%");

    public ConfigText showLandPower = ConfigText.of("<emphasis>Land<bracket>/</bracket>Power<bracket>/</bracket>Max Power:</emphasis> <highlight>%land%/%power%/%max_power%");

    public ConfigText showAllies = ConfigText.of("<emphasis>Allies <bracket>[<dark_purple>%ally_count%/%max_allies%</dark_purple>]</bracket>:</emphasis> <highlight>%allies%");

    public ConfigText showTruces = ConfigText.of("<emphasis>Truces <bracket>[<light_purple>%truce_count%/%max_truces%</light_purple>]</bracket>:</emphasis> <highlight>%truces%");

    public ConfigText showOnlineMembers = ConfigText.of("<emphasis>Online Members <bracket>[%count%]</bracket>:</emphasis> %members%");

    public ConfigText showOfflineMembers = ConfigText.of("<emphasis>Offline Members <bracket>[%count%]</bracket>:</emphasis> %members%");

    public ConfigText memberCountOnline = ConfigText.of("<green>%online%/%offline%");

    public ConfigText memberCountOffline = ConfigText.of("<red>%online%/%offline%");

    public ConfigText joinable = ConfigText.of("<green>True</green> <bracket>[<emphasis>Can Join</emphasis>]</bracket>");

    public ConfigText notJoinable = ConfigText.of("<red>False</red> <bracket>[<emphasis>Invite Only</emphasis>]</bracket>");

    @Comment({
            "",
            "| Relations"
    })
    public ConfigText alreadyRelation = ConfigText.of("%error_prefix% This relation is already set with %faction%");

    public ConfigText relationSelf = ConfigText.of("%error_prefix% Unable to declare a relation with %faction%");

    public ConfigText relationWishListTitle = ConfigText.of("Relation Requests");

    public ConfigText relationListTitle = ConfigText.of("Relations");

    public ConfigText relationListEntry = ConfigText.of("%faction% <separator>»</separator> %relation%");

    public LinkedHashMap<FactionRelation, ConfigText> relationDeclarations = new LinkedHashMap<>();
    {
        this.relationDeclarations.put(FactionRelation.NEUTRAL, ConfigText.of(
                "%prefix% %faction% is now a <aqua>neutral faction"));
        this.relationDeclarations.put(FactionRelation.TRUCE, ConfigText.of(
                "%prefix% %faction% is now a <light_purple>faction in truce"));
        this.relationDeclarations.put(FactionRelation.ALLY, ConfigText.of(
                "%prefix% %faction% is now an <dark_purple>allied faction"));
        this.relationDeclarations.put(FactionRelation.ENEMY, ConfigText.of(
                "%prefix% %faction% is now an <red>enemy faction"));
    }

    public LinkedHashMap<FactionRelation, ConfigText> relationRequest = new LinkedHashMap<>();
    {
        this.relationRequest.put(FactionRelation.NEUTRAL, ConfigText.of(
                "%prefix% %faction% were informed that you wish to be a <aqua>neutral faction"));
        this.relationRequest.put(FactionRelation.TRUCE, ConfigText.of(
                "%prefix% %faction% were informed that you wish to be a <light_purple>faction in truce"));
        this.relationRequest.put(FactionRelation.ALLY, ConfigText.of(
                "%prefix% %faction% were informed that you wish to be an <dark_purple>allied faction"));
    }

    public LinkedHashMap<FactionRelation, ConfigText> relationWishes = new LinkedHashMap<>();
    {
        this.relationWishes.put(FactionRelation.NEUTRAL, ConfigText.of(
                "%prefix% %faction% wishes to be a <aqua>neutral faction</aqua>. <aqua><b><click:run_command:\"/f neutral %faction_name%\">[Accept]"));
        this.relationWishes.put(FactionRelation.TRUCE, ConfigText.of(
                "%prefix% %faction% wishes to be a <light_purple>faction in truce</light_purple>. <light_purple><b><click:run_command:\"/f truce %faction_name%\">[Accept]"));
        this.relationWishes.put(FactionRelation.ALLY, ConfigText.of(
                "%prefix% %faction% wishes to be an <dark_purple>allied faction</dark_purple>. <dark_purple><b><click:run_command:\"/f ally %faction_name%\">[Accept]"));
    }

    @Comment({
            "",
            "| Faction State"
    })
    public ConfigText create = ConfigText.of("%prefix% You created a new faction named <highlight>%faction_name%</highlight>");

    public ConfigText disband = ConfigText.of("%prefix% %actor% disbanded %faction%");

    public ConfigText disbandConfirm = ConfigText.of("%prefix% Please confirm by sending this command again");

    public ConfigText unableToDisband = ConfigText.of("%error_prefix% This faction cannot be disbanded");

    public ConfigText listTitle = ConfigText.of("Factions List");

    public ConfigText listEntry = ConfigText.of("%faction% <separator>»</separator> %online%/%members% online, <emphasis>%land%/%power%/%max_power%</emphasis>");

    public ConfigText unknownFlag = ConfigText.of("%error_prefix% No flag was found with the ID <error_highlight>%value%</error_highlight>");

    public ConfigText invalidFlagValue = ConfigText.of("%error_prefix% Invalid flag input for flag %flag%");

    public ConfigText updatedFlagValue = ConfigText.of(
            "%prefix% %actor% set the faction flag <highlight>%flag_name%</highlight> to <highlight>%state%</highlight>",
            "    <emphasis><i>%flag_state_description%"
    );

    public ConfigText flagStateListTitle = ConfigText.of("%faction% | Faction Flags");

    public ConfigText flagStateListEntry = ConfigText.of(
            "<hover:show_text:\"%flag_state_description%\"><notice>%flag_name%</notice></hover> <separator>»</separator> Set to <emphasis>%state%</emphasis>"
    );

    public ConfigText flagListTitle = ConfigText.of("Available Flags");

    public ConfigText flagListEntry = ConfigText.of(
            "<hover:show_text:\"%flag_description%\"><notice>%flag_name%</notice></hover> <separator>»</separator> Set to <emphasis>%default_state%</emphasis> by default"
    );

    @Comment({
            "",
            "| Player/Member State"
    })
    public ConfigText stateChange = ConfigText.of("%prefix% <highlight>%subject%:</highlight> <emphasis>%state%</emphasis>");

    public ConfigText titleChange = ConfigText.of("%prefix% %actor% modified the title for %player%");

    @Comment({
            "",
            "| Land Claiming"
    })
    public ConfigText landClaim = ConfigText.of(
            "%prefix% %actor% %claim_type% <highlight>%amount% chunks</highlight> near <emphasis>(%world% %chunk_x% %chunk_z%)</emphasis> using %type%",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimSingle = ConfigText.of(
            "%prefix% %actor% %claim_type% <highlight>%amount% chunk</highlight> at <emphasis>(%world% %chunk_x%, %chunk_z%)</emphasis>",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimWorld = ConfigText.of(
            "%prefix% %actor% %claim_type% <highlight>%amount% chunks</highlight> in <emphasis>%world%</emphasis>",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimAll = ConfigText.of(
            "%prefix% %actor% %claim_type% <highlight>%amount% chunks</highlight> in <emphasis>all worlds</emphasis>",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText claimed = ConfigText.of("claimed");

    public ConfigText unclaimed = ConfigText.of("unclaimed");

    public ConfigText pillaged = ConfigText.of("pillaged");

    public ConfigText conquered = ConfigText.of("conquered");

    public ConfigText conquerFail = ConfigText.of("%error_prefix% %faction% owns this land and is strong enough to keep it");

    public ConfigText conquerFromEdge = ConfigText.of("%error_prefix% You must begin conquering at the edge of this territory");

    public ConfigText landOwned = ConfigText.of("%prefix% This land is already owned by %faction%");

    public ConfigText insufficientPower = ConfigText.of("%error_prefix% %faction% does not have enough power to claim this land");

    public ConfigText fillLimit = ConfigText.of("%error_prefix% Reached the fill limit of <error_highlight>%limit% chunks</error_highlight>");

    public ConfigText claimTooFar = ConfigText.of("%error_prefix% Unable to claim land too far away.");

    public ConfigText disableAutoSetting = ConfigText.of("%prefix% Disabled auto-claim.");

    public ConfigText enableAutoClaim = ConfigText.of("%prefix% Enabled auto-claim for %faction%");

    public ConfigText enableAutoUnclaim = ConfigText.of("%prefix% Enabled auto-unclaim for %faction%");

    @Comment({
            "",
            "| Alert"
    })
    public ConfigText alertTitle = ConfigText.of("<info>Faction Alert</info>");

    public ConfigText alertSubtitle = ConfigText.of("<emphasis>%actor_name%: %alert%</emphasis>");

    public ConfigText alertMessage = ConfigText.of(
            "<b></b>",
            "<info>Faction Alert</info>",
            "<emphasis>%actor%: %alert%",
            "<b></b>"
    );

    @Comment({
            "",
            "| Map"
    })
    public ConfigText mapTitle = ConfigText.of("<emphasis>%world% %chunk_x%, %chunk_z%</emphasis> <separator>|</separator> %faction%");

    public ConfigText mapLegendFormat = ConfigText.of("%character%: %faction%");

    public ConfigText mapLegendOverflowFormat = ConfigText.of("%character%: <error_highlight>Unable to represent all factions on this map.</error_highlight>");

    public ConfigText mapCenter = ConfigText.of("<aqua>+</aqua>");

    public String mapCompassStyle = "dark_gray";

    public String mapCompassDirectionStyle = "white";
}
