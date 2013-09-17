package com.macbury.fabula.editor.inspector;

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
}
