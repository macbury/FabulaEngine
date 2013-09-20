package com.macbury.fabula.map;

import java.io.File;

import org.simpleframework.xml.Serializer;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.system.CollisionRenderingSystem;
import com.macbury.fabula.game_objects.system.DecalRenderingSystem;
import com.macbury.fabula.game_objects.system.EditorEntityManagmentSystem;
import com.macbury.fabula.game_objects.system.PlayerSystem;
import com.macbury.fabula.game_objects.system.TileMovementSystem;
import com.macbury.fabula.graphics.SkyBox;
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
  private ModelBatch modelBatch;
  private World objectsWorld;
  private DecalRenderingSystem decalRenderingSystem;
  private Entity playerEntity;
  private PlayerSystem playerSystem;
  private ShapeRenderer shapeRenderer;
  private CollisionRenderingSystem collisionRenderingSystem;
  private TileMovementSystem tileMovementSystem;
  private SkyBox skybox;
  private EditorEntityManagmentSystem editorEntityManagmentSystem;

  public SkyBox getSkybox() {
    return skybox;
  }

  public void setSkybox(SkyBox skybox) {
    if (this.skybox != null) {
      this.skybox.dispose();
    }
    this.skybox = skybox;
  }

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
    if (this.skybox != null) {
      this.skybox.initialize();
    }
    
    this.decalBatch           = new DecalBatch(new CameraGroupWithCustomShaderStrategy(perspectiveCamera));
    this.shapeRenderer        = new ShapeRenderer();
    
    this.playerSystem             = this.objectsWorld.setSystem(new PlayerSystem(perspectiveCamera));
    this.tileMovementSystem       = this.objectsWorld.setSystem(new TileMovementSystem(terrain));
    
    this.decalRenderingSystem     = this.objectsWorld.setSystem(new DecalRenderingSystem(decalBatch, perspectiveCamera, this.terrain), true);
    
    if (debug) {
      this.editorEntityManagmentSystem = this.objectsWorld.setSystem(new EditorEntityManagmentSystem(this.shapeRenderer, this.terrain), true);
    }
    
    this.objectsWorld.setManager(new TagManager());
    this.objectsWorld.setManager(new GroupManager());
    this.objectsWorld.initialize();
    G.factory.setWorld(this.objectsWorld);
  }
  
  public Terrain getTerrain() {
    return this.terrain;
  }
  
  public void render(float delta) {
    this.objectsWorld.setDelta(delta);
    this.objectsWorld.process();
    this.shapeRenderer.setProjectionMatrix(perspectiveCamera.combined);
    
    sm.beginFB(MAIN_FRAME_BUFFER);
      if (this.skybox != null) {
        this.skybox.render(perspectiveCamera);
      }
      
      getModelBatch().begin(perspectiveCamera);
        this.terrain.render(perspectiveCamera, getModelBatch());
      getModelBatch().end();
      
      if (this.editorEntityManagmentSystem != null) {
        editorEntityManagmentSystem.process();
      }
      
      this.decalRenderingSystem.process();
    sm.endFB();
    
    sm.begin(finalShader); 
      sm.renderFB(MAIN_FRAME_BUFFER);
    sm.end();
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
      return scene;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public boolean save() {
    long start = System.currentTimeMillis();
    ScenePersister persister = null;
    try {
      persister = new ScenePersister(this);
      GameDatabase.save(persister, getPath());
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    long time = (System.currentTimeMillis() - start);
    Gdx.app.log(TAG, "Saved in: "+time + " miliseconds");
    Gdx.app.log(TAG, "Compressed from: "+persister.getUncompressedSize()/1024+ " KB to " + persister.getCompressedSize() / 1024 + " KB");
    return true;
  }
  
  public String getPath() {
    return "maps/"+this.name+"."+FILE_EXT;
  }

  @Override
  public void dispose() {
    this.terrain.dispose();
    this.skybox.dispose();
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
  }
  
  public ModelBatch getModelBatch() {
    if (modelBatch == null) {
      this.modelBatch   = new ModelBatch();
    }
    return modelBatch;
  }

  public void spawnOrMovePlayer(Vector2 spawnPosition) {
    if (playerEntity == null) {
      playerEntity = G.factory.buildPlayer(spawnPosition);
      playerEntity.addToWorld();
      objectsWorld.getManager(TagManager.class).register(PlayerSystem.TAG_PLAYER, playerEntity);
    }
    playerEntity.getComponent(PositionComponent.class).setPosition(spawnPosition);
  }
  
  public World getWorld() {
    return objectsWorld;
  }

  public PlayerSystem getPlayerSystem() {
    return this.playerSystem;
  }

  public void setSkyboxName(String skyboxName) {
    if (skyboxName != null && skyboxName.length() > 2) {
      this.setSkybox(new SkyBox(skyboxName));
    } else {
      this.setSkybox(null);
    }
  }

  public EditorEntityManagmentSystem getEditorEntityManagmentSystem() {
    return editorEntityManagmentSystem;
  }
}
