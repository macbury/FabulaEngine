package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.utils.TopDownCamera;

public class WorldScreen extends BaseScreen {
  private static final String TAG = "WorldScreen";
  private TopDownCamera camera;
  private Terrain terrain;
  private FPSLogger fpsLogger;
  public WorldScreen(GameManager manager) {
    super(manager);
    this.fpsLogger = new FPSLogger();
    this.camera = new TopDownCamera();
    Gdx.app.log(TAG, "Initialized screen");
    this.terrain = new Terrain(this, 50, 50);
    
    camera.position.set(0, 17, 0);
    camera.lookAt(0, 0, 0);
    Gdx.input.setInputProcessor(new CameraInputController(camera));
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
    
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
    this.terrain.render(this.camera);
    fpsLogger.log();
  }
  
  @Override
  public void resize(int width, int height) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void show() {
    Gdx.app.log(TAG, "Showed screen");
  }
  
  public TopDownCamera getCamera() {
    return camera;
  }
}
