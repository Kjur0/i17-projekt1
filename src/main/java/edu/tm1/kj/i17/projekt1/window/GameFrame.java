package edu.tm1.kj.i17.projekt1.window;

import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;

import static edu.tm1.kj.i17.projekt1.Game.Config;

@Log
public class GameFrame extends JFrame {

    public GameFrame() {
        log.finer("Initializing game frame...");

        setResizable(false);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        log.finest("Entering borderless maximized window mode");

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
            log.finest("Entering full screen mode");
        } else log.warning("Full screen mode not supported");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        log.finest("Exiting on close");

        setBackground(Color.BLACK);
        log.finest("Background color: " + getBackground());

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        log.finest("Focusing");

        log.config("Title: " + Config.Title);
        setTitle(Config.Title);

        setIgnoreRepaint(true);
        log.finest("Ignoring repaint");

        createBufferStrategy(2);
        log.finest("Buffer strategy created. Number of buffers: 2");

        log.fine("Game frame initialized");
    }
}
