package com.macbury.fabula.editor.inspector;

import java.awt.Dimension;
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
  
  public boolean getShowWireframe() {
    return this.screen.getScene().getEditorEntityManagmentSystem().isShowWireframe();
  }
  
  public void setShowWireframe(boolean show) {
    this.screen.getScene().getEditorEntityManagmentSystem().setShowWireframe(show);
  }
  
  public boolean getShowColliders() {
    return this.screen.getScene().getEditorEntityManagmentSystem().isShowColliders();
  }
  
  public void setShowColliders(boolean show) {
    this.screen.getScene().getEditorEntityManagmentSystem().setShowColliders(show);
  }
  
  public boolean getTerrainPassable() {
    return this.screen.getPassableBrush().getMode();
  }
  
  public void setTerrainPassable(boolean p) {
    this.screen.getPassableBrush().setMode(p);
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
  
  public void setLiquidHeight(float height) {
    this.screen.getLiquidBrush().setHeight(height);
  }
  
  public float getLiquidHeight() {
    return this.screen.getLiquidBrush().getHeight();
  }
  
  public void setLiquid(boolean liq) {
    this.screen.getLiquidBrush().setLiquid(liq);
  }
  
  public boolean getLiquid() {
    return this.screen.getLiquidBrush().isLiquid();
  }
  
  public void setLiquidAmplitude(float amp) {
    this.screen.getScene().getWater().setAmplitudeWave(amp);
  }
  
  public float getLiquidAmplitude() {
    return this.screen.getScene().getWater().getAmplitudeWave();
  }
  
  public void setLiquidSpeed(float speed) {
    this.screen.getScene().getWater().setAngleWaveSpeed(speed);
  }
  
  public float getLiquidSpeed() {
    return this.screen.getScene().getWater().getAngleWaveSpeed();
  }
  
  public void setLiquidMaterial(String name) {
    this.screen.getScene().getWater().setWaterTexture(name);
  }
  
  public String getLiquidMaterial() {
    return this.screen.getScene().getWater().getWaterMaterial();
  }
  
  public void setLiquidAnimationSpeed(float speed) {
    this.screen.getScene().getWater().setWaterAnimationSpeed(speed);
  }
  
  public float getLiquidAnimationSpeed() {
    return this.screen.getScene().getWater().getWaterAnimationSpeed();
  }
  
  public float getLiquidAlpha() {
    return this.screen.getScene().getWater().getAlpha();
  }
  
  public void setLiquidAlpha(float a) {
    this.screen.getScene().getWater().setAlpha(a);
  }
  
  public float getLiquidMix() {
    return this.screen.getScene().getWater().getMix();
  }
  
  public void setLiquidMix(float a) {
    this.screen.getScene().getWater().setMix(a);
  }
  
  public String getFoliageSet() {
    return this.screen.getScene().getTerrain().getFoliageSet().getName();
  }
  
  public void setFoliageSet(String name) {
    this.screen.getScene().getTerrain().setFoliageSet(name);
  }
  
  public String getFoliageDescriptor() {
    return this.screen.getFoliageBrush().getFoliageDescriptorName();
  }
  
  public void setFoliageDescriptor(String name) {
    this.screen.getFoliageBrush().setFoliageDescriptorName(name);
  }
}
