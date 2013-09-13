package com.macbury.fabula.game_objects.components;

import com.badlogic.gdx.math.Vector3;

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
  private Direction direction;
  
  public static enum Direction {
    Left, Right, Up, Down
  }
  
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

  public Vector3 getFinalPosition() {
    return finalPosition;
  }

  public boolean isLeft() {
    return this.direction == Direction.Left;
  }

  public boolean isRight() {
    return this.direction == Direction.Right;
  }

  public boolean isUp() {
    return this.direction == Direction.Up;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
    switch (this.direction) {
      case Left:
        setVector(DIRECTION_LEFT);
      break;
      
      case Right:
        setVector(DIRECTION_RIGHT);
      break;
      
      case Down:
        setVector(DIRECTION_DOWN);
      break;
      
      case Up:
        setVector(DIRECTION_UP);
      break;
    }
  }

}
