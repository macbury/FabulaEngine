package com.macbury.fabula.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.macbury.fabula.manager.G;

@Root(name="texture-atlas")
public class TextureAtlasLoader extends TextureAtlas {
  @Attribute
  private String name;
  
  public TextureAtlasLoader(@Attribute(name="name") String name) {
    super(G.fs("textures/"+name+".atlas"));
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
