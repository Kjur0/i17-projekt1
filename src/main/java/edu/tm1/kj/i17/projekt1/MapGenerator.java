package edu.tm1.kj.i17.projekt1;

import lombok.extern.java.Log;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static edu.tm1.kj.i17.projekt1.Game.Config.FieldSize;
import static edu.tm1.kj.i17.projekt1.Game.Config.GroundLevel;

@Log
public class MapGenerator {
    public static void main(String[] args) {
        log.fine("Starting map generation...");
        File file = new File("game.map");
        try {
            if (!file.createNewFile()) log.info("File already exists, overwriting");
        } catch (IOException e) {
            log.severe("Cannot create file: " + file.getAbsolutePath());
            System.err.println("Cannot create file");
            System.exit(1);
        }
        if (!file.canWrite()) {
            log.severe("Cannot write to file: " + file.getAbsolutePath());
            System.err.println("Cannot write to file");
            System.exit(1);
        }

        Map<Point, GameField.TileType> map = generateMap();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            log.fine("Writing map to file: " + file.getAbsolutePath());
            for (int row = -FieldSize.height / 2; row <= FieldSize.height / 2; row++) {
                for (int col = -FieldSize.width / 2; col <= FieldSize.width / 2; col++) {
                    byte out = map.get(new Point(col, row)).getSave();
                    fos.write(out);
                }
            }
            log.fine("Map successfully written to file");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error writing map to file", e);
        }
    }

    // TODO: Document
    public static Map<Point, GameField.TileType> generateMap() {
        log.fine("Generating new map...");
        Map<Point, GameField.TileType> map = new HashMap<>();

        log.finer("Filling game field with sky...");
        for (int row = GroundLevel; row <= FieldSize.height / 2; row++) {
            for (int col = -FieldSize.width / 2; col <= FieldSize.width / 2; col++) {
                map.put(new Point(col, row), GameField.TileType.SKY);
                log.finest("\t<Sky at (" + col + ", " + row + ")");
            }
        }

        log.finer("Generating ground...");
        for (int row = -FieldSize.height / 2; row < GroundLevel; row++) {
            for (int col = -FieldSize.width / 2; col <= FieldSize.width / 2; col++) {
                map.put(new Point(col, row), GameField.TileType.GROUND);
                log.finest("\t<Ground at (" + col + ", " + row + ")");
            }
        }

        log.fine("Map generated");

        return map;
    }
}
