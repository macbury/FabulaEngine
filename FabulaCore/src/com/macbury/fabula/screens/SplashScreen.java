package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.macbury.fabula.manager.GameManager;

public class SplashScreen extends BaseScreen {
  
  public SplashScreen(GameManager manager) {
    super(manager);
  }
  
  @Override
  public void show() {
    
  }
  
  @Override
  public void dispose() {
    
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
    Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
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
