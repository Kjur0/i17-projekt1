package edu.tm1.kj.i17.projekt1.window;

import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;

import static edu.tm1.kj.i17.projekt1.Game.*;
import static edu.tm1.kj.i17.projekt1.Game.Config.TileSize;

@Log
public class GamePanel extends JPanel {

    private final int gameWidth;
    private final int gameHeight;

    private final int tileOffset;

    private final int playerWidth;
    private final int playerHeight;

    private final int playerOffsetX;
    private final int playerOffsetY;

    private final int halfGameWidth;
    private final int halfGameHeight;

    private final int maxX;
    private final int maxY;

    private final int fieldWidth;
    private final int fieldHeight;

    private int screenWidth;
    private int screenHeight;

    private double playerX;
    private double playerY;

    private int playerXScreen;
    private int playerYScreen;

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public GamePanel() {
        log.finer("Creating game panel...");

        setFocusable(true);

        setBackground(Color.BLACK);
        requestFocusInWindow();
        setDoubleBuffered(true);

        setIgnoreRepaint(true);

        fieldWidth = Config.FieldSize.width;
        fieldHeight = Config.FieldSize.height;

        gameWidth = fieldWidth * TileSize;
        gameHeight = fieldHeight * TileSize;
        log.config("Game size: (" + gameWidth + ", " + gameHeight + ")");

        tileOffset = TileSize / 2;
        log.config("Tile size: " + TileSize);
        log.config("Tile offset: " + tileOffset);

        playerWidth = Config.PlayerSize.width;
        playerHeight = Config.PlayerSize.height;

        playerOffsetX = playerWidth / 2;
        playerOffsetY = playerHeight + TileSize / 2;
        log.config("Player offset: (" + playerOffsetX + ", " + playerOffsetY + ")");

        halfGameWidth = gameWidth / 2;
        halfGameHeight = gameHeight / 2;
        log.finest("Half game size: (" + halfGameWidth + ", " + halfGameHeight + ")");

        maxX = fieldWidth / 2;
        maxY = fieldHeight / 2;
        log.finest("Max position: (" + maxX + ", " + maxY + ")");
    }

    @Override
    protected void paintComponent(Graphics g) {
        log.finest("<Repainting...");

        super.paintComponent(g);
        log.finest("\t<Clearing screen...");

        screenWidth = getWidth();
        screenHeight = getHeight();

        log.finest("\tScreen size: (" + screenWidth + ", " + screenHeight + ")");

        Graphics2D g2d = (Graphics2D) g;

        g2d.setClip(0, 0, screenWidth, screenHeight);
        calculateTranslations(g2d);

        playerX = Player.getX();
        playerY = Player.getY();

        for (int row = startY; row <= endY; row++) {
            for (int col = startX; col <= endX; col++) {
                int x = col * TileSize - tileOffset;
                int y = -row * TileSize - tileOffset;
                g2d.setColor(Field.getTileAt(col, row).getColor());
                g2d.fillRect(x, y, TileSize, TileSize);
                log.finest("\t<Tile at (" + col + ", " + row + ") drawn at (" + x + ", " + y + ")");
            }
        }

        playerXScreen = (int) Math.round(playerX * TileSize - playerOffsetX);
        playerYScreen = (int) Math.round(-playerY * TileSize - playerOffsetY);
        log.finest("\t<Player drawn at: (" + playerXScreen + ", " + playerYScreen + ")");

        g2d.setColor(Color.RED);
        g2d.fillRect(playerXScreen, playerYScreen, playerWidth, playerHeight);
    }

    private void calculateTranslations(Graphics2D g2d) {
        log.finest("\tRecalculating painting translations...");

        int halfScreenWidth = screenWidth / 2;
        int halfScreenHeight = screenHeight / 2;
        log.finest("\t\tHalf screen size: (" + halfScreenWidth + ", " + halfScreenHeight + ")");

        double screenWidthTiles = (double) screenWidth / TileSize;
        double screenHeightTiles = (double) screenHeight / TileSize;
        log.finest("\t\tScreen tility: (" + screenWidthTiles + ", " + screenHeightTiles + ")");

        double halfScreenWidthTiles = screenWidthTiles / 2;
        double halfScreenHeightTiles = screenHeightTiles / 2;
        log.finest("\t\tHalf screen tility: (" + halfScreenWidthTiles + ", " + halfScreenHeightTiles + ")");

        log.finest("\t\tCalculating X translations...");
        startX = -maxX;
        endX = maxX;
        if (screenWidth >= gameWidth) {
            log.finest("\t\tScreen too wide");
            g2d.translate(halfScreenWidth, 0);
            log.finest("\t\tCentering screen horizontally");
        } else if (playerX <= -halfScreenWidthTiles) {
            log.finest("\t\tLeft edge near");
            g2d.translate(halfGameWidth, 0);
            log.finest("\t\tBordering left edge");

            endX = (int) Math.ceil(screenWidthTiles) - maxX + 1;
        } else if (playerX >= fieldWidth - halfScreenWidthTiles) {
            log.finest("\t\tRight edge near");
            g2d.translate(screenWidth - halfGameWidth, 0);
            log.finest("\t\tBordering right edge");

            startX = maxX - (int) Math.floor(screenWidthTiles) - 1;
        } else {
            g2d.translate(halfScreenWidth - playerXScreen, 0);
            log.finest("\t\tCentering player horizontally");

            startX = (int) Math.floor(playerX - halfScreenWidthTiles) - 1;
            endX = (int) Math.ceil(playerX + halfScreenWidthTiles) + 1;
        }
        log.finest("\t\tX translations calculated");
        log.finest("\t\t\tStart X: " + startX);
        log.finest("\t\t\tEnd X: " + endX);

        log.finest("\t\tCalculating Y translations...");
        startY = -maxY;
        endY = maxY;
        if (screenHeight >= gameHeight) {
            log.finest("\t\tScreen too tall");
            g2d.translate(0, halfScreenHeight);
            log.finest("\t\tCentering screen vertically");
        } else if (playerY >= halfScreenHeightTiles) {
            log.finest("\t\tTop edge near");
            g2d.translate(0, halfGameHeight);
            log.finest("\t\tBordering top edge");

            endY = (int) Math.ceil(screenHeightTiles) - maxY + 1;
        } else if (playerY <= fieldHeight - halfScreenHeightTiles) {
            log.finest("\t\tBottom edge near");
            g2d.translate(0, screenHeight - halfGameHeight);
            log.finest("\t\tBordering bottom edge");

            startY = maxY - (int) Math.floor(screenHeightTiles) - 1;
        } else {
            g2d.translate(0, halfScreenHeight - playerYScreen);
            log.finest("\t\tCentering player vertically");

            startY = (int) Math.floor(playerY - halfScreenHeightTiles) - 1;
            endY = (int) Math.ceil(playerY + halfScreenHeightTiles) + 1;
        }
        log.finest("\t\tY translations calculated");
        log.finest("\t\t\tStart Y: " + startY);
        log.finest("\t\t\tEnd Y: " + endY);

        log.finest("\tPainting translations recalculated");
    }
}
