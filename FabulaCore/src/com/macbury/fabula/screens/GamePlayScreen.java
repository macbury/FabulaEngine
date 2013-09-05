package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.map.AsyncSceneLoader;
import com.macbury.fabula.map.AsyncSceneLoader.AsyncSceneLoaderListener;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.utils.TopDownCamera;

public class GamePlayScreen extends BaseScreen {
  
  private static final String TAG = "GamePlayScreen";
  private Scene scene;
  private TopDownCamera camera;
  private Terrain terrain;
  private BitmapFont baseFont;
  private SpriteBatch uiSpriteBatch;
  private OrthographicCamera guiCamera;

  public GamePlayScreen(GameManager manager) {
    super(manager);
  }

  @Override
  public void show() {
    Gdx.app.log(TAG, "Show");
    
    G.shaders.createFB(Scene.MAIN_FRAME_BUFFER);
    
    this.baseFont      = G.db.getFont("base");
    this.uiSpriteBatch = new SpriteBatch();
    
    this.camera        = new TopDownCamera();
    this.guiCamera     = new OrthographicCamera();
    this.guiCamera.setToOrtho(false);
    this.guiCamera.update(true);
    this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    teleport(G.db.getPlayerStartPosition().getUUID(), 0,0);
  }
  
  public void teleport(String uuid, int tx, int ty) {
    if (scene != null) {
      scene.dispose();
      scene = null;
      terrain = null;
    }
    
    scene = Scene.open(G.db.getPlayerStartPosition().getFileHandler().file());
    scene.getTerrain().buildSectors();
    scene.initialize();
    this.terrain = scene.getTerrain();
    this.camera.position.set(terrain.getColumns()/2, 17, terrain.getRows()/2);
    this.camera.lookAt(terrain.getColumns()/2, 0, terrain.getRows()/2 - 3);
    scene.setCamera(camera);
    
    this.camera.update();
    this.guiCamera.update();
  }

  
  @Override
  public void dispose() {
    this.scene.dispose();
    this.baseFont.dispose();
    this.uiSpriteBatch.dispose();
    camera           = null;
    guiCamera        = null;
    scene            = null;
    terrain          = null;
  }
  
  @Override
  public void hide() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void pause() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void render(float delta) {
    camera.update();
    
    scene.render();
    
    uiSpriteBatch.setProjectionMatrix(guiCamera.combined);
    uiSpriteBatch.begin();
      baseFont.draw(uiSpriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 30);
    uiSpriteBatch.end();
  }
  
  @Override
  public void resize(int width, int height) {
    guiCamera.viewportWidth  = camera.viewportWidth  = width / G.game.getScaledDensity();
    guiCamera.viewportHeight = camera.viewportHeight = height / G.game.getScaledDensity();
    this.camera.update(true);
    this.guiCamera.update(true);
    this.guiCamera.position.set(guiCamera.viewportWidth/2, guiCamera.viewportHeight/2, 0);
    Gdx.app.log(TAG, "Viewport: " + guiCamera.viewportWidth + "x" + guiCamera.viewportHeight);
    G.shaders.resize(width, height, true);
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }

  
  
  
  
}
