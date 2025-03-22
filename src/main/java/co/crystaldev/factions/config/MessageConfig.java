package co.crystaldev.factions.config;

import co.crystaldev.alpinecore.framework.config.AlpineConfig;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.config.type.ConfigText;
import de.exlll.configlib.Comment;

import java.util.LinkedHashMap;

/**
 * @since 0.1.0
 */
public final class MessageConfig extends AlpineConfig {

    @Override
    public String getFileName() {
        return "fmessages.yml";
    }

    @Comment({
            "Alpine Factions v{{ pluginVersion }}",
            "Developed by Crystal Development, LLC.",
            ""
    })
    public ConfigText alphanumeric = ConfigText.of(
            "<error>»</error> Your input must be alphanumeric");

    public ConfigText operationCancelled = ConfigText.of(
            "<error>»</error> This operation was cancelled");

    public ConfigText confirm = ConfigText.of(
            "<info>»</info> Please confirm by sending this command again");

    public ConfigText missingFactionPerm = ConfigText.of(
            "<error>»</error> <error_highlight>%faction%</error_highlight> does not allow you to <error_highlight>%action%</error_highlight>");

    public ConfigText notInFaction = ConfigText.of(
            "<error>»</error> You are not in a faction");

    public ConfigText playerNotInFaction = ConfigText.of(
            "<error>»</error> %player% is not in a faction");

    public ConfigText unknownFaction = ConfigText.of(
            "<error>»</error> No faction or player was found with the name <error_highlight>%value%</error_highlight>");

    public ConfigText outsideTerritory = ConfigText.of(
            "<error>»</error> You are not within the territory of %faction%");

    public ConfigText rankTooHigh = ConfigText.of(
            "<error>»</error> You can't set ranks higher than or matching your own");

    public ConfigText unknownRelational = ConfigText.of(
            "<error>»</error> No rank or relation was found with the ID <error_highlight>%value%</error_highlight");

    public ConfigText none = ConfigText.of(
            "<gray>None</gray>");

    public ConfigText hidden = ConfigText.of(
            "<gray>Hidden</gray>");



    @Comment({
            "",
            ">>> Faction Metadata"
    })
    public ConfigText rename = ConfigText.of(
            "<info>»</info> %actor% set the faction name to <highlight>%faction_name%</highlight>");

    public ConfigText factionWithName = ConfigText.of(
            "<error>»</error> A faction with the name <error_highlight>%faction_name%</error_highlight> already exists");

    public ConfigText factionNameUnchanged = ConfigText.of(
            "<error>»</error> The new name must differ from the existing name");

    public ConfigText nameTooShort = ConfigText.of(
            "<error>»</error> Your faction name can't be shorter than <error_highlight>%length% characters</error_highlight>");

    public ConfigText nameTooLong = ConfigText.of(
            "<error>»</error> Your faction name can't be longer than <error_highlight>%length% characters</error_highlight>");

    public ConfigText description = ConfigText.of(
            "<info>»</info> %actor% set the faction's description to:<br>%description%");

    public ConfigText motd = ConfigText.of(
            "<info>»</info> %actor% set the faction's message of the day to:<br>%motd%");

    public ConfigText motdTitle = ConfigText.of(
            "%faction% <separator>|</separator> Message of the Day");



    @Comment({
            "",
            ">>> Join"
    })
    public ConfigText join = ConfigText.of(
            "<info>»</info> You joined <highlight>%faction_name%</highlight>");

    public ConfigText forceJoin = ConfigText.of(
            "<info>»</info> %actor% added you to %faction%");

    public ConfigText memberJoin = ConfigText.of(
            "<info>»</info> %player% joined the faction");

    public ConfigText memberForceJoin = ConfigText.of(
            "<info>»</info> %inviter% added %player% to the faction");

    public ConfigText attemptedMemberJoin = ConfigText.of(
            "<info>»</info> %player% attempted to join the faction");

    public ConfigText fullFaction = ConfigText.of(
            "<error>»</error> %faction% has reached its member limit of <error_highlight>%limit% members</error_highlight>");

    public ConfigText alreadyInFaction = ConfigText.of(
            "<error>»</error> You must leave your current faction first");

    public ConfigText playerAlreadyInFaction = ConfigText.of(
            "<error>»</error> %player% is already in a faction");



    @Comment({
            "",
            ">>> Leave"
    })
    public ConfigText leave = ConfigText.of(
            "<info>»</info> You left <highlight>%faction_name%</highlight>");

    public ConfigText memberLeave = ConfigText.of(
            "<info>»</info> %player% left the faction");

    public ConfigText promoteLeader = ConfigText.of(
            "<error>»</error> You must promote a new leader first");

    public ConfigText kick = ConfigText.of(
            "<info>»</info> %actor% kicked %player% from the faction");

    public ConfigText kicked = ConfigText.of(
            "<info>»</info> %actor% kicked you from %faction%");

    public ConfigText cantKick = ConfigText.of(
            "<error>»</error> You can't kick %player% from the faction");



    @Comment({
            "",
            ">>> Invite"
    })
    public ConfigText notInvited = ConfigText.of(
            "<error>»</error> You are not invited to %faction%");

    public ConfigText playerNotInvited = ConfigText.of(
            "<error>»</error> %player% is not invited to %faction%");

    public ConfigText inviteFail = ConfigText.of(
            "<error>»</error> %player% is already a member of %faction%");

    public ConfigText invite = ConfigText.of(
            "<info>»</info> %actor% invited %invitee% to your faction");

    public ConfigText invited = ConfigText.of(
            "<info>»</info> %actor% invited you to %faction_name%");

    public ConfigText inviteRevoke = ConfigText.of(
            "<info>»</info> %actor% revoked the invitation of %invitee%");

    public ConfigText inviteListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Invitations");

    public ConfigText inviteListEntry = ConfigText.of(
            "%player% <separator>»</separator> Invited by %inviter%");



    @Comment({
            "",
            ">>> Roster"
    })
    public ConfigText rosterAdd = ConfigText.of(
            "<info>»</info> %actor% added %player% to your faction roster as a <highlight>%rank%</highlight>");

    public ConfigText rosterAdded = ConfigText.of(
            "<info>»</info> %actor% added you to %faction_name%'s roster");

    public ConfigText rosterRemove = ConfigText.of(
            "<info>»</info> %actor% removed %player% from your faction's roster");

    public ConfigText rosterRemoved = ConfigText.of(
            "<info>»</info> %actor% removed you from %faction_name%'s roster");

    public ConfigText rosterSetRank = ConfigText.of(
            "<info>»</info> %actor% set the rank of %player% to <highlight>%rank%</highlight> in your faction roster");

    public ConfigText rosterFull = ConfigText.of(
            "<error>»</error> The faction roster is full");

    public ConfigText alreadyOnRoster = ConfigText.of(
            "<error>»</error> %player% is already on %faction_name%'s roster");

    public ConfigText notOnRoster = ConfigText.of(
            "<error>»</error> %player% is not on %faction_name%'s roster");

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
            "<error>»</error> This relation is already set with %faction%");

    public ConfigText relationSelf = ConfigText.of(
            "<error>»</error> Unable to declare a relation with %faction%");

    public ConfigText truceLimit = ConfigText.of(
            "<error>»</error> You are limited to <error_highlight>%limit% Truces</error_highlight>");

    public ConfigText allyLimit = ConfigText.of(
            "<error>»</error> You are limited to <error_highlight>%limit% Allies</error_highlight>");

    public ConfigText relationWishListTitle = ConfigText.of(
            "Relation Requests");

    public ConfigText relationListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Relations");

    public ConfigText relationListEntry = ConfigText.of(
            "%faction% <separator>»</separator> %relation%");

    public LinkedHashMap<FactionRelation, ConfigText> relationDeclarations = new LinkedHashMap<>();
    {
        this.relationDeclarations.put(FactionRelation.NEUTRAL, ConfigText.of(
                "<info>»</info> %faction% is now a <aqua>neutral faction"));
        this.relationDeclarations.put(FactionRelation.TRUCE, ConfigText.of(
                "<info>»</info> %faction% is now a <light_purple>faction in truce"));
        this.relationDeclarations.put(FactionRelation.ALLY, ConfigText.of(
                "<info>»</info> %faction% is now an <dark_purple>allied faction"));
        this.relationDeclarations.put(FactionRelation.ENEMY, ConfigText.of(
                "<info>»</info> %faction% is now an <red>enemy faction"));
    }

    public LinkedHashMap<FactionRelation, ConfigText> relationRequest = new LinkedHashMap<>();
    {
        this.relationRequest.put(FactionRelation.NEUTRAL, ConfigText.of(
                "<info>»</info> %faction% were informed that you wish to be a <aqua>neutral faction"));
        this.relationRequest.put(FactionRelation.TRUCE, ConfigText.of(
                "<info>»</info> %faction% were informed that you wish to be a <light_purple>faction in truce"));
        this.relationRequest.put(FactionRelation.ALLY, ConfigText.of(
                "<info>»</info> %faction% were informed that you wish to be an <dark_purple>allied faction"));
    }

    public LinkedHashMap<FactionRelation, ConfigText> relationWishes = new LinkedHashMap<>();
    {
        this.relationWishes.put(FactionRelation.NEUTRAL, ConfigText.of(
                "<info>»</info> %faction% wishes to be a <aqua>neutral faction</aqua>. <aqua><b><click:run_command:\"/f neutral %faction_name%\">[Accept]"));
        this.relationWishes.put(FactionRelation.TRUCE, ConfigText.of(
                "<info>»</info> %faction% wishes to be a <light_purple>faction in truce</light_purple>. <light_purple><b><click:run_command:\"/f truce %faction_name%\">[Accept]"));
        this.relationWishes.put(FactionRelation.ALLY, ConfigText.of(
                "<info>»</info> %faction% wishes to be an <dark_purple>allied faction</dark_purple>. <dark_purple><b><click:run_command:\"/f ally %faction_name%\">[Accept]"));
    }



    @Comment({
            "",
            ">>> Faction State"
    })
    public ConfigText create = ConfigText.of(
            "<info>»</info> You created a new faction named <highlight>%faction_name%</highlight>");

    public ConfigText disband = ConfigText.of(
            "<info>»</info> %actor% disbanded %faction%");

    public ConfigText unableToDisband = ConfigText.of(
            "<error>»</error> This faction cannot be disbanded");

    public ConfigText setHome = ConfigText.of(
            "<info>»</info> %actor% set the faction home near <emphasis>(%world% %x%, %y%, %z%)</emphasis>");

    public ConfigText unsetHome = ConfigText.of(
            "<info>»</info> Your faction home was un-set due to it no longer being in your territory");

    public ConfigText home = ConfigText.of(
            "<info>»</info> Warping to the home of %faction% in <highlight>%seconds% seconds</highlight> unless you move...");

    public ConfigText homeInstant = ConfigText.of(
            "<info>»</info> Warping to the home of %faction%...");

    public ConfigText noHome = ConfigText.of(
            "<error>»</error> %faction% does not have a home set");

    public ConfigText listTitle = ConfigText.of(
            "Factions List");

    public ConfigText listEntry = ConfigText.of(
            "%faction% <separator>»</separator> %online%/%members% online, <emphasis>%land%/%power%/%max_power%</emphasis>");



    @Comment({
            "",
            ">>> Warps"
    })
    public ConfigText setWarp = ConfigText.of(
            "<info>»</info> %actor% set warp <highlight>%warp%</highlight> near <emphasis>(%location%)</emphasis>");

    public ConfigText delWarp = ConfigText.of(
            "<info>»</info> %actor% removed warp %warp% near <emphasis>(%location%)</emphasis>");

    public ConfigText warp = ConfigText.of(
            "<info>»</info> Warping to warp <highlight>%warp%</highlight> in <highlight>%seconds% seconds</highlight> unless you move...");

    public ConfigText warpInstant = ConfigText.of(
            "<info>»</info> Warping to warp <highlight>%warp%<highlight>...");

    public ConfigText noWarp = ConfigText.of(
            "<error>»</error> %faction% does not have a warp %warp% set");

    public ConfigText warpInvalidPassword = ConfigText.of(
            "<error>»</error> Incorrect password supplied for warp <highlight>%warp%</highlight>");

    public ConfigText unsetWarp = ConfigText.of(
            "<info>»</info> Your faction warp %warp% was un-set due to it no longer being in your territory");

    public ConfigText warpListTitle = ConfigText.of(
            "%faction% <separator>|</separator> Warps");

    public ConfigText warpListEntry = ConfigText.of(
            "%warp% <separator>»</separator> <emphasis>Password: %status% - Location: %location%<emphasis>");

    public ConfigText warpHasPassword = ConfigText.of(
            "<green>Yes</green>");

    public ConfigText warpNoPassword = ConfigText.of(
            "<red>No</red>");

    public ConfigText warpHidden = ConfigText.of(
            "<red><i>Hidden</i></red>");

    public ConfigText warpLocation = ConfigText.of(
            "%x%, %y%, %z%, %world%");



    @Comment({
            "",
            ">>> Flags"
    })
    public ConfigText unknownFlag = ConfigText.of(
            "<error>»</error> No flag was found with the ID <error_highlight>%value%</error_highlight>");

    public ConfigText invalidFlagValue = ConfigText.of(
            "<error>»</error> Invalid flag input for flag %flag%");

    public ConfigText updatedFlagValue = ConfigText.of(
            "<info>»</info> %actor% set the faction flag <highlight>%flag_name%</highlight> to <highlight>%state%</highlight>",
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
            "<error>»</error> No permission was found with the ID <error_highlight>%value%</error_highlight>");

    public ConfigText updatedPermissionValue = ConfigText.of(
            "<info>»</info> %actor% set the permission <highlight>%permission_name%</highlight> to <highlight>%state%</highlight> for %relation%");

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
    public ConfigText elevatedAccess = ConfigText.of(
            "<info>»</info> You have elevated access in this chunk");

    public ConfigText standardAccess = ConfigText.of(
            "<info>»</info> You have standard access in this chunk");

    public ConfigText accessGrantedSingle = ConfigText.of(
            "<info>»</info> %subject% now has elevated privileges in this chunk");

    public ConfigText accessRevokedSingle = ConfigText.of(
            "<info>»</info> %subject% now has standard privileges in this chunk");

    public ConfigText accessGrantedAll = ConfigText.of(
            "<info>»</info> %subject% now has elevated privileges in <highlight>%amount% chunks</highlight>");

    public ConfigText accessRevokedAll = ConfigText.of(
            "<info>»</info> %subject% now has standard privileges in <highlight>%amount% chunks</highlight>");

    public ConfigText accessGranted = ConfigText.of(
            "<info>»</info> %subject% now has elevated privileges in <highlight>%amount% chunks</highlight> near <emphasis>(%world% %chunk_x%, %chunk_z%)</emphasis> using %type%");

    public ConfigText accessRevoked = ConfigText.of(
            "<info>»</info> %subject% now has standard privileges in <highlight>%amount% chunks</highlight> near <emphasis>(%world% %chunk_x%, %chunk_z%)</emphasis> using %type%");

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
            "<info>»</info> <highlight>%subject%:</highlight> <emphasis>%state%</emphasis>");

    public ConfigText mapHeightChange = ConfigText.of(
            "<info>»</info> Set automatic faction map height to <highlight>%size% rows</highlight>");

    public ConfigText modifyPower = ConfigText.of(
            "<info>»</info> Set power for %player% to <highlight>%power% power</highlight>",
            "    %power_indicator% <highlight>%power%/%maxpower%</highlight>");

    public ConfigText modifyPowerBoost = ConfigText.of(
            "<info>»</info> Set power boost for %player% to <highlight>%powerboost% power</highlight>",
            "    %power_indicator% <highlight>%power%/%maxpower%</highlight>");

    public ConfigText titleChange = ConfigText.of(
            "<info>»</info> %actor% modified the title for %player%");

    public ConfigText memberRank = ConfigText.of(
            "<info>»</info> %player% is a <highlight>%rank%</highlight> in %faction%");

    public ConfigText promote = ConfigText.of(
            "<info>»</info> %actor% promoted %player% to <highlight>%new_rank%</highlight> from %old_rank% in %faction%");

    public ConfigText demote = ConfigText.of(
            "<info>»</info> %actor% demoted %player% to <highlight>%new_rank%</highlight> from %old_rank% in %faction%");

    public ConfigText leader = ConfigText.of(
            "<info>»</info> %actor% gave %player% leadership of %faction%");

    public ConfigText unableToUpdateSelf = ConfigText.of(
            "<error>»</error> Unable to update the rank of yourself");

    public ConfigText unableToUpdateRank = ConfigText.of(
            "<error>»</error> Unable to update the rank of %player%");

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
            ">>> Combat"
    })
    public ConfigText cantHurtFriendly = ConfigText.of(
            "<error>»</error> You can't hurt %player%");

    public ConfigText cantHurtNeutral = ConfigText.of(
            "<error>»</error> You can't hurt neutral players in their own territory unless you declare them as an enemy");

    public ConfigText attemptedDamage = ConfigText.of(
            "<info>»</info> %attacker% tried to hurt you");

    public ConfigText combatDisabled = ConfigText.of(
            "<info>»</info> Combat is disabled in %faction%");



    @Comment({
            "",
            ">>> Land Claiming"
    })
    public ConfigText landClaim = ConfigText.of(
            "<info>»</info> %actor% %claim_type% <highlight>%amount% chunks</highlight> near <emphasis>(%world% %chunk_x%, %chunk_z%)</emphasis> using %type%",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimSingle = ConfigText.of(
            "<info>»</info> %actor% %claim_type% <highlight>%amount% chunk</highlight> at <emphasis>(%world% %chunk_x%, %chunk_z%)</emphasis>",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimWorld = ConfigText.of(
            "<info>»</info> %actor% %claim_type% <highlight>%amount% chunks</highlight> in <emphasis>%world%</emphasis>",
            "    <i>%old_faction% → %new_faction%</i>"
    );

    public ConfigText landClaimAll = ConfigText.of(
            "<info>»</info> %actor% %claim_type% <highlight>%amount% chunks</highlight> in <emphasis>all worlds</emphasis>",
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
            "<error>»</error> %faction% owns this land and is strong enough to keep it");

    public ConfigText conquerFromEdge = ConfigText.of(
            "<error>»</error> You must begin conquering at the edge of this territory");

    public ConfigText landOwned = ConfigText.of(
            "<info>»</info> This land is already owned by %faction%");

    public ConfigText insufficientPower = ConfigText.of(
            "<error>»</error> %faction% does not have enough power to claim this land");

    public ConfigText fillLimit = ConfigText.of(
            "<error>»</error> Reached the fill limit of <error_highlight>%limit% chunks</error_highlight>");

    public ConfigText claimTooFar = ConfigText.of(
            "<error>»</error> Unable to claim land too far away.");

    public ConfigText disableAutoSetting = ConfigText.of(
            "<info>»</info> Disabled auto-claim.");

    public ConfigText enableAutoClaim = ConfigText.of(
            "<info>»</info> Enabled auto-claim for %faction%");

    public ConfigText enableAutoUnclaim = ConfigText.of(
            "<info>»</info> Enabled auto-unclaim for %faction%");



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

    public ConfigText mapBorder = ConfigText.of(
            "<black>-</black>");

    public String mapCompassStyle = "dark_gray";

    public String mapCompassDirectionStyle = "white";



    @Comment({
            "",
            ">>> Login/Logout"
    })
    public ConfigText login = ConfigText.of(
            "<info>»</info> %player% has logged in");

    public ConfigText logout = ConfigText.of(
            "<error>»</error> %player% has logged off");



    @Comment({
            "",
            ">>> Informational Commands"
    })
    public ConfigText locationBroadcast = ConfigText.of(
            "<info>»</info> %player% pinged their location near <emphasis>(%world% %x%, %y%, %z%)</emphasis>");
}
