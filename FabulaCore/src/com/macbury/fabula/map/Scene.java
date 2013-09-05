package com.macbury.fabula.map;

import java.io.File;

import org.simpleframework.xml.Serializer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.persister.ScenePersister;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.utils.CameraGroupWithCustomShaderStrategy;
import com.thesecretpie.shader.ShaderManager;

public class Scene implements Disposable {
  private static final String MAIN_FRAME_BUFFER = "MAIN_FRAME_BUFFER";
  private static final String TAG               = "Scene";
  public static String FILE_EXT                 = "red";
  private String           name;
  private String           uid;
  private Terrain          terrain;
  private String           finalShader;

  private Lights           lights;
  private DirectionalLight sunLight;
  
  private SkyBox           skyBox;
  private ShaderManager    sm;
  
  private boolean debug;
  private DecalBatch decalBatch;
  private PerspectiveCamera perspectiveCamera;
  private Decal startPositionDecal;
  private ModelBatch modelBatch;
  
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
    this.modelBatch   = new ModelBatch();
    this.sm.createFB(MAIN_FRAME_BUFFER);
    
  }
  
  public Terrain getTerrain() {
    return this.terrain;
  }
  
  public void render() {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);;
    sm.beginFB(MAIN_FRAME_BUFFER);
      modelBatch.begin(perspectiveCamera);
        this.terrain.render(perspectiveCamera, lights, modelBatch);
        if (debug) {
          startPositionDecal.lookAt(perspectiveCamera.position, perspectiveCamera.up.cpy().nor());
          this.decalBatch.add(startPositionDecal);
        }
        this.decalBatch.flush();
      modelBatch.end();
    sm.endFB();
    
   sm.begin(finalShader); 
     sm.renderFB(MAIN_FRAME_BUFFER);
   sm.end();
  }
  
  public void setCamera(PerspectiveCamera camera) {
    this.perspectiveCamera = camera;
    this.decalBatch        = new DecalBatch(new CameraGroupWithCustomShaderStrategy(perspectiveCamera));
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
      return scenePersister.getScene();
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
}
