package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Pathfinder {

    static final int TILE_SIZE = MyGdxGame.TILE_SIZE;
    public static ArrayList<int[]> queue = new ArrayList<int[]>();
    public static int[][]          cells = new int[Stage.walls.length][Stage.walls[0].length];
    public static final int[][]    CELL_DIRECTIONS    = (new int[][] {{0,1},{1,0},{0,-1},{-1,0}});
    private static final int MAX_ITERATIONS = 1000;

    /**
     * Uses Djikstra's Algorithm to find the shortest path between two points
     * @param cx Current X coordinate
     * @param cy Current Y coordinate
     * @param tx Target X coordinate
     * @param ty Target Y coordinate
     * @return The next adjacent tile to move to.
     */
    public static int[] pathfind(int cx, int cy, int tx, int ty) {
        if ((cx==tx)&&(cy==ty))         //If already at target, no move.
        {
            return new int[] {cx,cy};
        }
        
        queue.clear();
        
        for (int x = 0; x<Stage.walls.length; x++)                  //Initialize cell array, marking walls.
            for (int y = 0; y<Stage.walls[0].length; y++)
                cells[x][y] = Stage.get(x,y) ? Integer.MAX_VALUE : Integer.MAX_VALUE-1;     
        
        queue.add(new int[] {tx,ty,0});
        cells[tx][ty] = 0;                                          //Target cell has weight 0
        boolean done = false;
        int iterationCount = 0;
        while (!done)                                               //This while loop calculates the weight of each cell in the grid.
        {                                                           //The weight at the target cell is 0, and weights increase as they get farther away.
            iterationCount++;
            if (iterationCount>MAX_ITERATIONS)
            {
                return null;
            }
            
            ArrayList<int[]> tempQueue = new ArrayList<int[]>();
            for (int[] queuedCell : queue)                              //For each cell C in queue,
            {                                                           //
                for (int[] adjDir : CELL_DIRECTIONS)                    //and each adjacent cell A,
                {                                                       //
                    int x = queuedCell[0]+adjDir[0];                    //
                    int y = queuedCell[1]+adjDir[1];                    //
                    if (cells[x][y]!=Integer.MAX_VALUE-1)               //if A has not been visited and is not a wall,
                        continue;                                       //
                    cells[x][y] = queuedCell[2]+1;                      //set A to weight(C)+1,
                    tempQueue.add(new int[] {x,y,queuedCell[2]+1});     //and place A in queue.
                    if ((x==cx)&&(y==cy))
                    {
                        done = true;
                    }
                }
            }
            queue.clear();
            for (int[] i : tempQueue)                               
            {
                queue.add(i);
            }
        }
        return getNextStep(cx,cy);
        
    }

    /**
     * Gets the next adjacent tile to move into.
     * @param cx Current X coordinate.
     * @param cy Current Y coordinate.
     * @return The tile to move into.
     */
    private static int[] getNextStep(int cx, int cy) {
        int[] lc = new int[] {cx,cy};
        int lw = Integer.MAX_VALUE;
        for (int[] i : CELL_DIRECTIONS)                 //Finds and returns the adjacent cell with the lowest weight (closest to target)
        {
            if (cells[cx+i[0]][cy+i[1]]<lw)
            {
                lw = cells[cx+i[0]][cy+i[1]];
                lc = new int[] {cx+i[0],cy+i[1]};
            }
        }
        return lc;
    }

    /**
     * Draws path weights for each tile.
     * @param batch the SpriteBatch to draw on.
     * @param font
     */
    public static void draw(SpriteBatch batch) {                            //This is not used outside of debugging.
        batch.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA,GL30.GL_ONE_MINUS_SRC_ALPHA);
        Color color = new Color(1,1,1,1);
        color.a = 1.0f;
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeType.Filled);
        int mc = 0;
        for (int x = 0; x<Stage.walls.length; x++)
        {
            for (int y = 0; y<Stage.walls[0].length; y++)
            {
                if (cells[x][y]>Integer.MAX_VALUE-2)
                    continue;

                if (cells[x][y]>mc)
                    mc = cells[x][y];
            }
        }
        for (int x = 0; x<Stage.walls.length; x++)
        {
            for (int y = 0; y<Stage.walls[0].length; y++)
            {
                if (cells[x][y]==Integer.MAX_VALUE)
                {
                    color.r = 0;
                    color.g = 0;
                    color.b = 1;
                } else if (cells[x][y]==Integer.MAX_VALUE-1)
                {
                    color.r = 0;
                    color.g = 0;
                    color.b = 0;
                } else
                {
                    float n = ((float) cells[x][y])/((float) mc);
                    color.r = n;
                    color.g = 1-n;
                    color.b = 0;
                }

                shapeRenderer.setColor(color);
                shapeRenderer.rect(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
                // font.draw(batch, ""+cells[x][y], x*TILE_SIZE, 24+y*TILE_SIZE);
            }
        }
        shapeRenderer.end();
        batch.begin();

    }
}
