package co.crystaldev.factions.api.faction;

import co.crystaldev.alpinecore.Reference;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.event.DisbandFactionEvent;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.flag.FlagHolder;
import co.crystaldev.factions.api.faction.member.Member;
import co.crystaldev.factions.api.faction.member.MemberInvitation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.PermissionHolder;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.FactionConfig;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.FactionHelper;
import co.crystaldev.factions.util.PlayerHelper;
import co.crystaldev.factions.util.RelationHelper;
import com.google.common.collect.ImmutableSet;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public final class Faction {

    public static final String WILDERNESS_ID = "factions_wilderness";

    public static final String SAFEZONE_ID = "factions_safezone";

    public static final String WARZONE_ID = "factions_warzone";

    public static final Component DEFAULT_DESCRIPTION = ComponentHelper.mini("<gray>No description set");

    public static final Component DEFAULT_MOTD = ComponentHelper.mini("<gray>No message of the day set");

    /**
     * The unique ID of this faction
     */
    private final @NotNull String id;

    private @NotNull String name;

    private @Nullable Component description;

    private @Nullable Component motd;

    private @Nullable Location home;

    private final long createdAt = System.currentTimeMillis();

    private final HashMap<UUID, MemberInvitation> invitees = new HashMap<>();

    private final HashMap<UUID, Member> roster = new HashMap<>();

    private final HashMap<UUID, Member> members = new HashMap<>();

    private final HashMap<String, FactionRelation> relationRequests = new HashMap<>();

    private final HashSet<Warp> facWarps = new HashSet<>();

    private final Map<String, FlagHolder<?>> flags = new HashMap<>();

    private final Map<String, PermissionHolder> permissions = new HashMap<>();

    private transient boolean dirty;

    @ApiStatus.Internal
    public Faction() {
        this.id = UUID.randomUUID().toString();
    }

    public Faction(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
        this.setDirty(true);
    }

    public Faction(@NotNull String name, @NotNull Player owner) {
        this(UUID.randomUUID().toString(), name);
        Member member = new Member(owner.getUniqueId(), Rank.LEADER);
        this.members.put(owner.getUniqueId(), member);
        this.roster.put(owner.getUniqueId(), member);
    }

    // region State
    public boolean isMinimal() {
        return this.getFlagValueOrDefault(FactionFlags.MINIMAL_VISIBILITY);
    }

    public boolean isWilderness() {
        return this.id.equals(WILDERNESS_ID);
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        if (name.equals(this.name)) {
            return;
        }

        this.name = name;
        this.setDirty(true);
    }

    public @Nullable Component getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable Component description) {
        if (Objects.equals(description, this.description)) {
            return;
        }

        this.description = description;
        this.setDirty(true);
    }

    public @Nullable Component getMotd() {
        return this.motd;
    }

    public void setMotd(@Nullable Component motd) {
        if (Objects.equals(motd, this.motd)) {
            return;
        }

        this.motd = motd;
        this.setDirty(true);
    }

    public @Nullable Location getHome() {
        return this.home;
    }

    public void setHome(@Nullable Location home) {
        if (home != null) {
            home = new Location(home.getWorld(), home.getBlockX() + 0.5, home.getBlockY(), home.getBlockZ() + 0.5);
        }

        if (Objects.equals(home, this.home)) {
            return;
        }

        this.home = home;
        this.setDirty(true);
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean canDisband() {
        return !this.getFlagValueOrDefault(FactionFlags.PERMANENT);
    }

    public void disband(@NotNull CommandSender actor) {
        MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);

        FactionAccessor factions = Factions.registry();
        ClaimAccessor claims = Factions.claims();
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
                    "actor", RelationHelper.formatPlayerName(observer, actor),
                    "actor_name", PlayerHelper.getName(actor),
                    "faction", RelationHelper.formatFactionName(observer, this),
                    "faction_name", this.getName()
            );
        });

        // remove all claims
        for (ClaimedChunk claim : claims.getClaims(this)) {
            claims.remove(claim.getWorld(), claim.getX(), claim.getZ());
        }

        // remove the faction from the registry
        factions.unregister(this);
    }
    // endregion State

    // region Warps
    public void addWarp(@NotNull Warp warp) {
        for (Warp existing : this.facWarps) {
            // update existing warp
            if (existing.getName().equals(warp.getName())) {
                existing.updateWarp(warp.getLocation(), warp.getPassword());
                this.setDirty(true);
                return;
            }
        }

        // add warp if non-existent
        this.facWarps.add(warp);
        this.setDirty(true);
    }

    public void delWarp(@NotNull Warp warp) {
        this.facWarps.remove(warp);
        this.setDirty(true);
    }

    public boolean hasWarp(@NotNull Warp warp) {
        return this.facWarps.contains(warp);
    }

    public @Nullable Warp getWarpByName(@NotNull String warp) {
        return this.facWarps.stream().filter(w -> w.getName().equals(warp))
                .findFirst()
                .orElse(null);
    }

    public @NotNull Set<Warp> getWarps() {
        return this.facWarps;
    }
    // endregion Warps

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
            powerLevel += user.getEffectivePower();
        }

        return Math.min(powerLevel, this.getPowerLevelLimit()) + this.getFlagValueOrDefault(FactionFlags.POWER_MODIFIER);
    }

    public long getMaxPowerLevel() {
        if (this.hasInfinitePower()) {
            return Integer.MAX_VALUE;
        }

        long powerLevel = 0L;
        for (Member member : this.members.values()) {
            FPlayer user = member.getUser();
            powerLevel += user.getMaxPower();
        }

        return Math.min(powerLevel, this.getPowerLevelLimit()) + this.getFlagValueOrDefault(FactionFlags.POWER_MODIFIER);
    }

    private long getPowerLevelLimit() {
        FactionConfig config = AlpineFactions.getInstance().getConfiguration(FactionConfig.class);
        return config.maxFactionPower;
    }

    public int getClaimCount(@Nullable World world) {
        return Factions.claims().countClaims(this, world);
    }

    public int getClaimCount() {
        return this.getClaimCount(null);
    }

    public long getRemainingClaims() {
        if (this.hasInfinitePower()) {
            return Integer.MAX_VALUE;
        }

        return this.getPowerLevel() - this.getClaimCount();
    }

    public boolean isConquerable() {
        return this.getClaimCount() > this.getPowerLevel();
    }
    // endregion Territory

    // region Relations
    public @NotNull FactionRelation relationTo(@Nullable Faction faction) {
        if (faction == null || this.isWilderness() || faction.isWilderness()) {
            return FactionRelation.NEUTRAL;
        }

        if (this.equals(faction)) {
            return FactionRelation.SELF;
        }

        FactionRelation relationToOther = this.relationRequests.getOrDefault(faction.getId(), FactionRelation.NEUTRAL);
        FactionRelation relationToSelf = faction.relationRequests.getOrDefault(this.getId(), FactionRelation.NEUTRAL);

        if (relationToOther == relationToSelf) {
            return relationToOther;
        } else if (relationToOther == FactionRelation.ENEMY || relationToSelf == FactionRelation.ENEMY) {
            // If either declares ENEMY, the other faction remains neutral
            return relationToOther == FactionRelation.ENEMY ? FactionRelation.ENEMY : FactionRelation.NEUTRAL;
        } else if (relationToOther.getWeight() > 1 && relationToSelf.getWeight() > 1) {
            return relationToOther.getWeight() < relationToSelf.getWeight() ? relationToOther : relationToSelf;
        } else {
            return FactionRelation.NEUTRAL;
        }
    }

    public @NotNull FactionRelation relationWishTo(@Nullable Faction faction) {
        if (faction == null || this.isWilderness() || faction.isWilderness()) {
            return FactionRelation.NEUTRAL;
        }

        if (this.equals(faction)) {
            return FactionRelation.SELF;
        }

        return this.relationRequests.getOrDefault(faction.getId(), FactionRelation.NEUTRAL);
    }

    public boolean isRelation(@Nullable Faction faction, @NotNull FactionRelation relation) {
        return this.relationTo(faction) == relation;
    }

    public boolean isRelationWish(@Nullable Faction faction, @NotNull FactionRelation relation) {
        return this.relationWishTo(faction) == relation;
    }

    public boolean isFriendly(@Nullable Faction faction) {
        if (faction == null || faction.isWilderness()) {
            return false;
        }

        FactionRelation relationToOther = this.relationTo(faction);
        FactionRelation relationToSelf = faction.relationTo(this);
        return relationToOther == relationToSelf && relationToOther.isFriendly();
    }

    public void setRelation(@NotNull Faction faction, @NotNull FactionRelation relation) {
        this.setDirty(true);

        if (relation == FactionRelation.NEUTRAL) {
            this.relationRequests.remove(faction.getId());
            return;
        }

        this.relationRequests.put(faction.getId(), relation);
    }

    public @NotNull Set<RelatedFaction> getRelatedFactions() {
        Set<RelatedFaction> related = new HashSet<>();
        FactionAccessor factions = Factions.registry();

        this.relationRequests.forEach((factionId, type) -> {
            Faction other = factions.getById(factionId);
            if (other != null && other.relationWishTo(this) == type) {
                related.add(new RelatedFaction(other, type));
            }
        });

        return related;
    }

    public @NotNull Set<RelatedFaction> getRelationWishes() {
        Set<RelatedFaction> related = new HashSet<>();
        FactionAccessor factions = Factions.registry();

        this.relationRequests.forEach((factionId, type) -> {
            Faction other = factions.getById(factionId);
            if (other != null && other.relationWishTo(this) != type) {
                related.add(new RelatedFaction(other, type));
            }
        });

        return related;
    }

    public @NotNull Set<Faction> getRelatedFactions(@NotNull FactionRelation relation) {
        Set<Faction> related = new HashSet<>();
        FactionAccessor factions = Factions.registry();

        this.relationRequests.forEach((factionId, type) -> {
            if (type != relation) {
                return;
            }

            Faction other = factions.getById(factionId);
            if (other != null && other.relationTo(this) == relation) {
                related.add(other);
            }
        });

        return ImmutableSet.copyOf(related);
    }

    public @NotNull Set<Faction> getRelationWishes(@NotNull FactionRelation relation) {
        Set<Faction> related = new HashSet<>();
        FactionAccessor factions = Factions.registry();

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
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getFlagValue(@NotNull FactionFlag<T> flag) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> {
            this.setDirty(true);
            return flag.wrapDefaultValue();
        });
        return flagHolder.getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T getFlagValueOrDefault(@NotNull FactionFlag<T> flag) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> {
            this.setDirty(true);
            return flag.wrapDefaultValue();
        });

        T value = flagHolder.getValue();
        if (value == null) {
            flagHolder.setValue(value = flag.getDefaultState());
            this.setDirty(true);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> void setFlagValue(@NotNull FactionFlag<T> flag, @NotNull T value) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> flag.wrapDefaultValue());
        flagHolder.setValue(value);
        this.setDirty(true);
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
        } else {
            Faction other = Factions.registry().find(player);
            return this.isPermitted(this.relationWishTo(other), permission);
        }
    }

    public boolean isPermitted(@NotNull Rank rank, @NotNull Permission permission) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> {
            this.setDirty(true);
            return new PermissionHolder(permission);
        });
        return permValue.isPermitted(rank);
    }

    public boolean isPermitted(@NotNull FactionRelation relation, @NotNull Permission permission) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> {
            this.setDirty(true);
            return new PermissionHolder(permission);
        });
        return permValue.isPermitted(relation);
    }

    public void setPermission(@NotNull Permission permission, boolean permitted, @NotNull Relational relation) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> new PermissionHolder(permission));
        permValue.set(relation, permitted);
        this.setDirty(true);
    }

    public void setPermissionBulk(@NotNull Permission permission, boolean permitted, @NotNull Relational... relations) {
        PermissionHolder permValue = this.permissions.computeIfAbsent(permission.getId(), id -> new PermissionHolder(permission));
        permValue.set(permitted, relations);
        this.setDirty(true);
    }
    // endregion Permissions & Flags

    // region Roster
    public int getMemberLimit() {
        return AlpineFactions.getInstance().getConfiguration(FactionConfig.class).memberLimit + this.getFlagValueOrDefault(FactionFlags.MEMBER_LIMIT_MODIFIER);
    }

    public int getRosterLimit() {
        return AlpineFactions.getInstance().getConfiguration(FactionConfig.class).rosterLimit + this.getFlagValueOrDefault(FactionFlags.ROSTER_LIMIT_MODIFIER);
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

        this.setDirty(true);
    }

    public @NotNull Collection<Member> getMembers() {
        return this.members.values();
    }

    public @NotNull Collection<Member> getRoster() {
        return this.roster.values();
    }

    public @NotNull Collection<Member> getOnlineMembers() {
        List<Member> members = new ArrayList<>();

        for (Member member : this.members.values()) {
            if (member.isOnline() && member.hasJoinedServer()) {
                members.add(member);
            }
        }

        return ImmutableSet.copyOf(members);
    }

    public @NotNull Collection<Member> getOfflineMembers() {
        List<Member> members = new ArrayList<>();

        for (Member member : this.members.values()) {
            if (!member.isOnline() && member.hasJoinedServer()) {
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

    public int countOfflineMembers() {
        int members = 0;

        for (Member member : this.members.values()) {
            if (!member.isOnline()) {
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

    public @Nullable Member getOwner() {
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
            } else {
                // move replacement owner into this faction
                Faction faction = Factions.registry().find(owner);
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

        this.setDirty(true);
    }

    public @NotNull Set<MemberInvitation> getInvitations() {
        return new HashSet<>(this.invitees.values());
    }

    public void addInvitation(@NotNull UUID player, @NotNull UUID inviter) {
        this.invitees.put(player, new MemberInvitation(player, inviter));
        this.setDirty(true);
    }

    public void removeInvitation(@NotNull UUID player) {
        this.invitees.remove(player);
        this.setDirty(true);
    }

    public void addMemberToRoster(@NotNull UUID player, @NotNull Rank rank) {
        this.roster.put(player, new Member(player, rank));
        this.setDirty(true);
    }

    public void removeMemberFromRoster(@NotNull UUID player) {
        this.roster.remove(player);
        this.setDirty(true);
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
        this.setDirty(true);
    }

    public void removeMember(@NotNull UUID player) {
        this.members.remove(player);
        this.invitees.remove(player);
        this.setDirty(true);
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

    public @Nullable MemberInvitation getInvite(@NotNull UUID player) {
        return this.invitees.get(player);
    }

    public @Nullable Member getMember(@NotNull UUID player) {
        return this.members.get(player);
    }

    public @NotNull Member getMember(@NotNull OfflinePlayer player) {
        if (this.isWilderness()) {
            return new Member(player.getUniqueId(), Rank.getDefault());
        }

        Member member = this.members.get(player.getUniqueId());
        if (member == null) {
            throw new NoSuchElementException(player.getName() + " is not a faction member");
        } else {
            return member;
        }
    }

    public @Nullable Rank getMemberRank(@NotNull UUID player) {
        Member member = this.getMember(player);
        if (member == null) {
            member = this.roster.get(player);
        }
        return member == null ? null : member.getRank();
    }

    public @NotNull Rank getMemberRankOrDefault(@NotNull UUID player, @NotNull Rank defaultRank) {
        Rank rank = this.getMemberRank(player);
        return rank == null ? defaultRank : rank;
    }

    public @NotNull Rank getMemberRankOrDefault(@NotNull UUID player) {
        Rank rank = this.getMemberRank(player);
        return rank == null ? Rank.getDefault() : rank;
    }

    public void setRosterRank(@NotNull UUID player, @NotNull Rank rank) {
        if (!this.isOnRoster(player)) {
            return;
        }

        this.roster.get(player).setRank(rank);
        this.setDirty(true);
    }
    // endregion Members

    // region Audiences
    /**
     * Get an {@link Audience} that represents this faction.
     *
     * @return the audience
     */
    public Audience audience() {
        return this.audience(m -> true);
    }

    /**
     * Get a filtered {@link Audience} that represents this faction.
     *
     * @param filter the filter to compare members against
     * @return the audience
     */
    public Audience audience(@NotNull Predicate<Member> filter) {
        return Reference.AUDIENCES.filter(sender -> {
            if (!(sender instanceof Player))
                return false;

            Player player = (Player) sender;
            Member member = this.getMember(player.getUniqueId());
            if (member == null)
                return false;

            return filter.test(member);
        });
    }

    /**
     * Get a filtered {@link Audience} that represents this faction and its relations.
     *
     * @param relations the faction relation types
     * @return the audience
     */
    public Audience audience(FactionRelation... relations) {
        return this.audience((f, m) -> true, relations);
    }

    /**
     * Get an {@link Audience} that represents this faction and its relations.
     *
     * @param filter    the filter to compare members against
     * @param relations the faction relation types
     * @return the audience
     */
    public Audience audience(@NotNull BiPredicate<Faction, Member> filter, @NotNull FactionRelation @NotNull ... relations) {
        List<Faction> related = Arrays.stream(relations)
                .map(this::getRelatedFactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        related.add(this);
        return Reference.AUDIENCES.filter(sender -> {
            if (!(sender instanceof Player))
                return false;

            Player player = (Player) sender;

            for (Faction faction : related) {
                Member member = faction.getMember(player.getUniqueId());
                if (member == null)
                    continue;

                return filter.test(faction, member);
            }

            return false;
        });
    }
    // endregion Audiences

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Faction))
            return false;
        Faction other = (Faction) obj;
        return this.id.equals(other.getId());
    }

    public String toString() {
        return "Faction(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", motd=" + this.motd + ", home=" + this.home + ", createdAt=" + this.createdAt + ", invitees=" + this.invitees + ", roster=" + this.getRoster() + ", members=" + this.getMembers() + ", relationRequests=" + this.relationRequests + ", facWarps=" + this.facWarps + ", flags=" + this.flags + ", permissions=" + this.permissions + ", dirty=" + this.dirty + ")";
    }
}
