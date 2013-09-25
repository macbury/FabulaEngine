package com.macbury.fabula.persister;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="foliage")
public class FoliagePersister {
  @Element
  public float speed;
  @Element
  public float amplitude;
}
