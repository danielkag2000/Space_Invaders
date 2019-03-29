package objects;

import game.GameLevel;

import interfaces.Collidable;
import interfaces.HitListener;
import interfaces.HitNotifier;
import interfaces.Shotable;
import interfaces.Sprite;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

/**
 * The Paddle is the player in the game.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-04-15
 */
public class Paddle implements Sprite, Collidable, HitNotifier, Shotable {

    private biuoop.KeyboardSensor keyboard;  // the players keyboard
    private Rectangle paddle;
    private int paddleMove = 5;  // the move of the paddle -- default 5
    private double paddleMoveOfDt = 5;  // the move of the paddle -- default 5
    private int fromX;  // the left border
    private int toX;  // the right border
    private List<HitListener> hitListeners;
    private double startTime;  // the time of start to count

    /**
     * the paddle constructor.
     *
     * @param keyboard the player's keybord
     * @param upperLeft the upper left point of the paddle
     * @param width the height of the paddle
     * @param height the height of the paddle
     * @param fromX the left border of the paddle
     * @param toX the right border of the paddle
     */
    public Paddle(biuoop.KeyboardSensor keyboard, Point upperLeft, int width, int height, int fromX, int toX) {
        this.keyboard = keyboard;
        this.paddle = new Rectangle(upperLeft, width, height);
        this.fromX = fromX;
        this.toX = toX;
        this.hitListeners = new ArrayList<HitListener>();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * move the player to left direction.
     */
    public void moveLeft() {
        double epsilon = 0.02;
        if (this.paddle.getUpperLeft().getX() - this.paddleMoveOfDt < this.fromX + epsilon) {
            this.paddle = new Rectangle(new Point(this.fromX + epsilon, this.paddle.getUpperLeft().getY()),
                                        (int) this.paddle.getWidth(), (int) this.paddle.getHeight());
        } else {
            this.paddle = new Rectangle(new Point(this.paddle.getUpperLeft().getX() - this.paddleMoveOfDt,
                    this.paddle.getUpperLeft().getY()),
                    (int) this.paddle.getWidth(), (int) this.paddle.getHeight());
        }
    }

    /**
     * move the player to right direction.
     */
    public void moveRight() {
        double epsilon = 0.02;
        if (this.paddle.getUpperLeft().getX() + this.paddle.getWidth() + this.paddleMoveOfDt > this.toX - epsilon) {
            this.paddle = new Rectangle(new Point(this.toX - this.paddle.getWidth() - epsilon,
                                        this.paddle.getUpperLeft().getY()),
                                        (int) this.paddle.getWidth(), (int) this.paddle.getHeight());
        } else {
            this.paddle = new Rectangle(new Point(this.paddle.getUpperLeft().getX() + this.paddleMoveOfDt,
                    this.paddle.getUpperLeft().getY()),
                    (int) this.paddle.getWidth(), (int) this.paddle.getHeight());
        }
    }

    @Override
    public Rectangle getCollisionRectangle() {
        return this.paddle;
    }

    @Override
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        // the new velocity
        Velocity newVelocity = new Velocity(currentVelocity.getDx(), currentVelocity.getDy());
        Line[] recLines = this.paddle.getLineArr();

        double thisX = collisionPoint.getX();
        double thisY = collisionPoint.getY();

        double stX = this.paddle.getUpperLeft().getX();
        double stY = this.paddle.getUpperLeft().getY();

        double enX = this.paddle.getUpperLeft().getX() + this.paddle.getWidth();
        //double enY = this.paddle.getUpperLeft().getY() + this.paddle.getHeight();

        this.notifyHit(hitter);

        // if the collision on the edge
        if (collisionPoint.equals(recLines[0].start()) || collisionPoint.equals(recLines[0].end())
            || collisionPoint.equals(recLines[2].start()) || collisionPoint.equals(recLines[2].end())) {

            if (collisionPoint.equals(recLines[0].start())) {
                return Velocity.fromAngleAndSpeed(-60, currentVelocity.speed());

            } else if (collisionPoint.equals(recLines[0].end())) {
                return Velocity.fromAngleAndSpeed(60, currentVelocity.speed());

            } else {
                return new Velocity(-1 * newVelocity.getDx(), -1 * newVelocity.getDy());
            }
        }

        if (((stX) < thisX && thisX < (enX)) && thisY == this.paddle.getUpperLeft().getY()) {
            if (thisY == stY) {
                int sector = (int) ((collisionPoint.getX() - this.paddle.getUpperLeft().getX())
                               / (this.paddle.getWidth() / 5));

                return Velocity.fromAngleAndSpeed(300 + 30 * sector, currentVelocity.speed());
            }
            newVelocity.setDy(-1 * newVelocity.getDy());
        }

        //if (stY < thisY && thisY < enY) {
            //newVelocity.setDx(-1 * newVelocity.getDx());
        //}

        return newVelocity;
    }

    @Override
    public void drawOn(DrawSurface d) {
        d.setColor(java.awt.Color.ORANGE);
        d.fillRectangle((int) this.paddle.getUpperLeft().getX(), (int) this.paddle.getUpperLeft().getY(),
                        (int) this.paddle.getWidth(), (int) this.paddle.getHeight());
        d.setColor(java.awt.Color.BLACK);
        d.drawRectangle((int) this.paddle.getUpperLeft().getX(), (int) this.paddle.getUpperLeft().getY(),
                (int) this.paddle.getWidth(), (int) this.paddle.getHeight());
    }

    @Override
    public void timePassed(double dt, GameLevel game) {

        this.paddleMoveOfDt = dt * this.paddleMove;
        if (this.keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
            this.moveLeft();
        } else if (this.keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
            this.moveRight();
        }

        if (this.keyboard.isPressed(KeyboardSensor.SPACE_KEY)) {
            if (this.isAbaleToShot() && !game.shouldStop()) {
                this.shot(game);
            }
        }
    }

    /**
     * align the Paddle to the center.
     */
    public void alignToCenter() {
        this.paddle = new Rectangle(new Point(800 / 2 - this.paddle.getWidth() / 2,
                                    600 - 20 - GameLevel.getDistanceFromEdge()), this.paddle.getWidth(), 20);
    }

    /**
     * add the paddle
     * as a Sprite and as a Collidable object to the game.
     *
     * @param g the game
     */
    public void addToGame(GameLevel g) {
        g.getSpriteCollection().addSprite(this);
        g.getGameEnvironment().addCollidable(this);
    }

    /**
     * get the speed of the paddle.
     *
     * @return the speed of the paddle.
     */
    public int getPaddleMove() {
        return (int) paddleMoveOfDt;
    }

    /**
     * set the speed of the paddle.
     *
     * @param speed the speed of the paddle
     */
    public void setPaddleMove(int speed) {
        this.paddleMove = speed;
        this.paddleMoveOfDt = speed;
    }

    /**
     * check if the the player can shoot.
     *
     * @return true if the the player can shoot
     *         otherwise, false.
     */
    private boolean isAbaleToShot() {
        double currentTime = System.currentTimeMillis();
        return Math.abs(currentTime - this.startTime) > (0.35 * 1000);
    }

    @Override
    public void shot(GameLevel game) {
        Ball ball = new Ball((int) this.paddle.getUpperLeft().getX() + (int) (this.paddle.getWidth() / 2),
                          (int) this.paddle.getUpperLeft().getY() - 20, 3, Color.WHITE);
        ball.setScreenSize(new Point(GameLevel.getDistanceFromEdge(), GameLevel.getDistanceFromEdge() * 2),
                        new Point(800 - GameLevel.getDistanceFromEdge(), 600));
        ball.setVelocity(0, -450);
        ball.addToGame(game);
        ball.setGameEnvironment(game.getGameEnvironment());
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * notify to the listeners that there was a hit.
     *
     * @param hitter the ball.
     */
    private void notifyHit(Ball hitter) {
        // Make a copy of the hitListeners before iterating over them.
        List<HitListener> listeners = new ArrayList<HitListener>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
           hl.hitEvent(new Block(paddle, 1, Color.YELLOW), hitter);
        }
    }

}
