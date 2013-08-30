package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.test.AutoTileTestScreen;

public class LoadingScreen extends BaseScreen {
  
  private static final String TAG = "LoadingScreen";

  public LoadingScreen(GameManager manager) {
    super(manager);
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
  public void render(float arg0) {
    this.gameManager.setScreen(new AutoTileTestScreen(this.gameManager));
  }
  
  @Override
  public void resize(int arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void show() {
    // TODO Auto-generated method stub
    
  }
  
}
