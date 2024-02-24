package co.crystaldev.factions.util.material;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/23/2024
 */
@UtilityClass
public final class MaterialMapping {

    public static final MappedMaterial DOOR = MappedMaterial.of(values -> values
            .filter(v -> v.name().endsWith("_DOOR")
                    || v.name().endsWith("_TRAPDOOR")
                    || v.name().endsWith("_FENCE_GATE")));

    @NotNull
    public static XMaterial from(@NotNull Material type) {
        return XMaterial.matchXMaterial(type);
    }

    @NotNull
    public static XMaterial from(@NotNull ItemStack item) {
        return XMaterial.matchXMaterial(item);
    }

    @NotNull
    public static XMaterial from(@NotNull Block block) {
        if (XMaterial.supports(12)) {
            return XMaterial.matchXMaterial(block.getTypeId(), block.getData()).orElse(XMaterial.AIR);
        }
        else {
            return XMaterial.matchXMaterial(block.getType());
        }
    }
}
