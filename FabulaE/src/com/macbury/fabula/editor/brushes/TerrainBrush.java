package com.macbury.fabula.editor.brushes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.editor.brushes.Brush.BrushType;
import com.macbury.fabula.editor.undo_redo.Changeable;
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
    TerrainTileChanger changer = new TerrainTileChanger(terrain);
    saveStateToChanger(changer);
    for (Tile tile : brushTiles) {
      tile.setY(power);
    }
    
    for (int i = 0; i < this.borderBrushTiles.size(); i++) {
      this.applySlope(this.borderBrushTiles.get(i), changer);
    }
    
    if (changer.haveTiles()) {
      changeManager.addChangeable(changer);
    }
  }
  
  public void saveStateToChanger(TerrainTileChanger changer) {
    for (Tile tile : brushTiles) {
      changer.add(tile);
    }
    
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
      
      changer.add(topTile);
      changer.add(bottomTile);
      changer.add(leftTile);
      changer.add(rightTile);
      changer.add(topLeftTile);
      changer.add(topRightTile);
      changer.add(bottomLeftTile);
      changer.add(bottomRightTile);
    }
  }
  
  public void applySlope(Tile currentTile, TerrainTileChanger changer) {
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
      topLeftTile.setType(Tile.Type.CornerTopLeft);
      //topLeftTile.setY2(currentTile.getY1());
    }
    
    if (topRightTile != null) {
      addSectorToRebuildFromTile(topRightTile);
      topRightTile.setY2(currentTile.getY3());
      topRightTile.setType(Tile.Type.CornerTopRight);
    }
    
    if (bottomLeftTile != null) {
      addSectorToRebuildFromTile(bottomLeftTile);
      bottomLeftTile.setY3(currentTile.getY2());
      bottomLeftTile.setType(Tile.Type.CornerBottomLeft);
    }
    
    if (bottomRightTile != null) {
      addSectorToRebuildFromTile(bottomRightTile);
      bottomRightTile.setY1(currentTile.getY4());
      bottomRightTile.setType(Tile.Type.CornerBottomRight);
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
  
  public static class TerrainTileChanger implements Changeable {
    private static final String TAG = "TerrainTileChanger";
    private ArrayList<Tile> undoTiles;
    private ArrayList<Tile> redotiles;
    private Terrain terrain;
    
    public TerrainTileChanger(Terrain terrain) {
      undoTiles = new ArrayList<Tile>();
      this.terrain = terrain;
    }
    
    public void addOrReplace(Tile tile) {
      int index = undoTiles.indexOf(tile);
      if (index == -1) {
        add(tile);
      } else {
        undoTiles.set(index, tile.clone());
      }
    }

    public boolean haveTiles() {
      return undoTiles.size() > 0;
    }

    public void add(Tile tile) {
      if (tile != null && undoTiles.indexOf(tile) == -1) {
        undoTiles.add(tile.clone());
      }
    }
    
    @Override
    public void undo() {
      redotiles = new ArrayList<Tile>(undoTiles.size());
      
      for (Tile undoTile : undoTiles) {
        Tile redoTile = terrain.getTile((int)undoTile.getX(), (int)undoTile.getZ()).clone();
        redotiles.add(redoTile);
        terrain.setTile((int)undoTile.getX(), (int)undoTile.getZ(), undoTile);
        terrain.addSectorToRebuildFromTile(undoTile);
      }
      terrain.rebuildUsedSectors();
    }

    @Override
    public void redo() {
      for (Tile redoTile : redotiles) {
        terrain.setTile((int)redoTile.getX(), (int)redoTile.getZ(), redoTile);
        terrain.addSectorToRebuildFromTile(redoTile);
      }
      terrain.rebuildUsedSectors();
    }
  }
}
