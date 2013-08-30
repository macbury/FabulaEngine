package com.macbury.fabula.editor.brushes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.editor.undo_redo.Changeable;
import com.macbury.fabula.editor.undo_redo.TileChanger;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.AutoTiles.Types;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;
import com.macbury.fabula.terrain.Tileset;
import com.macbury.fabula.utils.OffScreen2DRenderer;

public class AutoTileBrush extends Brush {
  private static final String TAG = "AutoTileBrush";
  private HashMap<String, ImageIcon> autoTileIcons;
  private ArrayList<String> autoTileNames;
  private ArrayList<String> allAutoTileNames;
  private AutoTiles currentAutoTiles;
  private PaintMode currentPaintMode = PaintMode.AutoTile;
  private AutoTile currentAutoTile;
  private boolean needToBuildPreviews;
  
  public static enum PaintMode {
    AutoTile, ExpandedTile
  };
  
  public AutoTileBrush(Terrain terrain) {
    super(terrain);
    this.autoTileIcons = new HashMap<String, ImageIcon>();
    this.autoTileNames = new ArrayList<String>();
    this.allAutoTileNames = new ArrayList<String>();
    setSize(0);
    setBrushType(BrushType.Rectangle);
  }

  
  public void buildAllPreviews() {
    Tileset tileset = terrain.getTileset();
    Array<AutoTile> icons = tileset.getIcons();
    buildPreviews(icons);
    
    for (AutoTile at : icons) {
      this.autoTileNames.add(at.getName());
    }
    
    for (AutoTiles autoTiles : tileset.getAutoTiles()) {
      buildPreviews(autoTiles.all());
      for (AutoTile at : autoTiles.all()) {
        if (!autoTiles.isSimple()) {
          allAutoTileNames.add(at.getName());
        }
        
      }
    }
  }
  
  private void buildPreviews(Array<AutoTile> icons) {
    for (AutoTile at : icons) {
      String filePath     = "./preprocessed/previews/" + at.getName()+ ".png";
      File file           = new File(filePath);
      
      if (!file.exists()) {
        AutoTilePreviewRenderer renderer = new AutoTilePreviewRenderer(at);
        Pixmap iconPixmap                = renderer.render();
        
        FileHandle image    = Gdx.files.absolute(filePath);
        PixmapIO.writePNG(image, iconPixmap);
        renderer.dispose();
      }
      
      ImageIcon icon = null;
      try {
        icon = new ImageIcon(ImageIO.read(file));
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      this.autoTileIcons.put(at.getName(), icon);
    }
  }

  @Override
  public void onApply() {
    if ((currentAutoTiles == null && currentPaintMode == PaintMode.AutoTile) || (currentAutoTile == null && currentPaintMode == PaintMode.ExpandedTile)) {
      return;
    }
    
    TileChanger changer = saveStateToChanger();
    
    for (Tile tile : brushTiles) {
      if (currentPaintMode == PaintMode.AutoTile) {
        tile.setAutoTile(getCurrentAutoTiles().getAutoTile(AutoTiles.Types.InnerReapeating));
      } else {
        Gdx.app.log(TAG, getImportMapping());
        tile.setAutoTile(currentAutoTile);
        rebuildCombinations();
      }
    }
    
    if (currentPaintMode == PaintMode.AutoTile) {
      for (Tile tile : this.borderBrushTiles) {
        applyAutoTileToTile(tile, true);
        
        int x = (int) tile.getX();
        int z = (int) tile.getZ();
        
        updateAutotile(tile, terrain.getTile(x-1, z-1));
        updateAutotile(tile, terrain.getTile(x, z-1));
        updateAutotile(tile, terrain.getTile(x-1, z+1));
        updateAutotile(tile, terrain.getTile(x-1, z));
        updateAutotile(tile, terrain.getTile(x+1, z));
        updateAutotile(tile, terrain.getTile(x+1, z-1));
        updateAutotile(tile, terrain.getTile(x, z+1));
        updateAutotile(tile, terrain.getTile(x+1, z+1));
      }
    }
    
    if (changer.haveTiles()) {
      changeManager.addChangeable(changer);
    }
  }
  
  private TileChanger saveStateToChanger() {
    TileChanger changer = new TileChanger(terrain);
    
    for (Tile tile : brushTiles) {
      changer.add(tile);
    }
    
    return changer;
  }


  public void rebuildCombinations() {
    Tile[][] tiles = terrain.getTiles();
    
    for (int x = 0; x < terrain.getColumns(); x++) {
      for (int y = 0; y < terrain.getRows(); y++) {
        Tile tile  = tiles[x][y];
        if (tile.getAutoTile().getAutoTiles() == currentAutoTiles) {
          String tid = Long.toHexString(computeAutoTileUID(tile)).toUpperCase();
          G.db.CORNER_MAP.put(tid, tile.getAutoType());
        }
      }
    }
  }


  private String getImportMapping() {
    Tile tile            = this.terrain.getTile((int)this.position.x, (int)this.position.y);
    if (tile != null) {
      long mask            = computeAutoTileUID(tile);
      String tid           = Long.toHexString(mask).toUpperCase();
      return "CORNER_MAP.put(\""+tid+"\", Types."+tile.getAutoTile().getType().toString()+");";
    } else {
      return "";
    }
  }


  private void updateAutotile(Tile currentTile, Tile tile) {
    if (tile != null && currentTile.haveTheSameAutoTile(tile.getAutoTile())) {
      applyAutoTileToTile(tile, false);
    }
  }

  public void applyAutoTileToTile(Tile tile, boolean debug) {
    long mask                = computeAutoTileUID(tile);
    String tid               = Long.toHexString(mask).toUpperCase();
    AutoTiles.Types type     = null;
    AutoTile defaultAutoTile = getCurrentAutoTiles().getAutoTile(AutoTiles.Types.Start);
    
    try {
      type = G.db.CORNER_MAP.get(tid);
    } catch (ArrayIndexOutOfBoundsException e) {
      
    }
    
    if (type != null) {
      tile.setAutoTile(getCurrentAutoTiles().getAutoTile(type));
    } else {
      if (debug) {
       // Gdx.app.log(TAG, "Mask: " + tid + " = " + mask);
      }
      //tile.setAutoTile(defaultAutoTile);
    }
  }
  
  public long computeAutoTileUID(Tile currentTile) {
    int x = (int) currentTile.getX();
    int z = (int) currentTile.getZ();
    
    this.terrain.addSectorToRebuildFromTile(currentTile);
    
    long out = 0;
    
    Tile topTile           = terrain.getTile(x, z-1);
    Tile bottomTile        = terrain.getTile(x, z+1);
    
    Tile leftTile          = terrain.getTile(x-1, z);
    Tile rightTile         = terrain.getTile(x+1, z);
    
    Tile topLeftTile       = terrain.getTile(x-1, z-1);
    Tile topRightTile      = terrain.getTile(x+1, z-1);
    
    Tile bottomLeftTile    = terrain.getTile(x-1, z+1);
    Tile bottomRightTile   = terrain.getTile(x+1, z+1);
    
    long mask = 0;
    
    if (topLeftTile != null && topLeftTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = topLeftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_RIGHT);
      mask <<= 28;
      this.terrain.addSectorToRebuildFromTile(topLeftTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (topTile != null && topTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = topTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_LEFT) | topTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_RIGHT);
      mask <<= 24;
      this.terrain.addSectorToRebuildFromTile(topTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (topRightTile != null && topRightTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = topRightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_LEFT);
      mask <<= 20;
      this.terrain.addSectorToRebuildFromTile(topRightTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (leftTile != null && leftTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = leftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_RIGHT) | leftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_RIGHT);
      mask <<= 16;
      this.terrain.addSectorToRebuildFromTile(leftTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (rightTile != null && rightTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = rightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_LEFT) | rightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_LEFT);
      mask <<= 12;
      this.terrain.addSectorToRebuildFromTile(rightTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (bottomLeftTile != null && bottomLeftTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = bottomLeftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_RIGHT);
      mask <<= 8;
      this.terrain.addSectorToRebuildFromTile(bottomLeftTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (bottomTile != null && bottomTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = bottomTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_LEFT) | bottomTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_RIGHT);
      mask <<= 4;
      this.terrain.addSectorToRebuildFromTile(bottomTile);
    }
    
    out |= mask;
    
    mask = 0;
    if (bottomRightTile != null && bottomRightTile.haveTheSameAutoTileAndIsNotSimple(currentTile.getAutoTile())) {
      mask = bottomRightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_LEFT);
      mask <<= 0;
      this.terrain.addSectorToRebuildFromTile(bottomRightTile);
    }
    
    out |= mask;
    
    return out;
  }

  public HashMap<String, ImageIcon> getAutoTileIcons() {
    return autoTileIcons;
  }
  
  public ArrayList<String> getOrderedTileNames() {
    return autoTileNames;
  }
  
  public ArrayList<String> getAllOrderedTileNames() {
    return allAutoTileNames;
  }
  
  private class AutoTilePreviewRenderer extends OffScreen2DRenderer {
    private AutoTile at;

    public AutoTilePreviewRenderer(AutoTile at) {
      super(AutoTiles.TILE_SIZE, AutoTiles.TILE_SIZE);
      this.at = at;
    }

    @Override
    public void onRender(SpriteBatch batch) {
      batch.draw(at.getRegion(), 0, 0);
    }
  }

  public AutoTiles getCurrentAutoTiles() {
    return currentAutoTiles;
  }

  public void setCurrentAutoTiles(AutoTiles currentAutoTiles) {
    this.currentAutoTiles = currentAutoTiles;
  }

  @Override
  public String getStatusBarInfo() {
    Tile tile            = this.terrain.getTile((int)this.position.x, (int)this.position.y);
    if (tile != null) {
      long mask            = computeAutoTileUID(tile);
      String tid           = Long.toHexString(mask).toUpperCase();
      return "AutoTile: " + tid + " => " + mask;
    } else {
      return "";
    }
  }

  public void setPaintMode(PaintMode selectedItem) {
    this.currentPaintMode = selectedItem;
  }
  
  public PaintMode getCurrentPaintMode() {
    return this.currentPaintMode;
  }

  public void setCurrentAutoTile(AutoTile at) {
    this.currentAutoTile = at;
  }


  public void buildAllPreviewsUnlessBuilded() {
    if (!this.needToBuildPreviews) {
      this.needToBuildPreviews = true;
      buildAllPreviews();
    }
  }
  
}
