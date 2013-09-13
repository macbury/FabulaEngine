package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.macbury.fabula.terrain.Tile;

public class DecalComponent extends Component {
  private Decal decal;
  private boolean visible;
  
  public DecalComponent() {
    this.visible = false;
  }
  
  public DecalComponent(Decal nd) {
    this.decal = nd;
    this.visible = false;
  }
  
  public DecalComponent(TextureRegion textureRegion) {
    setTextureRegion(textureRegion);
  }
  
  public void setTextureRegion(TextureRegion tr) {
    if (this.decal == null) {
      this.decal = Decal.newDecal(tr, true);
    } else {
      this.decal.setTextureRegion(tr);
    }
    this.decal.setWidth((float)tr.getRegionWidth() / Tile.TILE_SIZE_IN_PIXELS);
    this.decal.setHeight((float)tr.getRegionHeight() / Tile.TILE_SIZE_IN_PIXELS);
  }

  public Decal getDecal() {
    return decal;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
