package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class DecalComponent extends Component {
  private Decal decal;
  private boolean visible;
  
  public DecalComponent(Decal nd) {
    this.decal = nd;
    this.visible = false;
  }
  
  public DecalComponent(TextureRegion textureRegion) {
    Decal nd = Decal.newDecal(textureRegion, true);
    nd.setWidth(textureRegion.getRegionWidth() / 32.0f);
    nd.setHeight(textureRegion.getRegionHeight() / 32.0f);
    this.decal = nd;
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
