package com.macbury.fabula.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.manager.ResourceManager;

public class Tile {
  private TextureRegion textureRegion;
  private static int GID_COUNTER = 0;
  int y = 0;
  int gid = 0;
  public Tile(float x, float y, float z) {
    gid = GID_COUNTER++;
    
    Texture texture = ResourceManager.shared().getTexture("TEXTURE_DEBUG");
    
    //texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    textureRegion = new TextureRegion(texture, gid % 10 * 32, 0, 32, 32);
    //this.setPosition(x,y,z);
    
    this.y = 0;
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
}
