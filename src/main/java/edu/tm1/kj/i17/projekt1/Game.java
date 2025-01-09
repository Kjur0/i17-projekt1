package edu.tm1.kj.i17.projekt1;

import edu.tm1.kj.i17.projekt1.window.GameFrame;
import edu.tm1.kj.i17.projekt1.window.GamePanel;
import lombok.extern.java.Log;

import java.awt.*;

// TODO: Document
@Log
public class Game {
    public final static GameFrame Frame = new GameFrame();
    public final static GamePanel Panel = new GamePanel();
    public final static GameField Field = new GameField();
    public final static Player Player = new Player();
    public final static GameLoop Loop = new GameLoop();

    public static double deltaTime;

    public static void main(String[] args) {
        log.finer("Initializing game...");

        Frame.addKeyListener(Player.Controller);
        log.finer("Attaching player controller");

        Frame.add(Panel);
        log.finer("Attaching game panel");

        Frame.setVisible(true);
        log.fine("Game initialized");

        log.fine("Starting game...");
        Loop.start();

        log.info("Game started");
    }

    // TODO: Document
    public static class Config {
        public static final String Title = "Gra";
        public static final int FPS = 60;
        public static final double AnnounceInterval = 1.0; // seconds
        public static final int TileSize = 64;
        public static final Dimension FieldSize = new Dimension(65, 17);
        public static final int GroundLevel = -6;
        public static final Dimension PlayerSize = new Dimension(64, 96);
        public static final float Acceleration = 1f;
        public static final double maxSpeed = 10.0;
        public static final double Jump = 2.5;
        public static final double JumpTime = 2;
        public static final double Gravity = 9.8;
    }
}
