package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.utils.TopDownCamera;

public class GamePlayScreen extends BaseScreen {
  
  private static final String TAG = "GamePlayScreen";
  private Scene scene;
  private TopDownCamera camera;
  private Terrain terrain;
  private CameraInputController camController;
  private BitmapFont baseFont;
  private SpriteBatch uiSpriteBatch;
  private OrthographicCamera guiCamera;

  public GamePlayScreen(GameManager manager) {
    super(manager);
  }

  @Override
  public void show() {
    Gdx.app.log(TAG, "Show");
    
    this.baseFont      = G.db.getFont("base");
    this.uiSpriteBatch = new SpriteBatch();
    
    this.camera    = new TopDownCamera();
    this.guiCamera = new OrthographicCamera();
    this.scene     = Scene.open(G.db.getPlayerStartPosition().getFileHandler().file());
    this.terrain   = scene.getTerrain();
    terrain.buildSectors();
    
    this.guiCamera.setToOrtho(false);
    this.camera.position.set(terrain.getColumns()/2, 17, terrain.getRows()/2);
    this.camera.lookAt(terrain.getColumns()/2, 0, terrain.getRows()/2 - 3);
    this.scene.setCamera(camera);
    this.camController = new CameraInputController(camera);
    Gdx.input.setInputProcessor(camController);
    this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }
  
  @Override
  public void dispose() {
    this.terrain.dispose();
    this.baseFont.dispose();
    this.uiSpriteBatch.dispose();
    camera    = null;
    guiCamera = null;
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
    camController.update();
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
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  
  
}
