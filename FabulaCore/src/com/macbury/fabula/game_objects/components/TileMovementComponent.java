package com.macbury.fabula.game_objects.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.macbury.fabula.utils.BMath;

public class TileMovementComponent extends VectorComponent {
  public static final Vector3 DIRECTION_LEFT  = new Vector3(-1f, 0f, 0f);
  public static final Vector3 DIRECTION_RIGHT = new Vector3(1f, 0f, 0f);
  
  public static final Vector3 DIRECTION_UP    = new Vector3(0f, 0f, -1f);
  public static final Vector3 DIRECTION_DOWN  = new Vector3(0f, 0f, 1f);
  
  private boolean moving = false;
  private float   speed  = 3f;
  
  private Vector3 startPosition     = null;
  private Vector3 finalPosition     = null;
  private float alpha;
  private Vector3 tempPosition;

  public TileMovementComponent() {
    super(Vector3.Zero);
    finalPosition     = new Vector3();
    tempPosition      = new Vector3();
    startPosition     = new Vector3();
  }

  public boolean isMoving() {
    return moving;
  }

  public void setMoving(boolean moving) {
    this.moving = moving;
  }
  
  public void startMoving(Vector3 sp) {
    if (!this.moving) {
      this.alpha    = 0.0f;
      startPosition.set(sp);
      finalPosition.set(startPosition).add(getVector());
    }
    setMoving(true);
  }

  public void addDelta(float delta) {
    this.alpha += speed * delta;
    if (this.alpha >= 1.0f) {
      this.alpha = 1.0f;
    }
  }

  public boolean moveFinished() {
    return this.alpha >= 1f;
  }

  public Vector3 getCurrentPosition() {
    return tempPosition.set(startPosition).lerp(finalPosition, alpha);
  }

}
