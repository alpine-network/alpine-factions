package co.crystaldev.factions.util.event;

import co.crystaldev.factions.AlpineFactions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 11/16/2023
 */
public final class ServerTickEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final AlpineFactions plugin = AlpineFactions.getInstance();

    @Getter
    private final Server server = this.plugin.getServer();

    @Getter @Setter
    private long tick;

    public boolean isTime(long time, @NotNull TimeUnit unit) {
        return this.tick % (unit.toMillis(time) / 50) == 0;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
