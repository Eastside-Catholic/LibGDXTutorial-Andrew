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
	SpriteBatch batch;
	Texture wall;
	Texture player;
	Texture projs;
	Texture enemys;
	static int playerx=48;
	static int playery=48;
	BitmapFont font;
	int lastdir=0;
	static Projectile proj;
	static Enemy enemy;
	public static boolean lose;
	Random random;
	
	@Override
	public void create () {
		Stage.init();
		Gdx.graphics.setDisplayMode(620, 620, false);
		batch = new SpriteBatch();
		wall = new Texture("wall.png");
		player= new Texture("player.gif");
		projs = new Texture("proj.png");
		enemys = new Texture("blooper.png");
		font = new BitmapFont();
		font.setColor(Color.RED);
		enemy = new Enemy(304,304);
		Pathfinder.pathfind(1,1,9,9);
		random=new Random();
	}

	@Override
	public void render () {
	    int tx=(int)Math.floor(enemy.x/32);
        int ty=(int)Math.floor(enemy.y/32);
        int px=(int)Math.floor(MyGdxGame.playerx/32);
        int py=(int)Math.floor(MyGdxGame.playery/32);
        
        //Pathfinder.pathfind(tx,ty,px,py);
	    
		int keyspressed=0;
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			keyspressed+=1;
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			keyspressed+=2;
		if (Gdx.input.isKeyPressed(Input.Keys.UP))
			keyspressed+=4;
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
			keyspressed+=8;
		switch (keyspressed)
		{
			case 4:
				lastdir=1;
				break;
			case 6:
				lastdir=2;
				break;
			case 2:
				lastdir=3;
				break;
			case 10:
				lastdir=4;
				break;
			case 8:
				lastdir=5;
				break;
			case 9:
				lastdir=6;
				break;
			case 1:
				lastdir=7;
				break;
			case 5:
				lastdir=8;
				break;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
		{
			switch (lastdir)
			{
				case 1:
					proj=new Projectile(playerx,playery,0,1);
					break;
				case 2:
					proj=new Projectile(playerx,playery,1,1);
					break;
				case 3:
					proj=new Projectile(playerx,playery,1,0);
					break;
				case 4:
					proj=new Projectile(playerx,playery,1,-1);
					break;
				case 5:
					proj=new Projectile(playerx,playery,0,-1);
					break;
				case 6:
					proj=new Projectile(playerx,playery,-1,-1);
					break;
				case 7:
					proj=new Projectile(playerx,playery,-1,0);
					break;
				case 8:
					proj=new Projectile(playerx,playery,-1,1);
					break;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			if (playerx%32>=16 || !Stage.get(playerx/32-1, playery/32))
			{
				playerx--;
			}
			if (!Gdx.input.isKeyPressed(Input.Keys.UP)&&!Gdx.input.isKeyPressed(Input.Keys.DOWN))
			{
			if (playery%32>16)
				playery--;
			if (playery%32<16)
				playery++;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			if (playerx%32<=16 || !Stage.get(playerx/32+1, playery/32))
			{
			playerx++;
			}
			if (!Gdx.input.isKeyPressed(Input.Keys.UP)&&!Gdx.input.isKeyPressed(Input.Keys.DOWN))
			{
			if (playery%32>16)
				playery--;
			if (playery%32<16)
				playery++;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP))
		{
			if (playery%32<=16 || !Stage.get(playerx/32, playery/32+1))
			{
		
			playery++;
			}
			if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)&&!Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			{
				if (playerx%32>16)

				playerx--;
			if (playerx%32<16)
				playerx++;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
		
			if (playery%32>=16 || !Stage.get(playerx/32, playery/32-1))
			{
			playery--;
			}
			if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)&&!Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			{if (playerx%32>16)
				playerx--;
			if (playerx%32<16)
				playerx++;
			}
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, Stage.get(playerx/32, playery/32)?"T":"F", 500, 500);
		font.draw(batch, Stage.get(playerx/32+1, playery/32)?"T":"F", 520, 500);
		font.draw(batch, Stage.get(playerx/32-1, playery/32)?"T":"F", 480, 500);
		font.draw(batch, Stage.get(playerx/32, playery/32+1)?"T":"F", 500, 520);
		font.draw(batch, Stage.get(playerx/32, playery/32-1)?"T":"F", 500, 480);
		font.draw(batch, ""+playerx, 400,400);
		font.draw(batch, ""+playery, 500,400);
		font.draw(batch, ""+enemy.x, 400,420);
        font.draw(batch, ""+enemy.y, 500,420);
		if (lose)
		{
		    int dex=0;
		    int dey=0;
		    while (true)
		    {
		        dex=random.nextInt(11);
		        dey=random.nextInt(11);
		        if (Stage.get(dex, dey)==false)
		            break;
		    }
		    playerx=16+dex*32;
		    playery=16+dey*32;
		    while (true)
		    {
                dex=random.nextInt(11);
                dey=random.nextInt(11);
                if ((Stage.get(dex, dey)==false)&&(dex!=playerx)&&(dey!=playery))
                    break;
            }
		    enemy = new Enemy(16+dex*32,16+dey*32);
		    lose=false;
		}
		batch.draw(player, playerx-16, playery-16);
		if (proj!=null)
		{
			proj.tick();
			batch.draw(projs, proj.x-16, proj.y-16);
		}
		for (int i=0;i<11;i++)
			{
			for (int j=0;j<11;j++)
				{
				if (Stage.get(i, j))
					{
					batch.draw(wall, i*32, j*32);
					}
				}
			}
		if (enemy!=null)
				{
					enemy.tick();
					batch.draw(enemys, enemy.x-16,enemy.y-16);
				}
		Pathfinder.draw(batch, font);
		batch.end();
	}
}
