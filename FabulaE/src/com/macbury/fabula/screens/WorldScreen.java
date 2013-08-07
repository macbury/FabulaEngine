package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.utils.TopDownCamera;

public class WorldScreen extends BaseScreen {
  private static final String TAG = "WorldScreen";
  private TopDownCamera camera;
  private Terrain terrain;
  private BitmapFont font;
  private SpriteBatch guiBatch;
  
  public WorldScreen(GameManager manager) {
    super(manager);
    
    //guiCamera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    guiBatch = new SpriteBatch();
    
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/Courier New.ttf"));
    font = generator.generateFont(16);
    this.camera = new TopDownCamera();
    Gdx.app.log(TAG, "Initialized screen");
    this.terrain = new Terrain(this, 100, 100);
    
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
    //guiBatch.setProjectionMatrix(camera.combined);
    
    guiBatch.begin();
    font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    font.draw(guiBatch, "Visible sector count: "+ this.terrain.getVisibleSectorCount(), 20f, 90f);
    font.draw(guiBatch, "Sector count: "+ this.terrain.getTotalSectorCount(), 20f, 60f);
    font.draw(guiBatch, "FPS: "+ Gdx.graphics.getFramesPerSecond(), 20f, 30f);
    guiBatch.end();
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
