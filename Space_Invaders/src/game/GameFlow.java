package game;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import screens.GameOver;
import screens.HighScoresAnimation;
import screens.KeyPressStoppableAnimation;

import interfaces.LevelInformation;
import interfaces.Menu;
import interfaces.Sprite;
import interfaces.Task;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

import objects.AnimationRunner;
import objects.Counter;
import objects.HighScoresTable;
import objects.ScoreInfo;

/**
 * GameFlow is in charge of creating the differnet levels,
 * and moving from one level to the next.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-05-19
 */
public class GameFlow {

    // counters
    private Counter scoreCounter;
    private Counter live;
    private HighScoresTable scores;
    private static final String PATH_NAME = "highscores";
    private File file;

    private AnimationRunner animationRunner;
    private KeyboardSensor keyboardSensor;

    /**
     * create new GameFlow that run the levels.
     *
     * @param ar the animation runner
     * @param ks the player's keybord
     */
    public GameFlow(AnimationRunner ar, KeyboardSensor ks) {
        this.animationRunner = ar;
        this.keyboardSensor = ks;

        this.scoreCounter = new Counter();
        this.live = new Counter(3);

        this.file = new File(GameFlow.PATH_NAME);

        this.loadTable();
    }

    /**
     * run the game.
     *
     * @param menu the menu of the game
     */
    public void runGame(Menu<Task<Void>> menu) {

        while (true) {
               this.animationRunner.run(menu);

               // wait for user selection
               Task<Void> task = menu.getStatus();

               if (task != null) {
                   task.run();
               }
        }
    }

    /**
     * run the levels.
     */
    public void runLevels() {

        this.scoreCounter = new Counter();
        this.live = new Counter(3);
        final Counter i = new Counter(1);
        while (this.live.getValue() > 0) {

            LevelInformation levelInfo = makeLevelInfo(i.getValue());
            i.increase(1);
            GameLevel level = new GameLevel(levelInfo, this.keyboardSensor, this.animationRunner,
                                           this.scoreCounter, this.live, i.getValue());

            level.initialize();

            level.run();

        }

        this.animationRunner.run(new KeyPressStoppableAnimation(this.keyboardSensor, KeyboardSensor.SPACE_KEY,
                                                                    new GameOver(this.scoreCounter.getValue())));


        if (this.scores.getRank(this.scoreCounter.getValue()) <= this.scores.size()) {

            String name = this.animationRunner.askForUserName();
            this.scores.add(new ScoreInfo(name, this.scoreCounter.getValue()));
            try {
                this.scores.save(this.file);
            } catch (IOException e) {
                System.out.print("");
            }
        }

        this.animationRunner.run(new KeyPressStoppableAnimation(this.keyboardSensor, KeyboardSensor.SPACE_KEY,
                                                                new HighScoresAnimation(this.scores, "space")));
     }

    /**
     * make the i level for the game.
     * @param i the current level
     * @return the level to play
     */
    private LevelInformation makeLevelInfo(int i) {
        return new LevelInformation() {

            @Override
            public int paddleWidth() {
                return 70;
            }

            @Override
            public int paddleSpeed() {
                return 500;
            }

            @Override
            public String levelName() {
                return "Battle no." + i;
            }

            @Override
            public Sprite getBackground() {
                return new Sprite() {
                    @Override
                    public void drawOn(DrawSurface d) {
                        d.setColor(Color.BLACK);
                        d.fillRectangle(0, 0, 800, 600);
                    }

                    @Override
                    public void timePassed(double dt, GameLevel game) {
                        return;
                    }

                };
            }

            @Override
            public int numberOfInvadersToRemove() {
                return 50;
            }
        };
    }

    /**
     * load the table of the scores.
     */
    private void loadTable() {

        this.scores = HighScoresTable.loadFromFile(this.file);

        if (!(new File(GameFlow.PATH_NAME)).exists()) {
            try {
                this.scores.save(this.file);
            } catch (IOException e) {
                System.out.print("");
            }
        }
    }


}
