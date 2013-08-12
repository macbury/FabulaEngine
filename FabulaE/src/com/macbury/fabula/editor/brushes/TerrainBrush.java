package com.macbury.fabula.editor.brushes;

import com.macbury.fabula.terrain.Terrain;

public class TerrainBrush extends Brush {
  
  public TerrainBrush(Terrain terrain) {
    super(terrain);
  }

  @Override
  public void onApply() {
    terrain.applyHill(position, power);
  }
  
}
