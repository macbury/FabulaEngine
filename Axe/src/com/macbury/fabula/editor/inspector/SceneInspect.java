package com.macbury.fabula.editor.inspector;

import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.screens.WorldEditScreen;

public class SceneInspect {
  private static final String TAG = "TESt";
  private WorldEditScreen screen;

  public SceneInspect(WorldEditScreen screen) {
    this.screen = screen;
  }
  
  public String getTileset() {
    return screen.getScene().getTerrain().getTileset().getName();
  }
  
  public void setTileset(String tilesetName) {
    
  }
  
  public String getShader() {
    return screen.getScene().getFinalShader();
  }
  
  public void setShader(String shaderName) {
    screen.getScene().setFinalShader(shaderName);
  }
}
