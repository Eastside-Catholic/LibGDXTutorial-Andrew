package com.mygdx.game;

import java.util.Random;

public class Stage {

    public static boolean[][] walls;
    static final int                W = 16;
    static final int                H = 16;
    /**
     * Randomly generates a new stage. Higher-level stages are denser.
     * @param level The current difficulty level.
     */
    public static void init(int level, double density) {
        /*walls = new boolean[][] {{true,true,true,true,true,true,true,true,true,true,true},            //This is old. It's a maze to test pathfinding.
                {true,false,false,false,false,false,false,false,false,false,true},
                {true,false,true,false,true,true,true,true,true,true,true},
                {true,false,true,false,false,false,true,false,false,false,true},
                {true,true,true,false,true,false,true,false,true,true,true},
                {true,false,false,false,true,false,false,false,true,false,true},
                {true,false,true,false,true,true,true,true,true,false,true},
                {true,false,true,false,true,false,false,false,false,false,true},
                {true,false,true,true,true,false,true,true,true,false,true},
                {true,false,false,false,false,false,true,false,false,false,true},
                {true,true,true,true,true,true,true,true,true,true,true}};*/
        walls = new boolean[W][H];
        for (int x = 0; x<W; x++)
        {
            walls[x][0] = true;
            walls[x][W-1] = true;
        }
        for (int y = 0; y<H; y++)
        {
            walls[0][y] = true;
            walls[H-1][y] = true;
        }
        Random random = new Random();
        for (int x = 1; x<W-1; x++)
        {
            for (int y = 1; y<H-1; y++)
            {
                walls[x][y] = (random.nextFloat()<density);
            }
        }
    }

    /**
     * Gets the tile at a coordinate
     * @param x the tile X coordinate
     * @param y the tile y coordinate
     * @return true if the tile contains a wall.
     */
    public static boolean get(int x, int y) {
        if ((x<0)||(y<0)||(x>W)||(y>H))
            return true;
        return walls[x][y];
    }
}
