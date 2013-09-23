package com.macbury.fabula.persister;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="water")
public class WaterPersister {
  @Element
  public String material;
  @Element
  public float animationSpeed;
  @Element
  public float amplitude;
  @Element
  public float speed;
  @Element
  public float alpha;
  @Element
  public float mix;
}
