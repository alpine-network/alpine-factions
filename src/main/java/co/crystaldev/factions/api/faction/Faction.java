package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.accessor.Accessors;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.DisbandFactionEvent;
import co.crystaldev.factions.api.faction.member.MemberInvitation;
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
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@SuppressWarnings("unused")
@ToString
public final class Faction {

    public static final String WILDERNESS_ID = "factions_wilderness";

    public static final String SAFEZONE_ID = "factions_safezone";

    public static final String WARZONE_ID = "factions_warzone";

    public static final Component DEFAULT_DESCRIPTION = ComponentHelper.mini("<gray>No description set");

    public static final Component DEFAULT_MOTD = ComponentHelper.mini("<gray>No message of the day set");

    /** The unique ID of this faction */
    @Getter
    private @NotNull final String id;

    @Getter @Setter
    private @NotNull String name;

    @Getter @Setter
    private @Nullable Component description;

    @Getter @Setter
    private @Nullable Component motd;

    @Getter
    private final long createdAt = System.currentTimeMillis();

    private final HashMap<UUID, MemberInvitation> invitees = new HashMap<>();

    private final HashMap<UUID, Member> roster = new HashMap<>();

    private final HashMap<UUID, Member> members = new HashMap<>();

    private final HashMap<String, FactionRelation> relationRequests = new HashMap<>();

    private final Map<String, FlagHolder<?>> flags = new HashMap<>();

    private final Map<String, PermissionHolder> permissions = new HashMap<>();

    @Getter @Setter
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

    public boolean isMinimal() {
        return this.getFlagValueOrDefault(FactionFlags.MINIMAL_VISIBILITY);
    }

    public boolean isWilderness() {
        return this.id.equals(WILDERNESS_ID);
    }

    public boolean canDisband() {
        return !this.getFlagValueOrDefault(FactionFlags.PERMANENT);
    }

    public void disband(@NotNull CommandSender actor) {
        MessageConfig config = MessageConfig.getInstance();

        FactionAccessor factions = Accessors.factions();
        ClaimAccessor claims = Accessors.claims();
        Faction actingFaction = factions.findOrDefault(actor);

        // call event
        DisbandFactionEvent event = AlpineFactions.callEvent(new DisbandFactionEvent(this, actor));
        if (event.isCancelled()) {
            config.operationCancelled.send(actor);
            return;
        }

        // notify the faction
        FactionHelper.broadcast(this, actor, observer -> {
            return config.disband.build(
                    "actor", FactionHelper.formatRelational(observer, actingFaction, actor),
                    "actor_name", PlayerHelper.getName(actor),
                    "faction", FactionHelper.formatRelational(observer, this),
                    "faction_name", this.getName()
            );
        });

        // remove all claims
        for (ClaimedChunk claim : claims.getClaims(this)) {
            claims.remove(claim.getWorld(), claim.getChunkX(), claim.getChunkZ());
        }

        // remove the faction from the registry
        factions.unregister(this);
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
        return Accessors.claims().countClaims(this, world);
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
    public FactionRelation relationTo(@Nullable Faction faction) {
        if (faction == null) {
            return FactionRelation.NEUTRAL;
        }

        if (this.equals(faction)) {
            return FactionRelation.SELF;
        }

        return this.relationRequests.getOrDefault(faction.getId(), FactionRelation.NEUTRAL);
    }

    public boolean isRelation(@Nullable Faction faction, @NotNull FactionRelation relation) {
        if (faction == null) {
            return relation == FactionRelation.NEUTRAL;
        }

        FactionRelation relationToOther = this.relationTo(faction);
        FactionRelation relationToSelf = faction.relationTo(this);
        return relationToOther == relationToSelf && relationToOther == relation;
    }

    public void setRelation(@NotNull Faction faction, @NotNull FactionRelation relation) {
        this.markDirty();

        if (relation == FactionRelation.NEUTRAL) {
            this.relationRequests.remove(faction.getId());
            return;
        }

        this.relationRequests.put(faction.getId(), relation);
    }

    @NotNull
    public Set<RelatedFaction> getRelatedFactions() {
        Set<RelatedFaction> related = new HashSet<>();
        FactionAccessor factions = Accessors.factions();

        this.relationRequests.forEach((factionId, type) -> {
            Faction other = factions.getById(factionId);
            if (other != null && other.relationTo(this) == type) {
                related.add(new RelatedFaction(other, type));
            }
        });

        return related;
    }

    @NotNull
    public Set<RelatedFaction> getRelationWishes() {
        Set<RelatedFaction> related = new HashSet<>();
        FactionAccessor factions = Accessors.factions();

        this.relationRequests.forEach((factionId, type) -> {
            Faction other = factions.getById(factionId);
            if (other != null && other.relationTo(this) != type) {
                related.add(new RelatedFaction(other, type));
            }
        });

        return related;
    }

    @NotNull
    public Set<Faction> getRelatedFactions(@NotNull FactionRelation relation) {
        Set<Faction> related = new HashSet<>();
        FactionAccessor factions = Accessors.factions();

        this.relationRequests.forEach((factionId, type) -> {
            if (type != relation) {
                return;
            }

            Faction other = factions.getById(factionId);
            if (other != null) {
                related.add(other);
            }
        });

        return ImmutableSet.copyOf(related);
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

    public boolean isPermitted(@NotNull ServerOperator player, @NotNull Permission permission) {
        if (!(player instanceof OfflinePlayer)) {
            // allow console
            return true;
        }

        if (player instanceof Player && PlayerHandler.getInstance().isOverriding((Player) player)) {
            // allow players with admin access
            return true;
        }

        OfflinePlayer offlinePlayer = (OfflinePlayer) player;

        if (this.isMember(offlinePlayer.getUniqueId())) {
            return this.isPermitted(this.getMember(offlinePlayer).getRank(), permission);
        }
        else {
            Faction other = Accessors.factions().find(player);
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

    public boolean isPermitted(@NotNull FactionRelation relation, @NotNull Permission permission) {
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

    public void wrapMember(@NotNull UUID member, @NotNull Consumer<@NotNull Member> consumer) {
        Member found = this.getMember(member);
        if (found == null) {
            return;
        }

        consumer.accept(found);

        this.members.put(member, found);
        if (this.isOnRoster(member)) {
            this.roster.put(member, found);
        }

        this.markDirty();
    }

    @NotNull
    public Collection<Member> getMembers() {
        return this.members.values();
    }

    @NotNull
    public Collection<Member> getOnlineMembers() {
        List<Member> members = new ArrayList<>();

        for (Member member : this.members.values()) {
            if (member.isOnline()) {
                members.add(member);
            }
        }

        return ImmutableSet.copyOf(members);
    }

    @NotNull
    public Collection<Member> getOfflineMembers() {
        List<Member> members = new ArrayList<>();

        for (Member member : this.members.values()) {
            if (!member.isOnline()) {
                members.add(member);
            }
        }

        return ImmutableSet.copyOf(members);
    }

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
                Faction faction = Accessors.factions().find(owner);
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

    @NotNull
    public Set<MemberInvitation> getInvitations() {
        return new HashSet<>(this.invitees.values());
    }

    public void addInvitation(@NotNull UUID player, @NotNull UUID inviter) {
        this.invitees.put(player, new MemberInvitation(player, inviter));
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

    public void addMember(@NotNull UUID player) {
        this.addMember(player, Rank.getDefault());
    }

    public void addMember(@NotNull UUID player, @NotNull Rank rank) {
        // remove the player's invitation
        this.invitees.remove(player);

        // add the member to the member list
        Member member = this.roster.getOrDefault(player, new Member(player, rank));
        member.setRank(rank);
        this.members.put(player, member);
        if (this.isOnRoster(player)) {
            this.roster.put(player, member);
        }

        // mark this faction for update
        this.markDirty();
    }

    public void removeMember(@NotNull UUID player) {
        this.members.remove(player);
        this.invitees.remove(player);
        this.markDirty();
    }

    public boolean canJoin(@NotNull UUID player) {
        if (this.isMember(player)) {
            return false;
        }
        return this.getFlagValueOrDefault(FactionFlags.OPEN) || this.isOnRoster(player) || this.isInvited(player);
    }

    public boolean isInvited(@NotNull UUID player) {
        return this.invitees.containsKey(player);
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
    public MemberInvitation getInvite(@NotNull UUID player) {
        return this.invitees.get(player);
    }

    @Nullable
    public Member getMember(@NotNull UUID player) {
        return this.members.get(player);
    }

    @NotNull
    public Member getMember(@NotNull OfflinePlayer player) {
        if (this.isWilderness()) {
            return new Member(player.getUniqueId(), Rank.getDefault());
        }

        Member member = this.members.get(player.getUniqueId());
        if (member == null) {
            throw new NoSuchElementException(player.getName() + " is not a faction member");
        }
        else {
            return member;
        }
    }

    @Nullable
    public Rank getMemberRank(@NotNull UUID player) {
        Member member = this.getMember(player);
        if (member == null) {
            member = this.roster.get(player);
        }
        return member == null ? null : member.getRank();
    }

    @NotNull
    public Rank getMemberRank(@NotNull UUID player, @NotNull Rank defaultRank) {
        Rank rank = this.getMemberRank(player);
        return rank == null ? defaultRank : rank;
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
