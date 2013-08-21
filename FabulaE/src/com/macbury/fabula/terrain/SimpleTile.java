package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SimpleTile {
  protected TextureRegion textureRegion;
  protected Tileset tileset;
  protected int index;
  
  public void setIndex(int i) {
    index = i;
  }
  
  public int getIndex() {
    return index;
  }
  
  public String getName() {
    return "simple_"+getIndex();
  }
}
