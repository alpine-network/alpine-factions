package co.crystaldev.factions;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.storage.SerializerRegistry;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.command.argument.AlphanumericArgumentResolver;
import co.crystaldev.factions.event.ServerTickEvent;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import lombok.Getter;
import org.bukkit.Bukkit;
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
    public void onStop() {

    }

    @Override
    public void setupCommandManager(@NotNull LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder) {
        super.setupCommandManager(builder);

        builder.argument(String.class, ArgumentKey.of(AlphanumericArgumentResolver.KEY), new AlphanumericArgumentResolver());
    }

    @Override
    public void registerSerializers(@NotNull SerializerRegistry serializerRegistry) {
        super.registerSerializers(serializerRegistry);

        serializerRegistry.setMiniMessage(Reference.MINI_MESSAGE);
    }
}
