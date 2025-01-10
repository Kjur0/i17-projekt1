package edu.tm1.kj.i17.projekt1;

import lombok.Getter;
import lombok.extern.java.Log;

import java.awt.*;
import java.awt.geom.Path2D;

import static edu.tm1.kj.i17.projekt1.Game.*;
import static edu.tm1.kj.i17.projekt1.Game.Config.TileSize;

// TODO: Document
// TODO: Implement the Player class
@Getter
@Log
public class Player {
    public final PlayerController Controller = new PlayerController();

    private final int playerWidth;
    private final int playerHeight;

    private final int playerOffsetX;
    private final int playerOffsetY;

    private double x;
    private double y;

    private double vx;
    private double vy;

    private double ax;
    private double ay;

    private boolean onGround;

    private double jumpTime;

    public Player() {
        log.finer("Creating player...");
        log.config("Player size: (" + Config.PlayerSize.width + ", " + Config.PlayerSize.height + ")");

        playerWidth = Config.PlayerSize.width;
        playerHeight = Config.PlayerSize.height;

        playerOffsetX = playerWidth / 2;
        playerOffsetY = playerHeight + TileSize / 2;

        log.config("Player position: (" + x + ", " + y + ")");
        log.fine("Player created");
    }

    public void update() {
        if (Controller.isLeftPressed()) {
            log.finest("\t>Left pressed");
            vx = -Config.Acceleration;
        }
        if (Controller.isRightPressed()) {
            log.finest("\t>Right pressed");
            vx = Config.Acceleration;
        }
        if (Controller.isUpPressed()) {
            log.finest("\t>Up pressed");
            vy = Config.Acceleration;
        }
        if (Controller.isDownPressed()) {
            log.finest("\t>Down pressed");
            vy = -Config.Acceleration;
        }

        x += vx;
        y += vy;

        int maxX = Config.FieldSize.width / 2;
        if (Math.abs(x) > maxX) {
            x = Math.signum(x) * maxX;
        }
        int maxY = Config.FieldSize.height / 2;
        if (y < -maxY) {
            y = -maxY;
        } else if (y > maxY) {
            y = maxY;
        }

        vx = 0;
        vy = 0;
    }

    // TODO: fixTHIS
    @SuppressWarnings("unused")
    public void updateBROKEN() {
        log.finest("Updating player...");

        log.finest("\t>Player position: (" + x + ", " + y + ")");
        log.finest("\t>Player speed: (" + vx + ", " + vy + ")");

        onGround = Field.getTileAt(x, y - 1).isSolid() && x == (int) x;
        log.finest("\t>On ground: " + onGround);

        ax = 0;
        ay = 0;
        log.finest("\tCalculating acceleration & velocity...");


        log.finest("\tX-axis...");
        if (Controller.isLeftPressed()) {
            log.finest("\t>Left pressed");
            ax = -Config.Acceleration;
            vx = Math.max(vx + ax * deltaTime, -Config.maxSpeed);
        }
        if (Controller.isRightPressed()) {
            log.finest("\t>Right pressed");
            ax = Config.Acceleration;
            vx = Math.min(vx + ax * deltaTime, Config.maxSpeed);
        }
        if (!Controller.isRightPressed() && !Controller.isLeftPressed()) {
            log.finest("\t>No horizontal input. Calculating friction...");

            double friction = Field.getTileAt(x, y - 0.5).getFriction();
            log.finest("\t>Tile friction: " + friction);

            if (vx > 0) {
                vx = Math.max(vx - friction * deltaTime, 0);
            } else if (vx < 0) {
                vx = Math.min(vx + friction * deltaTime, 0);
            }
        }
        log.finest("\t<ax: " + ax + ", vx: " + vx);


        log.finest("\tY-axis...");
        if (onGround) {
            log.finest("\t>On ground");
            if (Controller.isJumpPressed()) {
                log.finest("\t>Jump pressed on ground");
                vy = Config.Jump / 2 / Config.JumpTime * deltaTime;
                jumpTime = Game.Loop.getSigmaTime();
            }
        } else {
            if (Controller.isJumpPressed() && vy < Config.Jump) {
                log.finest("\t>Jump pressed in air. Extending jump...");
                vy = Math.min(vy + Config.Jump * deltaTime, Config.Jump);
            } else {
                log.finest("\t>In air. Calculating gravity...");
                ay = -Config.Gravity;
                vy += ay * deltaTime;
            }
        }
        log.finest("\t<ay: " + ay + ", vy: " + vy);

        log.finest("\t<Player speed: (" + vx + ", " + vy + ")");

        log.finest("\tCalculating new position...");

        handleCollisions();

        log.finest("\t<Player position: (" + x + ", " + y + ")");
    }

    private void handleCollisions() {
        log.finest("\t\tHandling collisions...");

        log.finest("\t\t>Player position: (" + x + ", " + y + ")");
        log.finest("\t\t>Player speed: (" + vx + ", " + vy + ")");

        log.finest("\t\t>Calculating temporary position...");
        double oldX = x;
        double oldY = y;
        x = x + vx * deltaTime;
        y = y + vy * deltaTime;

        log.finest("\t\tTemporary position: (" + x + ", " + y + ")");

        log.finest("\t\tPerforming out-of-bounds check...");
        int maxX = Config.FieldSize.width / 2;
        int maxY = Config.FieldSize.height / 2;
        boolean outOfBounds = Math.abs(x) > maxX || Math.abs(y) > maxY;
        if (outOfBounds) {
            log.finest("\t\t>Out of bounds");
            if (Math.abs(x) > maxX) {
                log.finest("\t\t>Out of bounds on X-axis");
                int halfWidth = playerWidth / 2;
                if (x > 0) {
                    x = maxX - halfWidth;
                } else {
                    x = -maxX + halfWidth;
                }
            }
            if (Math.abs(y) > maxY) {
                log.finest("\t\t>Out of bounds on Y-axis");
                int halfTile = TileSize / 2;
                int halfHeight = playerHeight - halfTile;
                if (y > 0) {
                    y = maxY - halfHeight;
                } else {
                    y = -maxY + halfTile;
                }
            }
        }

        Rectangle hitbox = getHitbox(x, y);
        log.finest("\t\t>Player hitbox: " + hitbox);

        log.finest("\t\t>Calculating collision box...");
        double tmpX = x + (vx == 0 ? 0 : vx > 0 ? 0.5 : -0.5);
        double tmpY = y + (vy == 0 ? 0 : vy > 0 ? 0.5 : -0.5);

        Path2D collision = Field.getCollision(tmpX, tmpY);
        log.finest("\t\t>Collision box: " + collision);

        if (collision == null) {
            log.finest("\t\t>No collision detected");
            return;
        }
        if (collision.intersects(hitbox)) {
            log.finest("\t\t>Collision detected");

            log.finest("\t\t>Calculating overlap...");
            Rectangle overlap = calculateOverlap(collision, hitbox, oldX, oldY);
            log.finest("\t\t>Overlap: (" + overlap.width + ", " + overlap.height + ")");

            x -= overlap.width;
            y -= overlap.height;
        }

        log.finest("\t\t<Player position: (" + x + ", " + y + ")");
        log.finest("\t\tCollision handled");
    }

    private Rectangle getHitbox(double x, double y) {
        log.finest("\t\t\tCalculating player hitbox...");

        log.finest("\t\t\t > Hitbox origin: (" + x + ", " + y + ")");

        int boxX = (int) Math.round(x * TileSize - playerOffsetX);
        int boxY = (int) Math.round(-y * TileSize - playerOffsetY);
        return new Rectangle(boxX, boxY, playerWidth, playerHeight);
    }

    private Rectangle calculateOverlap(Path2D collision, Rectangle hitbox, double oldX, double oldY) {
        log.finest("\t\t\tCalculating overlap...");

        Rectangle overlap = new Rectangle();

        Rectangle current = getHitbox(oldX, oldY);

        if (vx > 0 && vy > 0) {
            int maxX = (int) hitbox.getMaxX();
            int maxY = (int) hitbox.getMaxY();
            int minX = (int) current.getMaxX();
            int minY = (int) current.getMaxY();

            while (maxX >= minX && maxY >= minY) {
                int midX = (maxX + minX) / 2;
                int midY = (maxY + minY) / 2;

                if (!collision.contains(midX, midY) && collision.contains(midX + 1, midY + 1)) {
                    overlap.width = (int) hitbox.getMaxX() - midX;
                    overlap.height = (int) hitbox.getMaxY() - midY;
                    return overlap;
                }
                if (collision.contains(midX, midY) && !collision.contains(midX - 1, midY - 1)) {
                    overlap.width = (int) hitbox.getMaxX() - midX - 1;
                    overlap.height = (int) hitbox.getMaxY() - midY - 1;
                    return overlap;
                }
                if (!collision.contains(midX, midY)) {
                    minX = midX;
                    minY = midY;
                } else if (collision.contains(midX, midY)) {
                    maxX = midX;
                    maxY = midY;
                }
            }
        } else if (vx > 0 && vy < 0) {
            int maxX = (int) hitbox.getMaxX();
            int maxY = (int) hitbox.getMinY();
            int minX = (int) current.getMaxX();
            int minY = (int) current.getMinY();

            while (maxX >= minX && maxY <= minY) {
                int midX = (maxX + minX) / 2;
                int midY = (maxY + minY) / 2;

                if (!collision.contains(midX, midY) && collision.contains(midX + 1, midY - 1)) {
                    overlap.width = (int) hitbox.getMaxX() - midX;
                    overlap.height = (int) hitbox.getMinY() - midY;
                    return overlap;
                }
                if (collision.contains(midX, midY) && !collision.contains(midX - 1, midY + 1)) {
                    overlap.width = (int) hitbox.getMaxX() - midX - 1;
                    overlap.height = (int) hitbox.getMinY() - midY + 1;
                    return overlap;
                }
                if (!collision.contains(midX, midY)) {
                    minX = midX;
                    maxY = midY;
                } else if (collision.contains(midX, midY)) {
                    maxX = midX;
                    minY = midY;
                }
            }
        } else if (vx < 0 && vy > 0) {
            int maxX = (int) hitbox.getMinX();
            int maxY = (int) hitbox.getMaxY();
            int minX = (int) current.getMinX();
            int minY = (int) current.getMaxY();

            while (maxX <= minX && maxY >= minY) {
                int midX = (maxX + minX) / 2;
                int midY = (maxY + minY) / 2;

                if (!collision.contains(midX, midY) && collision.contains(midX - 1, midY + 1)) {
                    overlap.width = (int) hitbox.getMinX() - midX;
                    overlap.height = (int) hitbox.getMaxY() - midY;
                    return overlap;
                }
                if (collision.contains(midX, midY) && !collision.contains(midX + 1, midY - 1)) {
                    overlap.width = (int) hitbox.getMinX() - midX + 1;
                    overlap.height = (int) hitbox.getMaxY() - midY - 1;
                    return overlap;
                }
                if (!collision.contains(midX, midY)) {
                    maxX = midX;
                    minY = midY;
                } else if (collision.contains(midX, midY)) {
                    minX = midX;
                    maxY = midY;
                }
            }
        } else {
            int maxX = (int) hitbox.getMinX();
            int maxY = (int) hitbox.getMinY();
            int minX = (int) current.getMinX();
            int minY = (int) current.getMinY();

            while (maxX <= minX && maxY <= minY) {
                int midX = (maxX + minX) / 2;
                int midY = (maxY + minY) / 2;

                if (!collision.contains(midX, midY) && collision.contains(midX - 1, midY - 1)) {
                    overlap.width = (int) hitbox.getMinX() - midX;
                    overlap.height = (int) hitbox.getMinY() - midY;
                    return overlap;
                }
                if (collision.contains(midX, midY) && !collision.contains(midX + 1, midY + 1)) {
                    overlap.width = (int) hitbox.getMinX() - midX + 1;
                    overlap.height = (int) hitbox.getMinY() - midY + 1;
                    return overlap;
                }
                if (!collision.contains(midX, midY)) {
                    maxX = midX;
                    maxY = midY;
                } else if (collision.contains(midX, midY)) {
                    minX = midX;
                    minY = midY;
                }
            }
        }

        return overlap;
    }
}
