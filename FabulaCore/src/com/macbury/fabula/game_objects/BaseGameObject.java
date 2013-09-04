package com.macbury.fabula.game_objects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class BaseGameObject implements Disposable, Poolable {
  private Vector3 position;
  private Vector3 rotation;
  
  public BaseGameObject() {
    this.position = new Vector3();
    this.rotation = new Vector3();
    this.initialize();
  }
  
  public abstract void initialize();
  
  public Vector3 getPosition() {
    return position;
  }

  public void setPosition(Vector3 position) {
    this.position = position;
  }

  public Vector3 getRotation() {
    return rotation;
  }

  public void setRotation(Vector3 rotation) {
    this.rotation = rotation;
  }

  @Override
  public void reset() {
    this.position.set(0,0,0);
    this.rotation.set(0,0,0);
  }
}
