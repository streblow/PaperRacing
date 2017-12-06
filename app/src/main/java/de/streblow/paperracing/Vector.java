package de.streblow.paperracing;

/**
 * Created by streblow on 30.10.17.
 */

/**
 * Dataclass for saving the car-movements
 */
public class Vector {

    int vx, vy; // Horizontal and vertical speed

    /**
     * Constructs a new Vector
     * @param x Horizontal speed
     * @param y Vertical speed
     */
    public Vector(int x, int y) {
        vx = x;
        vy = y;
    }

    /**
     * @return The mathematical length of this Vector
     */
    public double length() {
        return Math.sqrt(vx*vx+vy*vy);
    }

    /**
     * @return The horizontal speed
     */
    public int getx() {
        return vx;
    }

    /**
     * @return The vertical speed
     */
    public int gety() {
        return vy;
    }

    /**
     * @param x Horizontal speed to set
     * @return the new set horizontal speed
     */
    public int setx(int x) {
        vx = x;
        return vx;
    }

    /**
     * @param y Vertical speed to set
     * @return the new set vertical speed
     */
    public int sety(int y) {
        vy = y;
        return vy;
    }

    /**
     * @return A String representation of this Vector
     */
    public String toString() {
        return("(" + vx + "," + vy + ")");
    }

}
