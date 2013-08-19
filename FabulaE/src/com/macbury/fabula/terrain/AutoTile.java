package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AutoTile {
  private TextureRegion textureRegion;
  private AutoTiles.Types type;

  private int index;
  private AutoTiles autoTiles;
  
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

  public void setAutoTiles(AutoTiles autoTiles) {
    this.autoTiles = autoTiles;
  }
  
  public AutoTiles getAutoTiles() {
    return autoTiles;
  }

  public String getName() {
    return autoTiles.getName();
  }
  
  public AutoTiles.Types getType() {
    return type;
  }

  public long getCornerMask(int corner) {
    return autoTiles.getMaskForTypeAndIndex(type, corner);
  }

}
