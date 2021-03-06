package com.macbury.fabula.editor.brushes;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.editor.undo_redo.ChangeManager;
import com.macbury.fabula.editor.undo_redo.Changeable;
import com.macbury.fabula.screens.WorldEditScreen;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.tile.Tile;

public abstract class Brush {
  public enum BrushType {
    Pencil, Rectangle
  }
  protected BrushType brushType     = BrushType.Pencil;
  protected int size                = 4;
  protected float power             = 0.1f;
  protected Vector2 position        = new Vector2(0, 0);
  protected Vector2 startPosition;
  protected Terrain terrain;
  
  protected ArrayList<Tile>  brushTiles;
  protected ArrayList<Tile>  borderBrushTiles;
  protected ChangeManager changeManager;
  protected WorldEditScreen screen;
  
  public Brush(Terrain terrain) {
    this.terrain     = terrain;
    brushTiles       = new ArrayList<Tile>();
    borderBrushTiles = new ArrayList<Tile>();
    setSize(1);
  }
  
  public abstract String getStatusBarInfo();
  
  public int getSize() {
    return size;
  }
  public void setSize(int size) {
    this.size  = size;
  }
  public float getPower() {
    return power;
  }
  public void setPower(float power) {
    this.power = power;
  }
  public Vector2 getPosition() {
    return position;
  }
  
  public void setStartPosition(Vector2 sp) {
    this.startPosition = sp;
  }
  
  public void setStartPosition(float x, float y) {
    this.startPosition = new Vector2(x, y);
  }
  
  public void setPosition(Vector2 position) {
    this.position = position;
  }
  
  public void applyBrush() {
    brushTiles.clear();
    borderBrushTiles.clear();
    if (brushType == BrushType.Pencil) {
      for (int x = (int) (this.position.x - size); x < this.position.x + size + 1; x++) {
        for (int y = (int) (this.position.y - size); y < this.position.y + size + 1; y++) {
          Tile tile = this.terrain.getTile(x,y);
          if (tile != null) {
            this.brushTiles.add(tile);
            this.terrain.addSectorToRebuildFromTile(tile);
          }
        }
      }
    } else {
      int sx = (int) Math.min(this.startPosition.x, this.position.x);
      int sy = (int) Math.min(this.startPosition.y, this.position.y);
      
      int ex = (int) Math.max(this.startPosition.x, this.position.x);
      int ey = (int) Math.max(this.startPosition.y, this.position.y);
      
      for (int x = sx; x <= ex; x++) {
        for (int y = sy; y <= ey; y++) {
          Tile tile = this.terrain.getTile(x,y);
          if (tile != null) {
            if (x == sx || y == sy || x == ex || y == ey) {
              borderBrushTiles.add(tile);
            }
            
            this.brushTiles.add(tile);
            this.terrain.addSectorToRebuildFromTile(tile);
          }
        }
      }
    }
    
    this.onApply();
    this.terrain.rebuildUsedSectors();
  }
  
  public abstract void onApply();
  public void setPosition(float x, float z) {
    position.x = x;
    position.y = z;
  }

  public float getY() {
    Tile tile = getTile();
    if (tile == null) {
      return 0.0f;
    } else {
      return tile.getY();
    }
  }

  protected Tile getTile() {
    return terrain.getTile((int)getPosition().x, (int)getPosition().y);
  }

  public BrushType getBrushType() {
    return brushType;
  }

  public void setBrushType(BrushType brushType) {
    this.brushType = brushType;
  }

  public Vector2 getStartPosition() {
    return startPosition;
  }

  public void applyStartPositionIfNotSetted(Vector3 pos) {
    if (this.startPosition == null) {
      this.startPosition = new Vector2(pos.x, pos.z);
    }
  }

  public int getBrushShaderId() {
    return this.brushType == BrushType.Pencil ? 0 : 1;
  }

  public void setChangeManager(ChangeManager changeManager) {
    this.changeManager = changeManager;
  }

  public void setWorldEditScreen(WorldEditScreen worldEditScreen) {
    this.screen = worldEditScreen;
  }

  
}
