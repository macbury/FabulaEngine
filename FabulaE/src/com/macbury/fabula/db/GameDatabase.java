package com.macbury.fabula.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.management.RuntimeErrorException;

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
import com.macbury.fabula.manager.G;
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
    Style style           = new HyphenStyle();
    Format format         = new Format(style);
    Serializer serializer = new Persister(format);
    build++;
    File result = Gdx.files.internal("assets/game.features").file();
    try {
      serializer.write(this, result);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeErrorException(new Error(), "Game db broken!");
    }
  }

  public static boolean exists() {
    return Gdx.files.internal("assets/game.features").file().exists();
  }

  public static GameDatabase load() {
    Style style           = new HyphenStyle();
    Format format         = new Format(style);
    Serializer serializer = new Persister(format);
    File file             = Gdx.files.internal("assets/game.features").file();
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
}
