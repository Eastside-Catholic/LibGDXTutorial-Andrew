package com.mygdx.game;

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
	Stage stage = new Stage();
	int playerx=48;
	int playery=48;
	BitmapFont font;
	
	@Override
	public void create () {
		Gdx.graphics.setDisplayMode(620, 620, false);
		batch = new SpriteBatch();
		wall = new Texture("wall.png");
		player= new Texture("player.gif");
		font = new BitmapFont();
		font.setColor(Color.RED);
		
	}

	@Override
	public void render () {
		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			if (playerx%32>=16 || !stage.get(playerx/32-1, playery/32))
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
			if (playerx%32<=16 || !stage.get(playerx/32+1, playery/32))
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
			if (playery%32<=16 || !stage.get(playerx/32, playery/32+1))
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
		
			if (playery%32>=16 || !stage.get(playerx/32, playery/32-1))
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
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, stage.get(playerx/32, playery/32)?"T":"F", 500, 500);
		font.draw(batch, stage.get(playerx/32+1, playery/32)?"T":"F", 520, 500);
		font.draw(batch, stage.get(playerx/32-1, playery/32)?"T":"F", 480, 500);
		font.draw(batch, stage.get(playerx/32, playery/32+1)?"T":"F", 500, 520);
		font.draw(batch, stage.get(playerx/32, playery/32-1)?"T":"F", 500, 480);
		batch.draw(player, playerx-16, playery-16);
		for (int i=0;i<11;i++)
			{
			for (int j=0;j<11;j++)
				{
				if (stage.get(i, j))
					{
					batch.draw(wall, i*32, j*32);
					}
				}
			}
		batch.end();
	}
}
