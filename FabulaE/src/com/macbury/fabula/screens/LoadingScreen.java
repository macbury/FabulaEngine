package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;

public class LoadingScreen extends BaseScreen {
  
  private static final String TAG = "LoadingScreen";

  public LoadingScreen(GameManager manager) {
    super(manager);
    try {
      ResourceManager.shared().load();
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    if (ResourceManager.shared().isLoading()) {
      Gdx.app.log(TAG, "Loading assets progress: " + ResourceManager.shared().getLoadingProgress());
    } else {
      this.gameManager.setScreen(new MeshScreen(this.gameManager));
    }
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
