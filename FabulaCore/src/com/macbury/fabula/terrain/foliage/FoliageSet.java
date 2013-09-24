package com.macbury.fabula.terrain.foliage;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.macbury.fabula.manager.G;

@Root(name="foliage")
public class FoliageSet {
  @Attribute
  private String name;
  @Attribute(name="atlas")
  private String atlasName;
  
  private TextureAtlas atlas;
  
  @ElementList(required=false, name="leaves", entry="leaf")
  private ArrayList<FoliageDescriptor> leaves;
  private Texture texture;
  
  public FoliageSet(@Attribute(name="name") String name, @Attribute(name="atlas") String atlas, @ElementList(required=false, name="leaves", entry="leaf") ArrayList<FoliageDescriptor> leaves) {
    setAtlasName(atlas);
    this.leaves = leaves;
    
    for (FoliageDescriptor foliageDescriptor : this.leaves) {
      foliageDescriptor.setFoliageSet(this);
    }
    setName(name);
  }

  public String getName() {
    return name;
  }

  public String getAtlasName() {
    return atlasName;
  }

  public TextureAtlas getAtlas() {
    return atlas;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAtlasName(String atlasName) {
    this.atlasName = atlasName;
    this.atlas     = new TextureAtlas(G.fs("textures/"+atlasName+".atlas"));
    this.texture   = atlas.getRegions().get(0).getTexture();
  }

  public FoliageDescriptor findDescriptor(String name) {
    for (FoliageDescriptor foliageDescriptor : this.leaves) {
      if (foliageDescriptor.getRegionName().equalsIgnoreCase(name)) {
        return foliageDescriptor;
      }
    }
    return null;
  }

  public Texture getTexture() {
    return texture;
  }

  public ArrayList<FoliageDescriptor> getLeaves() {
    return leaves;
  }
}
