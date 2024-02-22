package co.crystaldev.factions.api.accessor;

import co.crystaldev.factions.api.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/21/2024
 */
public interface FactionAccessor {

    void register(@NotNull Faction faction);

    void unregister(@NotNull Faction faction);

    void save(@NotNull Faction faction);

    // region Get

    @Nullable
    Faction getById(@NotNull String id);

    @Nullable
    default Faction getByName(@NotNull String name) {
        return this.find(f -> f.getName().equalsIgnoreCase(name));
    }

    @NotNull
    Collection<Faction> get();

    @NotNull
    Faction getWilderness();

    @NotNull
    Faction getWarZone();

    @NotNull
    Faction getSafeZone();

    // endregion Get

    // region Find

    @Nullable
    Faction find(@NotNull Predicate<Faction> factionPredicate);

    @NotNull
    default Faction findOrDefault(@NotNull Predicate<Faction> factionPredicate) {
        Faction f = this.find(factionPredicate);
        return f == null ? this.getWilderness() : f;
    }

    @Nullable
    default Faction find(@NotNull UUID member) {
        return this.find(f -> f.isMember(member));
    }

    @NotNull
    default Faction findOrDefault(@NotNull UUID member) {
        Faction found = this.find(member);
        return found == null ? this.getWilderness() : found;
    }

    @Nullable
    default Faction find(@NotNull OfflinePlayer member) {
        return this.find(member.getUniqueId());
    }

    @NotNull
    default Faction findOrDefault(@NotNull OfflinePlayer member) {
        return this.findOrDefault(member.getUniqueId());
    }

    @Nullable
    default Faction find(@NotNull ServerOperator member) {
        if (member instanceof OfflinePlayer) {
            return this.find(((OfflinePlayer) member).getUniqueId());
        }
        return null;
    }

    @NotNull
    default Faction findOrDefault(@NotNull ServerOperator member) {
        if (member instanceof OfflinePlayer) {
            return this.findOrDefault(((OfflinePlayer) member).getUniqueId());
        }
        return this.getWilderness();
    }

    // endregion Find
}
