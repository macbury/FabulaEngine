package com.macbury.fabula.manager;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.screens.WorldEditScreen;
import com.macbury.fabula.test.ShaderTestScreen;
import com.thesecretpie.shader.ShaderManager;

public class GameManager extends Game {
  enum Mode { Game, Editor }
  private static final String TAG = "GameManager";
  private static final Object ARGUMENT_START_EDITOR = "--editor";
  private static GameManager _shared;
  private Mode mode;
  private WorldEditScreen worldEditScreen;
  private boolean loading = true;
  private ShaderManager shaderManager;
  
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
    this.shaderManager     = new ShaderManager("data/shaders", new AssetManager());
    G.game      = this;
    
    try {
      ResourceManager.shared().load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    G.resources = ResourceManager.shared();
    G.shaders   = shaderManager;
    
    if (getMode() == Mode.Game) {
      setScreen(new ShaderTestScreen(this));
    } else {
      setScreen(getWorldEditScreen());
    }
    loading = false;
  }
  
  public WorldEditScreen getWorldEditScreen() {
    if (worldEditScreen == null) {
      worldEditScreen = new WorldEditScreen(this);
    }
    
    return worldEditScreen;
  }

  @Override
  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    super.render();
  }
  
  public static void bootstrap(String[] args) {
    ArrayList<String> arguments  = new ArrayList<String>(Arrays.asList(args));
    final GameManager game             = new GameManager();
    
    if (arguments.indexOf(ARGUMENT_START_EDITOR) > -1) {
      game.setMode(Mode.Editor);
      startEditor(game);
    } else {
      game.setMode(Mode.Game);
      LwjglApplication application = new LwjglApplication(game, "FabulaEngine", 1366, 768, true);
    }
  }


  private static void startEditor(final GameManager game) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          WorldEditorFrame frame = new WorldEditorFrame(game);
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
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
