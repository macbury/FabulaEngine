package com.macbury.fabula.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.HyphenStyle;
import org.simpleframework.xml.stream.Style;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Tileset;

@Root(name="game-features")
public class GameDatabase {
  private static final String TAG = "GameDatabase";

  @Element(name="title")
  private String title = "Brutal Crasher";
  
  @Attribute
  @Namespace(reference="http://macbury.pl/", prefix="game")
  private int build = 0;
  
  @Element(required=false)
  private PlayerStartPosition playerStartPosition;
  
  @ElementArray(entry="shader", required=false)
  private String[] shaders;
  
  @ElementArray(entry="name", required=false)
  private String[] maps;
  
  @ElementList(required=false)
  private ArrayList<Tileset> tilesets;
  
  @ElementMap(name="autotile-combinations", entry="corner", key="combination", attribute=true, inline=true, required=false)
  public static HashMap<String, AutoTiles.Types> CORNER_MAP;
  
  public GameDatabase() {
    Gdx.app.log(TAG, "Game database initialized");
  }
  
  public void save() {
    build++;
    try {
      //GameDatabase.save(this, "game.features");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static GameDatabase load() {
    Style style           = new HyphenStyle();
    Format format         = new Format(style);
    Serializer serializer = new Persister(format);
    File file             = G.fs("game.features").file();
   
    Gdx.app.log(TAG, "Loading " + file.getAbsolutePath());
    
    try {
      return serializer.read(GameDatabase.class, file);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void initialize() {
    if (shaders != null) {
      for (int i = 0; i < shaders.length; i++) {
        String name = shaders[i];
        G.shaders.add(name, name+".vert", name+".frag");
      }
    } else {
      shaders = new String[0];
    }
    
    if (tilesets == null) {
      tilesets = new ArrayList<Tileset>();
    }
  }
  
  public Tileset getTileset(String name) {
    for (Tileset tileset : tilesets) {
      if (tileset.getName().equals(name)) {
        return tileset;
      }
    }
    throw new RuntimeException("Could not find tileset with name: "+ name);
  }

  public ArrayList<Tileset> getTilesets() {
    return this.tilesets;
  }
  
  public int getMapUid() {
    return 1;
  }
  
  public static void save(Object object, String path) throws Exception {
    Style style           = new HyphenStyle();
    Format format         = new Format(style);
    Serializer serializer = new Persister(format);
    File result = G.fs(path).file();
    serializer.write(object, result);
  }

  public String[] getMapNames() {
    return this.maps;
  }
}
