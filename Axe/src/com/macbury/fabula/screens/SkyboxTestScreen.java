package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.macbury.fabula.graphics.SkyBox;
import com.macbury.fabula.manager.GameManager;

public class SkyboxTestScreen extends BaseScreen {
  
  private SkyBox skybox;
  private PerspectiveCamera camera;

  public SkyboxTestScreen(GameManager manager) {
    super(manager);
    this.skybox = new SkyBox("day");
    camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    
    camera.position.x = 0.0f;
    camera.position.y = 0.0f;
    camera.position.z = 1.0f;
    camera.near = 0.1f;
    camera.far = 500.0f;
    camera.lookAt(0, 0, 0);
    camera.update();
    
    Gdx.input.setInputProcessor(new CameraInputController(camera));
  }

  @Override
  public void dispose() {
    this.skybox.dispose();
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
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    this.camera.update();
    this.skybox.render(camera);
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
