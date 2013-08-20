package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.AutoTiles.Types;

public class Tile {
  public enum Type {
    Normal, CornerBottomLeft, CornerBottomRight, CornerTopLeft, CornerTopRight
  }

  private TextureRegion textureRegion;
  public static int GID_COUNTER  = 0;
  private Type type               = Type.Normal;
  private Vector3 position;
  float y1 = 0; // |1----3|
  float y2 = 0; // |      |
  float y3 = 0; // |      |
  float y4 = 0; // |2----4|
  
  int gid = 0;
  private AutoTile autoTile; 
  
  public Tile(float x, float y, float z) {
    gid      = GID_COUNTER++;
    position = new Vector3(x,y,z);
    setY(y);
  }
  
  public void setY(float ny) {
    this.position.y = this.y1 = this.y2 = this.y3 = this.y4 = ny;
  }
  
  public TextureRegion getTextureRegion() {
    return autoTile.getRegion();
  }

  public float getY() {
    return this.position.y;
  }
  
  public int getId() {
    return this.gid;
  }

  public void setY(int i) {
    this.position.y = this.y1 = this.y2 = this.y3 = this.y4 = i;
  }
  
  public float getY1() {
    return y1;
  }

  public float getY2() {
    return y2;
  }

  public float getY3() {
    return y3;
  }

  public float getY4() {
    return y4;
  }

  public void calculateHeight() {
    this.position.y = (y1 + y2 + y3 + y4 + this.position.y) / 5;
  }
  
  public void setY1(float y1) {
    this.y1 = y1;
    calculateHeight();
  }

  public void setY2(float y2) {
    this.y2 = y2;;
    calculateHeight();
  }

  public void setY3(float y3) {
    this.y3 = y3;
    calculateHeight();
  }

  public void setY4(float y4) {
    this.y4 = y4;
    calculateHeight();
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public float getX() {
    return position.x;
  }

  public float getZ() {
    return position.z;
  }

  public void setAutoTile(AutoTile autoTile) {
    this.autoTile = autoTile;
  }
  
  public AutoTile getAutoTile() {
    return autoTile;
  }

  public Types getAutoType() {
    return autoTile.getType();
  }
  
  public boolean haveTheSameAutoTileAndIsNotSimple(AutoTile at) {
    return haveTheSameAutoTile(at) && !at.getAutoTiles().isSimple();
  }
  
  public boolean haveDiffrentAutotileThan(AutoTile at) {
    return !haveTheSameAutoTile(at);
  }

  public boolean haveTheSameAutoTile(AutoTile at) {
    return autoTile.getAutoTiles().equals(at.getAutoTiles());
  }

}
