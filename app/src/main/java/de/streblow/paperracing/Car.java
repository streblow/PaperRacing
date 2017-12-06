package de.streblow.paperracing;

/**
 * Created by streblow on 05.11.17.
 */

public class Car {

    int x;
    int y;
    int startx;
    int starty;
    int color;
    int turn = 0;

    /**
     * When a car hits the grass, the speed is reduced to zero.
     * Therefore, after the players' move, a second move is made,
     * reducing the speed to zero. This is counted separately, to
     * keep track of the real number of turns.
     */
    boolean[] fault = new boolean[500];
    int faultcount = 0;

    Vector[] hist = new Vector[500]; // The history, containing all move-vectors

    /**
     * Builds a new car
     * @param sx Horizontal start location
     * @param sy Vertical start location
     * @param c Color of the car
     */
    public Car(int sx, int sy, int c) {
        x = sx;
        y = sy;
        startx = sx;
        starty = sy;
        hist[0] = new Vector(0,0);
        color = c;
        for (int i = 0; i < 500; i++)
            fault[i] = false;
    }

    /**
     * Moves the car using the given vector
     * @param vec The new speed-vector
     */
    public void move(Vector vec) {
        x += vec.getx();
        y += vec.gety();
        hist[++turn] = vec;
    }

    /**
     * Is called when the grass is hit, to keep track of erronous counted turns
     */
    public void fault() {
        fault[turn] = true;
        faultcount++;
    }

    /**
     * @return The horizontal position of the car
     */
    public int getx() {
        return x;
    }

    /**
     * @return The vertical position of the car
     */
    public int gety() {
        return y;
    }

    /**
     * @return The horizontal start-position of the car
     */
    public int getstartx() {
        return startx;
    }

    /**
     * @return The vertical start-position of the car
     */
    public int getstarty() {
        return starty;
    }

    /**
     * @return The number of turns done, including erronous
     */
    public int getturns() {
        return turn;
    }

    /**
     * @return The number of turns done by the player
     */
    public int getplayerturns() {
        return turn-faultcount;
    }

    /**
     * @return The movement vector on a specific point of time
     * @param i Index of the desired vector
     */
    public Vector gethistory(int i) {
        return hist[i];
    }

    /**
     * @return The current movement-vector
     */
    public Vector getvector() {
        return hist[turn];
    }

    /**
     * @return The current speed of the car
     */
    public double getspeed() {
        return hist[turn].length();
    }

    /**
     * @return The color of the car
     */
    public int getcolor() {
        return color;
    }

    /**
     * @return A string representation of the car, including position and movement
     */
    public String toString() {
        return ("Car(" + x + "," + y + "," + hist[turn] + ")");
    }

    /**
     * This function undos the last players' move
     */
    public void undo() {
        x -= hist[turn].getx();
        y -= hist[turn].gety();
        if (--turn < 0)
            turn = 0;
        else if (fault[turn + 1]) {
            undo();
            faultcount--;
            fault[turn + 1] = false;
        }
    }
}
