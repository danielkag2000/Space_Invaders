package objects;

import java.awt.Color;

import biuoop.DrawSurface;
import game.GameLevel;
import interfaces.Collidable;
import interfaces.HitListener;
import interfaces.HitNotifier;
import interfaces.Shotable;
import interfaces.Sprite;

/**
 * A represent of invader.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-06-21
 */
public class Invader extends Block implements Collidable, Shotable, Sprite, HitNotifier {

    private Block block;
    private int startX;
    private int startY;

    private static final int MOVE_DOWN = 50;
    private static Screen screen = new Screen(new Point(GameLevel.getDistanceFromEdge(),
                                                        GameLevel.getDistanceFromEdge() * 2),
                                              new Point(800 - GameLevel.getDistanceFromEdge(), 600));

    /**
     * the constructor.
     *
     * @param block a block.
     */
    public Invader(Block block) {
        super(block);
        this.block = block;
        this.startX = (int) block.getCollisionRectangle().getUpperLeft().getX();
        this.startY = (int) block.getCollisionRectangle().getUpperLeft().getY();
    }

    @Override
    public void shot(GameLevel game) {
        Ball ball = new Ball((int) this.block.getCollisionRectangle().getUpperLeft().getX()
                             + (int) (this.block.getCollisionRectangle().getWidth() / 2),
                             (int) this.block.getCollisionRectangle().getUpperLeft().getY()
                             + (int) this.block.getCollisionRectangle().getHeight() + 20, 5, Color.RED);
        ball.setScreenSize(Invader.screen.startPoint(), Invader.screen.endPoint());
        ball.setVelocity(0, 450);
        ball.addToGame(game);
        ball.setGameEnvironment(game.getGameEnvironment());
    }

    /**
     * return the invader to the place he started from.
     */
    public void reset() {
        this.block.setRectangle(new Rectangle(new Point(this.startX, this.startY),
                                              this.getCollisionRectangle().getWidth(),
                                              this.getCollisionRectangle().getHeight()));
    }

    /**
     * move the invader.
     *
     * @param v the velocity
     */
    public void move(Velocity v) {

        Point p = new Point(this.getCollisionRectangle().getUpperLeft().getX() + v.getDx(),
                            this.getCollisionRectangle().getUpperLeft().getY());
        this.block.setRectangle(new Rectangle(p, this.getCollisionRectangle().getWidth(),
                                this.getCollisionRectangle().getHeight()));
    }

    /**
     * move down the invader.
     */
    public void moveDown() {

        Point p = new Point(this.block.getCollisionRectangle().getUpperLeft().getX(),
                this.block.getCollisionRectangle().getUpperLeft().getY() + MOVE_DOWN);
        this.block.setRectangle(new Rectangle(p, this.getCollisionRectangle().getWidth(),
                                this.getCollisionRectangle().getHeight()));
    }

    @Override
    public void addHitListener(HitListener hl) {
        this.block.addHitListener(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        this.block.removeHitListener(hl);
    }

    @Override
    protected void notifyHit(Ball hitter) {
        this.block.notifyHit(hitter);
    }

    @Override
    public Rectangle getCollisionRectangle() {
        return this.block.getCollisionRectangle();
    }

    @Override
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        return this.block.hit(hitter, collisionPoint, currentVelocity);
    }

    @Override
    public void drawOn(DrawSurface d) {
        this.block.drawOn(d);
    }

    /**
     * remove the block from the game.
     *
     * @param game the Game
     */
    public void removeFromGame(GameLevel game) {
        game.removeCollidable(this.block);
    }

    /**
     * add the block
     * as a Sprite and as a Collidable object to the game.
     *
     * @param g the game
     */
    public void addToGame(GameLevel g) {
        g.getGameEnvironment().addCollidable(this.block);
    }

    /**
     * A get function of the gui of the game.
     *
     * @return the gui of the game
     */
    public int getHit() {
        return this.block.getHit();
    }

    /**
     * get the block of the invader.
     *
     * @return the block of the invader
     */
    public Block getBlock() {
        return this.block;
    }
}
