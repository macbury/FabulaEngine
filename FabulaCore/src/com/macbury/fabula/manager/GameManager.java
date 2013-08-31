package com.macbury.fabula.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.terrain.AutoTiles;
import com.thesecretpie.shader.ShaderManager;

public class GameManager extends Game {
  protected static final String TAG = "GameManager";
  protected static final Object ARGUMENT_START_EDITOR = "--editor";
  protected static GameManager _shared;
  
  protected boolean loading = true;
  protected ShaderManager shaderManager;
  
  public static GameManager shared() {
    return _shared;
  }
  
  public GameManager() {
    super();
    _shared = this;
  }
  
  @Override
  public void create() {
    Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
    Gdx.gl.glDepthFunc(GL10.GL_LESS);
    
    ShaderProgram.pedantic = false;
    this.shaderManager     = new ShaderManager("assets/shaders", new AssetManager());
    G.game      = this;
    G.shaders   = shaderManager;
    G.db        = null;
    if (GameDatabase.exists()) {
      Gdx.app.log(TAG, "Loading game features...");
      G.db = GameDatabase.load();
    }

    G.db.initialize();
    G.db.save();
    setScreen(getInitialScreen());
    loading = false;
  }

  protected Screen getInitialScreen() {
    return null;
  }

  @Override
  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    super.render();
  }

  
  public boolean loading() {
    return loading;
  }

  public ShaderManager getShaderManager() {
    return shaderManager;
  }
}
