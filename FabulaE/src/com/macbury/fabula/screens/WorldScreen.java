package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;
import com.macbury.fabula.utils.EditorCamController;
import com.macbury.fabula.utils.TopDownCamera;

public class WorldScreen extends BaseScreen implements InputProcessor {
  private static final String TAG = "WorldScreen";
  private TopDownCamera camera;
  private Terrain terrain;
  private BitmapFont font;
  private SpriteBatch guiBatch;
  private ModelInstance cursorInstance;
  private ModelBatch modelBatch;
  //public  Lights lights;
  private EditorCamController camController;
  
  public WorldScreen(GameManager manager) {
    super(manager);
    
    //lights = new Lights();
    //lights.ambientLight.set(0.1f, 0.1f, 0.1f, 1f);
    //lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
    ModelBuilder modelBuilder = new ModelBuilder();
    Model model               = modelBuilder.createBox(1f, 0.1f, 1f,  new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
    cursorInstance            = new ModelInstance(model);
    
    modelBatch                = new ModelBatch();
    
    //guiCamera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    guiBatch = new SpriteBatch();
    
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/Courier New.ttf"));
    font = generator.generateFont(16);
    this.camera = new TopDownCamera();
    Gdx.app.log(TAG, "Initialized screen");
    this.terrain = new Terrain(this, 100, 100);
    //terrain.buildTerrainUsingImageHeightMap("data/textures/heightmap.png");
    terrain.fillEmptyTilesWithDebugTile();
    terrain.buildSectors();
    camera.position.set(0, 17, 0);
    camera.lookAt(0, 0, 0);
    
    this.camController = new EditorCamController(camera);
    Gdx.input.setInputProcessor(camController);
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
    camController.update();
    camera.update();
    
    this.terrain.render(this.camera);
    modelBatch.begin(camera);
    modelBatch.render(cursorInstance);
    modelBatch.end();
    
    //guiBatch.setProjectionMatrix(camera.combined);
    
    guiBatch.begin();
    font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    font.draw(guiBatch, "Visible sector count: "+ this.terrain.getVisibleSectorCount(), 20f, 90f);
    font.draw(guiBatch, "Sector count: "+ this.terrain.getTotalSectorCount(), 20f, 60f);
    font.draw(guiBatch, "FPS: "+ Gdx.graphics.getFramesPerSecond() + " Java Heap: " + (Gdx.app.getJavaHeap() / 1024) + " KB" + " Native Heap: " + (Gdx.app.getNativeHeap() / 1024) + " KB", 20f, 30f);
    guiBatch.end();
    
    handlePick();
  }
  
  private void handlePick() {
    if (Gdx.input.isKeyPressed(Keys.W)) {
      Ray ray     = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
      Vector3 pos = terrain.getSnappedPositionForRay(ray);
      
      if (pos != null) {
        cursorInstance.transform.setToTranslation(pos.add(-0.5f, 0.05f, -0.5f));
        //Gdx.app.log(TAG, "Picked: "+ pos.toString());
      }
      
    } else if (Gdx.input.isKeyPressed(Keys.F)) {
      Vector3 pos = new Vector3();
      cursorInstance.transform.getTranslation(pos);
      camera.lookAt(pos);
    } 
    
    if (Gdx.input.isKeyPressed(Keys.Q)) {
      Vector3 pos = new Vector3();
      cursorInstance.transform.getTranslation(pos);
      terrain.applyHill(pos, 0.1f);
    } else if (Gdx.input.isKeyPressed(Keys.A))  {
      Vector3 pos = new Vector3();
      cursorInstance.transform.getTranslation(pos);
      terrain.applyHill(pos, -0.1f);
    }
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

  @Override
  public boolean keyDown(int arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean keyTyped(char arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean keyUp(int arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean mouseMoved(int x, int y) {
    
    return true;
  }

  @Override
  public boolean scrolled(int arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    Gdx.app.log(TAG, "Pointer: " + pointer + " Button: " + button);
    return false;
  }

  @Override
  public boolean touchDragged(int arg0, int arg1, int arg2) {
    //Gdx.app.log(TAG, "Dragging");
    return false;
  }

  @Override
  public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
    // TODO Auto-generated method stub
    return false;
  }
}
