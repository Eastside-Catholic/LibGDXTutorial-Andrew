package com.mygdx.game;

public class Stage {
	boolean[][] walls;
	public Stage()
	{
		this.walls = new boolean[10][10];
		for (int i=0;i<10;i++)
		{
			this.walls[i][0]=true;
			this.walls[i][9]=true;
			this.walls[0][i]=true;
			this.walls[9][i]=true;
		}
		for (int i=2;i<10;i+=4)
		{
			for (int j=2;j<10;j+=4)
			{
				this.walls[i][j]=true;
			}
		}
	}
	public boolean get(int x, int y)
	{
		return this.walls[x][y];
	}
}
