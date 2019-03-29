package game;

import screens.CountdownAnimation;
import screens.KeyPressStoppableAnimation;
import screens.PauseScreen;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

import indicators.LevelIndicator;
import indicators.LivesIndicator;
import indicators.ScoreIndicator;

import interfaces.Animation;
import interfaces.Sprite;
import interfaces.Collidable;
import interfaces.LevelInformation;

import objects.AnimationRunner;
import objects.Ball;
import objects.Block;
import objects.Counter;
import objects.GameEnvironment;
import objects.Invader;
import objects.InvaderColection;
import objects.Paddle;
import objects.Point;
import objects.Screen;
import objects.SpriteCollection;
import objects.Velocity;

import listeners.BallRemover;
import listeners.BlockRemover;
import listeners.InvaderRemover;
import listeners.PaddleHit;
import listeners.ScoreTrackingListener;

/**
 * The GameLevel objects
 * have all the information of the levels,
 * and run them.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-04-19
 */
public class GameLevel implements Animation  {

    private LevelInformation levelInfo;  // the level information

    private SpriteCollection sprites;  // the Spirits collection
    private GameEnvironment environment;  // the Game Environment

    private biuoop.KeyboardSensor keyboard;  // the players keyboard

    private Screen screen;  // the screen of the game

    private static final int DISTANCE_FROM_EDGE = 0; // the distance from the edge

    private Paddle paddle;
    private InvaderColection invaderColection;
    private Block deathRegion;  // the death region

    // Indicators
    private ScoreIndicator showScore;
    private LivesIndicator showLives;
    private LevelIndicator showLevel;

    // counters
    private Counter scoreCounter;
    private Counter live;
    private Counter invaderCounter;
    private Counter remainBlocksInShield;

    // Listeners
    private BallRemover ballRemover;
    private BlockRemover blockRemover;
    private ScoreTrackingListener scoreListener;
    private InvaderRemover invaderRemover;
    private PaddleHit paddleHit;

    // Animation
    private AnimationRunner runner;
    private boolean running;

    private int level;

    /**
     * Create a new Game with the levels.
     *
     * @param levelInfo the order of the level to run
     * @param ks the player's keyboard
     * @param runner the animation runner
     * @param scoreCounter the score of the player
     * @param live the number of lives of the player
     * @param level the current level
     */
    public GameLevel(LevelInformation levelInfo, KeyboardSensor ks, AnimationRunner runner,
                     Counter scoreCounter, Counter live, int level) {
        this.levelInfo = levelInfo;
        this.keyboard = ks;
        this.runner = runner;

        this.sprites = new SpriteCollection();
        this.environment = new GameEnvironment();

        this.screen = new Screen(800, 600);  // the Screen of the game

        this.scoreCounter = scoreCounter;
        this.live = live;
        this.level = level;
    }

    /**
     * Initialize a new game: create the Blocks and Ball (and Paddle)
     * and add them to the game.
     */
    public void initialize() {

        // initialize runing
        this.running = false;

        // initialize counters
        this.invaderCounter = new Counter(this.levelInfo.numberOfInvadersToRemove());
        this.remainBlocksInShield = new Counter();

        // initialize Listeners
        this.scoreListener = new ScoreTrackingListener(this.scoreCounter);
        this.blockRemover = new BlockRemover(this, remainBlocksInShield);
        this.ballRemover = new BallRemover(this);
        this.paddleHit = new PaddleHit();

        // initialize Indicator
        this.showScore = new ScoreIndicator(this.scoreCounter);
        this.showLives = new LivesIndicator(this.live);
        this.showLevel = new LevelIndicator(this.levelInfo.levelName());

        this.sprites.addSprite(this.levelInfo.getBackground());

        // initialize for the death region
        this.deathRegion = new Block(new Point(0, this.screen.height() + 10), this.screen.width(),
                                     100, 0, java.awt.Color.GRAY);
        this.deathRegion.addToGame(this);
        this.deathRegion.addHitListener(ballRemover);

        // create the level objects
        this.createBorderBlocks();
        this.createDefBlocks();

        this.paddle = new Paddle(this.keyboard, new Point(this.screen.width() / 2 - this.levelInfo.paddleWidth() / 2,
                                 this.screen.height() - 20 - DISTANCE_FROM_EDGE),
                                 this.levelInfo.paddleWidth(), 20, DISTANCE_FROM_EDGE,
                                 this.screen.width() - DISTANCE_FROM_EDGE);

        this.paddle.setPaddleMove(this.levelInfo.paddleSpeed());
        this.paddle.addToGame(this);
        this.paddle.addHitListener(paddleHit);
        this.paddle.addHitListener(ballRemover);

        this.makeInvaderColection();
        this.invaderRemover = new InvaderRemover(this, this.invaderCounter, this.invaderColection);
        this.invaderColection.addListener(this.invaderRemover);
        this.invaderColection.addListener(this.ballRemover);
        this.invaderColection.addListener(this.scoreListener);

        // add the indicators spirits
        this.sprites.addSprite(this.showScore);
        this.sprites.addSprite(this.showLives);
        this.sprites.addSprite(this.showLevel);
    }

    /**
     * Run one turn the game -- start the animation loop.
     */
    public void playOneTurn() {
        this.runner.run(new CountdownAnimation(2, 3, this.sprites)); // count down before turn starts.

        this.running = true;
        this.runner.run(this);
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        this.sprites.drawAllOn(d);
        this.sprites.notifyAllTimePassed(dt, this);

        if (this.invaderCounter.getValue() <= 0) {
            this.running = false;
        }

        if (this.invaderColection.didThePlayerLose()) {
            this.running = false;
        }

        if (this.live.getValue() <= 0) {
            this.running = false;
        }

        if (this.remainBlocksInShield.getValue() <= 0) {
            this.createDefBlocks();
            this.running = false;
        }

        if (this.paddleHit.wasHit()) {
            this.running = false;
        }

        if (this.keyboard.isPressed("p")) {
            this.runner.run(new KeyPressStoppableAnimation(this.keyboard, KeyboardSensor.SPACE_KEY, new PauseScreen()));
        }
    }

    @Override
    public boolean shouldStop() {
        return !this.running;
    }

    /**
     * Run the game -- start the animation loop.
     */
    public void run() {
        while ((this.invaderCounter.getValue() > 0)
                && this.live.getValue() > 0) {
            this.playOneTurn();
            if (this.invaderCounter.getValue() <= 0) {
                break;

            } else {
                this.paddle.alignToCenter();
                this.invaderColection.resetAllInvaders();
                this.removeAllBallsFromTheGame();
                this.live.decrease(1);
            }
        }
    }

    /**
     * remove all the balls from the screen.
     */
    private void removeAllBallsFromTheGame() {

        int size = this.sprites.getSize();
        List<Ball> list = new ArrayList<Ball>();
        for (int i = 0; i < size; i++) {
            if (this.sprites.getIndexI(i) instanceof Ball) {
                list.add((Ball) this.sprites.getIndexI(i));
            }
        }

        size = list.size();
        for (int i = 0; i < size; i++) {
            this.sprites.removeSprite(list.get(0));
            list.get(0).removeFromGame(this);
            list.remove(0);
        }
    }

    /**
     * A get function of the Game Environment of the game.
     *
     * @return the Game Environment of the game
     */
    public GameEnvironment getGameEnvironment() {
        return this.environment;
    }

    /**
     * A get function of the Sprite Collection of the game.
     *
     * @return the SpriteCollection of the game
     */
    public SpriteCollection getSpriteCollection() {
        return this.sprites;
    }

    /**
     * remove from the game the Collidable object.
     *
     * @param c the Collidable object to remove
     */
    public void removeCollidable(Collidable c) {
        this.environment.removeCollidable(c);
    }

    /**
     * remove from the game the Spirit object.
     *
     * @param s the Spirit object to remove
     */
    public void removeSprite(Sprite s) {
        this.sprites.removeSprite(s);
    }

    /**
     * create the Blocks of the border.
     */
    public void createBorderBlocks() {
        Block b;  // the blocks that closes the game area

        // upper block
        b = new Block(new Point(0, 0), this.screen.width(),
                      20, 0, java.awt.Color.GRAY);
        b.addToGame(this);
        b.addHitListener(this.ballRemover);

        // right block
        b = new Block(new Point(this.screen.width() - DISTANCE_FROM_EDGE, DISTANCE_FROM_EDGE + DISTANCE_FROM_EDGE),
                       DISTANCE_FROM_EDGE, this.screen.height(), 0, java.awt.Color.GRAY);
        b.addToGame(this);

        // left block
        b = new Block(new Point(0, DISTANCE_FROM_EDGE + DISTANCE_FROM_EDGE), DISTANCE_FROM_EDGE,
                        this.screen.height(), 0, java.awt.Color.GRAY);
        b.addToGame(this);

    }

    /**
     * get the DISTANCE FROM EDGE.
     *
     * @return the DISTANCE FROM EDGE.
     */
    public static int getDistanceFromEdge() {
        return GameLevel.DISTANCE_FROM_EDGE;
    }

    /**
     * create the shield blocks.
     */
    private void createDefBlocks() {
        int x = 100;
        int y = 500;
        int xStart = 100;
        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {
                x = xStart;
                for (int k = 0; k < 30; k++) {

                    Block block = new Block(new Point(x, y), 5, 5, 1, Color.CYAN);
                    block.addToGame(this);
                    block.addHitListener(this.ballRemover);
                    block.addHitListener(this.blockRemover);
                    this.remainBlocksInShield.increase(1);
                    x += 5;
                }
                y += 5;
            }
            xStart += 225;
            y = 500;
        }
    }

    /**
     * make the collection of invaders.
     */
    private void makeInvaderColection() {

        int x = 155;
        int y = 50;

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("block_images/enemy.png");
        Image img = null;
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("Problem with loading the the image");
            System.exit(1);
        }
        Velocity v = new Velocity(20 * this.level, 0);
        List<Stack<Invader>> invaders = new ArrayList<Stack<Invader>>();
        for (int i = 0; i < 10; i++) {
            y = 50;
            Stack<Invader> invStack = new Stack<Invader>();
            invaders.add(invStack);
            for (int j = 0; j < 5; j++) {
                Invader inv = new Invader(new Block(new Point(x, y), 40, 30, 1, img));
                invStack.add(inv);
                inv.addToGame(this);

                y += 40;
            }
            x += 50;
        }

        this.invaderColection = new InvaderColection(invaders, v);
        this.sprites.addSprite(this.invaderColection);
    }
}
