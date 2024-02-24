package co.crystaldev.factions.util.material;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/23/2024
 */
@RequiredArgsConstructor
public final class MappedMaterial {

    private final Set<XMaterial> types;

    public boolean test(@NotNull XMaterial type) {
        return this.types.contains(type);
    }

    public boolean test(@NotNull Material type) {
        return this.test(MaterialMapping.from(type));
    }

    public boolean test(@NotNull Block block) {
        return this.test(MaterialMapping.from(block));
    }

    public boolean test(@NotNull ItemStack item) {
        return this.test(MaterialMapping.from(item));
    }

    @NotNull
    public static MappedMaterial of(@NotNull XMaterial... materials) {
        return new MappedMaterial(ImmutableSet.copyOf(materials));
    }

    @NotNull
    public static MappedMaterial of(@NotNull Function<Stream<XMaterial>, Stream<XMaterial>> streamFunction) {
        Set<XMaterial> values = streamFunction.apply(Stream.of(XMaterial.VALUES).filter(XMaterial::isSupported)).collect(Collectors.toSet());
        return new MappedMaterial(ImmutableSet.copyOf(values));
    }
}
