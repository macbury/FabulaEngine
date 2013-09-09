package com.macbury.fabula.map;

import java.io.File;

import org.simpleframework.xml.Serializer;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.system.DecalRenderingSystem;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.persister.ScenePersister;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;
import com.macbury.fabula.utils.CameraGroupWithCustomShaderStrategy;
import com.thesecretpie.shader.ShaderManager;

public class Scene implements Disposable {
  public static final String MAIN_FRAME_BUFFER = "MAIN_FRAME_BUFFER";
  private static final String TAG               = "Scene";
  public static String FILE_EXT                 = "red";
  private String           name;
  private String           uid;
  private Terrain          terrain;
  private String           finalShader;

  private Lights           lights;
  private DirectionalLight sunLight;
 
  private ShaderManager    sm;
  
  private boolean debug;
  private DecalBatch decalBatch;
  private PerspectiveCamera perspectiveCamera;
  private Decal startPositionDecal;
  private ModelBatch modelBatch;
  private World objectsWorld;
  private DecalRenderingSystem decalRenderingSystem;

  public Scene(String name, String uid, int width, int height) {
    this.name = name;
    this.uid  = uid;
    lights    = new Lights();
    lights.ambientLight.set(1f, 1f, 1f, 0.5f);
    sunLight  = new DirectionalLight();
    sunLight.set(Color.WHITE, new Vector3(-0.008f, -0.716f, -0.108f));
    lights.add(sunLight);
    
    this.terrain      = new Terrain(width, height);
    this.terrain.setTileset("outside");
    this.finalShader  = "default";
    this.sm           = G.shaders;
    
    this.objectsWorld         = new World();
  }
  
  public void initialize() {
    this.decalBatch           = new DecalBatch(new CameraGroupWithCustomShaderStrategy(perspectiveCamera));
    this.decalRenderingSystem = this.objectsWorld.setSystem(new DecalRenderingSystem(decalBatch, perspectiveCamera), true);
    
    this.terrain.buildSectors();
    this.objectsWorld.initialize();
  }
  
  public Terrain getTerrain() {
    return this.terrain;
  }
  
  public void render() {
    this.objectsWorld.setDelta(Gdx.graphics.getDeltaTime());
    this.objectsWorld.process();
    
    sm.beginFB(MAIN_FRAME_BUFFER);
      getModelBatch().begin(perspectiveCamera);
        this.terrain.render(perspectiveCamera, lights, getModelBatch());
      getModelBatch().end();
      renderDebugInfo();
      this.decalRenderingSystem.process();
    sm.endFB();
    
    sm.begin(finalShader); 
      sm.renderFB(MAIN_FRAME_BUFFER);
    sm.end();
  }
  
  private void renderDebugInfo() {
    if (debug) {
      PlayerStartPosition psp = G.db.getPlayerStartPosition();
      if (psp != null && psp.getUUID().equalsIgnoreCase(uid)) {
        Tile tile = terrain.getTile(psp.getTileX(), psp.getTileY());
        startPositionDecal.getPosition().set(psp.getTileX()+0.5f, tile.getY()+0.5f, psp.getTileY()+0.5f);
        startPositionDecal.lookAt(perspectiveCamera.position, perspectiveCamera.up.cpy().nor());
        this.decalBatch.add(startPositionDecal);
      }
    }
  }

  public void setCamera(PerspectiveCamera camera) {
    this.perspectiveCamera = camera;
  }
  
  public DirectionalLight getSunLight() {
    return this.sunLight;
  }
  
  public Lights getLights() {
    return this.lights;
  }
  
  public boolean haveName() {
    return name != null;
  }
  
  public static Scene open(File file) {
    Serializer serializer = GameDatabase.getDefaultSerializer();
    try {
      ScenePersister scenePersister = serializer.read(ScenePersister.class, file);
      Scene scene  = scenePersister.getScene();
      scene.initialize();
      return scene;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public boolean save() {
    long start = System.currentTimeMillis();
    try {
      GameDatabase.save(new ScenePersister(this), "maps/"+this.name+"."+FILE_EXT);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    long time = (System.currentTimeMillis() - start);
    Gdx.app.log(TAG, "Saved in: "+time + " miliseconds");
    return true;
  }
  
  @Override
  public void dispose() {
    this.terrain.dispose();
    //this.skyBox.dispose();
    this.decalBatch.dispose();
    this.modelBatch.dispose();
  }

  public String getFinalShader() {
    return finalShader;
  }

  public void setFinalShader(String finalShader) {
    this.finalShader = finalShader;
  }


  public void setName(String text) {
    this.name = text;
  }

  public String getName() {
    return this.name;
  }

  public String getUID() {
    return this.uid;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
    Texture startPositionTexture = new Texture(Gdx.files.classpath("com/macbury/icon/start_position.png"));
    this.startPositionDecal = Decal.newDecal(1,1,new TextureRegion(startPositionTexture), true);
    this.startPositionDecal.setWidth(1);
    this.startPositionDecal.setHeight(1);
    //this.startPositionDecal.setBlending(GL10.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    
    startPositionDecal.getPosition().set(15, 0.5f, 20);
  }
  
  public ModelBatch getModelBatch() {
    if (modelBatch == null) {
      this.modelBatch   = new ModelBatch();
    }
    return modelBatch;
  }
}
