package edu.tm1.kj.i17.projekt1;

import lombok.Getter;
import lombok.extern.java.Log;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.*;
import java.util.logging.Level;

import static edu.tm1.kj.i17.projekt1.Game.Config.FieldSize;
import static edu.tm1.kj.i17.projekt1.Game.Config.TileSize;
import static edu.tm1.kj.i17.projekt1.MapGenerator.generateMap;

// TODO: Document
@Log
public class GameField {
    private final Map<Point, TileType> map;

    // TODO: Document
    public GameField() {
        log.finer("Creating game field...");
        log.config("Field size: (" + FieldSize.width + ", " + FieldSize.height + ")");

        Map<Point, TileType> tmpMap;
        try {
            tmpMap = load();
        } catch (FileNotFoundException e) {
            log.log(Level.INFO, "Map file not found, generating new map", e);
            tmpMap = generateMap();
        } catch (EOFException e) {
            log.log(Level.INFO, "Map file is missing data, generating new map", e);
            tmpMap = generateMap();
        } catch (IOException e) {
            log.severe("Error loading map");
            throw new RuntimeException("Error loading map", e);
        }

        map = tmpMap;
        log.finer("Game map loaded");

        log.fine("Game field created");
    }

    // TODO: Document
    public TileType getTileAt(int x, int y) {
        return map.getOrDefault(new Point(x, y), TileType.VOID);
    }

    // TODO: Document
    public TileType getTileAt(double x, double y) {
        int col = (int) (x >= 0 ? Math.floor(x / TileSize) : Math.ceil(x / TileSize));
        int row = (int) (y >= 0 ? Math.floor(y / TileSize) : Math.ceil(y / TileSize));
        return getTileAt(col, row);
    }

    public Path2D getCollision(int x, int y) {
        if (!getTileAt(x, y).isSolid() || getTileAt(x, y) == TileType.VOID) return null;

        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        Path2D shape = new Path2D.Double();
        boolean firstPoint = true;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (!visited.contains(p)) {
                visited.add(p);
                int px = p.x, py = p.y;
                if (getTileAt(px, py).isSolid() && getTileAt(px, py) != TileType.VOID) {
                    int startX = px * TileSize;
                    int startY = -py * TileSize;

                    if (firstPoint) {
                        shape.moveTo(startX, startY);
                        firstPoint = false;
                    } else {
                        shape.lineTo(startX, startY);
                    }

                    queue.add(new Point(px - 1, py));
                    queue.add(new Point(px + 1, py));
                    queue.add(new Point(px, py - 1));
                    queue.add(new Point(px, py + 1));
                }
            }
        }

        shape.closePath();
        return shape;
    }

    public Path2D getCollision(double x, double y) {
        int col = (int) (x >= 0 ? Math.floor(x) : Math.ceil(x));
        int row = (int) (y >= 0 ? Math.floor(y) : Math.ceil(y));
        return getCollision(col, row);
    }

    // TODO: Document
    private Map<Point, TileType> load() throws IOException {
        log.fine("Loading map...");

        Map<Point, TileType> map = new HashMap<>();

        log.fine("Loading map from file...");
        try (InputStream fis = getClass().getClassLoader().getResourceAsStream("game.map")) {
            if (fis == null) {
                log.warning("Map file not found");
                throw new FileNotFoundException("Map file not found");
            }
            log.finer("Reading map file...");
            for (int row = -FieldSize.height / 2; row <= FieldSize.height / 2; row++) {
                for (int col = -FieldSize.width / 2; col <= FieldSize.width / 2; col++) {
                    byte in = (byte) fis.read();
                    if (in == -1) {
                        log.severe("Unexpected end of file");
                        throw new EOFException("Unexpected end of file");
                    }
                    TileType tile = TileType.fromSave(in);
                    map.put(new Point(col, row), tile);
                    log.finest("\t>Tile " + tile.name() + " at (" + col + ", " + row + ")");
                }
            }
        } catch (FileNotFoundException e) {
            log.log(Level.INFO, "Map file not found", e);
            throw e;
        } catch (EOFException e) {
            log.log(Level.INFO, "Map file is missing data", e);
            throw e;
        } catch (IOException e) {
            log.severe("Error reading map file");
            throw new IOException("Error reading map file", e);
        }

        return map;
    }


    // TODO: Document
    @Getter
    public enum TileType {
        SKY(false, Color.CYAN, 0, (byte) 0),
        GROUND(true, Color.GREEN, 0.5, (byte) 1),
        ICE(true, Color.BLUE, 0.1, (byte) 2),
        RUBBER(true, Color.RED, 0.9, (byte) 3),
        VOID(true, Color.BLACK, 0);

        private final boolean solid;
        private final Color color;
        private final double friction;
        private final byte save;

        TileType(boolean solid, Color color, double friction) {
            this.solid = solid;
            this.color = color;
            this.friction = friction;
            this.save = (byte) ordinal();
        }

        TileType(boolean solid, Color color, double friction, byte save) {
            this.solid = solid;
            this.color = color;
            this.friction = friction;
            this.save = save;
        }

        // TODO: Document
        public static TileType fromSave(byte save) {
            for (TileType type : values()) {
                if (type.save == save) {
                    return type;
                }
            }
            return VOID;
        }
    }
}
