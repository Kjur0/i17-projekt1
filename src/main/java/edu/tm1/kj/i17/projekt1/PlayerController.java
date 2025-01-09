package edu.tm1.kj.i17.projekt1;

import lombok.Getter;
import lombok.extern.java.Log;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static edu.tm1.kj.i17.projekt1.Game.Config;

// TODO: Document
@Log
@Getter
public class PlayerController implements KeyListener {
    private boolean jumpPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;

    public PlayerController() {
        log.finer("Creating player controller...");
        leftPressed = false;
        rightPressed = false;

        log.config("Acceleration: " + Config.Acceleration);
        log.config("Max speed: " + Config.maxSpeed);
        log.config("Jump height: " + Config.Jump);
        log.config("Gravity: " + Config.Gravity);

        log.fine("Player controller created");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        log.finest(">Key pressed: " + e.getKeyChar());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
            case KeyEvent.VK_SPACE:
                jumpPressed = true;
                upPressed = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                downPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.finest(">Key released: " + e.getKeyChar());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
            case KeyEvent.VK_SPACE:
                jumpPressed = false;
                upPressed = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                downPressed = false;
                break;
        }
    }
}
