package com.mygdx.game;

public class Enemy {

    static final int   TILE_SIZE       = MyGdxGame.TILE_SIZE;
    static final float TICKS_PER_PIXEL = 10;
    private static final int PAUSE_TIME = 300;
    float              x;
    float              y;
    double             dx;
    double             dy;
    int[]              tsq;
    boolean            active          = true;
    public int         speed           = 1;
    private int freezeTimer=0;

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
        /*
         * The collision/AI code expects enemies to move no more that one pixel between two calls.
         * To move more than 1 pixel/tick, we call the collision/AI code more than once.
         * 
         * In this case, this code moves the enemy 0.1 pixels/call, allowing the enemy to move as slowly as 1 pixel every 10 ticks.
         * The speed variable determines how many times the code is called per tick.
         */
        for (int i = 0; i<this.speed; i++)
        {
            int tx = (int) Math.floor(this.x/TILE_SIZE);                //Tile X coordinate
            int ty = (int) Math.floor(this.y/TILE_SIZE);                //Tile Y coordinate
            int px = (int) Math.floor(MyGdxGame.playerx/TILE_SIZE);     //Pixel X coordinate
            int py = (int) Math.floor(MyGdxGame.playery/TILE_SIZE);     //Pixel Y coordinate

            if ((((int) this.x)%TILE_SIZE==(TILE_SIZE/2))&&(((int) this.y)%TILE_SIZE==(TILE_SIZE/2)))   //Only tick the AI while in the center of a tile
            {
                aiTick(tx,ty,px,py);
            }
            if (freezeTimer>0)  //Don't move if frozen
            {
                freezeTimer--;
            }else{
            this.x += dx/TICKS_PER_PIXEL;
            this.y += dy/TICKS_PER_PIXEL;
            }
            for (Projectile proj : MyGdxGame.projl)
                if (proj!=null)
                {
                    if (!proj.toDel)
                    {
                    if (((int) this.x/TILE_SIZE==proj.x/TILE_SIZE)              //If hit by active bullet,
                            &&((int) this.y/TILE_SIZE==proj.y/TILE_SIZE))
                    {
                        if (MyGdxGame.dots.size()>0)                            //Freeze if not all dots eaten
                        {
                            freezeTimer=PAUSE_TIME*this.speed;
                            proj.del();
                            return;
                        }                     
                        if (MyGdxGame.generating)                              //This if is here to fix a bug. Disables death while generating levels.
                            MyGdxGame.regenLevel=true;
                        else
                            MyGdxGame.nextLevel();                            
                        return;
                    }
                    }
                }
            if (((int) this.x/TILE_SIZE==MyGdxGame.playerx/TILE_SIZE)           //If colliding with player,
                    &&((int) this.y/TILE_SIZE==MyGdxGame.playery/TILE_SIZE))
            {
                if (MyGdxGame.generating)                                      //This if is here to fix a bug. Disables death while generating levels.
                    MyGdxGame.regenLevel=true;
                else
                    MyGdxGame.resetLevel();
                return;
            }
        }
        MyGdxGame.generating = false;
    }

    /**
     * Called each time the enemy is in the center of a tile. Updates direction.
     * 
     * @param tx
     *            Target X coordinate
     * @param ty
     *            Target Y coordinate
     * @param px
     *            Player X coordinate
     * @param py
     *            PLayer Y coordainte
     */
    private void aiTick(int tx, int ty, int px, int py) {
        this.tsq = Pathfinder.pathfind(tx,ty,px,py);
        if (this.tsq==null)
        {
            MyGdxGame.regenLevel = true;
            return;
        }
        
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
