package co.crystaldev.factions.api.faction.permission;

import co.crystaldev.factions.PermissionNodes;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import lombok.experimental.UtilityClass;

/**
 * @since 0.1.0
 */
@UtilityClass
public final class Permissions {

    public static final Permission MODIFY_NAME = Permission.builder("modify_name")
            .name("Modify Name")
            .description("ability to modify the faction name")
            .permit(Rank.LEADER, Rank.COLEADER)
            .build();

    public static final Permission MODIFY_DESCRIPTION = Permission.builder("modify_description")
            .name("Modify Description")
            .description("ability to modify the faction description")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission MODIFY_MOTD = Permission.builder("modify_motd")
            .name("Modify MOTD")
            .description("ability to modify the faction motd")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission MODIFY_TITLE = Permission.builder("modify_title")
            .name("Modify Member Titles")
            .description("ability to modify member titles")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission ALERT = Permission.builder("alert")
            .name("Alert")
            .description("alert the faction")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission STATUS = Permission.builder("status")
            .name("Status")
            .description("view the state of the faction")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission INVITE_MEMBERS = Permission.builder("invite_members")
            .name("Invite Members")
            .description("ability to invite members")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission KICK_MEMBERS = Permission.builder("kick_members")
            .name("Kick Members")
            .description("ability to kick members")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission MODIFY_ROSTER = Permission.builder("modify_roster")
            .name("Modify Roster")
            .description("ability to modify roster")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission MODIFY_TERRITORY = Permission.builder("modify_territory")
            .name("Modify Territory")
            .description("ability to modify territory")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission MODIFY_ACCESS = Permission.builder("modify_access")
            .name("Modify Access")
            .description("ability to change access")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission MODIFY_FLAGS = Permission.builder("modify_flags")
            .name("Modify Flags")
            .description("ability to change flags")
            .permit(Rank.LEADER, Rank.COLEADER)
            .build();

    public static final Permission MODIFY_PERMS = Permission.builder("modify_perms")
            .name("Modify Permissions")
            .description("ability to change permissions")
            .permit(Rank.LEADER, Rank.COLEADER)
            .build();

    public static final Permission MODIFY_RELATIONS = Permission.builder("modify_relations")
            .name("Modify Relations")
            .description("ability to change relations")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission BANK_WITHDRAW = Permission.builder("bank_withdraw")
            .name("Bank Withdraw")
            .description("withdraw from the faction")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission BANK_DEPOSIT = Permission.builder("bank_deposit")
            .name("Bank Deposit")
            .description("deposit to the faction")
            .permit(Rank.values())
            .build();

    public static final Permission ACCESS_HOME = Permission.builder("access_home")
            .name("Access Home")
            .description("access the faction home")
            .permit(Rank.values())
            .build();

    public static final Permission MODIFY_HOME = Permission.builder("modify_home")
            .name("Modify Home")
            .description("set the faction home")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER)
            .build();

    public static final Permission USE_CONTAINERS = Permission.builder("access_containers")
            .name("Access Containers")
            .description("access containers")
            .permission(PermissionNodes.PERMISSION_CONTAINERS)
            .permit(Rank.values())
            .permit(FactionRelation.values())
            .build();

    public static final Permission BUILD = Permission.builder("build")
            .name("Build")
            .description("edit the terrain")
            .permit(Rank.values())
            .build();

    public static final Permission OPEN_DOORS = Permission.builder("open_doors")
            .name("Doors")
            .description("ability to open doors")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER, Rank.MEMBER)
            .build();

    public static final Permission USE_PRESSURE_PLATES = Permission.builder("pressure_plates")
            .name("Pressure Plates")
            .description("ability to trigger pressure plates")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER, Rank.MEMBER)
            .build();

    public static final Permission USE_SWITCHES = Permission.builder("switches")
            .name("Switches")
            .description("ability to access buttons and levers")
            .permit(Rank.LEADER, Rank.COLEADER, Rank.OFFICER, Rank.MEMBER)
            .build();

    public static final Permission[] VALUES = {
            MODIFY_NAME,
            MODIFY_DESCRIPTION,
            MODIFY_MOTD,
            MODIFY_TITLE,
            ALERT,
            STATUS,
            INVITE_MEMBERS,
            KICK_MEMBERS,
            MODIFY_ROSTER,
            MODIFY_TERRITORY,
            MODIFY_ACCESS,
            MODIFY_FLAGS,
            MODIFY_PERMS,
            MODIFY_RELATIONS,
            BANK_WITHDRAW,
            BANK_DEPOSIT,
            ACCESS_HOME,
            MODIFY_HOME,
            USE_CONTAINERS,
            BUILD,
            OPEN_DOORS,
            USE_PRESSURE_PLATES,
            USE_SWITCHES
    };
}
