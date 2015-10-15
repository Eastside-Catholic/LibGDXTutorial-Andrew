package com.mygdx.game;

public class Projectile {

    private static final int TILE_SIZE = MyGdxGame.TILE_SIZE;
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

    /**
     * Called once per frame.
     */
    public void tick() {
        if (!this.active)
            return;
        this.x += this.dx;
        this.y += this.dy;
        if (Stage.get(this.x/TILE_SIZE,this.y/TILE_SIZE))
        {
            this.x = 0;
            this.y = 0;
            this.active = false;
        }

    }
}
