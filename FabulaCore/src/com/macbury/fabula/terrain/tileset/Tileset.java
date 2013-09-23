package com.macbury.fabula.terrain.tileset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.tile.AutoTile;
import com.macbury.fabula.terrain.tileset.AutoTiles.Types;

@Root
public class Tileset {
  private static final String TAG = "Tileset";
  @Attribute
  private String name;
  @Attribute(name="atlas")
  private String atlasName;
  
  @ElementArray(name="auto-tiles", entry="auto-tile")
  private AutoTileBuilderInfo[] autoTileBuilderInfos;
  
  private TextureAtlas textureAtlas;
  private HashMap<String, AutoTiles> autotiles;
  private ArrayList<String> orderedAutotiles;
  private AutoTile defaultAutoTile;
  
  private Texture texture;
  
  public Tileset(@Attribute(name="name") String name, @Attribute(name="atlas") String atlasName, @ElementArray(name="auto-tiles") AutoTileBuilderInfo[] autoTileBuilderInfos) {
    this.name                 = name;
    this.atlasName            = atlasName;
    this.autoTileBuilderInfos = autoTileBuilderInfos;
    
    this.autotiles            = new HashMap<String, AutoTiles>();
    this.orderedAutotiles     = new ArrayList<String>();
    
    this.textureAtlas         = new TextureAtlas(G.fs("textures/"+atlasName+".atlas"));
    this.texture              = (Texture) this.textureAtlas.getTextures().toArray()[0];
    
    for (AutoTileBuilderInfo autoTileBuilderInfo : autoTileBuilderInfos) {
      buildAutotiles(autoTileBuilderInfo.name, autoTileBuilderInfo.slope);
    }
  }
  
  public Tileset(TextureAtlas atlas, String name) {
    this.name         = name;
    this.textureAtlas = atlas;
    this.autotiles    = new HashMap<String, AutoTiles>();
    this.orderedAutotiles = new ArrayList<String>();
    this.texture      = (Texture) this.textureAtlas.getTextures().toArray()[0];
  }
  
  public void buildAutotiles(String name, boolean slope) {
    AutoTiles autoTiles = new AutoTiles(this, name);
    autoTiles.setSlope(slope);
    if (this.orderedAutotiles.size() == 0) {
      this.defaultAutoTile = autoTiles.getAutoTile(AutoTiles.Types.InnerReapeating);
    }
    this.autotiles.put(name, autoTiles);
    this.orderedAutotiles.add(name);
    
    //Gdx.app.log(TAG, "Added auto tile " + name  +" = " + autoTiles.getId());
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

  public ArrayList<AutoTiles> getAutoTiles() {
    return new ArrayList<AutoTiles>(autotiles.values());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAtlasName(String atlas) {
    this.atlasName = atlas;
  }
  
  public String getAtlasName() {
    return atlasName;
  }

  public Texture getTexture() {
    return this.texture; 
  }
  
  @Root
  public static class AutoTileBuilderInfo {
    @Attribute
    public String name;
    
    @Attribute
    public boolean slope;
    
    public AutoTileBuilderInfo() {}
  }

  public AutoTiles getAutoTilesById(int autoTileId) {
    for (AutoTiles at : autotiles.values()) {
      if (at.getId() == autoTileId) {
        return at;
      }
    }
    return null;
  }
}
