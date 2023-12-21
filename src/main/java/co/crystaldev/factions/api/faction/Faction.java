package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FlagHolder;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.PermissionHolder;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.member.Member;
import co.crystaldev.factions.api.member.Rank;
import co.crystaldev.factions.store.FactionStore;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
@Data
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
    {
        for (FactionFlag<?> flag : FactionFlags.VALUES) {
            this.flags.put(flag.getId(), flag.wrapDefaultValue());
        }
    }

    private final Map<String, PermissionHolder> permissions = new HashMap<>();
    {
        for (Permission perm : Permissions.VALUES) {
            this.permissions.put(perm.getId(), new PermissionHolder(perm));
        }
    }

    @Getter
    private transient boolean dirty;

    public void markDirty() {
        this.dirty = true;
    }

    public boolean hasMember(@NotNull UUID player) {
        return this.members.containsKey(player);
    }

    public boolean hasMember(@NotNull OfflinePlayer player) {
        return this.members.containsKey(player.getUniqueId());
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

    @SuppressWarnings("unchecked")
    public <T> void setFlagValue(@NotNull FactionFlag<T> flag, @NotNull T value) {
        FlagHolder<T> flagHolder = (FlagHolder<T>) this.flags.computeIfAbsent(flag.getId(), id -> flag.wrapDefaultValue());
        flagHolder.setValue(value);
        this.markDirty();
    }

    public boolean isPermitted(@NotNull OfflinePlayer player, @NotNull Permission permission) {
        if (this.hasMember(player.getUniqueId())) {
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
