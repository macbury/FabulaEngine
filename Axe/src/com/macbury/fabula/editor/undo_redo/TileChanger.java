package com.macbury.fabula.editor.undo_redo;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.tile.Tile;

public class TileChanger implements Changeable {
  private static final String TAG = "TerrainTileChanger";
  private ArrayList<Tile> undoTiles;
  private ArrayList<Tile> redotiles;
  private Terrain terrain;
  
  public TileChanger(Terrain terrain) {
    undoTiles = new ArrayList<Tile>();
    redotiles = new ArrayList<Tile>();
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
  
  public void addToRedo(Tile tile) {
    redotiles.add(tile.clone());
  }
  
  @Override
  public void undo() {
    redotiles.clear();
    for (Tile undoTile : undoTiles) {
      addToRedo(terrain.getTileByTilePosition(undoTile));
      terrain.setTile(undoTile.getX(), undoTile.getZ(), undoTile);
      terrain.addSectorToRebuildFromTile(undoTile);
    }
    terrain.rebuildUsedSectors();
    Gdx.app.log(TAG, "Undoing terrain tiles: " + undoTiles.size());
  }

  @Override
  public void redo() {
    Gdx.app.log(TAG, "Redo terrain tiles: " + redotiles.size());
    for (Tile redoTile : redotiles) {
      terrain.setTile(redoTile.getX(), redoTile.getZ(), redoTile);
      terrain.addSectorToRebuildFromTile(redoTile);
    }
    
    terrain.rebuildUsedSectors();
  }
}