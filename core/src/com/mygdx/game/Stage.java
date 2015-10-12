package com.mygdx.game;

public class Stage {
	public static boolean[][] walls;
	public static void init()
	{
		walls = new boolean[][] {
        	{true,true,true,true,true,true,true,true,true,true,true},
        	{true,false,false,false,false,false,false,false,false,false,true},
        	{true,false,true,false,true,true,true,true,true,true,true},
        	{true,false,true,false,false,false,true,false,false,false,true},
            {true,true,true,false,true,false,true,false,true,true,true},
            {true,false,false,false,true,false,false,false,true,false,true},
            {true,false,true,false,true,true,true,true,true,false,true},
            {true,false,true,false,true,false,false,false,false,false,true},
            {true,false,true,true,true,false,true,true,true,false,true},
            {true,false,false,false,false,false,true,false,false,false,true},
            {true,true,true,true,true,true,true,true,true,true,true}
		};
	}
	public static boolean get(int x, int y)
	{
	    if ((x<0)||(y<0)||(x>10)||(y>10))
	        return true;
		return walls[x][y];
	}
}
