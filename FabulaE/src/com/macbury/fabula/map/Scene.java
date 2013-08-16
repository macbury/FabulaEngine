package com.macbury.fabula.map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.terrain.Terrain;

public class Scene {
  private Lights           lights;
  private DirectionalLight sunLight;
  private Terrain          terrain;
  
  public Scene(int width, int height) {
    lights = new Lights();
    lights.ambientLight.set(1f, 1f, 1f, 1f);
    sunLight = new DirectionalLight();
    sunLight.set(1f, 1f, 1f, -1f, -2f, -1f);
    sunLight.set(Color.WHITE, new Vector3(-0.008f, -0.716f, -0.108f));
    lights.add(sunLight);
    
    this.terrain = new Terrain(width, height);
  }
  
  public Terrain getTerrain() {
    return this.terrain;
  }
  
  public void render(Camera camera) {
    this.terrain.render(camera, this.lights);
  }
  
  public DirectionalLight getSunLight() {
    return this.sunLight;
  }
  
  public Lights getLights() {
    return this.lights;
  }
}
