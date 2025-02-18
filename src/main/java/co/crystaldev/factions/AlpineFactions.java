package co.crystaldev.factions;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.FlagRegistry;
import co.crystaldev.factions.api.PermissionRegistry;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.accessor.FactionAccessor;
import co.crystaldev.factions.api.accessor.PlayerAccessor;
import co.crystaldev.factions.api.faction.flag.FactionFlag;
import co.crystaldev.factions.api.faction.flag.FactionFlags;
import co.crystaldev.factions.api.faction.permission.Permission;
import co.crystaldev.factions.api.faction.permission.Permissions;
import co.crystaldev.factions.api.map.FactionMapFormatter;
import co.crystaldev.factions.api.show.ShowFormatter;
import co.crystaldev.factions.api.show.component.ShowComponents;
import co.crystaldev.factions.handler.PlayerHandler;
import co.crystaldev.factions.store.FactionStore;
import co.crystaldev.factions.store.PlayerStore;
import co.crystaldev.factions.store.claim.ClaimStore;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @since 0.1.0
 */
public abstract class AlpineFactions extends AlpinePlugin implements Factions {

    @Getter
    private static AlpineFactions instance;
    { instance = this; }

    @Getter
    private final PlayerHandler playerHandler = new PlayerHandler();

    private final FlagRegistry flagRegistry = new FlagRegistry();

    private final PermissionRegistry permissionRegistry = new PermissionRegistry();

    private final ShowFormatter showFormatter = new ShowFormatter();

    private final FactionMapFormatter mapFormatter = new FactionMapFormatter();

    private ClaimAccessor claimAccessor;

    private FactionAccessor factionAccessor;

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
        this.showFormatter.register(ShowComponents.VALUES);

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
    protected @NotNull Set<Class<?>> getScannablePackages() {
        return ImmutableSet.of(AlpineFactions.class, this.getClass());
    }

    @Override
    public @NotNull ClaimAccessor claims() {
        return this.claimAccessor;
    }

    @Override
    public @NotNull PlayerAccessor players() {
        return this.playerAccessor;
    }

    @Override
    public @NotNull FactionAccessor factions() {
        return this.factionAccessor;
    }

    @Override
    public @NotNull FlagRegistry flagRegistry() {
        return flagRegistry;
    }

    @Override
    public @NotNull PermissionRegistry permissionRegistry() {
        return permissionRegistry;
    }

    @Override
    public @NotNull ShowFormatter showFormatter() {
        return showFormatter;
    }

    @Override
    public @NotNull FactionMapFormatter mapFormatter() {
        return this.mapFormatter;
    }

    public static <T extends Event> @NotNull T callEvent(@NotNull T event) {
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
