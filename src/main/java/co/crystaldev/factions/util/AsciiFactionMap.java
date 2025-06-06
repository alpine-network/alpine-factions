package co.crystaldev.factions.util;

import co.crystaldev.alpinecore.util.Components;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.api.Factions;
import co.crystaldev.factions.api.accessor.ClaimAccessor;
import co.crystaldev.factions.api.faction.Claim;
import co.crystaldev.factions.api.faction.Faction;
import co.crystaldev.factions.api.faction.FactionRelation;
import co.crystaldev.factions.api.map.FactionMapFormatter;
import co.crystaldev.factions.api.player.FPlayer;
import co.crystaldev.factions.config.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @since 0.1.0
 */
public final class AsciiFactionMap {

    private static final FactionMapFormatter MAP_FORMATTER = Factions.mapFormatter();

    private static final char[] KEY_CHARS = "\\/#?ç¬£$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZÄÖÜÆØÅ1234567890abcdeghjmnopqrsuvwxyÿzäöüæøåâêîûô".toCharArray();

    private static final Component UNCLAIMED_CHUNK = Component.text("-").color(NamedTextColor.GRAY);

    private static final int MAP_WIDTH = 49;
    private static final int MAP_HEIGHT = 17;

    private final Player player;

    private final MessageConfig config = AlpineFactions.getInstance().getConfiguration(MessageConfig.class);

    private final ClaimAccessor claims = Factions.claims();

    private final Map<Faction, FactionMarker> factionColorMap = new LinkedHashMap<>();

    private final Component[][] storage;

    private final boolean minimal;

    private final int height;

    private AsciiFactionMap(@NotNull Player player, boolean minimal) {
        this.player = player;
        this.minimal = minimal;

        // Map size handled through MapHeightCommand (clamps are in place)
        FPlayer fPlayer = Factions.players().get(player);
        int minimalMapSize = fPlayer.getAutoMapHeight();
        if (minimalMapSize < 8) {
            minimalMapSize = 8;
            fPlayer.setAutoMapHeight(8);
        }

        this.height = minimal ? minimalMapSize : MAP_HEIGHT;
        this.storage = new Component[this.height][MAP_WIDTH];
    }

    public @NotNull Component build() {
        this.populateMap();

        List<Component> lines = new LinkedList<>();
        this.buildTitle(lines);
        this.buildMap(lines);
        this.buildLegend(lines);

        return Components.joinNewLines(lines);
    }

    private void buildTitle(@NotNull List<Component> components) {
        Chunk chunk = this.player.getLocation().getChunk();
        Faction faction = this.claims.getFactionOrDefault(chunk);

        components.add(Formatting.title(this.config.mapTitle.build(
                "world", this.player.getWorld().getName(),
                "chunk_x", chunk.getX(),
                "chunk_z", chunk.getZ(),
                "faction", RelationHelper.formatLiteralFactionName(this.player, faction),
                "faction_name", faction.getName()
        )));
    }

    private void buildMap(@NotNull List<Component> components) {
        for (Component[] line : this.storage) {
            components.add(Components.join(line));
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
                    "character", marker.displayCharacter,
                    "faction", RelationHelper.formatLiteralFactionName(this.player, faction),
                    "faction_name", faction.getName()
            );

            legend.add(component.style(marker.style));
        });

        // add the legend
        components.add(Components.joinSpaces(legend));

        // overflown
        if (this.factionColorMap.size() >= KEY_CHARS.length) {
            components.add(this.config.mapLegendOverflowFormat.build());
        }
    }

    private void populateMap() {
        String world = this.player.getWorld().getName();
        Chunk center = this.player.getLocation().getChunk();
        WorldBorder border = this.player.getWorld().getWorldBorder();
        Component borderComponent = this.config.mapBorder.build();
        Faction playerFaction = Factions.registry().findOrDefault(this.player);

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

                if (!LocationHelper.isChunkWithinBorder(border, chunkX, chunkZ)) {
                    // border
                    this.put(borderComponent, flippedX, z);
                    continue;
                }

                Claim claim = this.claims.getClaim(world, chunkX, chunkZ);
                if (claim != null) {
                    // chunk is claimed

                    boolean overflow = this.factionColorMap.size() >= KEY_CHARS.length;
                    Faction faction = claim.getFaction();
                    FactionMarker marker = this.factionColorMap.computeIfAbsent(faction, fac -> {

                        // get the relation between the factions
                        FactionRelation relation = playerFaction.relationTo(faction);
                        Style style = RelationHelper.formatComponent(this.player, fac, Component.text("")).style();

                        // create the symbol to represent the faction
                        char ch = overflow ? '#' : KEY_CHARS[this.factionColorMap.size()];
                        Component symbol = overflow
                                ? Component.text("#").decorate(TextDecoration.OBFUSCATED)
                                : Component.text(ch);

                        // set the hover event to the faction's name
                        symbol = symbol.style(style).hoverEvent(
                                HoverEvent.showText(Component.text(fac.getName()).style(style)));

                        return new FactionMarker(style, relation, ch, symbol, overflow);
                    });

                    Component symbol = MAP_FORMATTER.formatClaim(claim, faction, marker.relation,
                            marker.character, marker.symbol);
                    if (symbol == null) {
                        symbol = marker.symbol;
                    }

                    this.put(symbol, flippedX, z);
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
        this.put(Components.stylize(style, Component.text(ch)), x, z);
    }

    private void put(@NotNull Component component, int x, int y) {
        // origin is top left
        this.storage[y][x] = component;
    }

    public static @NotNull Component create(@NotNull Player player, boolean minimal) {
        AsciiFactionMap map = new AsciiFactionMap(player, minimal);
        return map.build();
    }

    private static final class FactionMarker {
        private final Style style;
        private final FactionRelation relation;
        private final char character;
        private final String displayCharacter;
        private final Component symbol;
        private final boolean overflown;

        private FactionMarker(@NotNull Style style, @NotNull FactionRelation relation, char character,
                              @NotNull Component symbol, boolean overflown) {
            this.style = style;
            this.relation = relation;
            this.character = character;
            this.displayCharacter = character == '\\' ? "\\\\" : Character.toString(character);
            this.symbol = symbol;
            this.overflown = overflown;
        }
    }
}
