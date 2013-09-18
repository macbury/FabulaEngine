package com.macbury.fabula.test;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.graphics.SkyBox;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.screens.BaseScreen;
import com.thesecretpie.shader.ShaderManager;

public class ShaderTestScreen extends BaseScreen {
  private ShaderManager sm;
  private Mesh cube;
  AssetManager am = new AssetManager();
  Matrix4 projection = new Matrix4();
  Matrix4 view = new Matrix4();
  Matrix4 model = new Matrix4();
  Matrix4 combined = new Matrix4();
  Vector3 axis = new Vector3(1, 0, 1).nor();
  float angle = 45;
  private SkyBox skybox;
  private PerspectiveCamera camera;
  
  public ShaderTestScreen(GameManager manager) {
    super(manager);
    
    this.sm = G.shaders;
    cube    = Shapes.genCube();
   // this.skybox = ResourceManager.shared().getSkyBox("SKYBOX_DAY");
    this.camera = new PerspectiveCamera();
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void hide() {

  }
  
  @Override
  public void pause() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void render(float arg0) {
    this.camera.update();
    skybox.render(this.camera);
  }
  
  @Override
  public void resize(int arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
  
  }
  
  @Override
  public void show() {
  }
  
}
