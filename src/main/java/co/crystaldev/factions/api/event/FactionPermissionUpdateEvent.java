package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permission;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
@Getter @Setter
public final class FactionPermissionUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull Permission permission;

    private final @NotNull Relational relational;

    private boolean value;

    private boolean cancelled;

    public FactionPermissionUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity,
                                        @NotNull Permission permission, @NotNull Relational relational,
                                        boolean value) {
        super(faction, entity);
        this.permission = permission;
        this.relational = relational;
        this.value = value;
    }

    public boolean isRank() {
        return this.relational instanceof Rank;
    }

    public boolean isFaction() {
        return this.relational instanceof FactionRelation;
    }
}
