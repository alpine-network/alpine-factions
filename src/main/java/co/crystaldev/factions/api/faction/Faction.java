package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.flag.FlagHolder;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.PermissionHolder;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.store.ClaimStore;
import co.crystaldev.factions.util.ComponentHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@SuppressWarnings("unused")
@Getter @Setter @ToString
public final class Faction {

    public static final Component DEFAULT_DESCRIPTION = ComponentHelper.mini("<gray>No description set");

    public static final Component DEFAULT_MOTD = ComponentHelper.mini("<gray>No message of the day set");

    /** The unique ID of this faction */
    private final String id;

    private String name;

    private Component description;

    private Component motd;

    private final long createdAt = System.currentTimeMillis();

    private final ArrayList<UUID> invitees = new ArrayList<>();

    private final HashMap<UUID, Member> roster = new HashMap<>();

    private final HashMap<UUID, Member> members = new HashMap<>();

    private final HashMap<String, RelationType> relationRequests = new HashMap<>();

    private final Map<String, FlagHolder<?>> flags = new HashMap<>();

    private final Map<String, PermissionHolder> permissions = new HashMap<>();

    @Getter
    private transient boolean dirty;

    public Faction() {
        this.id = UUID.randomUUID().toString();
    }

    public Faction(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
        this.markDirty();
    }

    public Faction(@NotNull String name, @NotNull Player owner) {
        this(UUID.randomUUID().toString(), name);
        Member member = new Member(owner.getUniqueId(), Rank.LEADER);
        this.members.put(owner.getUniqueId(), member);
        this.roster.put(owner.getUniqueId(), member);
    }

    // region Territory

    public boolean hasInfinitePower() {
        return this.getFlagValueOrDefault(FactionFlags.INFINITE_POWER);
    }

    public long getPowerLevel() {
        if (this.hasInfinitePower()) {
            return Integer.MAX_VALUE;
        }

        long powerLevel = 0L;
        for (Member member : this.members.values()) {
            FPlayer user = member.getUser();
            powerLevel += Math.round(user.getPowerLevel()) + user.getPowerBoost();
        }

        return powerLevel;
    }

    public long getMaxPowerLevel() {
        if (this.hasInfinitePower()) {
            return Integer.MAX_VALUE;
        }

        FactionConfig config = FactionConfig.getInstance();

        long powerLevel = 0L;
        for (Member member : this.members.values()) {
            FPlayer user = member.getUser();
            powerLevel += config.maxPlayerPower + user.getPowerBoost();
        }

        return powerLevel + this.getFlagValueOrDefault(FactionFlags.POWER_MODIFIER);
    }

    public long getRemainingPower() {
        if (this.hasInfinitePower()) {
            return Integer.MAX_VALUE;
        }

        return this.getMaxPowerLevel() - this.getPowerLevel();
    }

    public int getClaimCount(@Nullable World world) {
        return ClaimStore.getInstance().countClaims(this, world);
    }

    public int getClaimCount() {
        return this.getClaimCount(null);
    }

    public boolean isConquerable() {
        return this.getClaimCount() > this.getPowerLevel();
    }

    // endregion Territory

    // region Relations

    @NotNull
    public RelationType relationTo(@Nullable Faction faction) {
        if (faction == null)
            return RelationType.NEUTRAL;
        if (this.equals(faction))
            return RelationType.SELF;
        return this.relationRequests.getOrDefault(faction.getId(), RelationType.NEUTRAL);
    }

    public boolean isRelation(@Nullable Faction faction, @NotNull RelationType relation) {
        if (faction == null) {
            return relation == RelationType.NEUTRAL;
        }

        RelationType relationToOther = this.relationTo(faction);
        RelationType relationToSelf = faction.relationTo(this);
        return relationToOther == relationToSelf && relationToOther == relation;
    }

    // endregion Relations

    // region Permissions & Flags

    @Nullable @SuppressWarnings("unchecked")
    public <T> T getFlagValue(@NotNull FactionFlag<T> flag) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> {
            this.markDirty();
            return flag.wrapDefaultValue();
        });
        return flagHolder.getValue();
    }

    @NotNull @SuppressWarnings("unchecked")
    public <T> T getFlagValueOrDefault(@NotNull FactionFlag<T> flag) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> {
            this.markDirty();
            return flag.wrapDefaultValue();
        });

        T value = flagHolder.getValue();
        if (value == null) {
            flagHolder.setValue(value = flag.getDefaultState());
            this.markDirty();
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> void setFlagValue(@NotNull FactionFlag<T> flag, @NotNull T value) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> flag.wrapDefaultValue());
        flagHolder.setValue(value);
        this.markDirty();
    }

    public boolean isPermitted(@NotNull OfflinePlayer player, @NotNull Permission permission) {
        if (this.isMember(player.getUniqueId())) {
            return this.isPermitted(this.getMember(player).getRank(), permission);
        }
        else {
            Faction other = FactionStore.getInstance().findFaction(player);
            return this.isPermitted(this.relationTo(other), permission);
        }
    }

    public boolean isPermitted(@NotNull Rank rank, @NotNull Permission permission) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> {
            this.markDirty();
            return new PermissionHolder(permission);
        });
        return permValue.isPermitted(rank);
    }

    public boolean isPermitted(@NotNull RelationType relation, @NotNull Permission permission) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> {
            this.markDirty();
            return new PermissionHolder(permission);
        });
        return permValue.isPermitted(relation);
    }

    public void setPermission(@NotNull Permission permission, boolean permitted, @NotNull Relational relation) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> new PermissionHolder(permission));
        permValue.set(relation, permitted);
        this.markDirty();
    }

    public void setPermissionBulk(@NotNull Permission permission, boolean permitted, @NotNull Relational... relations) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> new PermissionHolder(permission));
        permValue.set(permitted, relations);
        this.markDirty();
    }

    // endregion Permissions & Flags

    // region Roster

    public int getMemberLimit() {
        return FactionConfig.getInstance().memberLimit + this.getFlagValueOrDefault(FactionFlags.MEMBER_LIMIT_MODIFIER);
    }

    public int getRosterLimit() {
        return FactionConfig.getInstance().rosterLimit + this.getFlagValueOrDefault(FactionFlags.ROSTER_LIMIT_MODIFIER);
    }

    public int getMemberCount() {
        return this.members.size();
    }

    public int getRosterCount() {
        return this.roster.size();
    }

    public boolean isFull() {
        return this.members.size() >= this.getMemberLimit();
    }

    public boolean isRosterFull() {
        return this.roster.size() >= this.getRosterLimit();
    }

    // endregion Roster

    // region Members

    public int countOnlineMembers() {
        int members = 0;

        for (Member member : this.members.values()) {
            if (member.isOnline()) {
                members++;
            }
        }

        return members;
    }

    public int countMembers() {
        return this.members.size();
    }

    public int countTotalMembers() {
        return this.members.size() + this.roster.size();
    }

    public boolean hasOwner() {
        for (Member member : this.members.values()) {
            if (member.getRank() == Rank.LEADER) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Member getOwner() {
        for (Member member : this.members.values()) {
            if (member.getRank() == Rank.LEADER) {
                return member;
            }
        }
        return null;
    }

    public void setOwner(@Nullable UUID owner) {
        // demote the current owner
        if (this.hasOwner()) {
            Member resolved = this.getOwner();
            this.setMemberRank(resolved.getId(), resolved.getRank().getPreviousRank());
        }

        // a replacement owner was provided
        if (owner != null) {
            if (this.isInFaction(owner)) {
                // replacement owner is in the faction already
                this.setMemberRank(owner, Rank.LEADER);
            }
            else {
                // move replacement owner into this faction
                Faction faction = FactionStore.getInstance().findFaction(owner);
                if (faction != null) {
                    faction.removeMember(owner);
                }
                this.addMember(owner, Rank.LEADER);
            }
        }
    }

    public void setMemberRank(@NotNull UUID member, @Nullable Rank rank) {
        // ensure member is in this faction
        if (!this.isInFaction(member)) {
            return;
        }

        // default to Recruit if no rank was provided
        if (rank == null) {
            rank = Rank.getDefault();
        }

        Member resolved = this.roster.get(member);
        if (resolved != null) {
            // update the roster
            resolved.setRank(rank);
        }

        resolved = this.members.get(member);
        if (resolved != null) {
            // update the actual member
            resolved.setRank(rank);
        }

        this.markDirty();
    }

    public void memberJoined(@NotNull UUID player) {
        // remove the player's invitation
        this.invitees.remove(player);

        // add the member to the member list
        Member member = this.roster.getOrDefault(player, new Member(player, Rank.getDefault()));
        this.members.put(player, member);

        // mark this faction for update
        this.markDirty();
    }

    public void inviteMember(@NotNull UUID player) {
        this.invitees.add(player);
        this.markDirty();
    }

    public void removeInvitation(@NotNull UUID player) {
        this.invitees.remove(player);
        this.markDirty();
    }

    public void addMemberToRoster(@NotNull UUID player, @NotNull Rank rank) {
        this.roster.put(player, new Member(player, rank));
        this.markDirty();
    }

    public void removeMemberFromRoster(@NotNull UUID player) {
        this.roster.remove(player);
        this.markDirty();
    }

    public void addMember(@NotNull UUID player, @NotNull Rank rank) {
        this.members.put(player, new Member(player, rank));
        this.markDirty();
    }

    public void removeMember(@NotNull UUID player) {
        this.members.remove(player);
        this.markDirty();
    }

    public boolean isInvited(@NotNull UUID player) {
        return this.invitees.contains(player);
    }

    public boolean isOnRoster(@NotNull UUID player) {
        return this.roster.containsKey(player);
    }

    public boolean isMember(@NotNull UUID player) {
        return this.members.containsKey(player);
    }

    public boolean isInFaction(@NotNull UUID player) {
        return this.isOnRoster(player) || this.isMember(player);
    }

    @Nullable
    public Member getMember(@NotNull UUID player) {
        return this.members.get(player);
    }

    @NotNull
    public Member getMember(@NotNull OfflinePlayer player) {
        Member member = this.members.get(player.getUniqueId());
        if (member == null) {
            throw new NoSuchElementException(player.getName() + " is not a faction member");
        }
        else {
            return member;
        }
    }

    // endregion Members

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Faction))
            return false;
        Faction other = (Faction) obj;
        return this.id.equals(other.getId());
    }
}
