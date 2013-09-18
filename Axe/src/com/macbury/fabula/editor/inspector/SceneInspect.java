package com.macbury.fabula.editor.inspector;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import com.macbury.fabula.editor.brushes.AutoTileBrush.PaintMode;
import com.macbury.fabula.screens.WorldEditScreen;

public class SceneInspect {
  private WorldEditScreen screen;

  public SceneInspect(WorldEditScreen screen) {
    this.screen = screen;
  }
  
  public String getTileset() {
    return screen.getScene().getTerrain().getTileset().getName();
  }
  
  public void setTileset(String tilesetName) {
    screen.getScene().getTerrain().setTileset(tilesetName);
  }
  
  public String getShader() {
    return screen.getScene().getFinalShader();
  }
  
  public void setShader(String shaderName) {
    screen.getScene().setFinalShader(shaderName);
  }
  
  public void setTerrainHeight(float terrain) {
    screen.getTerrainBrush().setPower(terrain);
  }
  
  public float getTerrainHeight() {
    return screen.getTerrainBrush().getPower();
  }
  
  public void setPaintMode(String paintMode) {
    screen.getAutoTileBrush().setPaintMode(PaintMode.valueOf(paintMode));
  }
  
  public String getPaintMode() {
    return screen.getAutoTileBrush().getCurrentPaintMode().toString();
  }
  
  public void setMapSize(Dimension dimension) throws PropertyVetoException {
    if (!screen.getScene().getTerrain().resize(dimension.width, dimension.height)) {
      throw new PropertyVetoException("Error", new PropertyChangeEvent(this, "mapSize", getMapSize(), dimension));
    }
  }
  
  public Dimension getMapSize() {
    return new Dimension(screen.getScene().getTerrain().getColumns(), screen.getScene().getTerrain().getRows());
  }
  
  public String getSkyBox() {
    if (screen.getScene().getSkybox() == null) {
      return "";
    } else {
      return screen.getScene().getSkybox().getName();
    }
  }
  
  public void setSkyBox(String name) {
    if (name == null || name.length() <= 2) {
      screen.getScene().setSkyboxName(null);
    } else {
      screen.getScene().setSkyboxName(name);
      screen.getScene().getSkybox().initialize();
    }
  }
}
