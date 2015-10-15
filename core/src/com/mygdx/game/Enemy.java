package com.mygdx.game;

public class Enemy {

    static final int TILE_SIZE = MyGdxGame.TILE_SIZE;
    float   x;
    float   y;
    int     dx;
    int     dy;
    int[]   tsq;
    boolean active = true;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Called once per frame.
     */
    public void tick() {
        if (!this.active)
            return;
        int tx = (int) Math.floor(this.x/TILE_SIZE);
        int ty = (int) Math.floor(this.y/TILE_SIZE);
        int px = (int) Math.floor(MyGdxGame.playerx/TILE_SIZE);
        int py = (int) Math.floor(MyGdxGame.playery/TILE_SIZE);

        if ((this.x%TILE_SIZE==(TILE_SIZE/2))&&(this.y%TILE_SIZE==(TILE_SIZE/2)))
        {
            aiTick(tx,ty,px,py);
        }
        this.x += dx/2.0;
        this.y += dy/2.0;
        if (MyGdxGame.proj!=null)
        {
            if (((int) this.x/TILE_SIZE==MyGdxGame.proj.x/TILE_SIZE)&&((int) this.y/TILE_SIZE==MyGdxGame.proj.y/TILE_SIZE))
            {
                MyGdxGame.level++;
                MyGdxGame.lose = true;
            }
        }
        if (((int) this.x/TILE_SIZE==MyGdxGame.playerx/TILE_SIZE)&&((int) this.y/TILE_SIZE==MyGdxGame.playery/TILE_SIZE))
        {
            MyGdxGame.level=0;
            MyGdxGame.lose = true;
        }

    }

    /**
     * Called each time the enemy is in the center of a tile. Updates direction.
     * @param tx Target X coordinate
     * @param ty Target Y coordinate
     * @param px Player X coordinate
     * @param py PLayer Y coordainte
     */
    private void aiTick(int tx, int ty, int px, int py) {
        this.tsq = Pathfinder.pathfind(tx,ty,px,py);
        this.dx = 0;
        this.dy = 0;
        if (tx<tsq[0])
            this.dx = 1;
        if (tx>tsq[0])
            this.dx = -1;
        if (ty<tsq[1])
            this.dy = 1;
        if (ty>tsq[1])
            this.dy = -1;
    }
}
