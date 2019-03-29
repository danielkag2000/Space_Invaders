package objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import biuoop.DrawSurface;
import game.GameLevel;
import interfaces.HitListener;
import interfaces.Sprite;

/**
 * the collectio of the invaders.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-06-21
 */
public class InvaderColection implements Sprite {

    private List<Stack<Invader>> invaders;
    private Velocity velocity;
    private Velocity startVelocity;
    private int startScreen;
    private int endScreen;
    private long startTime;
    private final int loseY = 500;

    /**
     * the constructor.
     *
     * @param invaders the invaders.
     * @param v the initial velocity
     */
    public InvaderColection(List<Stack<Invader>> invaders, Velocity v) {
        this.invaders = invaders;
        this.velocity = v;
        this.startScreen = GameLevel.getDistanceFromEdge();
        this.endScreen = 800 - GameLevel.getDistanceFromEdge();
        this.startTime = System.currentTimeMillis();
        this.startVelocity = new Velocity(v.getDx(), v.getDy());
    }

    @Override
    public void drawOn(DrawSurface d) {

        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                inv.drawOn(d);
            }
        }
    }

    /**
     * get the front lines invaders.
     *
     * @return the front lines invaders.
     */
    private List<Invader> getFirsts() {
        List<Invader> firsts = new ArrayList<Invader>();
        for (Stack<Invader> invStack : this.invaders) {
            firsts.add(invStack.peek());
        }
        return firsts;
    }

    @Override
    public void timePassed(double dt, GameLevel game) {
        this.velocity.setDx(dt * this.velocity.getDx());
        moveAll();
        if (this.outOfScreen()) {
            moveAllDown();
            this.velocity.setDx(-1.1 * this.velocity.getDx());
            moveAll();
        }

        if (canShot() && !game.shouldStop()) {
            List<Invader> invFirst = this.getFirsts();
            java.util.Random rnd = new java.util.Random();
            invFirst.get(rnd.nextInt(invFirst.size())).shot(game);
            this.startTime = System.currentTimeMillis();
        }
        this.velocity.setDx(this.velocity.getDx() / dt);
    }

    /**
     * move all the invaders down.
     */
    private void moveAllDown() {
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                inv.moveDown();
            }
        }
    }

    /**
     * move all the invaders in the velocity direction.
     */
    private void moveAll() {
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                inv.move(this.velocity);
            }
        }
    }

    /**
     * check if the invaders can shoot.
     *
     * @return true if tthey can shoot
     *         otherwise, false.
     */
    private boolean canShot() {
        double currenTine = System.currentTimeMillis();
        return Math.abs(currenTine - this.startTime) > (0.5 * 1000);
    }

    /**
     * check if the invader went out from the screen.
     *
     * @return true if the invaders out of the screen
     *         otherwise, false.
     */
    private boolean outOfScreen() {
        if ((getMaxInvaderX() > this.endScreen) || (this.startScreen > getMinInvaderX())) {
            return true;
        }
        return false;
    }

    /**
     * get the x of the last invader from left.
     *
     * @return the x of the last invader from left.
     */
    private double getMaxInvaderX() {
        double max = 0;
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                double invMax = inv.getCollisionRectangle().getUpperLeft().getX()
                             + inv.getCollisionRectangle().getWidth();
                if (max < invMax) {
                    max = invMax;
                }
            }
        }
        return max;
    }

    /**
     * get the x of the first invader from right.
     *
     * @return the x of the first invader from right.
     */
    private double getMinInvaderX() {
        double min = 800;
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                double invMin = inv.getCollisionRectangle().getUpperLeft().getX();
                if (min > invMin) {
                    min = invMin;
                }
            }
        }
        return min;
    }

    /**
     * remove invader from the collection.
     *
     * @param invader the invader to remove
     * @param game the game.
     * @return true if sucsses to remove,
     *         otherwise, false.
     */
    public boolean remove(Invader invader, GameLevel game) {
        if (invader != null) {
            invader.removeFromGame(game);
            game.removeCollidable(invader);
            for (Stack<Invader> invaderStack : invaders) {
                invaderStack.remove(invader);
            }
            for (int i = 0; i < invaders.size(); i++) {
                if (invaders.get(i).isEmpty()) {
                    invaders.remove(i);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * remove invader from the collection.
     *
     * @param invaderBlock the block of the invader.
     * @param game the game
     * @return true if sucsses to remove,
     *         otherwise, false.
     */
    public boolean remove(Block invaderBlock, GameLevel game) {
        return remove(this.findInvader(invaderBlock), game);
    }

    /**
     * find the invader given by  his block.
     *
     * @param invaderBlock the block of the invader
     * @return the invader of the block.
     */
    private Invader findInvader(Block invaderBlock) {
        for (Stack<Invader> invaderStack : invaders) {
            for (Invader invader : invaderStack) {
                if (invaderBlock.equals(invader.getBlock())) {
                    return invader;
                }
            }
        }
        return null;
    }

    /**
     * add Listener to the invaders.
     *
     * @param hl the hit Listener
     */
    public void addListener(HitListener hl) {
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                inv.addHitListener(hl);
            }
        }
    }

    /**
     * check if the player lost.
     *
     * @return true if one invader got to the shield hight.
     *         otherwise, return false.
     */
    public boolean didThePlayerLose() {
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                double y = inv.getBlock().getCollisionRectangle().getUpperLeft().getY()
                           + inv.getBlock().getCollisionRectangle().getHeight();
                if (y > loseY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * reset all the invaders.
     */
    public void resetAllInvaders() {
        for (Stack<Invader> invStack : this.invaders) {
            for (Invader inv : invStack) {
                inv.reset();
            }
        }
        this.velocity = new Velocity(this.startVelocity.getDx(), this.startVelocity.getDy());
    }
}
