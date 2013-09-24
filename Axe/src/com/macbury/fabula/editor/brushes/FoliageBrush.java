package com.macbury.fabula.editor.brushes;

import com.macbury.fabula.editor.undo_redo.TileChanger;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.foliage.FoliageDescriptor;
import com.macbury.fabula.terrain.tile.Tile;

public class FoliageBrush extends Brush {
  private String foliageDescriptorName = null;
  
  public FoliageBrush(Terrain terrain) {
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
    TileChanger changer  = new TileChanger(terrain);
    FoliageDescriptor fs = null;
    if (foliageDescriptorName != null) {
      fs = this.terrain.getFoliageSet().findDescriptor(foliageDescriptorName);
    }
    for (Tile tile : brushTiles) {
      changer.add(tile);
      tile.setFoliage(fs);
      this.terrain.addSectorToRebuildFromTile(tile);
    }
    
    if (changer.haveTiles()) {
      changeManager.addChangeable(changer);
    }
  }

  public String getFoliageDescriptorName() {
    return foliageDescriptorName;
  }

  public void setFoliageDescriptorName(String fs) {
    if (fs == null || fs.length() <= 1) {
      this.foliageDescriptorName = null;
    } else {
      this.foliageDescriptorName = fs;
    }
    
  }
  
}
