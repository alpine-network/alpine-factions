package co.crystaldev.factions;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.show.ShowFormatter;
import co.crystaldev.factions.api.show.component.DefaultShowComponents;
import co.crystaldev.factions.command.argument.*;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.util.claims.ClaimType;
import co.crystaldev.factions.event.ServerTickEvent;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.util.claims.WorldClaimType;
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

    @Getter
    private final ShowFormatter showFormatter = new ShowFormatter();

    @Override
    public void onStart() {

        // register flags and permissions
        for (FactionFlag<?> flag : FactionFlags.VALUES) {
            this.flagRegistry.register(this, flag);
        }
        for (Permission permission : Permissions.VALUES) {
            this.permissionRegistry.register(this, permission);
        }

        // register f show elements
        this.showFormatter.register(DefaultShowComponents.VALUES);

        // setup server tick event
        ServerTickEvent event = new ServerTickEvent();
        PluginManager pluginManager = this.getServer().getPluginManager();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            pluginManager.callEvent(event);
            event.setTick(TICK_COUNTER.incrementAndGet());
        }, 0L, 1L);
    }

    @Override
    public void onStop() {
        // flush all dirty factions
        FactionStore.getInstance().saveFactions();
    }

    @Override
    public void setupCommandManager(@NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder) {
        super.setupCommandManager(builder);

        builder.argument(String.class, ArgumentKey.of(Args.ALPHANUMERIC), new AlphanumericArgumentResolver());
        builder.argument(Enum.class, ArgumentKey.of(Args.LC_ENUM), new LowercaseEnumArgumentResolver());
        builder.argument(OfflinePlayer.class, ArgumentKey.of(Args.OFFLINE_PLAYER), new OfflinePlayerArgumentResolver());
        builder.argument(Faction.class, ArgumentKey.of(Args.FACTION), new FactionArgumentResolver());
        builder.argument(FactionFlag.class, ArgumentKey.of(Args.FACTION_FLAG), new FactionFlagArgumentResolver());
        builder.argument(FactionRelation.class, ArgumentKey.of(Args.FACTION_RELATION), new FactionRelationArgumentResolver());
        builder.argument(ClaimType.class, ArgumentKey.of(Args.CLAIM_TYPE), new ClaimTypeArgumentResolver());
        builder.argument(WorldClaimType.class, ArgumentKey.of(Args.WORLD_CLAIM_TYPE), new WorldClaimArgumentResolver());
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
