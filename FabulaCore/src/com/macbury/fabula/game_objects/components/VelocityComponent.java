package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

public class VelocityComponent extends PositionComponent {
  private Vector3 tempScaledVector;
  
  public VelocityComponent(Vector3 pos) {
    super(pos);
    
    tempScaledVector = new Vector3();
  }

  public Vector3 getScaledVector(float delta) {
    return tempScaledVector.set(vector).scl(delta);
  }
}
