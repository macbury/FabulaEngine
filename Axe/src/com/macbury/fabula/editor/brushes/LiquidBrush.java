package com.macbury.fabula.editor.brushes;

import com.macbury.fabula.editor.undo_redo.TileChanger;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.tile.Tile;

public class LiquidBrush extends Brush {
  private boolean liquid = true;
  private float   height = 0.5f;
  
  public LiquidBrush(Terrain terrain) {
    super(terrain);
    
    setSize(0);
    setBrushType(BrushType.Rectangle);
  }

  @Override
  public String getStatusBarInfo() {
    return "";
  }
  
  @Override
  public void onApply() {
    TileChanger changer = new TileChanger(terrain);
    
    for (Tile tile : brushTiles) {
      tile.setLiquid(liquid);
      tile.setLiquidHeight(height);
      this.terrain.addSectorToRebuildFromTile(tile);
      changer.add(tile);
    }
    
    if (changer.haveTiles()) {
      changeManager.addChangeable(changer);
    }
  }

  public void setHeight(float h) {
    height = h;
  }

  public boolean isLiquid() {
    return liquid;
  }

  public float getHeight() {
    return height;
  }

  public void setLiquid(boolean liquid) {
    this.liquid = liquid;
  }
  
}
