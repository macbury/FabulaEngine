package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class DecalComponent extends Component {
  private Decal decal;
  
  public DecalComponent(Decal nd) {
    this.decal = nd;
  }
  
  public DecalComponent(AtlasRegion textureRegion) {
    Decal nd = Decal.newDecal(textureRegion, true);
    nd.setWidth(textureRegion.getRegionWidth() / 32.0f);
    nd.setHeight(textureRegion.getRegionHeight() / 32.0f);
    this.decal = nd;
  }

  public Decal getDecal() {
    return decal;
  }
}
