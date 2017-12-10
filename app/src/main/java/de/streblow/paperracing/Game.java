package de.streblow.paperracing;

import android.graphics.Color;

/**
 * Created by streblow on 08.11.17.
 */

public class Game {

    public int type;
    public Circuit circuit;
    public Player [] player;
    public int playercount;
    public int curplayer = 0;
    public int winner = 0;
    public boolean finish = false;
    public boolean gamestarted = false;

    public final static int QUICKRACE = 0;
    public final static int RACE = 1;
    public final static int SEASON = 2;

    public Game() {
        circuit = null;
    }

    /**
     * This method is called to start a new game
     * @param type Array with value 'true' for computer player and 'false' for human
     * @param name Array with the names of the players
     */
    public void newgame(int[] type, String[] name) {
        gamestarted=true;
        int sx = circuit.getstartx1();
        int sy = circuit.getstarty();
        playercount = type.length;
        player = new Player [playercount];
        int col = Color.DKGRAY;
        for (int i = 0; i < playercount; i++) {
            switch (i)
            {
                case 0:
                    col = Color.RED;
                    break;
                case 1:
                    col = Color.GREEN;
                    break;
                case 2:
                    col = Color.BLUE;
                    break;
                case 3:
                    col = Color.YELLOW;
                    break;
                case 4:
                    col = Color.MAGENTA;
                    break;
                case 5:
                    col = Color.CYAN;
                    break;
                case 6:
                    col = Color.GRAY;
                    break;
                default:
                    break;
            }
            player[i] = new Player(name[i], type[i], startpos(i), sy, col);
            player[i].circuit = circuit;
        }
        finish = false;
        winner = 0;
        curplayer = playercount;
        nextplayer();
    }

    /**
     * Starts the game loop
     */
    public void start() {
        if (!finish)
            while (currentplayer().type() == Player.COM) {
                currentplayer().ask();
                nextplayer();
            }
    }

    /**
     * Starts the game loop
     */
    public void go() {
        start();
    }

    /**
     * Shifts the turn to the next player
     */
    public void nextplayer() {
        curplayer = ++curplayer >= playercount ? 0 : curplayer;
        if (curplayer == 0)
            win();
    }

    /**
     * This checks if there is a winner. If so, it stops the game and makes an anouncement
     */
    public void win() {
        int w = checkfinished();
        if (w == -1)
            return;
        gamestarted = false;
        winner = w;
        finish = true;
        curplayer = winner;
    }

    /**
     * Returns the player who has won or -1
     * The player who started last is always in advantage, for starting in outer row.
     * If more players finish in the same turn, the last of them wins the game.
     */
    int checkfinished() {
        if (player[0].getcar().getturns() < 5)
            return -1; // Don't do nothing during the first turns
        Car c;
        int vx,vy,x1,y1, ret = -1;
        double rc=0;
        double x;
        int sy = circuit.starty;
        int sx1= circuit.startx1 - 1;
        int sx2= circuit.startx2 - 1;
        for(int i=0; i<playercount; i++) //Repeat for all players
        {
            if (player[i].travelledtotal() < 2.0 * Math.PI)
                continue;
            c = player[i].getcar();
            vx= c.getvector().getx();
            vy= c.getvector().gety();
            x1= c.getx()-vx;
            y1= c.gety()-vy;
            if (vx==0)  // Car goes Vertical
            {if ((x1+vx>=sx1)&&(x1+vx<=sx2)) // Within finish line on x-axis
            {if(((y1<=sy)&&(y1+vy>=sy))||((y1>=sy)&&(y1+vy<=sy))) // Around or on finish on y-axis
            {ret=i;
            }
            }
            }
            else
            {rc = (double)vy/(double)vx;
                if (rc==0) // Car goes Horizontal
                {if ((x1+vx>=sx1)&&(x1+vx<=sx2)) // Ending on finsh-line on x-axis
                {if (y1+vy==sy)                 // Ending on finsh-line on y-axis
                {ret=i;
                }
                }
                }
                else       // Car goes Diagonal
                {x = (double)(sy-y1)/rc;
                    x+=x1;
                    if (((x>=x1)&&(x<=x1+vx))||((x<=x1)&&(x>=x1+vx))) // Within the vector, not just 'in line'
                    {if ((x>=sx1)&&(x<=sx2)) // Within the start/finish line
                    {ret=i;
                    }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @return True if the game has ended
     */
    public boolean finished() {
        return finish;
    }


//-------------------------------------------
//------ Some various util-functions --------
    /**
     * @return The size of the grid
     */
    public int getgridsize() {
        return circuit.gridsize;
    }

    /**
     * @return The current circuit
     */
    public Circuit getcirc() {
        return circuit;
    }

    /**
     * @return the number of players
     */
    public int getplayercount() {
        return playercount;
    }

    /**
     * @return The requested player
     * @param i Index of the player requested
     */
    public Player getplayer(int i) {
        if ((i < playercount) && (i >= 0))
            return player[i];
        return currentplayer();
    }

    /**
     * @return The current player
     */
    public Player currentplayer() {
        return player[curplayer];
    }

    /**
     * this method undoes a move, if possible.
     */
    public void undo() {
        if (--curplayer < 0)
            curplayer = playercount - 1;
        currentplayer().getcar().undo();
    }

    public int startpos(int i) {
        int x1 = circuit.getstartx1();
        int x2 = circuit.getstartx2();
        int p = (i % (x2 - 1 - x1)) + x1;
        return (i % (x2 - 1 - x1)) + x1;
    }
}
