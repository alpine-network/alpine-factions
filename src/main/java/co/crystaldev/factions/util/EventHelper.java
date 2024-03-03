package co.crystaldev.factions.util;

import lombok.experimental.UtilityClass;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 03/02/2024
 */
@UtilityClass
public final class EventHelper {
    public static boolean isOffHand(@NotNull PlayerInteractEvent event) {
        try {
            return event.getHand() == EquipmentSlot.OFF_HAND;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
