package co.crystaldev.factions;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.*;
import co.crystaldev.factions.util.claims.ClaimType;
import co.crystaldev.factions.event.ServerTickEvent;
import co.crystaldev.factions.handler.PlayerHandler;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
public final class AlpineFactions extends AlpinePlugin {

    @Getter
    private static AlpineFactions instance;
    { instance = this; }

    private static final AtomicInteger TICK_COUNTER = new AtomicInteger();

    @Getter
    private final PlayerHandler playerHandler = new PlayerHandler();

    @Getter
    private final FlagRegistry flagRegistry = new FlagRegistry();

    @Getter
    private final PermissionRegistry permissionRegistry = new PermissionRegistry();

    @Override
    public void onStart() {

        // register flags and permissions
        for (FactionFlag<?> flag : FactionFlags.VALUES) {
            this.flagRegistry.register(this, flag);
        }
        for (Permission permission : Permissions.VALUES) {
            this.permissionRegistry.register(this, permission);
        }

        // setup server tick event
        ServerTickEvent event = new ServerTickEvent();
        PluginManager pluginManager = this.getServer().getPluginManager();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            pluginManager.callEvent(event);
            event.setTick(TICK_COUNTER.incrementAndGet());
        }, 0L, 1L);
    }

    @Override
    public void setupCommandManager(@NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder) {
        super.setupCommandManager(builder);

        builder.argument(ClaimType.class, ArgumentKey.of(ClaimTypeArgumentResolver.KEY), new ClaimTypeArgumentResolver());
        builder.argument(Faction.class, ArgumentKey.of(FactionArgumentResolver.KEY), new FactionArgumentResolver());
        builder.argument(String.class, ArgumentKey.of(WorldClaimArgumentResolver.KEY), new WorldClaimArgumentResolver());
        builder.argument(String.class, ArgumentKey.of(AlphanumericArgumentResolver.KEY), new AlphanumericArgumentResolver());
        builder.argument(OfflinePlayer.class, ArgumentKey.of(OfflinePlayerArgumentResolver.KEY), new OfflinePlayerArgumentResolver());
    }

    @Override
    public void registerSerializers(@NotNull SerializerRegistry serializerRegistry) {
        super.registerSerializers(serializerRegistry);

        serializerRegistry.setMiniMessage(Reference.MINI_MESSAGE);
    }

    public static void schedule(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(instance, runnable);
    }

    public static void schedule(@NotNull Runnable runnable, long wait) {
        Bukkit.getScheduler().runTaskLater(instance, runnable, wait);
    }

    public static void scheduleAsync(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, runnable);
    }
}
