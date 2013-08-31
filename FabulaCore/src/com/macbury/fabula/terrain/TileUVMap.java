package com.macbury.fabula.terrain;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.macbury.fabula.terrain.Tile.TypeSlope;

public class TileUVMap {
  private float u;
  private float v;
  private float u2;
  private float v2;
  private float[] mat;
  
  public TileUVMap() {
    this.mat = new float[4];
  }
  
  private void rotateBySlope(TypeSlope slope) {
    this.mat[0] = u;
    this.mat[1] = v;
    this.mat[2] = u2;
    this.mat[3] = v2;

    switch (slope) {
      case Up:
        u2 = mat[0];
        v2 = mat[1];
        u  = mat[2];
        v  = mat[3];
      break;
      /*case Left:
        u  = -mat[0];
        v  = mat[1];
        u2 = -mat[2];
        v2 = mat[3];
      break;*/
    }
  }
  
  public float getU() {
    return u;
  }
  public void setU(float u) {
    this.u = u;
  }
  public float getV() {
    return v;
  }
  public void setV(float v) {
    this.v = v;
  }
  public float getU2() {
    return u2;
  }
  public void setU2(float u2) {
    this.u2 = u2;
  }
  public float getV2() {
    return v2;
  }
  public void setV2(float v2) {
    this.v2 = v2;
  }

  public void setTile(Tile tile) {
    TextureRegion region = tile.getTextureRegion();
    this.u = region.getU();
    this.v = region.getV();
    
    this.u2 = region.getU2();
    this.v2 = region.getV2();
    
    rotateBySlope(tile.getSlope());
  }
}
