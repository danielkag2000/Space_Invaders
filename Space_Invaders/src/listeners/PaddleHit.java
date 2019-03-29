package listeners;

import interfaces.HitListener;
import objects.Ball;
import objects.Block;

/**
 * A paddle hit listener.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-06-21
 */
public class PaddleHit implements HitListener {

    private boolean hit;

    /**
     * the constructor.
     */
    public PaddleHit() {
        this.hit = false;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        this.hit = true;
    }

    /**
     * check if the paddle was hit.
     *
     * @return true if the paddle was hit,
     *         otherwise, false.
     */
    public boolean wasHit() {
        if (this.hit) {
            this.hit = false;
            return true;
        }
        return false;
    }
}
