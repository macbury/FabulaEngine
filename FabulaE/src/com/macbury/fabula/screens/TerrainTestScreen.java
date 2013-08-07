package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.utils.TopDownCamera;

public class TerrainTestScreen extends BaseScreen {
  
  private TopDownCamera camera;
  private ShaderProgram meshShader;
  private Texture texture;
  private TextureRegion textureRegion;
  private FPSLogger fps;

  private int heightmap[][] = new int[][] {
    { 0, 0, 1, 1, 2 },
    { 0, 1, 2, 2, 3 },
    { 0, 1, 3, 2, 3 },
    { 0, 1, 2, 1, 2 },
    { 0, 0, 1, 1, 1 }
  };
  
  public TerrainTestScreen(GameManager manager) {
    super(manager);
    
    fps                   = new FPSLogger();
    String vertexShader   = Gdx.files.internal("data/shaders/mesh.vert").readString();
    String fragmentShader = Gdx.files.internal("data/shaders/mesh.frag").readString();
    
    this.camera = new TopDownCamera();
    camera.position.set(0, 0, 32);
    camera.lookAt(0, 0, 0);
    
    CameraInputController cont = new CameraInputController(camera);
    Gdx.input.setInputProcessor(cont);
    meshShader = new ShaderProgram(vertexShader, fragmentShader);
    if (!meshShader.isCompiled())
      throw new IllegalStateException(meshShader.getLog());
    
    texture = ResourceManager.shared().getTexture("TEXTURE_DEBUG");
    //texture.setFilter(Filter.NearestNeighbour, Filter.NearestNeighbour);
    textureRegion = new TextureRegion(texture, 32, 32, 32, 32);
  }

  @Override
  public void render(float arg0) {
    camera.update();
    fps.log();
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
