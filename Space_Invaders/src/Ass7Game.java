import java.io.File;

import biuoop.GUI;
import biuoop.KeyboardSensor;

import game.GameFlow;

import interfaces.Menu;
import interfaces.Task;

import objects.AnimationRunner;
import objects.HighScoresTable;

import screens.HighScoresAnimation;
import screens.KeyPressStoppableAnimation;
import screens.MenuAnimation;

/**
 * the ass6game is the main that run the game.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-06-10
 */
public class Ass7Game {

    /**
     * the main method.
     *
     * @param args the relative path to the level sets.
     *             if arg.length == 0 it uses the default.
     */
    public static void main(String[] args) {

        GUI gui = new GUI("Arkanoid", 800, 600);
        AnimationRunner animationRunner = new AnimationRunner(gui, 60);
        GameFlow game = new GameFlow(animationRunner, gui.getKeyboardSensor());

        Menu<Task<Void>> menu = getMenu(animationRunner, gui.getKeyboardSensor(), game);
        game.runGame(menu);
    }

    /**
     * get the menu of the game.
     *
     * @param animationRunner the animation runner
     * @param k the user's keyboard
     * @param game the game.
     * @return the menu of the game
     */
    private static Menu<Task<Void>> getMenu(AnimationRunner animationRunner,
                                            KeyboardSensor k, GameFlow game) {

        MenuAnimation<Task<Void>> menu = new MenuAnimation<Task<Void>>("Space Invaders", k);
        menu.setAnimationRunner(animationRunner);

        menu.addSelection("s", "start game", new Task<Void>() {

            @Override
            public Void run() {
                game.runLevels();
                return null;
            }

        });

        menu.addSelection("h", "see the high scores", new Task<Void>() {

            @Override
            public Void run() {
                animationRunner.run(new KeyPressStoppableAnimation(k, KeyboardSensor.SPACE_KEY,
                        new HighScoresAnimation(HighScoresTable.loadFromFile(new File("highscores")), "space")));
                return null;
            }

        });

        menu.addSelection("q", "quit", new Task<Void>() {

            @Override
            public Void run() {
                System.exit(0);
                return null;
            }

        });

        return menu;
    }
}
