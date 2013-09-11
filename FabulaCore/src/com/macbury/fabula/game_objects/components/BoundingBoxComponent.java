package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class BoundingBoxComponent extends Component {
  private Vector3 size;
  private Vector3 tempVector;
  private BoundingBox boundingBox;
  
  public BoundingBoxComponent(Vector3 size) {
    this.size = size;
    this.tempVector  = new Vector3();
    this.boundingBox = new BoundingBox();
  }
  
  public BoundingBox getBoundingBox(Vector3 position) {
    boundingBox.set(position, tempVector.set(position).add(size));
    return boundingBox;
  }

}
