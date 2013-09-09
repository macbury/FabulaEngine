package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PositionComponent extends Component {
  private Vector3 position;
  
  public PositionComponent(Vector3 pos) {
    this.position = pos;
  }
  
  public PositionComponent(Vector2 pos) {
    this.position = new Vector3(pos.x, 0, pos.y);
  }

  public Vector3 getVector() {
    return this.position;
  }

  public float getX() {
    return this.position.x;
  }

  public float getY() {
    return this.position.y;
  }
  
  public float getZ() {
    return this.position.z;
  }

  public void setPosition(Vector2 pos) {
    this.position = new Vector3(pos.x, 0, pos.y);
  }
}
