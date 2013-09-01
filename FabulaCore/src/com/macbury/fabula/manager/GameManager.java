package com.macbury.fabula.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.db.GameDatabase;
import com.thesecretpie.shader.ShaderManager;

public abstract class GameManager extends Game {
  public static final String GAME_DIRECTORY_NAME = "brutal-crasher";
  protected static final String TAG = "GameManager";
  protected static GameManager _shared;
  
  protected boolean loading = true;
  protected ShaderManager shaderManager;
  private String storePath;
  
  public static GameManager shared() {
    return _shared;
  }
  
  public GameManager(String storePath) {
    super();
    this.storePath = storePath;
    _shared = this;
  }
  
  public String getStorePath() {
    return this.storePath;
  }
  
  @Override
  public void create() {
    Gdx.app.log(TAG, "Store path: " + this.storePath);
    Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
    Gdx.gl.glDepthFunc(GL10.GL_LESS);
    
    ShaderProgram.pedantic = false;
    
    Gdx.app.log(TAG, "Loading shaders");
    this.shaderManager     = new ShaderManager(new AssetManager());
    G.game      = this;
    G.shaders   = shaderManager;
    Gdx.app.log(TAG, "Preparing game DB");
    G.db        = GameDatabase.load();

    if (G.db == null) {
      onNoGameData();
    } else {
      G.db.initialize();
      G.db.save();
      setScreen(getInitialScreen());
      loading = false;
    }
  }

  public abstract Screen getInitialScreen();
  public abstract void   onNoGameData();
  
  @Override
  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    super.render();
  }

  
  public boolean loading() {
    return loading;
  }

}
