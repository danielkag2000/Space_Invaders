package interfaces;

import biuoop.DrawSurface;
import game.GameLevel;

/**
 * A game object that can be drawn to the screen.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-04-15
 */
public interface Sprite {

    /**
     * Draw the sprite to the screen.
     *
     * @param d a DrawSurface
     */
    void drawOn(DrawSurface d);

    /**
     * Notify the sprite that time has passed.
     *
     * @param dt the amount of seconds passed since the last call
     * @param game the game.
     */
    void timePassed(double dt, GameLevel game);
}
