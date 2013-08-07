package com.macbury.fabula.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class TopDownCamera extends PerspectiveCamera {
  private float rotation = 70;

  public TopDownCamera() {
    super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    near = 0.1f;
    far  = 300f;
    
    setRotation(70);
  }
  
  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
    rotate(this.rotation, -1, 0, 0);
  }
}
