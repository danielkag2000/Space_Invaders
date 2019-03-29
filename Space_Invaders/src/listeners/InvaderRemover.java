package listeners;

import java.awt.Color;

import game.GameLevel;
import interfaces.HitListener;
import objects.Ball;
import objects.Block;
import objects.Counter;
import objects.InvaderColection;

/**
 * invader remover.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-06-21
 */
public class InvaderRemover implements HitListener {

    private GameLevel game;
    private Counter remainingInvaders;
    private InvaderColection invaders;

    /**
     * the constructor of the Listener.
     *
     * @param game the current game
     * @param remainingInvaders the counter of the invaders
     * @param invaders the invaders
     */
    public InvaderRemover(GameLevel game, Counter remainingInvaders, InvaderColection invaders) {
        this.game = game;
        this.remainingInvaders = remainingInvaders;
        this.invaders = invaders;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        if (!(hitter.getColor().equals(Color.RED))) {
            if (this.invaders.remove(beingHit, this.game)) {
                this.remainingInvaders.decrease(1);
            }
        }
    }
}
