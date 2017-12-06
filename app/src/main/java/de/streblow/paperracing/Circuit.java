package de.streblow.paperracing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by streblow on 03.11.2017.
 */

public class Circuit {

    public boolean noGridPoints;
    public int nonvalidGridPointColor;
    public int validGridPointColor;
    public int whiteCurbStoneColor;
    public int redCurbStoneColor;
    public int whiteStartLineColor;
    public int grayStartLineColor;

    public int[][] circ;
    public int sizex;
    public int sizey;
    public int startx1;
    public int startx2;
    public int starty;
    public boolean correct = false;

    public int checkpoints;
    public int[] chkx;
    public int[] chky;

    public int hsize;
    public int vsize;
    public int gridsize;

    private final int marginsize = 5;

    private int x;
    private int y;
    private int vx;
    private int vy;
    private int x_oud;
    private int y_oud;
    private int vx_oud;
    private int vy_oud;
    private int curbcount;

    /**
     * Constructs a new circuit
     * @param sx Horizontal size of the circuit
     * @param sy Vertical size of the circuit
     * @param chk Number of checkpoints, or corners in the circuit
     */
    public Circuit(int sx, int sy, int gs, int chk) {
        sizex = sx + 2 * marginsize;
        sizey = sy + 2 * marginsize;
        gridsize = gs;
        checkpoints = chk;
        init();
    }

    /**
     * Initialises a new circuit
     */
    public void init() {
        circ = new int[sizex][sizey];
        chkx = new int[checkpoints + marginsize];
        chky = new int[checkpoints + marginsize];
        generateCircuit();
        setStartPoint();
        correct=true;
        noGridPoints = true;
        nonvalidGridPointColor = Color.argb(0,149,194,229);
        validGridPointColor = Color.argb(128,149,194,229);
        whiteCurbStoneColor = Color.rgb(196,196,196);
        redCurbStoneColor = Color.rgb(234,134,134);
        whiteStartLineColor = Color.WHITE;
        grayStartLineColor = Color.DKGRAY;
    }

    /**
     * Draws the circuit
     * @param bm Bitmap to draw at
     */
    public void drawCircuit(Bitmap bm) {
        hsize = sizex * gridsize;
        vsize = sizey * gridsize;
        drawGridPoints(bm);
        drawStartFinishLine(bm);
        drawCurbstones(bm);
    }

    /**
     * Returns status of circuit at grid point (x, y)
     * @param x Desired x-coordinate
     * @param y Desired y-coordinate
     * @return -1 if the if coordinates are out of range
     * @return 0 if the coordinate contains grass
     * @return 1 or higher if the coordinate contains tarmac
     */
    public int getTerrain(int x, int y) {
        if ((x < 0) || (x >= sizex) ||
            (y < 0) || (y >= sizey))
            return -1;
        return circ[x][y];
    }

    /*
    * @return the horizontal size of the circuit
    */
    public int getsizex() {
        return sizex;
    }

    /*
    * @return the vertical size of the circuit
    */
    public int getsizey() {
        return sizey;
    }

    /*
    * @return the vertical coordinate of the start/finish
    */
    public int getstarty() {
        return starty;
    }

    /*
    * @return the 1st horizontal coordinate of the start/finish
    */
    public int getstartx1() {
        return startx1;
    }

    /*
    * @return the 2nd horizontal coordinate of the start/finish
    */
    public int getstartx2() {
        return startx2;
    }

    /**
     * @return the String version of the circuit, for uploading to network
     */
    public String saveToString() {
        String s = sizex + "," + sizey + ",";
        for (int y = 0; y < sizey; y++)
            for (int x = 0; x < sizex; x++)
                s += circ[x][y];
        return s;
    }

    /**
     * Sets the circuit to the given String.
     */
    public void restoreFromString(String s) {
        correct = false;
        String[] st = s.split(",");
        String n = "";
        int pos;
        try {
            sizex = Integer.parseInt(st[0]);
            sizey = Integer.parseInt(st[1]);
            n = st[2];
            circ = new int[sizex][sizey];
            for (int y = 0; y < sizey; y++)
                for (int x = 0; x < sizex; x++) {
                    pos = y * sizex + x;
                    circ[x][y] = Integer.parseInt(n.substring(pos, pos + 1));
                }
            correct = true;
            setStartPoint();
        } catch(Exception e) {
        }
    }

    /**
     * This function generates a random circuit, following the given number of checkpoints.
     * These marks are stored in the circuit-matrix with value 1.
     * The circuit always has a width of three, but because of overlapping it has sometimes more.
     */
    void generateCircuit()
    {
        int i = 0;
        int j;
        int k;
        int x;
        int y;
        int rx = (int)((sizex - 2 * marginsize) / 2); // Radius for ellipse's x
        int ry = (int)((sizey - 2 * marginsize) / 2); // Radius for ellipse's y
        float b; // Angle
        float rc;
        // Generate random marks along an ellipse
        for (b = 0; b <= 2 * Math.PI; b += (2 * Math.PI) / checkpoints) {
            chkx[i] = (int)((Math.random() * (.5 * rx) + .5 * rx) * Math.cos(b) + rx);
            chky[i] = (int)((Math.random() * (.5 * ry) + .5 * ry) * Math.sin(b) + ry);
            i++;
        }
        // Save extra marginsize marks, for completing the circle
        for (i = 0; i < marginsize; i++) {
            chkx[i + checkpoints] = chkx[i];
            chky[i + checkpoints] = chky[i];
        }
        // Draw "lines" between checkpoints setting grid values to 1
        for (i = 0; i < checkpoints; i++) {
            k = i + 1;
            if (i == checkpoints - 1)
                k = 0; // The line between the first and last mark
            rc = ((float)(chky[k] - chky[i]) / (float)(chkx[k] - chkx[i]));
            if (rc >= 1 || rc < -1) { // Vertical iteration
                rc = 1 / rc;
                if (chky[i] < chky[k])
                    for (j = 0; j <= (chky[k] - chky[i]); j++)
                        circ[chkx[i] + (int)(rc * j) + marginsize][chky[i] + j + marginsize] = 1;
                if (chky[i] >= chky[k])
                    for (j = 0; j >= (chky[k] - chky[i]); j--)
                        circ[chkx[i] + (int)(rc * j) + marginsize][chky[i] + j + marginsize] = 1;
            } else if (chkx[i] == chkx[k]) { // Vertical exception: rc == infinite.
                if (chky[i] < chky[k])
                    for (j = 0; j <= (chky[k] - chky[i]); j++)
                        circ[chkx[i] + marginsize][chky[i] + j + marginsize] = 1;
                if (chky[i]>=chky[k])
                    for (j = 0; j >= (chky[k] - chky[i]); j--)
                        circ[chkx[i] + marginsize][chky[i] + j + marginsize] = 1;
            } else { // Horizontal iteration
                if (chkx[k] > chkx[i])
                    for (j = 0; j <= (chkx[k] - chkx[i]); j++)
                        circ[chkx[i] + j + marginsize][chky[i] + (int)(rc * j) + marginsize] = 1;

                if (chkx[k] <= chkx[i])
                    for (j = 0; j >= (chkx[k] - chkx[i]); j--)
                        circ[chkx[i] + j + marginsize][chky[i] + (int)(rc * j) + marginsize] = 1;
            }
        }
        // Expand circuit to points around the route
        // 0rrr0
        // r222r
        // r212r
        // r222r
        // 0rrr0
        for (x = 1; x < sizex; x++)
            for (y = 1; y < sizey; y++)
                if (circ[x][y] == 1)
                {
                    if (circ[x+1][y]  != 1) circ[x+1][y]  = 2;
                    if (circ[x-1][y]  != 1) circ[x-1][y]  = 2;
                    if (circ[x][y+1]  != 1) circ[x][y+1]  = 2;
                    if (circ[x][y-1]  != 1) circ[x][y-1]  = 2;
                    if (circ[x+1][y+1]!= 1) circ[x+1][y+1]= 2;
                    if (circ[x-1][y+1]!= 1) circ[x-1][y+1]= 2;
                    if (circ[x+1][y-1]!= 1) circ[x+1][y-1]= 2;
                    if (circ[x-1][y-1]!= 1) circ[x-1][y-1]= 2;
                    // Add randomly 3 fields right, under, left or above
                    if ((int)(Math.random() * 8 ) == 0) // probability is 1/8
                    { // right
                        if (circ[x+2][y-1]!= 1) circ[x+2][y-1]= 2;
                        if (circ[x+2][y]  != 1) circ[x+2][y]  = 2;
                        if (circ[x+2][y+1]!= 1) circ[x+2][y+1]= 2;
                    }
                    if ((int)(Math.random() * 8) == 0) // probability is 1/8
                    { // under
                        if (circ[x-1][y+2]!= 1) circ[x-1][y+2]= 2;
                        if (circ[x][y+2]  != 1) circ[x][y+2]  = 2;
                        if (circ[x+1][y+2]!= 1) circ[x+1][y+2]= 2;
                    }
                    if ((int)(Math.random() * 8) == 0) // probability is 1/8
                    { // left
                        if (circ[x-2][y-1]!= 1) circ[x-2][y-1]= 2;
                        if (circ[x-2][y]  != 1) circ[x-2][y]  = 2;
                        if (circ[x-2][y+1]!= 1) circ[x-2][y+1]= 2;
                    }
                    if ((int)(Math.random() * 8) == 0) // probability is 1/8
                    { // above
                        if (circ[x-1][y-2]!= 1) circ[x-1][y-2]= 2;
                        if (circ[x][y-2]  != 1) circ[x][y-2]  = 2;
                        if (circ[x+1][y-2]!= 1) circ[x+1][y-2]= 2;
                    }
                }
        // Store the modified circuit in the array
        for (x = 0; x < sizex; x++)
            for (y = 0; y < sizey; y++)
                if (circ[x][y] == 2)
                    circ[x][y] = 1;

    }

    /**
     * This function calculates the start/finish coordinates
     */
    void setStartPoint() {
        starty = (int)getsizey() / 2;
        int x = (int)getsizex() / 2;
        while (getTerrain(x++, starty) == 0);
        startx1 = x - 1;
        while (getTerrain(x++, starty) != 0);
        startx2 = x;
    }

    /**
     * Draws the circuit-grid into the bitmap
     */
    void drawGridPoints(Bitmap bm) {
        if (noGridPoints)
            return;
        for (x = 0; x <= sizex; x++)
            for (y = 0; y <= sizey; y++)
                drawGridPixel(bm, x, y, nonvalidGridPointColor);

        for (x = 0; x < sizex; x++)
            for (y = 0; y < sizey; y++)
                if (getTerrain(x, y) != 0)
                    drawGridPixelThick(bm, x, y, validGridPointColor);
    }

    /**
     * Call this function to Draw the curb-stones into the bitmap
     */
    void drawCurbstones(Bitmap bm) {
        curbcount = 2 * sizex + 2 * sizey; // Maximum number of curbstones
        x = 0;
        y = 0;
        do { // Search first white dot for the outer curb-stones
            if (x > sizex) {
                x = 0;
                y++;
            }
            x++;
        } while (getTerrain(x, y) <= 0);
        x -= 1;
        vx = -1;
        vy = 0;
        drawCircuitContour(bm, curbcount);	// Draw the outer curbstones
        x = (int)(marginsize + sizex) / 2;
        y = (int)(marginsize + sizey) / 2;
        do { // Search first white dot for the inner curb-stones
            x++;
        } while (getTerrain(x, y) <= 0);
        x -= 1;
        vx = -1;
        vy = 0;
        drawCircuitContour(bm, curbcount);	// Draw the inner curbstones
    }

    /**
     * Draws the circuit-contour
     * This function uses the functions left, right, back and ahead
     * to find the borders of the circuit step by step.
     * @param end Number of iterations.
     */
    void drawCircuitContour(Bitmap bm, int end) {
        int z = 0;
        int x1;
        int y1;
        boolean curb = true;
        boolean onTrack;
        x_oud=x;
        y_oud=y;
        vx_oud = vx;
        vy_oud = vy;
        do {
            x1 = x;
            y1 = y;
            int i=0;
            onTrack = false;
            if (ahead()) {
                while (left() && i < 4)
                    i++;
                if (i >= 3)
                    z = 9999;
            } else {
                if (!onTrack) onTrack = right();
                if (!onTrack) onTrack = right();
                if (!onTrack) onTrack = ahead();
                if (!onTrack) onTrack = right();
                if (!onTrack) onTrack = ahead();
                if (!onTrack) onTrack = right();
                if (!onTrack) onTrack = ahead();
                back();
                curb = !curb;
                drawGridLineThick(bm, x1, y1, x, y, curb? redCurbStoneColor : whiteCurbStoneColor);
                z++;
            }
        } while (z <= end);
    }

    void back() {
        x = x_oud;
        y = y_oud;
        vx = vx_oud;
        vy = vy_oud;
    }

    boolean ahead() {
        x_oud = x;
        y_oud = y;
        vx_oud = vx;
        vy_oud = vy;
        x += vx;
        y += vy;
        if (getTerrain(x, y) != 0) {
            x -= vx;
            y -= vy;
            return true;
        }
        return false;
    }

    boolean right() {
        x_oud = x;
        y_oud = y;
        vx_oud = vx;
        vy_oud = vy;
        if (vy == 0) {
            if (vx == 1) {
                vy = 1;
                vx = 0;
            } else if (vx == -1) {
                vy = -1;
                vx = 0;
            }
        } else {
            if (vy == 1) {
            vy = 0;
            vx = -1;
            } else if (vy == -1) {
                vy = 0;
                vx = 1;
            }
        }
        x += vx;
        y += vy;
        if (getTerrain(x, y) != 0) {
            x -= vx;
            y -= vy;
            return true;
        }
        return false;
    }

    boolean left() {
        x_oud = x;
        y_oud = y;
        vx_oud = vx;
        vy_oud = vy;
        if (vy == 0) {
            if (vx == 1) {
                vy = -1;
                vx = 0;
            } else if (vx == -1) {
                vy = 1;
                vx = 0;
            }
        } else {
            if (vy == 1) {
                vy = 0;
                vx = 1;
            } else if (vy == -1) {
                vy = 0;
                vx = -1;
            }
        }
        x += vx;
        y += vy;
        if (getTerrain(x, y) != 0) {
            x -= vx;
            y -= vy;
            return true;
        }
        x -= vx;
        y -= vy;
        return false;
    }

    /**
     * Draws a line between two points
     */
    void drawGridLine(Bitmap bm, int x1, int y1, int x2, int y2, int color) {
        if ((x1 < 0) || (y1 < 0) || (x2 < 0) || (y2 < 0))
            return;
        Paint paint = new Paint();
        paint.setColor(color);
        Canvas canvas = new Canvas(bm);
        canvas.drawLine(x1 * gridsize, y1 * gridsize, x2 * gridsize, y2 * gridsize, paint);
    }

    void drawGridLineThick(Bitmap bm, int x1, int y1, int x2, int y2, int color) {
        if ((x1 < 0) || (y1 < 0) || (x2 < 0) || (y2 < 0))
            return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(3);
        Canvas canvas = new Canvas(bm);
        canvas.drawLine(x1 * gridsize, y1 * gridsize, x2 * gridsize, y2 * gridsize, paint);
    }

    /**
     * Draws a single pixel on a given location
     */
    void drawGridPixel(Bitmap bm, int x, int y, int color) {
        if ((x < 0) || (y <0 ))
            return;
        Paint paint = new Paint();
        paint.setColor(color);
        Canvas canvas = new Canvas(bm);
        canvas.drawLine(x * gridsize,y * gridsize,x * gridsize,y * gridsize, paint);
    }

    void drawGridPixelThick(Bitmap bm, int x, int y, int color) {
        if ((x < 0) || (y <0 ))
            return;
        Paint paint = new Paint();
        paint.setColor(color);
        Canvas canvas = new Canvas(bm);
        canvas.drawLine(x * gridsize - 1,y * gridsize - 1,x * gridsize + 1,y * gridsize - 1, paint);
        canvas.drawLine(x * gridsize - 1,y * gridsize,x * gridsize + 1,y * gridsize, paint);
        canvas.drawLine(x * gridsize - 1,y * gridsize + 1,x * gridsize + 1,y * gridsize + 1, paint);
    }

    /**
     * Draw the start-finish line
     */
    public void drawStartFinishLine(Bitmap bm) {
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm);
        paint.setColor(grayStartLineColor);
//        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize - 6, (startx2 -1) * gridsize, starty * gridsize - 6, paint);
//        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize - 5, (startx2 -1) * gridsize, starty * gridsize - 5, paint);
//        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize - 4, (startx2 -1) * gridsize, starty * gridsize - 4, paint);
        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize - 3, (startx2 -1) * gridsize, starty * gridsize - 3, paint);
        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize - 2, (startx2 -1) * gridsize, starty * gridsize - 2, paint);
        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize - 1, (startx2 -1) * gridsize, starty * gridsize - 1, paint);
        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize, (startx2 -1) * gridsize, starty * gridsize, paint);
        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize + 1, (startx2 -1) * gridsize, starty * gridsize + 1, paint);
        canvas.drawLine((startx1 - 1) * gridsize, starty * gridsize + 2, (startx2 -1) * gridsize, starty * gridsize + 2, paint);
        paint.setColor(whiteStartLineColor);
        for (int i = 0; i < (startx2 - startx1) * gridsize; i += 6) {
//            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize - 6, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize - 6, paint);
//            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize - 5, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize - 5, paint);
//            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize - 4, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize - 4, paint);
            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize, paint);
            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize + 1, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize + 1, paint);
            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize + 2, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize + 2, paint);
        }
        for (int i = 3; i < (startx2 - startx1) * gridsize; i += 6) {
            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize - 3, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize - 3, paint);
            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize - 2, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize - 2, paint);
            canvas.drawLine((startx1 - 1) * gridsize + i - 1, starty * gridsize - 1, (startx1 - 1) * gridsize + i - 1 + 3, starty * gridsize - 1, paint);
        }
    }

}
