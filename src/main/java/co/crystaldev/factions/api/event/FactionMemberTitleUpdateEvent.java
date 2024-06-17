package co.crystaldev.factions.api.event;

import co.crystaldev.factions.api.event.framework.FactionEntityEvent;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.member.Member;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 0.1.0
 */
@Getter @Setter
public final class FactionMemberTitleUpdateEvent extends FactionEntityEvent<Member> implements Cancellable {

    private @Nullable Component title;

    private boolean cancelled;

    public FactionMemberTitleUpdateEvent(@NotNull Faction faction, @NotNull Member entity, @Nullable Component title) {
        super(faction, entity);
        this.title = title;
    }
}
