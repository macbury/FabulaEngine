package com.macbury.fabula.editor.brushes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.editor.brushes.TerrainBrush.TerrainBrushType;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;
import com.macbury.fabula.terrain.Tileset;
import com.macbury.fabula.utils.OffScreen2DRenderer;
import com.macbury.fabula.utils.PNG;

public class AutoTileBrush extends Brush {
  private static final String TAG = "AutoTileBrush";
  private HashMap<String, ImageIcon> autoTileIcons;
  private ArrayList<String> autoTileNames;
  private ArrayList<String> allAutoTileNames;
  private AutoTiles currentAutoTiles;
  private PaintMode currentPaintMode = PaintMode.AutoTile;
  private AutoTile currentAutoTile;
  
  public static enum PaintMode {
    AutoTile, Tile
  };
  
  public AutoTileBrush(Terrain terrain) {
    super(terrain);
    this.autoTileIcons = new HashMap<String, ImageIcon>();
    this.autoTileNames = new ArrayList<String>();
    this.allAutoTileNames = new ArrayList<String>();
    setSize(0);
  }

  
  public void buildAllPreviews() {
    Tileset tileset = terrain.getTileset();
    buildPreviews(tileset.getIcons());
    
    for (AutoTile at : tileset.getIcons()) {
      this.autoTileNames.add(at.getName());
    }
    
    for (AutoTiles autoTiles : tileset.getAutoTiles()) {
      buildPreviews(autoTiles.all());
      for (AutoTile at : autoTiles.all()) {
        allAutoTileNames.add(at.getName());
      }
    }
  }
  
  private void buildPreviews(Array<AutoTile> icons) {
    for (AutoTile at : icons) {
      String filePath     = "./preprocessed/previews/" + at.getName()+ ".png";
      File file           = new File(filePath);
      
      if (!file.exists()) {
        AutoTilePreviewRenderer renderer = new AutoTilePreviewRenderer(at);
        Pixmap iconPixmap   = renderer.render();
        
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
    if ((currentAutoTiles == null && currentPaintMode == PaintMode.AutoTile) || (currentAutoTile == null && currentPaintMode == PaintMode.Tile)) {
      return;
    }
    
    for (Tile tile : brushTiles) {
      if (currentPaintMode == PaintMode.AutoTile) {
        applyAutoTileToTile(tile);
      } else {
        tile.setAutoTile(currentAutoTile);
      }
    }
  }
  
  private void updateAutotile(Tile tile) {
    if (tile != null) {
      applyAutoTileToTile(tile);
    }
  }

  public void applyAutoTileToTile(Tile tile) {
    long mask            = computeAutoTileUID(tile);
    String tid           = Long.toHexString(mask).toUpperCase();
    AutoTiles.Types type = null;
    AutoTile defaultAutoTile = getCurrentAutoTiles().getAutoTile(AutoTiles.Types.Start);
    try {
      type = AutoTiles.getCornerMap().get(tid);
    } catch (ArrayIndexOutOfBoundsException e) {
      
    }
    
    if (type != null) {
      tile.setAutoTile(getCurrentAutoTiles().getAutoTile(type));
    } else {
      Gdx.app.log(TAG, "Mask: " + tid + " = "+mask);
      tile.setAutoTile(defaultAutoTile);
    }
  }
  
  public long computeAutoTileUID(Tile currentTile) {
    int x = (int) currentTile.getX();
    int z = (int) currentTile.getZ();
    
    long out = 0;
    
    Tile topTile           = terrain.getTile(x, z-1);
    Tile bottomTile        = terrain.getTile(x, z+1);
    
    Tile leftTile          = terrain.getTile(x-1, z);
    Tile rightTile         = terrain.getTile(x+1, z);
    
    Tile topLeftTile       = terrain.getTile(x-1, z-1);
    Tile topRightTile      = terrain.getTile(x+1, z-1);
    
    Tile bottomLeftTile    = terrain.getTile(x-1, z+1);
    Tile bottomRightTile   = terrain.getTile(x+1, z+1);
    
    long mask = 28 << 16;
    
    if (topLeftTile != null && topLeftTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = topLeftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_RIGHT);
      mask <<= 28;
    }
    
    out |= mask;
    
    mask = 24 << 16;
    if (topTile != null && topTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = topTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_LEFT) | topTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_RIGHT);
      mask <<= 24;
    }
    
    out |= mask;
    
    mask = 20 << 16;
    if (topRightTile != null && topRightTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = topRightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_LEFT);
      mask <<= 20;
    }
    
    out |= mask;
    
    mask = 16 << 16;
    if (leftTile != null && leftTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = leftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_RIGHT) | leftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_RIGHT);
      mask <<= 16;
    }
    
    mask = 12 << 16;
    if (rightTile != null && rightTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = rightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_LEFT) | rightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_BOTTOM_LEFT);
      mask <<= 12;
    }
    
    out |= mask;
    
    mask = 8 << 16;
    if (bottomLeftTile != null && bottomLeftTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = bottomLeftTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_RIGHT);
      mask <<= 8;
    }
    
    out |= mask;
    
    mask = 4 << 16;
    if (bottomTile != null && bottomTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = bottomTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_LEFT) | bottomTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_RIGHT);
      mask <<= 4;
    }
    
    out |= mask;
    
    mask = 16;
    if (bottomRightTile != null && bottomRightTile.haveTheSameAutoTile(currentTile.getAutoTile())) {
      mask = bottomRightTile.getAutoTile().getCornerMask(AutoTiles.CORNER_TOP_LEFT);
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

  
}
