package com.macbury.fabula.editor.brushes;

import com.badlogic.gdx.math.Vector2;
import com.macbury.fabula.terrain.Terrain;

public abstract class Brush {
  protected int size         = 4;
  protected float power      = 0.1f;
  protected Vector2 position = new Vector2(0, 0);
  protected Terrain terrain;
  
  public Brush(Terrain terrain) {
    this.terrain = terrain;
  }
  
  public int getSize() {
    return size;
  }
  public void setSize(int size) {
    this.size = size;
  }
  public float getPower() {
    return power;
  }
  public void setPower(float power) {
    this.power = power;
  }
  public Vector2 getPosition() {
    return position;
  }
  public void setPosition(Vector2 position) {
    this.position = position;
  }
  
  public abstract void onApply();

  public void setPosition(float x, float z) {
    position.x = x;
    position.y = z;
  }
}
