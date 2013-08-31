package com.macbury.fabula.editor.brushes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.editor.undo_redo.Changeable;
import com.macbury.fabula.editor.undo_redo.TileChanger;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class TerrainBrush extends Brush {
  
  public TerrainBrush(Terrain terrain) {
    super(terrain);
    setPower(1.0f);
    setSize(0);
    setBrushType(BrushType.Rectangle);
  }
  
  @Override
  public void onApply() {
    TileChanger changer = saveStateToChanger();
    
    for (Tile tile : brushTiles) {
      tile.setY(power);
    }
    
    for (int i = 0; i < this.borderBrushTiles.size(); i++) {
      this.applySlope(this.borderBrushTiles.get(i));
    }
    
    if (changer.haveTiles()) {
      changeManager.addChangeable(changer);
    }
  }
  
  public TileChanger saveStateToChanger() {
    TileChanger changer = new TileChanger(terrain);
    
    for (Tile tile : borderBrushTiles) {
      int x                  = (int)tile.getX();
      int z                  = (int)tile.getZ();
      Tile topTile           = getTileIfUnused(x, z-1);
      Tile bottomTile        = getTileIfUnused(x, z+1);
      
      Tile leftTile          = getTileIfUnused(x-1, z);
      Tile rightTile         = getTileIfUnused(x+1, z);
      
      Tile topLeftTile       = getTileIfUnused(x-1, z-1);
      Tile topRightTile      = getTileIfUnused(x+1, z-1);
      
      Tile bottomLeftTile    = getTileIfUnused(x-1, z+1);
      Tile bottomRightTile   = getTileIfUnused(x+1, z+1);
      
      changer.add(tile);
      changer.add(topTile);
      changer.add(bottomTile);
      changer.add(leftTile);
      changer.add(rightTile);
      changer.add(topLeftTile);
      changer.add(topRightTile);
      changer.add(bottomLeftTile);
      changer.add(bottomRightTile);
    }
    
    for (Tile tile : brushTiles) {
      changer.add(tile);
    }
    
    return changer;
  }
  
  public void saveRedoStateToChanger(TileChanger changer) {
    for (Tile tile : borderBrushTiles) {
      int x                  = (int)tile.getX();
      int z                  = (int)tile.getZ();
      Tile topTile           = getTileIfUnused(x, z-1);
      Tile bottomTile        = getTileIfUnused(x, z+1);
      
      Tile leftTile          = getTileIfUnused(x-1, z);
      Tile rightTile         = getTileIfUnused(x+1, z);
      
      Tile topLeftTile       = getTileIfUnused(x-1, z-1);
      Tile topRightTile      = getTileIfUnused(x+1, z-1);
      
      Tile bottomLeftTile    = getTileIfUnused(x-1, z+1);
      Tile bottomRightTile   = getTileIfUnused(x+1, z+1);
      
      changer.addToRedo(tile);
      changer.addToRedo(topTile);
      changer.addToRedo(bottomTile);
      changer.addToRedo(leftTile);
      changer.addToRedo(rightTile);
      changer.addToRedo(topLeftTile);
      changer.addToRedo(topRightTile);
      changer.addToRedo(bottomLeftTile);
      changer.addToRedo(bottomRightTile);
    }
    
    for (Tile tile : brushTiles) {
      changer.addToRedo(tile);
    }
  }
  
  public void applySlope(Tile currentTile) {
    int x = (int)currentTile.getX();
    int z = (int)currentTile.getZ();
    
    Tile topTile           = getTileIfUnused(x, z-1);
    Tile bottomTile        = getTileIfUnused(x, z+1);
    
    Tile leftTile          = getTileIfUnused(x-1, z);
    Tile rightTile         = getTileIfUnused(x+1, z);
    
    Tile topLeftTile       = getTileIfUnused(x-1, z-1);
    Tile topRightTile      = getTileIfUnused(x+1, z-1);
    
    Tile bottomLeftTile    = getTileIfUnused(x-1, z+1);
    Tile bottomRightTile   = getTileIfUnused(x+1, z+1);
    
    if (topTile != null) {
      addSectorToRebuildFromTile(topTile);
      topTile.setY2(currentTile.getY1());
      topTile.setY4(currentTile.getY3());
    }
    
    if (bottomTile != null) {
      addSectorToRebuildFromTile(bottomTile);
      bottomTile.setY1(currentTile.getY2());
      bottomTile.setY3(currentTile.getY4());
    }
    
    if (leftTile != null) {
      addSectorToRebuildFromTile(leftTile);
      leftTile.setY3(currentTile.getY1());
      leftTile.setY4(currentTile.getY2());
    }
    
    if (rightTile != null) {
      addSectorToRebuildFromTile(rightTile);
      rightTile.setY1(currentTile.getY3());
      rightTile.setY2(currentTile.getY4());
    }
    
    if (topLeftTile != null) {
      addSectorToRebuildFromTile(topLeftTile);
      topLeftTile.setY4(currentTile.getY1());
      //topLeftTile.setY2(currentTile.getY1());
    }
    
    if (topRightTile != null) {
      addSectorToRebuildFromTile(topRightTile);
      topRightTile.setY2(currentTile.getY3());
    }
    
    if (bottomLeftTile != null) {
      addSectorToRebuildFromTile(bottomLeftTile);
      bottomLeftTile.setY3(currentTile.getY2());
    }
    
    if (bottomRightTile != null) {
      addSectorToRebuildFromTile(bottomRightTile);
      bottomRightTile.setY1(currentTile.getY4());
    }
  }

  private Tile getTileIfUnused(int x, int z) {
    Tile tile = terrain.getTile(x, z);
    
    return tile;
  }

  private void addSectorToRebuildFromTile(Tile tile) {
    this.terrain.addSectorToRebuildFromTile(tile);
  }


  @Override
  public String getStatusBarInfo() {
    Tile tile = getTile();
    if (tile != null) {
      return "Slope: "  + tile.getSlopeDebugInfo();
    } else {
      return "";
    }
  }
  
  
}
