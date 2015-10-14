package com.mygdx.game;

public class Projectile {

    int     x;
    int     y;
    int     dx;
    int     dy;
    boolean active = true;

    public Projectile(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void tick() {
        if (!this.active)
            return;
        this.x += this.dx;
        this.y += this.dy;
        if (Stage.get(this.x/32,this.y/32))
        {
            this.x = -64;
            this.y = -64;
            this.active = false;
        }

    }
}
