package com.macbury.fabula.terrain.tile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.terrain.foliage.FoliageDescriptor;
import com.macbury.fabula.terrain.tileset.AutoTiles;
import com.macbury.fabula.terrain.tileset.AutoTiles.Types;

public class Tile implements Cloneable, Comparable<Tile> {

  public enum TypeSlope {
    None, Down, Up, Left, Right, CornerBottomLeft, CornerBottomRight, CornerTopLeft, CornerTopRight, EdgeBottomRight, EdgeBottomLeft, EdgeTopLeft, EdgeTopRight
  }
  
  public static int GID_COUNTER  = 0;
  public static final float TILE_SIZE_IN_PIXELS = 32;
  private TypeSlope slope        = TypeSlope.None;
  private Vector3 position;
  float y1 = 0; // |1----3|
  float y2 = 0; // |      |
  float y3 = 0; // |      |
  float y4 = 0; // |2----4|
  
  private int gid = 0;
  private AutoTile autoTile;
  private boolean passable = true;
  private boolean liquid   = false;
  private float   liquidHeight   = 0.0f;
  private FoliageDescriptor  foliage;
  
  public Tile(float x, float y, float z) {
    gid      = GID_COUNTER++;
    position = new Vector3(x,y,z);
    setY(y);
  }
  
  public Tile(Vector3 cpy) {
    gid      = GID_COUNTER++;
    position = cpy;
    setY(cpy.y);
  }

  public void setPosition(Vector3 pos) {
    this.position = pos;
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
    calculateHeight();
  }
  
  public void setRawY(float f) {
    this.position.y = f;
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
    maskSlope();
  }
  
  private void maskSlope() {
    switch (computeSlope()) {
      case 14:
        this.slope = TypeSlope.EdgeTopRight;
      break;
      case 13:
        this.slope = TypeSlope.EdgeBottomLeft;
      break;
      case 12:
        this.slope = TypeSlope.Left;
      break;
      case 11:
        this.slope = TypeSlope.EdgeTopLeft;
      break;
      case 10:
        this.slope = TypeSlope.Up;
      break;
      case 8:
        this.slope = TypeSlope.CornerTopLeft;
      break;
      case 7:
        this.slope = TypeSlope.EdgeBottomRight;
      break;
      case 5:
        this.slope = TypeSlope.Down;
      break;
      case 4:
        this.slope = TypeSlope.CornerBottomLeft;
      break;
      case 3:
        this.slope = TypeSlope.Right;
      break;
      case 2:
        this.slope = TypeSlope.CornerTopRight;
      break;
      case 1:
        this.slope = TypeSlope.CornerBottomRight;
      break;
      default:
        this.slope = TypeSlope.None;
      break;
    }
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

  public AutoTiles getAutoTiles() {
    return autoTile.getAutoTiles();
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
  
  public float slopeAngle (float ny) {
    float angle = (float)Math.atan2(ny, 0) * MathUtils.radiansToDegrees;
    if (angle < 0) angle += 360;
    return angle;
  }
  
  public boolean isSlope(float ny) {
    float diff = Math.abs(minY() - ny);
    return diff >= 0.1f;
  }
  
  private float minY() {
    return Math.min(y1, Math.min(y2, Math.min(y3,y4)));
  }

  public void setSlope(TypeSlope slope2) {
    this.slope = slope2;
  }
  
  public int computeSlope() {
    byte slopeMask   = 0;
    
    if (isSlope(y1)) {
      slopeMask |= 1;
    }
    
    if (isSlope(y2)) {
      slopeMask |= 2;
    }
    
    if (isSlope(y3)) {
      slopeMask |= 4;
    }
    
    if (isSlope(y4)) {
      slopeMask |= 8;
    }
    
    return slopeMask;
  }
  
  public String getSlopeDebugInfo() {
    return this.slope.toString() + " Y1 = "+this.y1 + " Y2 = "+ y2 + " Y3 = " + y3 + " Y4 = " + y4 + " Slope: " + computeSlope() + " == " + this.slope;
  }
  
  public int getGid() {
    return this.gid;
  }
  
  public void setGid(int gid2) {
    this.gid = gid2;
  }
  
  @Override
  public Tile clone() {
    Tile tile = new Tile(this.position.cpy());
    tile.setY1(new Float(getY1()));
    tile.setY2(new Float(getY2()));
    tile.setY3(new Float(getY3()));
    tile.setY4(new Float(getY4()));
    
    tile.setAutoTile(this.getAutoTile());
    tile.setSlope(this.getSlope());
    tile.setFoliage(this.getFoliage());
    tile.setPassable(new Boolean(this.isPassable()));
    tile.setLiquid(new Boolean(this.isLiquid()));
    tile.setLiquidHeight(new Float(this.getLiquidHeight()));
    tile.setGid(this.getGid());
    return tile;
  }

  @Override
  public int compareTo(Tile o) {
    return o.getGid() - this.getGid();
  }

  @Override
  public String toString() {
    return "Tile: " + this.position.toString();
  }

  @Override
  public boolean equals(Object obj) {
    Tile tile = (Tile) obj;
    return tile.getGid() == this.getGid();
  }

  public void setX(int x) {
    this.position.x = x;
  }

  public void setZ(int z) {
    this.position.z = z;
  }

  public void setPassable(boolean b) {
    this.passable = b;
  }
  
  public boolean isPassable() {
    return this.passable;
  }

  public float getMinY() {
    return Math.min(y1, Math.min(y2, Math.min(y3, y4)));
  }
  
  public float getMaxY() {
    return Math.max(y1, Math.max(y2, Math.max(y3, y4)));
  }
  
  public float getHeight() {
    return Math.abs(getMaxY() - getMinY());
  }

  public boolean isLiquid() {
    return liquid;
  }

  public float getLiquidHeight() {
    return liquidHeight;
  }

  public void setLiquid(boolean l) {
    this.liquid = l;
  }

  public void setLiquidHeight(float liquidHeight) {
    this.liquidHeight = liquidHeight;
  }

  public FoliageDescriptor getFoliage() {
    return foliage;
  }

  public void setFoliage(FoliageDescriptor foliage) {
    this.foliage = foliage;
  }
  
}
