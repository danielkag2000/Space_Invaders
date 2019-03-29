package interfaces;

import game.GameLevel;

/**
 * A object that can shot.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-06-21
 */
public interface Shotable {

    /**
     * shot a ball.
     *
     * @param game the game
     */
    void shot(GameLevel game);
}
