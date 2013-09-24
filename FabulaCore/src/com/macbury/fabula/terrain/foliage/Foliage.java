package com.macbury.fabula.terrain.foliage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.macbury.fabula.map.Scene;

public class Foliage {
  private Material material;
  private FoliageShader shader;
  
  private float time      = 0.0f;
  private float amplitude = 0.04f;
  private float speed     = 6f;
  private Scene scene;
  
  public Foliage(Scene scene) {
    this.scene    = scene;
    this.material = new Material(TextureAttribute.createDiffuse(getTexture()));
    this.shader   = new FoliageShader(this);
  }
  
  public Texture getTexture() {
    return this.scene.getTerrain().getFoliageSet().getTexture();
  }
  
  public TextureDescriptor getTextureId() {
    TextureAttribute textureAttr = (TextureAttribute) material.get(TextureAttribute.Diffuse);
    return textureAttr.textureDescription;
  }
  
  public void update(float delta) {
    this.time += delta * speed;
  }

  public Material getMaterial() {
    return material;
  }

  public FoliageShader getShader() {
    return shader;
  }

  public float getTime() {
    return time;
  }

  public float getAmplitude() {
    return amplitude;
  }

  public float getSpeed() {
    return speed;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public void setShader(FoliageShader shader) {
    this.shader = shader;
  }

  public void setTime(float time) {
    this.time = time;
  }

  public void setAmplitude(float amplitude) {
    this.amplitude = amplitude;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }
}
