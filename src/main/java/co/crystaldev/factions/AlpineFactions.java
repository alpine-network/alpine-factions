package co.crystaldev.factions;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.show.ShowFormatter;
import co.crystaldev.factions.api.show.component.DefaultShowComponents;
import co.crystaldev.factions.store.claim.ClaimStore;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.store.PlayerStore;
import co.crystaldev.factions.handler.PlayerHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 12/12/2023
 */
public final class AlpineFactions extends AlpinePlugin {

    @Getter
    private static AlpineFactions instance;
    { instance = this; }

    @Getter
    private final PlayerHandler playerHandler = new PlayerHandler();

    @Getter
    private final FlagRegistry flagRegistry = new FlagRegistry();

    @Getter
    private final PermissionRegistry permissionRegistry = new PermissionRegistry();

    @Getter
    private final ShowFormatter showFormatter = new ShowFormatter();

    @Getter
    private ClaimAccessor claimAccessor;

    @Getter
    private FactionAccessor factionAccessor;

    @Getter
    private PlayerAccessor playerAccessor;

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

        // setup accessors
        this.claimAccessor = this.getActivatable(ClaimStore.class);
        this.factionAccessor = this.getActivatable(FactionStore.class);
        this.playerAccessor = this.getActivatable(PlayerStore.class);
    }

    @Override
    public void onStop() {
        this.getActivatable(ClaimStore.class).saveClaims();
        this.getActivatable(FactionStore.class).saveFactions();
    }

    @Override
    public void setupDefaultVariables(@NotNull VariableConsumer variableConsumer) {
        variableConsumer.addVariable("prefix", "<info>Factions</info> <separator>»</separator><text>");
        variableConsumer.addVariable("error_prefix", "<error>Factions</error> <separator>»</separator><text>");
    }

    @NotNull @ApiStatus.Internal
    public <T> T getActivatable(@NotNull Class<T> type) {
        for (Activatable activatable : this.getActivatables()) {
            if (activatable.getClass().equals(type)) {
                return (T) activatable;
            }
        }
        throw new IllegalArgumentException();
    }

    @NotNull
    public static <T extends Event> T callEvent(@NotNull T event) {
        instance.getServer().getPluginManager().callEvent(event);
        return event;
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
