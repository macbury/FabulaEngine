package com.macbury.fabula.game_objects.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class DecalComponent extends Component {
  private Decal decal;
  
  public DecalComponent(Decal nd) {
    this.decal = nd;
  }
  
  public Decal getDecal() {
    return decal;
  }
}
