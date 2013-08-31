package com.macbury.fabula.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

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
}
