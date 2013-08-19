package com.macbury.fabula.editor.brushes;

import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class TerrainBrush extends Brush {
  public enum TerrainBrushType {
    Up, Down, Set
  }
  
  private TerrainBrushType type;
  
  public TerrainBrush(Terrain terrain) {
    super(terrain);
    type = TerrainBrushType.Up;
  }

  @Override
  public void onApply() {
    for (Tile tile : brushTiles) {
      if (type == TerrainBrushType.Up) {
        tile.setY(tile.getY()+power);
      } else if (type == TerrainBrushType.Down) {
        tile.setY(tile.getY()-power);
      } else {
        tile.setY(power);
      }
    }
    
    for (Tile tile : brushTiles) {
      terrain.applySlope(tile);
    }
  }

  public TerrainBrushType getType() {
    return type;
  }

  public void setType(TerrainBrushType type) {
    this.type = type;
  }

  @Override
  public String getStatusBarInfo() {
    return "";
  }
  
}
