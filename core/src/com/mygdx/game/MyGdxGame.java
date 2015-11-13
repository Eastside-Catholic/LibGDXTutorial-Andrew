package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyGdxGame extends ApplicationAdapter {

    static final int SCREEN_W = 720;
    static final int SCREEN_H = 620;

    private static final int INIT_PLYR_POS_X = 48;
    private static final int INIT_PLYR_POS_Y = 48;
    private static final int INIT_ENEM_POS_X = 304;
    private static final int INIT_ENEM_POS_Y = 304;

    static final int TILE_SIZE      = 32;
    static final int HALF_TILE_SIZE = TILE_SIZE/2;

    // Constants for key-pressed bitmap
    private static final int KEY_LEFT  = 0b0001;
    private static final int KEY_RIGHT = 0b0010;
    private static final int KEY_UP    = 0b0100;
    private static final int KEY_DOWN  = 0b1000;

    // Direction constants
    private static final int DIR_N  = 1;
    private static final int DIR_NE = 2;
    private static final int DIR_E  = 3;
    private static final int DIR_SE = 4;
    private static final int DIR_S  = 5;
    private static final int DIR_SW = 6;
    private static final int DIR_W  = 7;
    private static final int DIR_NW = 8;

    // The next 3 variables tune the difficulty increase ramp.
    // _______
    // Speed=K1+K2*√ level
    //
    // K1
    // Density= ----------
    // level+K1
    //
    private static final int    SPEED_K1   = 2;      // Base speed.
    private static final double SPEED_K2   = 4.0;    // Rate at which speed increases.
    private static final double DENSITY_K1 = 16.0;   // Density ramp divisor. The greater this number, the more slowly the level density increases.
    private static final double SCORE_K1   = 1000.0; //Maximum possible level score.
    private static final double SCORE_K2   = 1000;   // Score ramp divisor. The greater this number, the more slowly the level density decreases.

    private static final double MIN_ENEMY_DIST         = 8;   // The enemy will always spawn this distance from the player.
    private static final int    PROJ_SPEED             = 8;   // The projectile moves this many pixels per frame.
    private static final int    PROJ_DELAY             = 60;  // The cooldown after shooting.
    private static final int    NUM_DOTS               = 5;   // The number of dots to spawn.
    private static final int    PLAYER_ROTATION_OFFSET = 135; // The player faces up when rotated this amount.
    private static final int    MAX_DOT_ITERS          = 1000;// The maximum number of times to try generating dots before giving up.

    private SpriteBatch               batch;
    private Texture                   wall;
    private Texture                   player;
    private Texture                   projs;
    private Texture                   enemys;
    private Texture                   dotsp;
    static int                        playerx      = INIT_PLYR_POS_X;
    static int                        playery      = INIT_PLYR_POS_Y;
    private BitmapFont                font;
    private int                       lastdir      = 0;
    static ArrayList<Projectile>      projl        = new ArrayList<Projectile>();
    private static Enemy              enemy;
    static boolean                    regenLevel;
    static boolean                    generating;
    static int                        level        = 0;
    private Random                    random;
    private int                       lastprojtick = 0;
    private int                       tick         = 0;
    static ArrayList<int[]>           dots         = new ArrayList<int[]>();
    private int                       leveltick    = 0;
    private static int                score        = 0;
    private static int                levelscore   = 0;
    private static SortedSet<Integer> highscores   = new TreeSet<Integer>();

    @Override
    public void create() {
        Stage.init(level,0);
        Gdx.graphics.setDisplayMode(SCREEN_W,SCREEN_H,false);
        batch = new SpriteBatch();
        wall = new Texture("wall.png");
        player = new Texture("player.gif");
        projs = new Texture("proj.png");
        enemys = new Texture("blooper.png");
        dotsp = new Texture("dot.png");
        font = new BitmapFont();
        font.setColor(Color.RED);
        enemy = new Enemy(INIT_ENEM_POS_X,INIT_ENEM_POS_Y);
        enemy.speed = SPEED_K1+(int) (SPEED_K2*Math.sqrt(level));
        random = new Random();
        generateLevel(); // This is called to randomize player and enemy positions and spawn dots.
    }

    @Override
    public void render() {
        this.tick++;
        this.leveltick++;

        levelscore = (int) (SCORE_K1*(SCORE_K2/(this.leveltick+SCORE_K2)));
        processDebugKeys(); // TODO: Remove debugging code
        int keyspressed = getKeysPressed();
        lastdir = getDirectionFromKeys(keyspressed,lastdir);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            spawnProjectile();
        }
        moveCharacter();
        testDots();
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        while (regenLevel)
        {
            generateLevel();
        }
        // Pathfinder.draw(batch);
        TextureRegion plr = new TextureRegion(player);
        batch.draw(plr,playerx-(HALF_TILE_SIZE),playery-(HALF_TILE_SIZE),HALF_TILE_SIZE,HALF_TILE_SIZE,TILE_SIZE,
                TILE_SIZE,1,1,PLAYER_ROTATION_OFFSET-(45*lastdir));
        drawProjectiles();
        drawStage();
        drawEnemy();
        drawDots();
        drawText();
        //CHECKSTYLE DISABLE MagicNumber FOR 3 LINES
        if (regenLevel)
        {
            font.draw(batch,"Generating level... Please wait.",20,540);
        }
        // drawDebugText();
        batch.end();
    }

    /**
     * Draws instructions and score.
     */
    private void drawText() {
        // CHECKSTYLE DISABLE MagicNumber FOR 11 LINES
        font.draw(batch,"Move with arrow keys, shoot with space, press R to restart level",20,620);
        font.draw(batch,"Eat the dots, then shoot the enemy to go to the next level",20,600);
        font.draw(batch,"Until the dots are eaten, shooting the enemy freezes it for 5s.",20,580);
        font.draw(batch,"Level: "+level+"        "+"Dots eaten: "+(NUM_DOTS-dots.size())+"/"+NUM_DOTS,20,560);
        font.draw(batch,"Score: "+score+"        "+"Level score: "+levelscore,20,540);
        font.draw(batch,"High Scores:",520,500);
        int i = 480;
        for (Integer s : highscores)
        {
            font.draw(batch,""+s,520,i);
            i -= 20;
        }
    }

    /**
     * Performs debug actions when debug keys are pressed.
     */
    private void processDebugKeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) // Debug key: Skip to the next level
        {
            nextLevel();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R))
        {
            regenLevel = true;
        }
    }

    /**
     * Deletes any dots that share a tile with the player.
     */
    private void testDots() {
        // Since we cannot modify a list in-place,
        // and java lacks list comprehensions,
        // we need to do crazy list shuffling.
        //
        // Why, Java, do you forsake me?
        // It could have been so nice:
        // dots = [i for i in dots if <condition>]
        //
        // Instead, we're stuck with this:
        ArrayList<int[]> temparr = new ArrayList<int[]>();
        for (int[] i : dots)
        {
            boolean del = false;
            if ((((playerx-(HALF_TILE_SIZE))/TILE_SIZE)==i[0])&&(((playery-(HALF_TILE_SIZE))/TILE_SIZE)==i[1]))
            {
                del = true;
            }
            if (!del)
            {
                temparr.add(i);
            }
        }
        dots = temparr;
    }

    /**
     * Draws the dot sprites.
     */
    private void drawDots() {
        for (int[] i : dots)
        {
            batch.draw(dotsp,i[0]*TILE_SIZE+HALF_TILE_SIZE-2,i[1]*TILE_SIZE+HALF_TILE_SIZE-2);
        }
    }

    /**
     * Draws the enemy sprite.
     */
    private void drawEnemy() {
        if (enemy!=null)
        {
            enemy.tick();
            batch.draw(enemys,enemy.x-(HALF_TILE_SIZE),enemy.y-(HALF_TILE_SIZE));
        }
    }

    /**
     * Draws the projectile.
     */
    private void drawProjectiles() {
        for (Projectile proj : projl)
        {
            if (proj!=null)
            {
                if (proj.toDel) //          ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
                { //                        ┃This is here because an object can't set itself to null.   ┃
                    proj = null; //         ┃Instead, it sets the toDel flag,                           ┃
                    continue; //            ┃and this sets it to null.                                  ┃
                } //                        ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
                proj.tick();
                TextureRegion prr = new TextureRegion(projs);
                batch.draw(prr,proj.x-(HALF_TILE_SIZE),proj.y-(HALF_TILE_SIZE),HALF_TILE_SIZE,HALF_TILE_SIZE,TILE_SIZE,
                        TILE_SIZE,1,1,-45-(45*proj.rotation));
            }
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
     * Advances to the next level.
     */
    public static void nextLevel() {
        level++;
        regenLevel = true;
        score += levelscore;
    }

    /**
     * Resets to level 0.
     */
    public static void resetLevel() {
        level = 0;
        regenLevel = true;
        if (score!=0)
            highscores.add(score);
    }

    /**
     * Reinitializes the level, placing the character and enemy at random locations.
     */
    private void generateLevel() {
        leveltick = 0;
        regenLevel = false;
        generating = true; // The generating flag inhibits player/enemy death.
                           // This solves a bug where the game resets because, while generating, the ghost
                           // is occasionally ticked while on top of either the player or a projectile.
        double density = (1.0-(DENSITY_K1/(level+DENSITY_K1)));
        Stage.init(level,density);
        projl.clear();
        int dex = 0;
        int dey = 0;
        while (true)
        {
            dex = random.nextInt(Stage.walls.length);
            dey = random.nextInt(Stage.walls[0].length);
            if (Stage.get(dex,dey)==false)
                break;
        }
        playerx = (HALF_TILE_SIZE)+dex*TILE_SIZE;
        playery = (HALF_TILE_SIZE)+dey*TILE_SIZE;
        while (true)
        {
            dex = random.nextInt(Stage.walls.length);
            dey = random.nextInt(Stage.walls[0].length);
            if ((Stage.get(dex,dey)==false)&&(dex!=playerx)&&(dey!=playery))
                break;
        }
        enemy = new Enemy((HALF_TILE_SIZE)+dex*TILE_SIZE,(HALF_TILE_SIZE)+dey*TILE_SIZE);
        enemy.speed = SPEED_K1+(int) (SPEED_K2*Math.sqrt(level));
        int dx = ((int) (playerx/TILE_SIZE))-((int) (enemy.x/TILE_SIZE));
        int dy = ((int) (playery/TILE_SIZE))-((int) (enemy.y/TILE_SIZE));
        if (Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2))<MIN_ENEMY_DIST)
        {
            regenLevel = true;
        }
        enemy.tick();
        dots.clear();
        int iters = 0;
        while (dots.size()<NUM_DOTS)
        {
            iters += 1;
            if (iters>this.MAX_DOT_ITERS)
            {
                this.regenLevel = true;
                return;
            }
            dex = random.nextInt(Stage.walls.length);
            dey = random.nextInt(Stage.walls[0].length);
            if ((Stage.get(dex,dey)==false)&&(Pathfinder.pathfind((playerx-(HALF_TILE_SIZE))/TILE_SIZE,
                    (playery-(HALF_TILE_SIZE))/TILE_SIZE,dex,dey)!=null))
            {
                boolean edot = false;
                for (int[] i : dots)
                {
                    if (dex==i[0]&&dey==i[1])
                    {
                        edot = true;
                    }
                }
                if (!edot)
                {
                    dots.add(new int[] {dex,dey});
                }
            }
        }
    }

    /**
     * Draws additional text for debugging.
     */
    // CHECKSTYLE DISABLE NPath FOR 1 LINES
    @SuppressWarnings("unused")
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
        if (playery%TILE_SIZE>=(HALF_TILE_SIZE)||!Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE-1))
        {
            playery--;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)&&!Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            if (playerx%TILE_SIZE>(HALF_TILE_SIZE))
                playerx--;
            if (playerx%TILE_SIZE<(HALF_TILE_SIZE))
                playerx++;
        }
    }

    /**
     * Moves the character south if its path is not blocked. Centers player E/W.
     */
    private void tryMoveNorth() {
        if (playery%TILE_SIZE<=(HALF_TILE_SIZE)||!Stage.get(playerx/TILE_SIZE,playery/TILE_SIZE+1))
        {

            playery++;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)&&!Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            if (playerx%TILE_SIZE>(HALF_TILE_SIZE))
                playerx--;
            if (playerx%TILE_SIZE<(HALF_TILE_SIZE))
                playerx++;
        }
    }

    /**
     * Moves the character east if its path is not blocked. Centers player N/S.
     */
    private void tryMoveEast() {
        if (playerx%TILE_SIZE<=(HALF_TILE_SIZE)||!Stage.get(playerx/TILE_SIZE+1,playery/TILE_SIZE))
        {
            playerx++;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.UP)&&!Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            if (playery%TILE_SIZE>(HALF_TILE_SIZE))
                playery--;
            if (playery%TILE_SIZE<(HALF_TILE_SIZE))
                playery++;
        }
    }

    /**
     * Moves the character west if its path is not blocked. Centers player N/S.
     */
    private void tryMoveWest() {
        if (playerx%TILE_SIZE>=(HALF_TILE_SIZE)||!Stage.get(playerx/TILE_SIZE-1,playery/TILE_SIZE))
        {
            playerx--;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.UP)&&!Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            if (playery%TILE_SIZE>(HALF_TILE_SIZE))
                playery--;
            if (playery%TILE_SIZE<(HALF_TILE_SIZE))
                playery++;
        }
    }

    /**
     * Spawns a projectile in front of the character.
     */
    private void spawnProjectile() {
        if (tick-lastprojtick<PROJ_DELAY)
        {
            return;
        }
        lastprojtick = tick;
        switch (lastdir)
        {
            case DIR_N:
                projl.add(new Projectile(playerx,playery,0,PROJ_SPEED,lastdir));
                break;
            case DIR_NE:
                projl.add(new Projectile(playerx,playery,PROJ_SPEED,PROJ_SPEED,lastdir));
                break;
            case DIR_E:
                projl.add(new Projectile(playerx,playery,PROJ_SPEED,0,lastdir));
                break;
            case DIR_SE:
                projl.add(new Projectile(playerx,playery,PROJ_SPEED,-PROJ_SPEED,lastdir));
                break;
            case DIR_S:
                projl.add(new Projectile(playerx,playery,0,-PROJ_SPEED,lastdir));
                break;
            case DIR_SW:
                projl.add(new Projectile(playerx,playery,-PROJ_SPEED,-PROJ_SPEED,lastdir));
                break;
            case DIR_W:
                projl.add(new Projectile(playerx,playery,-PROJ_SPEED,0,lastdir));
                break;
            case DIR_NW:
                projl.add(new Projectile(playerx,playery,-PROJ_SPEED,PROJ_SPEED,lastdir));
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
