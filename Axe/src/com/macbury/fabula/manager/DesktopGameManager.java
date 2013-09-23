package com.macbury.fabula.manager;

import com.badlogic.gdx.Screen;
import com.macbury.fabula.screens.CubeMapScreen;
import com.macbury.fabula.screens.GamePlayScreen;
import com.macbury.fabula.screens.GrassTestScreen;
import com.macbury.fabula.screens.SkyboxTestScreen;

public class DesktopGameManager extends GameManager {
  protected static final String TAG = "DesktopGameManager";
  
  public DesktopGameManager() {
    super(System.getProperty("user.dir")+"/assets/data/");
    this.scaledDensity = 1.0f;
  }

  @Override
  public Screen getInitialScreen() {
    return new GrassTestScreen(this);
  }
  
  @Override
  public void onNoGameData() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onNoPlayerStartPosition() {
    // TODO Auto-generated method stub
    
  }
  
}
