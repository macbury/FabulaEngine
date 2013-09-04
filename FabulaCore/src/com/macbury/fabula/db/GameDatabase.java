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
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.persister.ScenePersister;
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
  
  @ElementList(required=false)
  private ArrayList<Tileset> tilesets;
  
  @ElementMap(name="maps", entry="map", key="uuid", attribute=true, inline=true, required=false)
  public static HashMap<String, String> maps;
  
  @ElementMap(name="autotile-combinations", entry="corner", key="combination", attribute=true, inline=true, required=false)
  public static HashMap<String, AutoTiles.Types> CORNER_MAP;

  
  public GameDatabase() {
    Gdx.app.log(TAG, "Game database initialized");
  }
  
  public void save() {
    build++;
    try {
      GameDatabase.save(this, "game.features");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void reloadMapData() {
    Serializer serializer = GameDatabase.getDefaultSerializer();
    for (FileHandle fh : G.fs("maps/").list()) {
      if (fh.extension().equalsIgnoreCase(Scene.FILE_EXT)) {
        ScenePersister scenePersister = new ScenePersister();
        scenePersister.setSkipLoadingTerrainData(true);
        try {
          serializer.read(scenePersister, fh.file());
        } catch (Exception e) {
          e.printStackTrace();
        }
        
        maps.put(scenePersister.getUID(), fh.nameWithoutExtension());
      }
    }
    
    save();
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
    
    if (maps == null) {
      maps = new HashMap<String, String>();
    }
  }
  
  public Tileset getTileset(String name) {
    for (Tileset tileset : tilesets) {
      if (tileset.getName().equals(name)) {
        return tileset;
      }
    }
    throw new GdxRuntimeException("Could not find tileset with name: "+ name);
  }

  public ArrayList<Tileset> getTilesets() {
    return this.tilesets;
  }
  
  public int getMapUid() {
    int i = 1;
    for (FileHandle fh : G.fs("maps/").list()) {
      if (!fh.isDirectory()) {
        try {
          int id = Integer.parseInt(fh.nameWithoutExtension().replaceAll("\\D", ""));
          i = Math.max(i, id);
        } catch (NumberFormatException e) {
          
        }
      }
    }
    return i;
  }
  
  public static Serializer getDefaultSerializer() {
    Style style           = new HyphenStyle();
    Format format         = new Format(style);
    Serializer serializer = new Persister(format);
    return serializer;
  }
  
  public static void save(Object object, String path) throws Exception {
    Style style           = new HyphenStyle();
    Format format         = new Format(style);
    Serializer serializer = new Persister(format);
    File result = G.fs(path).file();
    serializer.write(object, result);
  }


  public PlayerStartPosition getPlayerStartPosition() {
    return this.playerStartPosition;
  }

  public void setPlayerStartPosition(PlayerStartPosition psp) {
    this.playerStartPosition = psp;
  }

  public FileHandle getMapFile(String uuid) {
    return G.fs("maps/"+this.maps.get(uuid)+"."+Scene.FILE_EXT);
  }
}
