package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AutoTile {
  private TextureRegion textureRegion;
  private AutoTiles.Types type;
  private int index;
  public AutoTile(TextureRegion textureRegion, AutoTiles.Types type) {
    this.textureRegion = textureRegion;
    this.type          = type;
  }

  public TextureRegion getRegion() {
    return this.textureRegion;
  }

  public void setIndex(int i) {
    index = i;
  }
  
  public int getIndex() {
    return index;
  }
}
