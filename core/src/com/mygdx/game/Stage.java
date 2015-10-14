package com.mygdx.game;

import java.util.Random;

public class Stage {
    public static boolean[][] walls;
    static int w = 16;
    static int h = 16;

    public static void init() {
        walls = new boolean[][] { { true, true, true, true, true, true, true, true, true, true, true },
                { true, false, false, false, false, false, false, false, false, false, true },
                { true, false, true, false, true, true, true, true, true, true, true },
                { true, false, true, false, false, false, true, false, false, false, true },
                { true, true, true, false, true, false, true, false, true, true, true },
                { true, false, false, false, true, false, false, false, true, false, true },
                { true, false, true, false, true, true, true, true, true, false, true },
                { true, false, true, false, true, false, false, false, false, false, true },
                { true, false, true, true, true, false, true, true, true, false, true },
                { true, false, false, false, false, false, true, false, false, false, true },
                { true, true, true, true, true, true, true, true, true, true, true } };
        walls = new boolean[w][h];
        for (int x = 0; x < w; x++)
        {
            walls[x][0] = true;
            walls[x][w-1] = true;
        }
        for (int y = 0; y < h; y++)
        {
            walls[0][y] = true;
            walls[h-1][y] = true;
        }
        Random random = new Random();
        for (int x = 1; x < w-1; x++)
            for (int y = 1; y < h-1; y++)
                walls[x][y] = random.nextBoolean();
        // walls[1][1]=false;
        // walls[9][9]=false;
    }

    public static boolean get(int x, int y) {
        if ((x < 0) || (y < 0) || (x > w) || (y > h))
            return true;
        return walls[x][y];
    }
}
