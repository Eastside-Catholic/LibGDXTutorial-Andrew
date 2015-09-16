package com.mygdx.game;

public class Stage {
	boolean[][] walls;
	public Stage()
	{
		this.walls = new boolean[11][11];
		for (int i=0;i<11;i++)
		{
			this.walls[i][0]=true;
			this.walls[i][10]=true;
			this.walls[0][i]=true;
			this.walls[10][i]=true;
		}
		for (int i=2;i<11;i+=3)
		{
			for (int j=2;j<11;j+=3)
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
