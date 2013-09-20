package com.macbury.fabula.editor.brushes;

import com.macbury.fabula.editor.undo_redo.TileChanger;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class PassableBrush extends Brush {
  private boolean passable = false;
  
  public PassableBrush(Terrain terrain) {
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
      tile.setPassable(passable);
      this.terrain.addSectorToRebuildFromTile(tile);
      changer.add(tile);
    }
    
    if (changer.haveTiles()) {
      changeManager.addChangeable(changer);
    }
  }
  
  public boolean getMode() {
    return passable;
  }
  
  public void setMode(boolean p) {
    this.passable = p;
  }
}
