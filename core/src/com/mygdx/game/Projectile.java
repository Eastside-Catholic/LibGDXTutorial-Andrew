package com.mygdx.game;

public class Projectile {
	int x;
	int y;
	int dx;
	int dy;
	public Projectile(int x, int y, int dx, int dy)
	{
		this.x=x;
		this.y=y;
		this.dx=dx;
		this.dy=dy;
	}
	public void tick()
	{
		this.x+=this.dx;
		this.y+=this.dy;
	}
}
