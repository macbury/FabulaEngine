package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.BaseLight;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.editor.brushes.AutoTileBrush;
import com.macbury.fabula.editor.brushes.Brush;
import com.macbury.fabula.editor.brushes.Brush.BrushType;
import com.macbury.fabula.editor.brushes.TerrainBrush;
import com.macbury.fabula.editor.tiles.AutoTileDebugFrame;
import com.macbury.fabula.editor.undo_redo.ChangeManager;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Terrain.TerrainDebugListener;
import com.macbury.fabula.terrain.Tile;
import com.macbury.fabula.utils.ActionTimer;
import com.macbury.fabula.utils.ActionTimer.TimerListener;
import com.macbury.fabula.utils.EditorCamController;
import com.macbury.fabula.utils.TopDownCamera;

public class WorldEditScreen extends BaseScreen implements InputProcessor, TimerListener, TerrainDebugListener {
  public String debugInfo = "";
  private static final String TAG = "WorldScreen";
  private static final float APPLY_BRUSH_EVERY = 0.02f;
  private TopDownCamera camera;
  private EditorCamController camController;
  private ActionTimer   brushTimer;
  private Brush         currentBrush;
  private TerrainBrush  terrainBrush;
  private AutoTileBrush autoTileBrush;
  private Scene scene;
  private Terrain terrain;
  private boolean isPaused;
  private boolean isDragging = false;
  
  public WorldEditScreen(GameManager manager) {
    super(manager);
  }

  @Override
  public void dispose() {
    this.terrain.dispose();
  }
  
  @Override
  public void show() {
    Gdx.app.log(TAG, "Showed screen");
    this.brushTimer = new ActionTimer(APPLY_BRUSH_EVERY, this);
    this.camera = new TopDownCamera();
    Gdx.app.log(TAG, "Initialized screen");
    
    int width = 150;
    int height = 150;
    
    this.scene   = new Scene(width, height);
    this.terrain = this.scene.getTerrain();
    terrain.setDebugListener(this);
    terrain.fillEmptyTilesWithDebugTile();
    terrain.buildSectors();
    camera.position.set(width/2, 17, height/2);
    camera.lookAt(width/2, 0, height/2);
    
    terrainBrush  = new TerrainBrush(terrain);
    autoTileBrush = new AutoTileBrush(terrain);
    //setCurrentBrush(terrainBrush);
    
    this.camController = new EditorCamController(camera);
    InputMultiplexer inputMultiplexer = new InputMultiplexer(this, camController);
    Gdx.input.setInputProcessor(inputMultiplexer);
  }
  
  @Override
  public void hide() {
  }
  
  @Override
  public void pause() {
    this.isPaused = true;
  }
  
  @Override
  public void render(float delta) {
    if (isPaused) {
      debugInfo = "Paused";
      return;
    }
    this.brushTimer.update(delta);
    camController.update();
    camera.update();
    this.scene.render(this.camera);
    //modelBatch.begin(camera);
    //modelBatch.render(cursorInstance);
    //modelBatch.end();
    if (getCurrentBrush() != null) {
      debugInfo = "X: "+ getCurrentBrush().getPosition().x + " Y " + getCurrentBrush().getY() + " Z: " +  getCurrentBrush().getPosition().y + " " + getCurrentBrush().getStatusBarInfo() + " ";
    } else {
      debugInfo = "";
    }
    debugInfo += "FPS: "+ Gdx.graphics.getFramesPerSecond() + " Java Heap: " + (Gdx.app.getJavaHeap() / 1024) + " KB" + " Native Heap: " + (Gdx.app.getNativeHeap() / 1024);
    
  }

  @Override
  public void resize(int width, int height) {
    G.shaders.resize(width, height, true);
    camera.viewportWidth = Gdx.graphics.getWidth();
    camera.viewportHeight = Gdx.graphics.getHeight();
    this.camera.update(true);
  }
  
  @Override
  public void resume() {
    this.isPaused = false;
  }
  
  public TopDownCamera getCamera() {
    return camera;
  }

  @Override
  public boolean keyDown(int arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean keyTyped(char arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean keyUp(int arg0) {
    // TODO Auto-generated method stub
    return false;
  }
  
  Vector3 mouseTilePosition = new Vector3();
  private ChangeManager changeManager;
  @Override
  public boolean mouseMoved(int x, int y) {
    Ray ray     = camera.getPickRay(x, y);
    Vector3 pos = terrain.getSnappedPositionForRay(ray, mouseTilePosition);
    
    if (pos != null && currentBrush != null) {
      currentBrush.setPosition(pos.x, pos.z);
    }
    return false;
  }

  @Override
  public boolean scrolled(int arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    if (button == Buttons.LEFT && currentBrush != null) {
      Vector3 pos = getPositionForMouse(x, y);
      
      if (pos != null) {
        currentBrush.setStartPosition(pos.x, pos.z);
      }
      
      this.brushTimer.start();
      isDragging = true;
      return true;
    }
    return false;
  }

  @Override
  public boolean touchDragged(int x, int y, int pointer) {
    if (isDragging && currentBrush != null) {
      Vector3 pos = getPositionForMouse(x, y);
      
      if (pos != null) {
        currentBrush.applyStartPositionIfNotSetted(pos);
        currentBrush.setPosition(pos.x, pos.z);
      }
    }
    
    return false;
  }

  @Override
  public boolean touchUp(int x, int y, int pointer, int button) {
    if (button == Buttons.LEFT && currentBrush != null) {
      isDragging  = false;
      Vector3 pos = getPositionForMouse(x, y);
      if (pos != null) {
        currentBrush.setPosition(pos.x, pos.z);
        currentBrush.applyBrush();
      }
      currentBrush.setStartPosition(null);
      this.brushTimer.stop();
      return true;
    }
    return false;
  }

  @Override
  public void onTimerTick(ActionTimer timer) {
    if (currentBrush != null && currentBrush.getBrushType() == BrushType.Pencil) {
      currentBrush.applyBrush();
    }
  }

  @Override
  public void onDebugTerrainConfigureShader(ShaderProgram shader) {
    if (currentBrush != null) {
      shader.setUniformf("u_brush_position", currentBrush.getPosition());
      shader.setUniformf("u_brush_size", currentBrush.getSize());
      
      if (currentBrush.getStartPosition() == null) {
        shader.setUniformi("u_brush_type", 0);
      } else {
        shader.setUniformf("u_brush_start_position", currentBrush.getStartPosition());
        shader.setUniformi("u_brush_type", currentBrush.getBrushShaderId());
      }
    }
  }

  public Brush getCurrentBrush() {
    return currentBrush;
  }

  public void setCurrentBrush(Brush currentBrush) {
    this.currentBrush = currentBrush;
    this.currentBrush.setChangeManager(this.changeManager);
  }

  public TerrainBrush getTerrainBrush() {
    return terrainBrush;
  }

  public AutoTileBrush getAutoTileBrush() {
    return autoTileBrush;
  }

  public Scene getScene() {
    return this.scene;
  }
  
  public Vector3 getPositionForMouse(float x, float y) {
    Ray ray     = camera.getPickRay(x, y);
    return terrain.getSnappedPositionForRay(ray, mouseTilePosition);
  }

  public void setChangeManager(ChangeManager changeManager) {
    this.changeManager = changeManager;
  }

}
