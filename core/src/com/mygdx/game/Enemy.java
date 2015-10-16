package com.mygdx.game;

public class Enemy {

    static final int TILE_SIZE = MyGdxGame.TILE_SIZE;
    static final float TICKS_PER_PIXEL = 10;
    float   x;
    float   y;
    double     dx;
    double     dy;
    int[]   tsq;
    boolean active = true;
    public int speed = 1;

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
        for (int i=0; i<this.speed; i++)
        {
            int tx = (int) Math.floor(this.x/TILE_SIZE);
            int ty = (int) Math.floor(this.y/TILE_SIZE);
            int px = (int) Math.floor(MyGdxGame.playerx/TILE_SIZE);
            int py = (int) Math.floor(MyGdxGame.playery/TILE_SIZE);
    
            if ((((int)this.x)%TILE_SIZE==(TILE_SIZE/2))&&(((int)this.y)%TILE_SIZE==(TILE_SIZE/2)))
            {
                aiTick(tx,ty,px,py);
            }
            this.x += dx/TICKS_PER_PIXEL;
            this.y += dy/TICKS_PER_PIXEL;
            if (MyGdxGame.proj!=null)
            {
                if (((int) this.x/TILE_SIZE==MyGdxGame.proj.x/TILE_SIZE)&&((int) this.y/TILE_SIZE==MyGdxGame.proj.y/TILE_SIZE))
                {
                    if (!MyGdxGame.generating)
                    {
                        MyGdxGame.level++;
                    }
                    MyGdxGame.regenLevel = true;
                    return;
                }
            }
            if (((int) this.x/TILE_SIZE==MyGdxGame.playerx/TILE_SIZE)&&((int) this.y/TILE_SIZE==MyGdxGame.playery/TILE_SIZE))
            {
                if (!MyGdxGame.generating)
                {
                    MyGdxGame.level=0;
                }
                MyGdxGame.regenLevel = true;
                return;
            }
        }
        MyGdxGame.generating=false;
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
