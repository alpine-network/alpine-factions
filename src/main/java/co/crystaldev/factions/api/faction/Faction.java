package co.crystaldev.factions.api.faction;

import co.crystaldev.factions.api.member.Member;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    private final ArrayList<Member> roster = new ArrayList<>();

    private final ArrayList<Member> members = new ArrayList<>();

    private final ArrayList<Relation> relations = new ArrayList<>();

    private final ArrayList<Relation> relationRequests = new ArrayList<>();

    private final Map<String, FactionFlagValue<?>> flags = new HashMap<>();
    {
        for (FactionFlag<?> flag : FactionFlags.VALUES) {
            this.flags.put(flag.getId(), flag.wrapDefaultValue());
        }
    }

    @Getter
    private transient boolean dirty;

    public void markDirty() {
        this.dirty = true;
    }

    public boolean hasMember(@NotNull UUID member) {
        return this.members.stream().anyMatch(m -> m.getId().equals(member));
    }

    @Nullable @SuppressWarnings("unchecked")
    public <T> T getFlagValue(@NotNull FactionFlag<T> flag) {
        FactionFlagValue<T> flagValue = (FactionFlagValue<T>) this.flags.computeIfAbsent(flag.getId(), id -> {
            this.markDirty();
            return flag.wrapDefaultValue();
        });
        return flagValue.getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> void setFlagValue(@NotNull FactionFlag<T> flag, @NotNull T value) {
        FactionFlagValue<T> flagValue = (FactionFlagValue<T>) this.flags.computeIfAbsent(flag.getId(), id -> flag.wrapDefaultValue());
        flagValue.setValue(value);
        this.markDirty();
    }

    public boolean isNeutral(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.NEUTRAL);
    }

    public boolean isEnemy(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.ENEMY);
    }

    public boolean isTruce(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.TRUCE);
    }

    public boolean isAlly(@NotNull Faction faction) {
        return this.isRelation(faction, RelationType.ALLY);
    }

    private boolean isRelation(@NotNull Faction faction, @NotNull RelationType type) {
        for (Relation relation : this.relations) {
            if (relation.getFaction().equals(faction.getId()) && relation.getType() == type)
                return true;
        }
        return false;
    }
}
