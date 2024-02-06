package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.flag.FlagHolder;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.PermissionHolder;
import co.crystaldev.factions.api.member.Member;
import co.crystaldev.factions.api.member.Rank;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.store.FactionStore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@SuppressWarnings("unused")
@Data @NoArgsConstructor
public final class Faction {

    /** The unique ID of this faction */
    private final UUID id = UUID.randomUUID();

    private String name;

    private Component description;

    private Component motd;

    private final long createdAt = System.currentTimeMillis();

    private final ArrayList<UUID> invitees = new ArrayList<>();

    private final HashMap<UUID, Member> roster = new HashMap<>();

    private final HashMap<UUID, Member> members = new HashMap<>();

    private final ArrayList<Relation> relations = new ArrayList<>();

    private final ArrayList<Relation> relationRequests = new ArrayList<>();

    private final Map<String, FlagHolder<?>> flags = new HashMap<>();

    private final Map<String, PermissionHolder> permissions = new HashMap<>();

    @Getter
    private transient boolean dirty;

    public Faction(@NotNull String name, @NotNull Player owner) {
        this.name = name;

        Member member = new Member(owner.getUniqueId(), Rank.LEADER);
        this.members.put(owner.getUniqueId(), member);
        this.roster.put(owner.getUniqueId(), member);
        this.markDirty();
    }

    public void markDirty() {
        this.dirty = true;
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

    public int getMemberLimit() {
        return FactionConfig.getInstance().factionMemberLimit + this.getFlagValueOrDefault(FactionFlags.MEMBER_LIMIT_MODIFIER);
    }

    public int getRosterLimit() {
        return FactionConfig.getInstance().factionRosterLimit + this.getFlagValueOrDefault(FactionFlags.ROSTER_LIMIT_MODIFIER);
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

    public long getPowerLevel() {
        long powerLevel = 0L;
        for (Member member : this.members.values()) {
            powerLevel += member.getUser().getPower();
        }
        return powerLevel + this.getFlagValueOrDefault(FactionFlags.POWER_MODIFIER);
    }

    public boolean isOverClaimable() {
        return this.getPowerLevel() < 0;
    }

    public void joinFaction(@NotNull UUID player) {
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

    public void setPermission(@NotNull Rank rank, @NotNull Permission permission, boolean permitted) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> new PermissionHolder(permission));
        permValue.set(rank, permitted);
        this.markDirty();
    }

    public void setPermission(@NotNull RelationType relation, @NotNull Permission permission, boolean permitted) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> new PermissionHolder(permission));
        permValue.set(relation, permitted);
        this.markDirty();
    }

    @NotNull
    public RelationType relationTo(@Nullable Faction faction) {
        if (faction == null)
            return RelationType.NEUTRAL;

        for (Relation relation : this.relations) {
            if (relation.getFaction().equals(faction.getId())) {
                return relation.getType();
            }
        }
        return RelationType.NEUTRAL;
    }
}
