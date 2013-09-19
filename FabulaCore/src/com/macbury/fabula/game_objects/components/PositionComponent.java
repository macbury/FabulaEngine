package com.macbury.fabula.game_objects.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PositionComponent extends VectorComponent {

  public PositionComponent(Vector2 pos) {
    super(pos);
  }

  public PositionComponent() {
    super(new Vector3());
  }

  public void setPosition(Vector2 spawnPosition) {
    this.vector.set(spawnPosition.x, 0.0f, spawnPosition.y);
  }
  
}
