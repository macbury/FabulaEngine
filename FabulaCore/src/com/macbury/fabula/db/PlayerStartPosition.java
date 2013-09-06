package com.macbury.fabula.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.macbury.fabula.manager.G;

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
    return G.db.getMapFile(map);
  }

  public String getUUID() {
    return map;
  }

  public int getTileX() {
    return x;
  }

  public int getTileY() {
    return y;
  }

  public Vector2 getSpawnPosition() {
    return new Vector2(x, y);
  }
  
  
}
