package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.Relational;
import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.member.Rank;
import co.crystaldev.factions.api.faction.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.0
 */
public final class FactionPermissionUpdateEvent extends FactionEntityEvent<CommandSender> implements Cancellable {

    private final @NotNull Permission permission;

    private final @NotNull Relational relational;

    private boolean allowed;

    private boolean cancelled;

    public FactionPermissionUpdateEvent(@NotNull Faction faction, @NotNull CommandSender entity,
                                        @NotNull Permission permission, @NotNull Relational relational,
                                        boolean allowed) {
        super(faction, entity);
        this.permission = permission;
        this.relational = relational;
        this.allowed = allowed;
    }

    public boolean isRank() {
        return this.relational instanceof Rank;
    }

    public boolean isFaction() {
        return this.relational instanceof FactionRelation;
    }

    public @NotNull Permission getPermission() {
        return this.permission;
    }

    public @NotNull Relational getRelational() {
        return this.relational;
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
