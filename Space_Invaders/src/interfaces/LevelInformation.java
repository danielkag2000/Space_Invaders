package interfaces;

/**
 * The LevelInformation interface specifies
 * the information required to fully describe a level.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-05-19
 */
public interface LevelInformation {

    /**
     * get the speed of the paddle.
     *
     * @return the speed of the paddle.
     */
    int paddleSpeed();

    /**
     * get the width of the paddle.
     *
     * @return the width of the paddle.
     */
    int paddleWidth();

    /**
     * get the name of the level.
     *
     * @return the name of the level
     */
    String levelName();

    /**
     * Returns a sprite with the background of the level.
     *
     * @return a sprite with the background of the level.
     */
    Sprite getBackground();

    /**
     * Number of levels that should be removed
     * before the level is considered to be "cleared".
     *
     * @return the number of blocks to remove
     */
    int numberOfInvadersToRemove();
}
