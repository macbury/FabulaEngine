package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.terrain.AutoTiles.Types;

public class Tile {
  public enum Type {
    Normal, CornerBottomLeft, CornerBottomRight, CornerTopLeft, CornerTopRight
  }
  
  public enum TypeSlope {
    None, Down, Up, Left, Right, CornerBottomLeft, CornerBottomRight, CornerTopLeft, CornerTopRight
  }
  
  private TextureRegion textureRegion;
  public static int GID_COUNTER  = 0;
  private Type type              = Type.Normal;
  private TypeSlope slope        = TypeSlope.None;
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
    int slopeMask = computeSlope();
    switch (slopeMask) {
      case 323:
        this.slope = TypeSlope.Down;
      break;
      case 40:
        this.slope = TypeSlope.Up;
      break;
      case 6:
        this.slope = TypeSlope.Left;
      break;
      case 240:
        this.slope = TypeSlope.Right;
      break;
      case 0:
        this.slope = TypeSlope.CornerTopLeft;
      break;
      case 56:
        this.slope = TypeSlope.CornerTopRight;
      break;
      case 7:
        this.slope = TypeSlope.CornerBottomLeft;
      break;
      case 448:
        this.slope = TypeSlope.CornerBottomRight;
      break;
      default:
        this.slope = TypeSlope.None;
      break;
    }
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
  
  public TypeSlope getSlope() {
    return this.slope;
  }
  
  public int computeSlope() {
    int slopeMask   = 0;
    
    if (y1 > y2) {
      slopeMask |= 256;
    }
    
    if (y1 > y3) {
      slopeMask |= 128;
    }
    
    if (y1 > y4) {
      slopeMask |= 64;
    }
    
    if (y2 > y3) {
      slopeMask |= 32;
    }
    
    if (y2 > y4) {
      slopeMask |= 16;
    }
    
    if (y2 > y1) {
      slopeMask |= 8;
    }
    
    if (y3 > y1) {
      slopeMask |= 4;
    }
    
    if (y3 > y2) {
      slopeMask |= 2;
    }
    
    if (y3 > y4) {
      slopeMask |= 1;
    }
    
    return slopeMask;
  }
  
  public String getSlopeDebugInfo() {
    return this.slope.toString() + " Y1 = "+this.y1 + " Y2 = "+ y2 + " Y3 = " + y3 + " Y4 = " + y4 + " Slope: " + this.slope + " - " + computeSlope();
  }
}
