package co.crystaldev.factions.util;

import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.config.MessageConfig;
import co.crystaldev.factions.store.ClaimStore;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/07/2024
 */
public final class AsciiFactionMap {

    private static final char[] KEY_CHARS = "\\/#?ç¬£$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZÄÖÜÆØÅ1234567890abcdeghjmnopqrsuvwxyÿzäöüæøåâêîûô".toCharArray();

    private static final Component UNCLAIMED_CHUNK = Component.text("-").color(NamedTextColor.GRAY);

    private static final int MAP_WIDTH = 49;
    private static final int MAP_HEIGHT = 17;
    private static final int MINIMAL_MAP_HEIGHT = 8;

    private final Player player;

    private final MessageConfig config = MessageConfig.getInstance();

    private final ClaimStore store = ClaimStore.getInstance();

    private final Map<Faction, FactionMarker> factionColorMap = new LinkedHashMap<>();

    private final Component[][] storage;

    private final boolean minimal;

    private final int height;

    private AsciiFactionMap(@NotNull Player player, boolean minimal) {
        this.player = player;
        this.minimal = minimal;
        this.height = minimal ? MINIMAL_MAP_HEIGHT : MAP_HEIGHT;
        this.storage = new Component[this.height][MAP_WIDTH];
    }

    @NotNull
    public Component build() {
        this.populateMap();

        List<Component> lines = new LinkedList<>();
        this.buildTitle(lines);
        this.buildMap(lines);
        this.buildLegend(lines);

        return ComponentHelper.joinNewLines(lines);
    }

    private void buildTitle(@NotNull List<Component> components) {
        Chunk chunk = this.player.getLocation().getChunk();
        Faction faction = this.store.getFactionOrDefault(chunk);

        components.add(Formatting.title(this.config.mapTitle.build(
                "world", this.player.getWorld().getName(),
                "chunk_x", chunk.getX(),
                "chunk_z", chunk.getZ(),
                "faction", FactionHelper.formatRelational(this.player, faction, faction.getName()),
                "faction_name", faction.getName()
        )));
    }

    private void buildMap(@NotNull List<Component> components) {
        for (Component[] line : this.storage) {
            components.add(ComponentHelper.join(line));
        }
    }

    private void buildLegend(@NotNull List<Component> components) {
        if (this.factionColorMap.isEmpty()) {
            return;
        }

        // compile the legend
        List<Component> legend = new LinkedList<>();
        this.factionColorMap.forEach((faction, marker) -> {
            if (marker.overflown) {
                return;
            }

            Component component = this.config.mapLegendFormat.build(
                    "character", marker.character,
                    "faction", FactionHelper.formatRelational(this.player, faction, faction.getName()),
                    "faction_name", faction.getName()
            );

            legend.add(component.style(marker.style));
        });

        // add the legend
        components.add(ComponentHelper.joinSpaces(legend));

        // overflown
        if (this.factionColorMap.size() >= KEY_CHARS.length) {
            components.add(this.config.mapLegendOverflowFormat.build());
        }
    }

    private void populateMap() {
        String world = this.player.getWorld().getName();
        Chunk center = this.player.getLocation().getChunk();

        int centerX = center.getX();
        int centerZ = center.getZ();

        int originX = centerX + (MAP_WIDTH - 1) / 2;
        int originZ = centerZ - (this.minimal ? this.height : (this.height - 1)) / 2;

        // populate claimed chunks
        for (int z = 0; z < this.height; z++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int flippedX = MAP_WIDTH - 1 - x;

                int chunkX = originX - x;
                int chunkZ = originZ + z;
                if (centerX == chunkX && centerZ == chunkZ) {
                    // map center marker
                    this.put(this.config.mapCenter.build(), flippedX, z);
                    continue;
                }

                Faction faction = this.store.getFaction(world, chunkX, chunkZ);
                if (faction != null) {
                    // chunk is claimed

                    boolean overflow = this.factionColorMap.size() >= KEY_CHARS.length;
                    FactionMarker marker = this.factionColorMap.computeIfAbsent(faction, fac -> {
                        char ch = overflow ? '#' : KEY_CHARS[this.factionColorMap.size()];
                        Style style = FactionHelper.formatRelational(this.player, fac, Component.text("")).style();
                        Component symbol = overflow
                                ? Component.text("#").decorate(TextDecoration.OBFUSCATED)
                                : Component.text(ch);
                        symbol = symbol.style(style).hoverEvent(HoverEvent.showText(Component.text(fac.getName()).style(style)));

                        return new FactionMarker(style, ch == '\\' ? "\\\\" : Character.toString(ch), symbol, overflow);
                    });

                    this.put(marker.symbol, flippedX, z);
                }
                else {
                    // wilderness

                    this.put(UNCLAIMED_CHUNK, flippedX, z);
                }
            }
        }

        // compass
        BlockFace facing = LocationHelper.getFullFacing(this.player.getLocation());

        this.putDirection(0, 0, '\\', facing == BlockFace.NORTH_WEST);
        this.putDirection(1, 0, 'N',  facing == BlockFace.NORTH);
        this.putDirection(2, 0, '/',  facing == BlockFace.NORTH_EAST);
        this.putDirection(0, 1, 'W',  facing == BlockFace.WEST);
        this.putDirection(1, 1, '+',  facing == BlockFace.SELF);
        this.putDirection(2, 1, 'E',  facing == BlockFace.EAST);
        this.putDirection(0, 2, '/',  facing == BlockFace.SOUTH_WEST);
        this.putDirection(1, 2, 'S',  facing == BlockFace.SOUTH);
        this.putDirection(2, 2, '\\', facing == BlockFace.SOUTH_EAST);
    }

    private void putDirection(int x, int z, char ch, boolean facing) {
        String style = facing ? this.config.mapCompassDirectionStyle : this.config.mapCompassStyle;
        this.put(ComponentHelper.stylize(style, Component.text(ch)), x, z);
    }

    private void put(@NotNull Component component, int x, int y) {
        // origin is top left
        this.storage[y][x] = component;
    }

    @NotNull
    public static Component create(@NotNull Player player, boolean minimal) {
        AsciiFactionMap map = new AsciiFactionMap(player, minimal);
        return map.build();
    }

    @AllArgsConstructor
    private static final class FactionMarker {
        private final Style style;
        private final String character;
        private final Component symbol;
        private final boolean overflown;
    }
}
