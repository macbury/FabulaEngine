package com.macbury.fabula.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class Tileset {
  private TextureAtlas textureAtlas;
  private String name;
  private HashMap<String, AutoTiles> autotiles;
  private ArrayList<String> orderedAutotiles;
  private AutoTile defaultAutoTile;
  
  public Tileset(TextureAtlas atlas, String name) {
    this.name         = name;
    this.textureAtlas = atlas;
    this.autotiles    = new HashMap<String, AutoTiles>();
    this.orderedAutotiles = new ArrayList<String>();
  }
  
  public void buildAutotiles(String name) {
    AutoTiles autoTiles = new AutoTiles(this, name);
    if (this.orderedAutotiles.size() == 0) {
      this.defaultAutoTile = autoTiles.getAutoTile(AutoTiles.Types.InnerReapeating);
    }
    this.autotiles.put(name, autoTiles);
    this.orderedAutotiles.add(name);
    //Gdx.app.log("Test", name);
  }
  
  public AutoTiles getAutoTiles(String key) {
    return autotiles.get(key);
  }

  public AutoTile getAutoTile(String key) {
    for (AutoTiles at : autotiles.values()) {
      for (AutoTile tile : at.all()) {
        if (tile.getName().equals(key)) {
          return tile;
        }
      }
    }
    return null;
  }
  
  public TextureAtlas getAtlas() {
    return textureAtlas;
  }

  public Array<AutoTile> getIcons() {
    Array<AutoTile> out = new Array<AutoTile>();
    
    for (String key : orderedAutotiles) {
      AutoTiles at = autotiles.get(key);
      out.add(at.getAutoTile(AutoTiles.Types.Start));
    }
    
    return out;
  }

  public AutoTile getDefaultAutoTile() {
    return defaultAutoTile;
  }

  public Collection<AutoTiles> getAutoTiles() {
    return autotiles.values();
  }
}
