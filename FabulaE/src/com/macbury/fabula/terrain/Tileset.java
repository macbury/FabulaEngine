package com.macbury.fabula.terrain;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class Tileset {
  private TextureAtlas textureAtlas;
  private String name;
  private HashMap<String, AutoTiles> autotiles;
  
  public Tileset(TextureAtlas atlas, String name) {
    this.name         = name;
    this.textureAtlas = atlas;
    this.autotiles    = new HashMap<String, AutoTiles>();
  }
  
  public void buildAutotiles(String name) {
    this.autotiles.put(name, new AutoTiles(this, name));
  }

  public TextureAtlas getAtlas() {
    return textureAtlas;
  }

  public Array<AutoTile> getIcons() {
    Array<AutoTile> out = new Array<AutoTile>();
    
    for (String key : autotiles.keySet()) {
      AutoTiles at = autotiles.get(key);
      out.add(at.getAutoTile(AutoTiles.Types.Start));
    }
    
    return out;
  }
}
