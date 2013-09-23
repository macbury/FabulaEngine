package com.macbury.fabula.terrain.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GridVertex {
  public Vector3 position;
  public Color   color;
  public Vector3 normal;
  public Vector2 tilePosition;
  public Vector2 textureCordinates;
  public boolean passable;
  
  public GridVertex() {
    this.position          = new Vector3(0, 0, 0);
    this.color             = new Color(255, 255, 255, 255);
    this.normal            = new Vector3(0,0,0);
    this.tilePosition      = new Vector2(0,0);
    this.textureCordinates = new Vector2(0,0);
    this.passable          = true;
  }
}
