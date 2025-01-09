package edu.tm1.kj.i17.projekt1;

import lombok.Getter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static edu.tm1.kj.i17.projekt1.Game.Config;
import static edu.tm1.kj.i17.projekt1.Game.deltaTime;

// TODO: Document
@Log
public class GameLoop extends Timer implements ActionListener {
    @Getter
    private long frame;
    @Getter
    private double sigmaTime;
    private long lastTime = System.nanoTime();
    private double announce = 0;

    public GameLoop() {
        super(1000 / Config.FPS, null);
        log.finer("Initializing game loop...");
        log.config("Target FPS: " + Config.FPS);
        addActionListener(this);
        log.fine("Game loop initialized");
    }

    // TODO: Document
    @Override
    public void actionPerformed(ActionEvent e) {
        log.finest("Tick: " + frame++);

        long now = System.nanoTime();
        deltaTime = (now - lastTime) / 1000000000D;
        lastTime = now;

        log.finest("Delta time: " + deltaTime);

        sigmaTime += deltaTime;

        log.finest("Sigma time: " + sigmaTime);

        announce += deltaTime;

        log.finest("FPS: " + (1 / deltaTime));

        if (announce >= Config.AnnounceInterval) {
            log.fine("FPS: " + (1 / deltaTime));
            announce = 0;
        }

        log.finest("FRAME START:");

        Game.Player.update();

        Graphics g = Game.Frame.getBufferStrategy().getDrawGraphics();

        log.finest("Graphics: " + g);

        Game.Panel.paintAll(g);

        Game.Frame.getBufferStrategy().show();
        log.finest("FRAME END");
    }
}
