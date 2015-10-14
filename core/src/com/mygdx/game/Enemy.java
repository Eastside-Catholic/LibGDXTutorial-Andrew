package com.mygdx.game;

public class Enemy {

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

    public void tick() {
        if (!this.active)
            return;
        int tx = (int) Math.floor(this.x/32);
        int ty = (int) Math.floor(this.y/32);
        int px = (int) Math.floor(MyGdxGame.playerx/32);
        int py = (int) Math.floor(MyGdxGame.playery/32);

        if ((this.x%32==16)&&(this.y%32==16))
        {
            System.out.println("BPF");
            this.tsq = Pathfinder.pathfind(tx,ty,px,py);
            System.out.println("EPF");
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
            System.out.println('a');
            System.out.println(tx);
            System.out.println(ty);
            System.out.println(px);
            System.out.println(py);
            System.out.println(tsq[0]);
            System.out.println(tsq[1]);
        }
        this.x += dx/2.0;
        this.y += dy/2.0;
        if (MyGdxGame.proj!=null)
        {
            if (((int) this.x/32==MyGdxGame.proj.x/32)&&((int) this.y/32==MyGdxGame.proj.y/32))
            {
                this.x = -64;
                this.y = -64;
                this.active = false;
                MyGdxGame.lose = true;
            }
        }
        if (((int) this.x/32==MyGdxGame.playerx/32)&&((int) this.y/32==MyGdxGame.playery/32))
        {
            MyGdxGame.lose = true;
        }

    }
}
