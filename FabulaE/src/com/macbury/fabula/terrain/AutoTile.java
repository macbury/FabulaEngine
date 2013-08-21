package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AutoTile extends SimpleTile {
  private AutoTiles.Types type;

  
  private AutoTiles autoTiles;
  
  public AutoTile(TextureRegion textureRegion, AutoTiles.Types type) {
    this.textureRegion = textureRegion;
    this.type          = type;
  }

  public TextureRegion getRegion() {
    return this.textureRegion;
  }

  

  public void setAutoTiles(AutoTiles autoTiles) {
    this.autoTiles = autoTiles;
  }
  
  public AutoTiles getAutoTiles() {
    return autoTiles;
  }

  public String getName() {
    return autoTiles.getName()+"_"+getIndex();
  }
  
  public AutoTiles.Types getType() {
    return type;
  }

  public long getCornerMask(int corner) {
    return autoTiles.getMaskForTypeAndIndex(type, corner);
  }

}
