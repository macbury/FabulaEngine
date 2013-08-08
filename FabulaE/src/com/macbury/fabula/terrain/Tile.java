package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.manager.ResourceManager;

public class Tile {
  public enum Type {
    Normal, CornerBottomLeft, CornerBottomRight, CornerTopLeft, CornerTopRight
  }

  private TextureRegion textureRegion;
  private static int GID_COUNTER  = 0;
  private Type type               = Type.Normal;
  float y  = 0;
  float y1 = 0; // |1----3|
  float y2 = 0; // |      |
  float y3 = 0; // |      |
  float y4 = 0; // |2----4|
  
  int gid = 0; 
  
  public Tile(float x, float y, float z) {
    gid = GID_COUNTER++;
    
    Texture texture = ResourceManager.shared().getTexture("TEXTURE_DEBUG");
    
    //texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    textureRegion = new TextureRegion(texture, gid % 16 * 64, 0, 64, 64);
    //this.setPosition(x,y,z);
    
    setY(y);
  }
  
  public void setY(float ny) {
    this.y = this.y1 = this.y2 = this.y3 = this.y4 = ny;
  }

  
  
  public TextureRegion getTextureRegion() {
    return textureRegion;
  }
  
  /*public void setPosition(float x, float y, float z) {
    this.setX(x);
    this.setY(y);
    this.setZ(z);
  }

  public void setX(float x) {
    this.decal.setX(x);
  }
  
  public void setY(float y) {
    this.decal.setY(y);
  }
  
  public void setZ(float z) {
    this.decal.setZ(z);
  }

  public Decal getDecal() {
    return decal;
  }*/
  

  public float getY() {
    return y;
  }
  
  public int getId() {
    return this.gid;
  }

  public void setY(int i) {
    y = 1;
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
    this.y = (y1 + y2 + y3 + y4) / 4;
  }
  
  public void setY1(float y1) {
    this.y1 = y1;
    calculateHeight();
  }

  public void setY2(float y2) {
    this.y2 = y2;
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

}
