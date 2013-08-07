package com.macbury.fabula.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.macbury.fabula.screens.LoadingScreen;
import com.macbury.fabula.screens.WorldScreen;

public class GameManager extends Game {
  
  private static final String TAG = "GameManager";

  @Override
  public void create() {
    Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
    Gdx.gl.glDepthFunc(GL10.GL_LESS);
    setScreen(new LoadingScreen(this));
  }

  @Override
  public void render() {
    
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

    super.render();
  }
}
