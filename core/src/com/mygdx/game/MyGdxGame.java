package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture wall;
	Texture player;
	Stage stage = new Stage();
	int playerx=32;
	int playery=32;
	@Override
	public void create () {
		batch = new SpriteBatch();
		wall = new Texture("wall.png");
		player= new Texture("player.gif");
	}

	@Override
	public void render () {
		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			playerx--;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			playerx++;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP))
		{
			playery++;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			playery--;
		}
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(player, playerx, playery);
		for (int i=0;i<10;i++)
			{
			for (int j=0;j<10;j++)
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
