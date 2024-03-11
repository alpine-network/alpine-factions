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
        return "messages.yml";
    }

    @Comment({
            "Alpine Factions v{{ pluginVersion }}",
            "Developed by Crystal Development, LLC.",
            ""
    })
    public ConfigText alphanumeric = ConfigText.of(
            "%error_prefix% Your input must be alphanumeric");

    public ConfigText operationCancelled = ConfigText.of(
            "%error_prefix% This operation was cancelled");

    public ConfigText confirm = ConfigText.of(
            "%prefix% Please confirm by sending this command again");

    public ConfigText missingFactionPerm = ConfigText.of(
            "%error_prefix% <error_highlight>%faction%</error_highlight> does not allow you to <error_highlight>%action%</error_highlight>");

    public ConfigText notInFaction = ConfigText.of(
            "%error_prefix% You are not in a faction");

    public ConfigText playerNotInFaction = ConfigText.of(
            "%error_prefix% %player% is not in a faction");

    public ConfigText unknownFaction = ConfigText.of(
            "%error_prefix% No faction or player was found with the name <error_highlight>%value%</error_highlight>");

    public ConfigText unknownPlayer = ConfigText.of(
            "%error_prefix% No player was found with the name <error_highlight>%player_name%</error_highlight>");

    public ConfigText rankTooHigh = ConfigText.of(
            "%error_prefix% You can't set ranks higher than or matching your own");

    public ConfigText unknownRelational = ConfigText.of(
            "%error_prefix% No rank or relation was found with the ID <error_highlight>%value%</error_highlight");

    public ConfigText none = ConfigText.of(
            "<gray>None</gray>");

    public ConfigText hidden = ConfigText.of(
            "<gray>Hidden</gray>");



    @Comment({
            "",
            ">>> Formatting",
            "  > Title"
    })
    public ConfigText titleFormat = ConfigText.of(
            "<bracket><</bracket> %content% <bracket>></bracket>");

    public boolean titleUsesPadding = true;

    public String paddingCharacter = "-";

    public String paddingStyle = "dark_gray strikethrough";



    @Comment("  > Pagination")
    public ConfigText paginatorTitleFormat = ConfigText.of(
            "<bracket><</bracket> %content% <separator>|</separator> %previous% %page%/%max_pages% %next% <bracket>></bracket>");

    public ConfigText previous = ConfigText.of(
            "<bracket>[</bracket><emphasis><</emphasis><bracket>]</bracket>");

    public ConfigText next = ConfigText.of(
            "<bracket>[</bracket><emphasis>></emphasis><bracket>]</bracket>");

    public ConfigText previousDisabled = ConfigText.of(
            "<emphasis>[<]</emphasis>");

    public ConfigText nextDisabled = ConfigText.of(
            "<emphasis>[>]</emphasis>");

    public ConfigText noPages = ConfigText.of(
            "%error_prefix% No pages available to display");



    @Comment("  > Progress Indicator")
    public ConfigText progressBarFormat = ConfigText.of(
            "<bracket>[</bracket>%progress%<bracket>]</bracket>");

    public int progressLength = 20;

    public String progressIndicatorCharacter = "=";

    public String progressRemainingCharacter = "⋯";

    public String progressIndicatorStyle = "aqua strikethrough";

    public String progressRemainingStyle = "light_gray";



    @Comment({
            "",
            ">>> Faction Metadata"
    })
    public ConfigText rename = ConfigText.of(
            "%prefix% %actor% set the faction name to <highlight>%faction_name%</highlight>");

    public ConfigText factionWithName = ConfigText.of(
            "%error_prefix% A faction with the name <error_highlight>%faction_name%</error_highlight> already exists");

    public ConfigText factionNameUnchanged = ConfigText.of(
            "%error_prefix% The new name must differ from the existing name");

    public ConfigText nameTooShort = ConfigText.of(
            "%error_prefix% Your faction name can't be shorter than <error_highlight>%length% characters</error_highlight>");

    public ConfigText nameTooLong = ConfigText.of(
            "%error_prefix% Your faction name can't be longer than <error_highlight>%length% characters</error_highlight>");

    public ConfigText description = ConfigText.of(
            "%prefix% %actor% set the faction's description to:<br>%description%");

    public ConfigText motd = ConfigText.of(
            "%prefix% %actor% set the faction's message of the day to:<br>%motd%");

    public ConfigText motdTitle = ConfigText.of(
            "%faction% <separator>|</separator> Message of the Day");



    @Comment({
            "",
            ">>> Join"
    })
    public ConfigText join = ConfigText.of(
            "%prefix% You joined <highlight>%faction_name%</highlight>");

    public ConfigText forceJoin = ConfigText.of(
            "%prefix% %actor% added you to %faction%");

    public ConfigText memberJoin = ConfigText.of(
            "%prefix% %player% joined the faction");

    public ConfigText memberForceJoin = ConfigText.of(
            "%prefix% %inviter% added %player% to the faction");

    public ConfigText attemptedMemberJoin = ConfigText.of(
            "%prefix% %player% attempted to join the faction");

    public ConfigText fullFaction = ConfigText.of(
            "%error_prefix% %faction% has reached its member limit of <error_highlight>%limit% members</error_highlight>");

    public ConfigText alreadyInFaction = ConfigText.of(
            "%error_prefix% You must leave your current faction first");

    public ConfigText playerAlreadyInFaction = ConfigText.of(
            "%error_prefix% %player% is already in a faction");



    @Comment({
            "",
            ">>> Leave"
    })
    public ConfigText leave = ConfigText.of(
            "%prefix% You left <highlight>%faction_name%</highlight>");

    public ConfigText memberLeave = ConfigText.of(
            "%prefix% %player% left the faction");

    public ConfigText promoteLeader = ConfigText.of(
            "%error_prefix% You must promote a new leader first");

    public ConfigText kick = ConfigText.of(
            "%prefix% %actor% kicked %player% from the faction");

    public ConfigText kicked = ConfigText.of(
            "%prefix% %actor% kicked you from %faction%");

    public ConfigText cantKick = ConfigText.of(
            "%error_prefix% You can't kick %player% from the faction");



    @Comment({
            "",
            ">>> Invite"
    })
    public ConfigText notInvited = ConfigText.of(
            "%error_prefix% You are not invited to %faction%");

    public ConfigText playerNotInvited = ConfigText.of(
            "%error_prefix% %player% is not invited to %faction%");

    public ConfigText inviteFail = ConfigText.of(
            "%error_prefix% %player% is already a member of %faction%");

    public ConfigText invite = ConfigText.of(
            "%prefix% %actor% invited %invitee% to your faction");

    public ConfigText invited = ConfigText.of(
            "%prefix% %actor% invited you to %faction_name%");

    public ConfigText inviteRevoke = ConfigText.of(
            "%prefix% %actor% revoked the invitation of %invitee%");

    public ConfigText inviteListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Invitations");

    public ConfigText inviteListEntry = ConfigText.of(
            "%player% <separator>»</separator> Invited by %inviter%");



    @Comment({
            "",
            ">>> Roster"
    })
    public ConfigText rosterAdd = ConfigText.of(
            "%prefix% %actor% added %player% to your faction roster as a <highlight>%rank%</highlight>");

    public ConfigText rosterAdded = ConfigText.of(
            "%prefix% %actor% added you to %faction_name%'s roster");

    public ConfigText rosterRemove = ConfigText.of(
            "%prefix% %actor% removed %player% from your faction's roster");

    public ConfigText rosterRemoved = ConfigText.of(
            "%prefix% %actor% removed you from %faction_name%'s roster");

    public ConfigText rosterSetRank = ConfigText.of(
            "%prefix% %actor% set the rank of %player% to <highlight>%rank%</highlight> in your faction roster");

    public ConfigText rosterFull = ConfigText.of(
            "%error_prefix% The faction roster is full");

    public ConfigText alreadyOnRoster = ConfigText.of(
            "%error_prefix% %player% is already on %faction_name%'s roster");

    public ConfigText notOnRoster = ConfigText.of(
            "%error_prefix% %player% is not on %faction_name%'s roster");

    public ConfigText rosterListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Roster");

    public ConfigText rosterListEntry = ConfigText.of(
            "%player% <separator>»</separator> %rank%");



    @Comment({
            "",
            ">>> Show"
    })
    public ConfigText showTitle = ConfigText.of(
            "%faction% <separator>|</separator> Faction Info");

    @SuppressWarnings("unused")
    public ConfigText genericShowEntry = ConfigText.of(
            "<emphasis>%name%:</emphasis> <highlight>%value%</highlight>");

    public ConfigText showDesc = ConfigText.of(
            "<emphasis>Description: \"%description%\"");

    public ConfigText showId = ConfigText.of(
            "<hover:show_text:Copy To Clipboard><click:copy_to_clipboard:%id%><emphasis>Faction ID: <highlight>%id%</highlight>");

    public ConfigText showCreated = ConfigText.of(
            "<emphasis>Created:</emphasis> <highlight>%created%");

    public ConfigText showJoinState = ConfigText.of(
            "<emphasis>Joining:</emphasis> %joining%");

    public ConfigText showLandPower = ConfigText.of(
            "<emphasis>Land<bracket>/</bracket>Power<bracket>/</bracket>Max Power:</emphasis> <highlight>%land%/%power%/%max_power%");

    public ConfigText showAllies = ConfigText.of(
            "<emphasis>Allies <bracket>[<dark_purple>%ally_count%/%max_allies%</dark_purple>]</bracket>:</emphasis> <highlight>%allies%");

    public ConfigText showTruces = ConfigText.of(
            "<emphasis>Truces <bracket>[<light_purple>%truce_count%/%max_truces%</light_purple>]</bracket>:</emphasis> <highlight>%truces%");

    public ConfigText showOnlineMembers = ConfigText.of(
            "<emphasis>Online Members <bracket>[%count%]</bracket>:</emphasis> %members%");

    public ConfigText showOfflineMembers = ConfigText.of(
            "<emphasis>Offline Members <bracket>[%count%]</bracket>:</emphasis> %members%");

    public ConfigText memberCountOnline = ConfigText.of(
            "<green>%online_count%/%member_count%");

    public ConfigText memberCountOffline = ConfigText.of(
            "<red>%online_count%/%member_count%");

    public ConfigText joinable = ConfigText.of(
            "<green>True</green> <bracket>[<emphasis>Can Join</emphasis>]</bracket>");

    public ConfigText notJoinable = ConfigText.of(
            "<red>False</red> <bracket>[<emphasis>Invite Only</emphasis>]</bracket>");



    @Comment({
            "",
            ">>> Relations"
    })
    public ConfigText alreadyRelation = ConfigText.of(
            "%error_prefix% This relation is already set with %faction%");

    public ConfigText relationSelf = ConfigText.of(
            "%error_prefix% Unable to declare a relation with %faction%");

    public ConfigText truceLimit = ConfigText.of(
            "%error_prefix% You are limited to <error_highlight>%limit% Truces</error_highlight>");

    public ConfigText allyLimit = ConfigText.of(
            "%error_prefix% You are limited to <error_highlight>%limit% Allies</error_highlight>");

    public ConfigText relationWishListTitle = ConfigText.of(
            "Relation Requests");

    public ConfigText relationListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Relations");

    public ConfigText relationListEntry = ConfigText.of(
            "%faction% <separator>»</separator> %relation%");

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
            ">>> Faction State"
    })
    public ConfigText create = ConfigText.of(
            "%prefix% You created a new faction named <highlight>%faction_name%</highlight>");

    public ConfigText disband = ConfigText.of(
            "%prefix% %actor% disbanded %faction%");

    public ConfigText unableToDisband = ConfigText.of(
            "%error_prefix% This faction cannot be disbanded");

    public ConfigText listTitle = ConfigText.of(
            "Factions List");

    public ConfigText listEntry = ConfigText.of(
            "%faction% <separator>»</separator> %online%/%members% online, <emphasis>%land%/%power%/%max_power%</emphasis>");



    @Comment({
            "",
            ">>> Flags"
    })
    public ConfigText unknownFlag = ConfigText.of(
            "%error_prefix% No flag was found with the ID <error_highlight>%value%</error_highlight>");

    public ConfigText invalidFlagValue = ConfigText.of(
            "%error_prefix% Invalid flag input for flag %flag%");

    public ConfigText updatedFlagValue = ConfigText.of(
            "%prefix% %actor% set the faction flag <highlight>%flag_name%</highlight> to <highlight>%state%</highlight>",
            "    <emphasis><i>%flag_state_description%"
    );

    public ConfigText flagStateListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Faction Flags");

    public ConfigText flagStateListEntry = ConfigText.of(
            "<hover:show_text:\"%flag_state_description%\"><notice>%flag_name%</notice></hover> <separator>»</separator> Set to <emphasis>%state%</emphasis>"
    );

    public ConfigText flagListTitle = ConfigText.of("Available Flags");

    public ConfigText flagListEntry = ConfigText.of(
            "<hover:show_text:\"%flag_description%\"><notice>%flag_name%</notice></hover> <separator>»</separator> Set to <emphasis>%default_state%</emphasis> by default"
    );



    @Comment({
            "",
            ">>> Permissions"
    })
    public ConfigText unknownPermission = ConfigText.of(
            "%error_prefix% No permission was found with the ID <error_highlight>%value%</error_highlight>");

    public ConfigText updatedPermissionValue = ConfigText.of(
            "%prefix% %actor% set the permission <highlight>%permission_name%</highlight> to <highlight>%state%</highlight> for %relation%");

    public ConfigText permissionStateListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Faction Permissions");

    public ConfigText permissionStateListEntry = ConfigText.of(
            "%relation_states% <separator>»</separator> <hover:show_text:\"%permission_description%\"><notice>%permission_name%</notice></hover>");

    public ConfigText permissionListTitle = ConfigText.of("Available Permissions");

    public ConfigText permissionListEntry = ConfigText.of(
            "<notice>%permission_name%</notice> <separator>»</separator> %permission_description%");

    public ConfigText permissionRelationStateTitle = ConfigText.of(
            "<red>ENE</red> <aqua>NEU</aqua> <light_purple>TRU</light_purple> <dark_purple>ALL</dark_purple> <green>REC MEM OFF COL LEA</green>");

    public ConfigText permissionRelationState = ConfigText.of(
            "%enemy% %neutral% %truce% %ally% %recruit% %member% %officer% %coleader% %leader%");

    public ConfigText permissionYes = ConfigText.of(
            "<success_highlight>YES</success_highlight>");

    public ConfigText permissionNo = ConfigText.of(
            "<error_highlight>NOO</error_highlight>");



    @Comment({
            "",
            ">>> Chunk Access"
    })
    public ConfigText accessGranted = ConfigText.of(
            "%prefix% %subject% now has elevated privileges in this chunk");

    public ConfigText accessRevoked = ConfigText.of(
            "%prefix% %subject% now has standard privileges in this chunk");

    public ConfigText accessViewTitle = ConfigText.of(
            "<emphasis>%world% %chunk_x%, %chunk_z%</emphasis> <separator>|</separator> Chunk Access");

    public ConfigText accessViewBody = ConfigText.of(
            "<emphasis>Faction:</emphasis> <highlight>%faction%</highlight>",
            "<emphasis>Granted Players:</emphasis> <highlight>%players%</highlight>",
            "<emphasis>Granted Factions:</emphasis> <highlight>%factions%</highlight>"
    );



    @Comment({
            "",
            ">>> Player/Member State"
    })
    public ConfigText stateChange = ConfigText.of(
            "%prefix% <highlight>%subject%:</highlight> <emphasis>%state%</emphasis>");

    public ConfigText titleChange = ConfigText.of(
            "%prefix% %actor% modified the title for %player%");

    public ConfigText memberRank = ConfigText.of(
            "%prefix% %player% is a <highlight>%rank%</highlight> in %faction%");

    public ConfigText promote = ConfigText.of(
            "%prefix% %actor% promoted %player% to <highlight>%new_rank%</highlight> from %old_rank% in %faction%");

    public ConfigText demote = ConfigText.of(
            "%prefix% %actor% demoted %player% to <highlight>%new_rank%</highlight> from %old_rank% in %faction%");

    public ConfigText leader = ConfigText.of(
            "%prefix% %actor% gave %player% leadership of %faction%");

    public ConfigText unableToUpdateSelf = ConfigText.of(
            "%error_prefix% Unable to update the rank of yourself");

    public ConfigText unableToUpdateRank = ConfigText.of(
            "%error_prefix% Unable to update the rank of %player%");

    public ConfigText factionStatusTitle = ConfigText.of(
            "%faction% <separator>|</separator> Faction Status");

    public ConfigText factionStatusOnlineEntry = ConfigText.of(
            "%player% <separator>»</separator> <emphasis>%power%<bracket>/</bracket>%maxpower% Power</emphasis> <separator>|</separator> <green>✔ online");

    public ConfigText factionStatusOfflineEntry = ConfigText.of(
            "%player% <separator>»</separator> <emphasis>%power%<bracket>/</bracket>%maxpower% Power</emphasis> <separator>|</separator> <red>✘ offline");

    public ConfigText memberStatusTitle = ConfigText.of(
            "%player% <separator>|</separator> Member Status");

    public ConfigText memberStatusBody = ConfigText.of(
            "<emphasis>Power: %power_indicator%</emphasis> <highlight>%power%/%maxpower%</highlight>",
            "<emphasis>Power Gain:</emphasis> <highlight>%power_per_hour%/hour</highlight>",
            "<emphasis>Power Loss:</emphasis> <highlight>%power_per_death%/death</highlight>"
    );



    @Comment({
            "",
            ">>> Land Claiming"
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

    public ConfigText claimed = ConfigText.of(
            "claimed");

    public ConfigText unclaimed = ConfigText.of(
            "unclaimed");

    public ConfigText pillaged = ConfigText.of(
            "pillaged");

    public ConfigText conquered = ConfigText.of(
            "conquered");

    public ConfigText conquerFail = ConfigText.of(
            "%error_prefix% %faction% owns this land and is strong enough to keep it");

    public ConfigText conquerFromEdge = ConfigText.of(
            "%error_prefix% You must begin conquering at the edge of this territory");

    public ConfigText landOwned = ConfigText.of(
            "%prefix% This land is already owned by %faction%");

    public ConfigText insufficientPower = ConfigText.of(
            "%error_prefix% %faction% does not have enough power to claim this land");

    public ConfigText fillLimit = ConfigText.of(
            "%error_prefix% Reached the fill limit of <error_highlight>%limit% chunks</error_highlight>");

    public ConfigText claimTooFar = ConfigText.of(
            "%error_prefix% Unable to claim land too far away.");

    public ConfigText disableAutoSetting = ConfigText.of(
            "%prefix% Disabled auto-claim.");

    public ConfigText enableAutoClaim = ConfigText.of(
            "%prefix% Enabled auto-claim for %faction%");

    public ConfigText enableAutoUnclaim = ConfigText.of(
            "%prefix% Enabled auto-unclaim for %faction%");



    @Comment({
            "",
            ">>> Alert"
    })
    public ConfigText alertTitle = ConfigText.of(
            "<info>Faction Alert</info>");

    public ConfigText alertSubtitle = ConfigText.of(
            "<emphasis>%actor_name%: %alert%</emphasis>");

    public ConfigText alertMessage = ConfigText.of(
            "<b></b>",
            "<info>Faction Alert</info>",
            "<emphasis>%actor%: %alert%",
            "<b></b>"
    );



    @Comment({
            "",
            ">>> Map"
    })
    public ConfigText mapTitle = ConfigText.of(
            "<emphasis>%world% %chunk_x%, %chunk_z%</emphasis> <separator>|</separator> %faction%");

    public ConfigText mapLegendFormat = ConfigText.of(
            "%character%: %faction%");

    public ConfigText mapLegendOverflowFormat = ConfigText.of(
            "%character%: <error_highlight>Unable to represent all factions on this map.</error_highlight>");

    public ConfigText mapCenter = ConfigText.of(
            "<aqua>+</aqua>");

    public String mapCompassStyle = "dark_gray";

    public String mapCompassDirectionStyle = "white";
}
