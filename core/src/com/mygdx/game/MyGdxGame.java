package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {

    static final int         SCREEN_W        = 620;
    static final int         SCREEN_H        = 620;
    private static final int INIT_PLYR_POS_X = 48;
    private static final int INIT_PLYR_POS_Y = 48;
    private static final int INIT_ENEM_POS_X = 304;
    private static final int INIT_ENEM_POS_Y = 304;
    static final int         TILE_SIZE       = 32;
    private static final int KEY_LEFT        = 1;
    private static final int KEY_RIGHT       = 2;
    private static final int KEY_UP          = 4;
    private static final int KEY_DOWN        = 8;
    private static final int DIR_N           = 1;
    private static final int DIR_NE          = 2;
    private static final int DIR_E           = 3;
    private static final int DIR_SE          = 4;
    private static final int DIR_S           = 5;
    private static final int DIR_SW          = 6;
    private static final int DIR_W           = 7;
    private static final int DIR_NW          = 8;
    private static final int SPEED_K1 = 2;
    private static final double SPEED_K2 = 1.0;
    private static final double DENSITY_K1 = 16.0;
    private static final double DENSITY_K2 = 16.0;
    private static final double MIN_ENEMY_DIST = 8;

    SpriteBatch           batch;
    Texture               wall;
    Texture               player;
    Texture               projs;
    Texture               enemys;
    static int            playerx = INIT_PLYR_POS_X;
    static int            playery = INIT_PLYR_POS_Y;
    BitmapFont            font;
    int                   lastdir = 0;
    static Projectile     proj;
    static Enemy          enemy;
    public static boolean regenLevel;
    public static boolean generating;
    public static int     level   = 0;
    Random                random;
    int lnum;
    int plnum;

    @Override
    public void create() {
        Stage.init(level);
        Gdx.graphics.setDisplayMode(SCREEN_W,SCREEN_H,false);
        batch = new SpriteBatch();
        wall = new Texture("wall.png");
        player = new Texture("player.gif");
        projs = new Texture("proj.png");
        enemys = new Texture("blooper.png");
        font = new BitmapFont();
        font.setColor(Color.RED);
        enemy = new Enemy(INIT_ENEM_POS_X,INIT_ENEM_POS_Y);
        random = new Random();
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.K))
        {
            System.out.println(lnum+","+level);
            level++;
            regenLevel=true;   
        }
        int keyspressed = getKeysPressed();
        lastdir = getDirectionFromKeys(keyspressed,lastdir);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            spawnProjectile();
        }
        moveCharacter();
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (regenLevel)
        {
            regenLevel = false;
            restartLevel();
        }
        Pathfinder.draw(batch);
        batch.draw(player,playerx-(TILE_SIZE/2),playery-(TILE_SIZE/2));
        drawProjectile();
        drawStage();
        drawEnemy();
        //CHECKSTYLE DISABLE MagicNumber FOR 5 LINES
        font.draw(batch,"Move with arrow keys, shoot with space",20,600);
        font.draw(batch,"Shoot the enemy to go to the next level",20,580);
        font.draw(batch,"Level: "+level,20,560);
        if (regenLevel)
            font.draw(batch,"Generating level... Please wait.",20,540);
        else
        {
            System.out.println((lnum-plnum)+","+level);
            plnum=lnum;
            level++;
            regenLevel=true;
            if (level==25)
                level=0;
        }
        drawDebugText();
        batch.end();
    }

    /**
     * Draws the enemy sprite.
     */
    private void drawEnemy() {
        if (enemy!=null)
        {
            enemy.tick();
            batch.draw(enemys,enemy.x-(TILE_SIZE/2),enemy.y-(TILE_SIZE/2));
        }
    }

    /**
     * Draws the projectile.
     */
    private void drawProjectile() {
        if (proj!=null)
        {
            proj.tick();
            batch.draw(projs,proj.x-(TILE_SIZE/2),proj.y-(TILE_SIZE/2));
        }
    }

    /**
     * Draws the walls.
     */
    private void drawStage() {
        for (int i = 0; i<Stage.walls.length; i++)
        {
            for (int j = 0; j<Stage.walls[0].length; j++)
            {
                if (Stage.get(i,j))
                {
                    batch.draw(wall,i*TILE_SIZE,j*TILE_SIZE);
                }
            }
        }
    }

    /**
     * Reinitializes the level, placing the character and enemy at random
     * locations.
     */
    private void restartLevel() {
        lnum++;
        generating=true;
        Stage.density=(1.0-(DENSITY_K1/(level+DENSITY_K2)));
        Stage.init(level);
        proj=null;
        int dex = 0;
        int dey = 0;
        while (true)
        {
            dex = random.nextInt(Stage.walls.length);
            dey = random.nextInt(Stage.walls[0].length);
            if (Stage.get(dex,dey)==false)
                break;
        }
        playerx = (TILE_SIZE/2)+dex*TILE_SIZE;
        playery = (TILE_SIZE/2)+dey*TILE_SIZE;
        while (true)
        {
            dex = random.nextInt(Stage.walls.length);
            dey = random.nextInt(Stage.walls[0].length);
            if ((Stage.get(dex,dey)==false)&&(dex!=playerx)&&(dey!=playery))
                break;
        }
        enemy = new Enemy((TILE_SIZE/2)+dex*TILE_SIZE,(TILE_SIZE/2)+dey*TILE_SIZE);
        enemy.speed=SPEED_K1+(int)(SPEED_K2*Math.sqrt(level));
        int dx = ((int)(playerx/TILE_SIZE))-((int)(enemy.x/TILE_SIZE));
        int dy = ((int)(playery/TILE_SIZE))-((int)(enemy.y/TILE_SIZE));
        if (Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2))<MIN_ENEMY_DIST)
            regenLevel=true;
    }

    /**
     * Draws additional text for debugging.
     */
    // CHECKSTYLE DISABLE NPath FOR 1 LINES
    private void drawDebugText() {
        // CHECKSTYLE DISABLE MagicNumber FOR 16 LINES
        font.draw(batch,Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE) ? "T" : "F",500,500);
        font.draw(batch,Stage.get(playerx/TILE_SIZE+1,playery/TILE_SIZE) ? "T" : "F",520,500);
        font.draw(batch,Stage.get(playerx/TILE_SIZE-1,playery/TILE_SIZE) ? "T" : "F",480,500);
        font.draw(batch,Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE+1) ? "T" : "F",500,520);
        font.draw(batch,Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE-1) ? "T" : "F",500,480);
        font.draw(batch,Stage.get(((int) enemy.x)/TILE_SIZE,((int) enemy.y)/TILE_SIZE) ? "T" : "F",400,500);
        font.draw(batch,Stage.get(((int) enemy.x)/TILE_SIZE+1,((int) enemy.y)/TILE_SIZE) ? "T" : "F",420,500);
        font.draw(batch,Stage.get(((int) enemy.x)/TILE_SIZE-1,((int) enemy.y)/TILE_SIZE) ? "T" : "F",380,500);
        font.draw(batch,Stage.get(((int) enemy.x)/TILE_SIZE,((int) enemy.y)/TILE_SIZE+1) ? "T" : "F",400,520);
        font.draw(batch,Stage.get(((int) enemy.x)/TILE_SIZE,((int) enemy.y)/TILE_SIZE-1) ? "T" : "F",400,480);
        font.draw(batch,""+playerx,400,400);
        font.draw(batch,""+playery,500,400);
        font.draw(batch,""+((int) enemy.x),400,420);
        font.draw(batch,""+((int) enemy.y),500,420);
        font.draw(batch,""+((int) enemy.x%32),400,440);
        font.draw(batch,""+((int) enemy.y%32),500,440);
    }

    /**
     * Moves the character based on the keys that are pressed.
     */
    private void moveCharacter() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            tryMoveWest();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            tryMoveEast();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            tryMoveNorth();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {

            tryMoveSouth();
        }
    }

    /**
     * Moves the character south if its path is not blocked. Centers player E/W.
     */
    private void tryMoveSouth() {
        if (playery%TILE_SIZE>=(TILE_SIZE/2)||!Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE-1))
        {
            playery--;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)&&!Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            if (playerx%TILE_SIZE>(TILE_SIZE/2))
                playerx--;
            if (playerx%TILE_SIZE<(TILE_SIZE/2))
                playerx++;
        }
    }

    /**
     * Moves the character south if its path is not blocked. Centers player E/W.
     */
    private void tryMoveNorth() {
        if (playery%TILE_SIZE<=(TILE_SIZE/2)||!Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE+1))
        {

            playery++;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)&&!Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            if (playerx%TILE_SIZE>(TILE_SIZE/2))

                playerx--;
            if (playerx%TILE_SIZE<(TILE_SIZE/2))
                playerx++;
        }
    }

    /**
     * Moves the character east if its path is not blocked. Centers player N/S.
     */
    private void tryMoveEast() {
        if (playerx%TILE_SIZE<=(TILE_SIZE/2)||!Stage.get(playerx/TILE_SIZE+1,playery/TILE_SIZE))
        {
            playerx++;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.UP)&&!Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            if (playery%TILE_SIZE>(TILE_SIZE/2))
                playery--;
            if (playery%TILE_SIZE<(TILE_SIZE/2))
                playery++;
        }
    }

    /**
     * Moves the character west if its path is not blocked. Centers player N/S.
     */
    private void tryMoveWest() {
        if (playerx%TILE_SIZE>=(TILE_SIZE/2)||!Stage.get(playerx/TILE_SIZE-1,playery/TILE_SIZE))
        {
            playerx--;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.UP)&&!Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            if (playery%TILE_SIZE>(TILE_SIZE/2))
                playery--;
            if (playery%TILE_SIZE<(TILE_SIZE/2))
                playery++;
        }
    }

    /**
     * Spawns a projectile in front of the character.
     */
    private void spawnProjectile() {
        switch (lastdir)
        {
            case DIR_N:
                proj = new Projectile(playerx,playery,0,1);
                break;
            case DIR_NE:
                proj = new Projectile(playerx,playery,1,1);
                break;
            case DIR_E:
                proj = new Projectile(playerx,playery,1,0);
                break;
            case DIR_SE:
                proj = new Projectile(playerx,playery,1,-1);
                break;
            case DIR_S:
                proj = new Projectile(playerx,playery,0,-1);
                break;
            case DIR_SW:
                proj = new Projectile(playerx,playery,-1,-1);
                break;
            case DIR_W:
                proj = new Projectile(playerx,playery,-1,0);
                break;
            case DIR_NW:
                proj = new Projectile(playerx,playery,-1,1);
                break;
        }
    }

    /**
     * Returns a direction based on which keys are pressed.
     * 
     * @param keyspressed
     *            The keys that are pressed.
     * @param lastdir
     *            The direction to return if no/invalid keys are pressed.
     * @return The direction.
     */
    private int getDirectionFromKeys(int keyspressed, int lastdir) {
        switch (keyspressed)
        {
            case KEY_UP:
                lastdir = DIR_N;
                break;
            case KEY_UP|KEY_RIGHT:
                lastdir = DIR_NE;
                break;
            case KEY_RIGHT:
                lastdir = DIR_E;
                break;
            case KEY_RIGHT|KEY_DOWN:
                lastdir = DIR_SE;
                break;
            case KEY_DOWN:
                lastdir = DIR_S;
                break;
            case KEY_DOWN|KEY_LEFT:
                lastdir = DIR_SW;
                break;
            case KEY_LEFT:
                lastdir = DIR_W;
                break;
            case KEY_LEFT|KEY_UP:
                lastdir = DIR_NW;
                break;
        }
        return lastdir;
    }

    /**
     * Detects which keys are pressed.
     * 
     * @return An int with bits set for each arrow key
     */
    private int getKeysPressed() {
        int keyspressed = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            keyspressed += KEY_LEFT;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            keyspressed += KEY_RIGHT;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            keyspressed += KEY_UP;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            keyspressed += KEY_DOWN;
        return keyspressed;
    }
}
