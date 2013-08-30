package com.macbury.fabula.map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.Terrain;
import com.thesecretpie.shader.ShaderManager;

@Root(name="map")
public class Scene implements Disposable {
  @Attribute
  private String           name;
  @Attribute
  private int              uid;
  @Element
  private Terrain          terrain;
  
  private static final String MAIN_FRAME_BUFFER = "MAIN_FRAME_BUFFER";
  private static final String TAG               = "Scene";
  
  private Lights           lights;
  private DirectionalLight sunLight;
  
  private SkyBox           skyBox;
  private ShaderManager sm;
  
  private boolean debug;
  private String finalShader;
  
  public Scene(String name, int uid, int width, int height) {
    //skyBox = ResourceManager.shared().getSkyBox("SKYBOX_DAY");
    this.name = name;
    this.uid  = uid;
    lights    = new Lights();
    lights.ambientLight.set(1f, 1f, 1f, 0.5f);
    sunLight = new DirectionalLight();
    sunLight.set(Color.WHITE, new Vector3(-0.008f, -0.716f, -0.108f));
    lights.add(sunLight);
    
    this.terrain = new Terrain(width, height);
    this.terrain.setTileset("outside");
    this.finalShader = "default";
    this.sm = G.shaders;
    this.sm.createFB(MAIN_FRAME_BUFFER);
  }
  
  public Terrain getTerrain() {
    return this.terrain;
  }
  
  public void render(Camera camera) {
    GL20 gl = Gdx.graphics.getGL20();
    Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    sm.beginFB(MAIN_FRAME_BUFFER);
      
      this.terrain.render(camera, this.lights);
      if (debug) {
        sm.debugToDisk(MAIN_FRAME_BUFFER, "assets/debug.png");
      }
    sm.endFB();
    
    
    sm.begin(finalShader); 
      sm.renderFB(MAIN_FRAME_BUFFER);
    sm.end();
    
    this.debug = false;
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
  
  public boolean save() {
    try {
      GameDatabase.save(this, "maps/"+this.uid+".map");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }
  
  public void debug() {
    this.debug = true;
  }
  
  @Override
  public void dispose() {
    this.terrain.dispose();
    this.skyBox.dispose();
  }

  public String getFinalShader() {
    return finalShader;
  }

  public void setFinalShader(String finalShader) {
    this.finalShader = finalShader;
  }
  
}
