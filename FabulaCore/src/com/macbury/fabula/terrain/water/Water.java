package com.macbury.fabula.terrain.water;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.graphics.CubeMap;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.map.Scene;

public class Water implements Disposable {
  private Scene scene;
  private WaterShader shader;
  
  private float         waterAnimationSpeed     = 0.05f;
  private float         amplitudeWave   = 0.1f;
  private float         angleWave       = 0.0f;
  private float         angleWaveSpeed  = 2.0f;
  private float         alpha           = 0.7f;
  private float         mix             = 0.6f;
  private Animation     animation;
  private Material      waterMaterial;
  private String        regionsName;
  
  public Water(Scene scene) {
    this.scene  = scene;
    this.shader = new WaterShader(this);

    setWaterTexture("water");
  }
  
  public void setWaterTexture(String name) {
    this.regionsName = name;
    this.animation   = new Animation(waterAnimationSpeed, G.db.getLiquidAtlas().findRegions(name));
    this.animation.setPlayMode(Animation.LOOP);
    
    this.waterMaterial = new Material(TextureAttribute.createDiffuse(getTexture()));
  }

  public void update(float delta) {
    this.angleWave += delta * angleWaveSpeed;
  }
  
  public TextureDescriptor getWaterTextureId() {
    TextureAttribute textureAttr = (TextureAttribute) waterMaterial.get(TextureAttribute.Diffuse);
    return textureAttr.textureDescription;
  }
  
  public Material getMaterial() {
    return waterMaterial;
  }
  
  public Texture getTexture() {
    return this.animation.getKeyFrame(0).getTexture();
  }
  
  public float getAmplitudeWave() {
    return amplitudeWave;
  }

  public float getAngleWaveSpeed() {
    return angleWaveSpeed;
  }

  public void setAmplitudeWave(float amplitudeWave) {
    this.amplitudeWave = amplitudeWave;
  }
  
  public CubeMap getCubeMap() {
    if (this.scene.getSkybox() != null) {
      return this.scene.getSkybox().getCubeMap();
    }
    return null;
  }
  
  public void setAngleWaveSpeed(float speed) {
    this.angleWaveSpeed = speed;
  }

  @Override
  public void dispose() {
    this.scene = null;
  }

  public TextureRegion getCurrentRegion() {
    return this.animation.getKeyFrame(angleWave);
  }

  public float getAngleWave() {
    return this.angleWave;
  }

  public WaterShader getShader() {
    return this.shader;
  }

  public String getWaterMaterial() {
    return regionsName;
  }

  public float getWaterAnimationSpeed() {
    return waterAnimationSpeed;
  }

  public void setWaterAnimationSpeed(float waterAnimationSpeed) {
    this.waterAnimationSpeed = waterAnimationSpeed;
    setWaterTexture(regionsName);
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public float getMix() {
    return this.mix ;
  }

  public void setMix(float mix) {
    this.mix = mix;
  }
  
}
