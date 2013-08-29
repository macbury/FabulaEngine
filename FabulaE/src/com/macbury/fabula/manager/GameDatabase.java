package com.macbury.fabula.manager;

import java.io.File;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.badlogic.gdx.Gdx;

@Root(name="game-features")
public class GameDatabase {
  @Element(name="title")
  private String title = "Brutal Crasher";
  
  @Attribute
  private int build = 0;
  
  public void save() {
    Serializer serializer = new Persister();
    File result = Gdx.files.internal("assets/game.features").file();
    try {
      serializer.write(this, result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
