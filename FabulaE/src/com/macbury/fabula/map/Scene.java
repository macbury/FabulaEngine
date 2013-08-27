package com.macbury.fabula.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.Terrain;
import com.thesecretpie.shader.ShaderManager;

public class Scene implements Disposable {
  private static final String MAIN_FRAME_BUFFER = "MAIN_FRAME_BUFFER";
  private Lights           lights;
  private DirectionalLight sunLight;
  private Terrain          terrain;
  private SkyBox           skyBox;
  private String           name;
  private ShaderManager sm;
  private String TAG = "Scene";
  
  public Scene(int width, int height) {
    skyBox = ResourceManager.shared().getSkyBox("SKYBOX_DAY");
    lights = new Lights();
    lights.ambientLight.set(1f, 1f, 1f, 1f);
    sunLight = new DirectionalLight();
    sunLight.set(1f, 1f, 1f, -1f, -2f, -1f);
    sunLight.set(Color.WHITE, new Vector3(-0.008f, -0.716f, -0.108f));
    lights.add(sunLight);
    
    this.terrain = new Terrain(width, height);
    
    this.sm = G.shaders;
    this.sm.createFB(MAIN_FRAME_BUFFER);
  }
  
  public Terrain getTerrain() {
    return this.terrain;
  }
  
  public void render(Camera camera) {
    sm.beginFB(MAIN_FRAME_BUFFER);
      this.terrain.render(camera, this.lights);
      ///this.skyBox.render(camera);
    sm.endFB();
    
    sm.begin("SHADER_BLOOM");
      sm.renderFB(MAIN_FRAME_BUFFER);
    sm.end();
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
    return true;
  }

  @Override
  public void dispose() {
    this.terrain.dispose();
    this.skyBox.dispose();
  }
}
