package com.macbury.fabula.manager;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.test.ShaderTestScreen;
import com.thesecretpie.shader.ShaderManager;

public class GameManager extends Game {
  enum Mode { Game, Editor }
  protected static final String TAG = "GameManager";
  protected static final Object ARGUMENT_START_EDITOR = "--editor";
  protected static GameManager _shared;
  protected Mode mode;
  
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
    
    try {
      ResourceManager.shared().load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    G.resources = ResourceManager.shared();
    G.shaders   = shaderManager;
    
    setScreen(getInitialScreen());
    loading = false;
  }

  protected Screen getInitialScreen() {
    return new ShaderTestScreen(this);
  }

  @Override
  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    super.render();
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }
  
  public boolean loading() {
    return loading;
  }

  public ShaderManager getShaderManager() {
    return shaderManager;
  }
}
