package com.macbury.fabula.db;

import java.util.logging.FileHandler;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.files.FileHandle;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.persister.ScenePersister;

@Root
public class PlayerStartPosition {
  @Attribute
  private int x;
  @Attribute
  private int y;
  @Attribute
  private String map; 
  
  public PlayerStartPosition(@Attribute(name="x") int x, @Attribute(name="y") int y, @Attribute(name="map") String map) {
    this.x = x;
    this.y = y;
    this.map = map;
  }

  public FileHandle getFileHandler() {
    return G.fs("maps/"+map+"."+Scene.FILE_EXT);
  }
}
