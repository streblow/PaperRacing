package de.streblow.paperracing;

/**
 * Created by streblow on 05.11.17.
 */

public class Player {

    public int type;
    public String name;
    public Car car;
    public Circuit circuit;
    public int maxrecursiondepth;
    public int recursiondepth;

    /**
     * The directions to which a player can go
     */
    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    public final static int UP = 2;
    public final static int DOWN = 3;
    public final static int SAME = 4;
    public final static int UPLEFT = 5;
    public final static int UPRIGHT = 6;
    public final static int DOWNLEFT = 7;
    public final static int DOWNRIGHT = 8;

    /**
     * The type of the player: Computer, Human or Network-player
     */
    public final static int COM = 0;
    public final static int HUM = 1;
    public final static int NET = 2;

    private int cx;
    private int cy;
    private int level;
    private int j;

    /**
     * Constructs a new player
     * @param name The name of the player
     * @param startx Horizontal start location
     * @param starty Vertical start location
     * @param color Color of the player
     */
    public Player(String name, int type, int startx, int starty, int color) {
        this.name = name;
        this.type = type;
        car = new Car(startx, starty, color);
        maxrecursiondepth = 7;
        if (type == Player.COM)
            recursiondepth = Math.min(maxrecursiondepth,
                    3 + (int)Math.round(4.0 * Math.random())); // Recursion depth is 3 + random(0.0, 4.0)
    }

    public double travelledtotal() {
        int x1, y1, x2, y2, cx, cy;
        x1 = car.getstartx();
        y1 = car.getstarty();
        cx = circuit.getsizex() / 2;
        cy = circuit.getsizey() / 2;
        double dist = 0.0;
        for (int i = 1; i <= getcar().getturns(); i++) {
            x2 = x1 + car.gethistory(i).getx();
            y2 = y1 + car.gethistory(i).gety();
            double d1 = Math.atan2(-(cy - y1), (cx - x1)) + Math.PI; // angle from (x1, y1) to startline
            double d2 = Math.atan2(-(cy - y2), (cx - x2)) + Math.PI; // angle from (x2, y2) to startline
            if (d1 >= d2) // backwards
                if (d1 - d2 <= Math.PI)
                    dist += d2 - d1; // really backwards
                else // (d1 - d2 > Math.PI)
                    dist += 2.0 * Math.PI - (d1 - d2); // forwards crossing 0/360 degrees
            else // (d1 < d2) // forwards
                if (d2 - d1 <= Math.PI)
                    dist += d2 - d1; // really forwards
                else // (d2 - d1 > Math.PI)
                    dist += -(2.0 * Math.PI - (d2 - d1)); // backwards crossing 0/360 degrees
            x1 = x2;
            y1 = y2;
        }
        return dist;
    }

    public double travelled(int x1, int y1, int x2, int y2) {
        int cx = circuit.getsizex() / 2;
        int cy = circuit.getsizey() / 2;
        double dist = 0.0;
        double d1 = Math.atan2(-(cy - y1), (cx - x1)) + Math.PI; // angle from (x1, y1) to startline
        double d2 = Math.atan2(-(cy - y2), (cx - x2)) + Math.PI; // angle from (x, y) to startline
        if (d1 >= d2) // backwards
            if (d1 - d2 <= Math.PI)
                dist += d2 - d1; // really backwards
            else // (d1 - d2 > Math.PI)
                dist += 2.0 * Math.PI - (d1 - d2); // forwards crossing 0/360 degrees
        else // (d1 < d2) // forwards
            if (d2 - d1 <= Math.PI)
                dist += d2 - d1; // really forwards
            else // (d2 - d1 > Math.PI)
                dist += -(2.0 * Math.PI - (d2 - d1)); // backwards crossing 0/360 degrees
        return dist;
    }

    /**
     * Used to make the computer do a move
     */
    public void ask() {
        if (type == COM)
            moveai();
    }

    /**
     * @return True if the player is a computer player
     */
    public boolean isai() {
        return type == COM;
    }

    /**
     * @return True if the player is a network player
     */
    public boolean isnet() {
        return type == NET;
    }

    /**
     * @return The type of this player: HUM, COM, or NET
     */
    public int type() {
        return type;
    }

    public void settype(int type) {
        this.type = type;
    }

    /**
     * For user input from mouse
     * @param x Horizontal (circuit) location
     * @param x Vertical (circuit) location
     */
    public void clicked(int x, int y) {
        int cx = car.getx() + car.getvector().getx();
        int cy = car.gety() + car.getvector().gety();
        int m = -1;
        if((x == cx) && (y == cy)) m = SAME;
        else if ((cx - x ==  -1) && (cy - y == 0)) m = RIGHT;
        else if ((cx - x == 1) && (cy - y == 0)) m = LEFT;
        else if ((cx - x == 0) && (cy - y ==  -1)) m = DOWN;
        else if ((cx - x == 0) && (cy - y == 1)) m = UP;
        else if ((cx - x == 1) && (cy - y == 1)) m = UPLEFT;
        else if ((cx - x ==  -1) && (cy - y == 1)) m = UPRIGHT;
        else if ((cx - x == 1) && (cy - y ==  -1)) m = DOWNLEFT;
        else if ((cx - x ==  -1) && (cy - y ==  -1)) m = DOWNRIGHT;
        if (m >= 0) move(m);
    }

    /**
     * For actually moving the car
     * @param m One of: LEFT, RIGHT, UP, DOWN, SAME, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
     */
    public synchronized void move(int m) {
        Vector v = car.getvector();
        switch (m) {
            case LEFT:  movecar(v.getx() - 1, v.gety()); break;
            case RIGHT: movecar(v.getx() + 1, v.gety()); break;
            case UP: movecar(v.getx(), v.gety() - 1); break;
            case DOWN: movecar(v.getx(), v.gety() + 1); break;
            case SAME: movecar(v.getx(), v.gety()); break;
            case UPLEFT: movecar(v.getx() - 1, v.gety() - 1); break;
            case UPRIGHT: movecar(v.getx() + 1, v.gety() - 1); break;
            case DOWNLEFT: movecar(v.getx() - 1, v.gety() + 1); break;
            case DOWNRIGHT: movecar(v.getx() + 1, v.gety() + 1); break;
        }
    }

    /**
     *Internal function for moving the car to the new position.
     */
    public void movecar(int x, int y) {
        if (circuit.getTerrain(car.getx() + x, car.gety() + y) <= 0) {
            car.move(new Vector(0, 0));
            car.fault();
        } else
            car.move(new Vector(x,y));
    }

    /**
     * @return The name of the player
     */
    public String getname() {
        return name;
    }

    /**
     * @return The car of the player
     */
    public Car getcar() {
        return car;
    }

    public String toString() {
        return getname();
    }

    /**
     * This function is called by ask() to move the computer player
     */
    public void moveai() {
        double mv;
        if (!isai())
            return;
        cx = circuit.getsizex() / 2;
        cy = circuit.getsizey() / 2;
        level = Math.min(maxrecursiondepth, recursiondepth);
        Vector v = car.getvector();
        // Get the angle from current point to start-finish line
        //distance = Math.atan2(-(cy - car.gety()), (cx - car.getx())) + Math.PI;
        double dist = travelledtotal();
        mv = ai(car.getx(), car.gety(), v.getx(), v.gety(), level, dist);
        move((int) mv);
    }

    /**
     * This method does a depth-first search for the longest arc-distance that can be made
     * within the given number of moves (level). As soon as the required depth is reached,
     * the method returns the distance of the trip. For each level, the longest trip is chosen,
     * until the best direction is found. When all possibilities have been searched, the function
     * returns the best move to make.
     * To reduce the number of searches, the hit of grass will reduce the depthlevel by 2.
     * If the car actually hits the grass, it will crash... :-) (Due to the algorithm) Probably the
     * only way to solve this problem accurately, is by using a genetic algorithm. Yet this routine
     * is clean and simple :-)
     * @param x Horizontal start location
     * @param y Vertical start location
     * @param vx Current horizontal speed
     * @param vy Current vertical speed
     * @param l Level of search (or number of moves to maximize)
     * @param dist current travelled distance to (x,y)
     */
    public double ai(int x, int y, int vx, int vy, int l, double dist) {
        double[] e = new double[9];
        double fault_distance;
        Boolean fault_distance_calculated;
        if (l > 0) {
            // If we're not done yet, check all possible routes.
            fault_distance = 0.0;
            fault_distance_calculated = false;
            if (circuit.getTerrain(x + vx - 1, y + vy) <= 0) { // LEFT
                e[0] = ai(x, y, 0, 0, l - 2, dist);
                fault_distance = e[0];
                fault_distance_calculated = true;
            }
            else
                e[0] = ai(x + vx - 1, y + vy, vx - 1, vy , l - 1,
                    dist + travelled(x, y, x + vx - 1, y + vy));
            if (circuit.getTerrain(x + vx + 1, y + vy) <= 0) // RIGHT
                if (fault_distance_calculated)
                    e[1] = fault_distance;
                else {
                    e[1] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[1];
                    fault_distance_calculated = true;
                }
            else
                e[1] = ai(x + vx + 1, y + vy, vx + 1, vy , l - 1,
                    dist + travelled(x, y, x + vx + 1, y + vy));
            if (circuit.getTerrain(x + vx, y + vy - 1) <= 0) // UP
                if (fault_distance_calculated)
                    e[2] = fault_distance;
                else {
                    e[2] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[2];
                    fault_distance_calculated = true;
                }
            else
                e[2] = ai(x + vx, y + vy - 1, vx, vy - 1, l - 1,
                    dist + travelled(x, y, x + vx, y + vy - 1));
            if (circuit.getTerrain(x + vx, y + vy + 1) <= 0) // DOWN
                if (fault_distance_calculated)
                    e[3] = fault_distance;
                else {
                    e[3] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[3];
                    fault_distance_calculated = true;
                }
            else
                e[3] = ai(x + vx, y + vy + 1, vx, vy + 1, l - 1,
                    dist + travelled(x, y, x + vx, y + vy + 1));
            if (circuit.getTerrain(x + vx, y + vy) <= 0) // SAME
                if (fault_distance_calculated)
                    e[4] = fault_distance;
                else {
                    e[4] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[4];
                    fault_distance_calculated = true;
                }
            else
                e[4] = ai(x + vx, y + vy, vx, vy , l - 1,
                    dist + travelled(x, y, x + vx, y + vy));
            if (circuit.getTerrain(x + vx - 1, y + vy - 1) <= 0) // UPLEFT
                if (fault_distance_calculated)
                    e[5] = fault_distance;
                else {
                    e[5] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[5];
                    fault_distance_calculated = true;
                }
            else
                e[5] = ai(x + vx - 1, y + vy - 1, vx - 1, vy - 1, l - 1,
                    dist + travelled(x, y, x + vx - 1, y + vy - 1));
            if (circuit.getTerrain(x + vx + 1, y + vy - 1) <= 0) // UPRIGHT
                if (fault_distance_calculated)
                    e[6] = fault_distance;
                else {
                    e[6] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[6];
                    fault_distance_calculated = true;
                }
            else
                e[6] = ai(x + vx + 1, y + vy - 1, vx + 1, vy - 1, l - 1,
                    dist + travelled(x, y, x + vx + 1, y + vy - 1));
            if (circuit.getTerrain(x + vx - 1, y + vy + 1) <= 0) // DOWNLEFT
                if (fault_distance_calculated)
                    e[7] = fault_distance;
                else {
                    e[7] = ai(x, y, 0, 0, l - 2, dist);
                    fault_distance = e[7];
                    fault_distance_calculated = true;
                }
            else
                e[7] = ai(x + vx - 1, y + vy + 1, vx - 1, vy + 1, l - 1,
                    dist + travelled(x, y, x + vx - 1, y + vy + 1));
            if (circuit.getTerrain(x + vx + 1, y + vy + 1) <= 0) // DOWNRIGHT
                if (fault_distance_calculated)
                    e[8] = fault_distance;
                else
                    e[8] = ai(x, y, 0, 0, l - 2, dist);
            else
                e[8] = ai(x + vx + 1, y + vy + 1, vx + 1, vy + 1, l - 1,
                    dist + travelled(x, y, x + vx + 1, y + vy + 1));
            // Next get the largest distance
            for (int i = 0; i < 9; i++) {
                if (e[i] > dist) {
                    dist = e[i];
                    j = i;
                }
            }
        }
        if (l == level)
            return j; // If this was the top-recursion, return the best route
        return dist; // Else return the distance
    }
}
