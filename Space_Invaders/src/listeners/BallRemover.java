package listeners;

import game.GameLevel;

import interfaces.HitListener;

import objects.Ball;
import objects.Block;

/**
 * the Ball Remover listener remove the ball from the game
 * whenever a ball hit a death block.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-05-17
 */
public class BallRemover implements HitListener {

    private GameLevel game;

    /**
     * the constructor of the Listener.
     *
     * @param game the current game
     */
    public BallRemover(GameLevel game) {
        this.game = game;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {

        hitter.removeFromGame(game);
    }
}
