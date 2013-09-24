package com.macbury.fabula.terrain.foliage;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

@Root(name="leave")
public class FoliageDescriptor {
  private FoliageSet foliageSet;
  private TextureRegion region;
  
  @Attribute(name="region")
  private String regionName;
  
  @Attribute
  private boolean animated;
  
  public void setFoliageSet(FoliageSet fs) {
    this.foliageSet = fs;
    this.region = this.foliageSet.getAtlas().findRegion(regionName);
  }

  public FoliageSet getFoliageSet() {
    return foliageSet;
  }

  public TextureRegion getRegion() {
    return region;
  }

  public String getRegionName() {
    return regionName;
  }

  public boolean isAnimated() {
    return animated;
  }

  public void setRegion(TextureRegion region) {
    this.region = region;
  }

  public void setRegionName(String regionName) {
    this.regionName = regionName;
  }

  public void setAnimated(boolean animated) {
    this.animated = animated;
  }
}
