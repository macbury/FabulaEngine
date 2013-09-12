package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.terrain.Terrain;

public abstract class VectorComponent extends Component {
  protected Vector3 vector;
  
  public VectorComponent(Vector3 pos) {
    this.vector = pos;
  }
  
  public VectorComponent(Vector2 pos) {
    this.vector = new Vector3(pos.x, 0.5f, pos.y);
  }

  public Vector3 getVector() {
    return this.vector;
  }

  public float getX() {
    return this.vector.x;
  }

  public float getY() {
    return this.vector.y;
  }
  
  public void setY(float y) {
    this.vector.y = y;
  }
  
  public float getZ() {
    return this.vector.z;
  }

  public int getTileX() {
    return Math.round(this.vector.x);
  }
  
  public int getTileZ() {
    return Math.round(this.vector.z);
  }
  
  
  public void setVector(float x, float z) {
    this.vector.x = x;
    this.vector.z = z;
  }
  
  public boolean isVisible(Terrain terrain) {
    return terrain.isVisible(vector);
  }
  
  public void setVector(Vector3 vec) {
    this.vector.set(vec);
  }
}
