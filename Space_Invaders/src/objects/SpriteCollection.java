package objects;

import biuoop.DrawSurface;
import game.GameLevel;
import interfaces.Sprite;

/**
 * A collection of Sprites.
 *
 * @author Daniel Kaganovich
 * @version 1.0
 * @since 2018-04-15
 */
public class SpriteCollection {

    // the list of Sprite objects
    private java.util.List<Sprite> spriteList;

    /**
     * Create new Sprite Collection.
     */
    public SpriteCollection() {
        this.spriteList = new java.util.ArrayList<Sprite>();
    }

    /**
     * add a new Sprite to the list.
     *
     * @param s the Spirit to add
     */
    public void addSprite(Sprite s) {
        spriteList.add(s);
    }

    /**
     * remove a new Sprite to the list.
     *
     * @param s the Spirit to remove
     */
    public void removeSprite(Sprite s) {
        spriteList.remove(s);
    }

    /**
     * get the number of objects.
     *
     * @return the number of objects
     */
    public int getSize() {
        return this.spriteList.size();
    }

    /**
     * get the i index object.
     *
     * @param i the index
     * @return the object in the i index
     */
    public Sprite getIndexI(int i) {
        return this.spriteList.get(i);
    }

    /**
     * call timePassed() on all sprites.
     *
     * @param dt the amount of seconds passed since the last call
     * @param game the game
     */
    public void notifyAllTimePassed(double dt, GameLevel game) {
        java.util.List<Sprite> copy = new java.util.ArrayList<Sprite>(this.spriteList);
        for (Sprite spirit : copy) {
            spirit.timePassed(dt, game);
        }
    }

    /**
     * call drawOn(d) on all sprites.
     *
     * @param d the surface to draw on
     */
    public void drawAllOn(DrawSurface d) {
        for (int i = 0; i < this.spriteList.size(); i++) {
            this.spriteList.get(i).drawOn(d);
        }
    }
}
