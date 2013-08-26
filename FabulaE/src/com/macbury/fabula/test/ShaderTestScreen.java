package com.macbury.fabula.test;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;
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
  
  public ShaderTestScreen(GameManager manager) {
    super(manager);
    
    this.sm = ResourceManager.shared().getShaderManager();
    cube    = Shapes.genCube();
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
    
    angle += Gdx.graphics.getDeltaTime() * 40.0f;
    float aspect = Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
    projection.setToProjection(1.0f, 20.0f, 60.0f, aspect);
    view.idt().trn(0, 0, -2.0f);
    model.setToRotation(axis, angle);
    combined.set(projection).mul(view).mul(model);

    Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
    
    sm.beginFB("bloom_fb");
      sm.begin("empty");
        sm.setUniformMatrix("u_worldView", combined);
        cube.render(sm.getCurrent(), Gdx.gl20.GL_TRIANGLES);
      sm.end();
    sm.endFB();
    
    sm.begin("SHADER_BLOOM");
    sm.renderFB("bloom_fb");
    sm.end();
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
    sm.createFB("bloom_fb");
  }
  
}
