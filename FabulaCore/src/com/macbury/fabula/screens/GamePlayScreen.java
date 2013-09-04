package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
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

  public GamePlayScreen(GameManager manager) {
    super(manager);
    
  }

  @Override
  public void show() {
    Gdx.app.log(TAG, "Show");
    this.camera  = new TopDownCamera();
    this.scene   = Scene.open(G.db.getPlayerStartPosition().getFileHandler().file());
    this.terrain = scene.getTerrain();
    
    terrain.buildSectors();
    camera.position.set(terrain.getColumns()/2, 17, terrain.getRows()/2);
    camera.lookAt(terrain.getColumns()/2, 0, terrain.getRows()/2 - 3);
  }
  
  @Override
  public void dispose() {
    this.terrain.dispose();
    camera = null;
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
    scene.render(camera);
  }
  
  @Override
  public void resize(int arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  
  
}
