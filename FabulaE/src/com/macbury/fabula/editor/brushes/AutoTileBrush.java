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
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tileset;
import com.macbury.fabula.utils.OffScreen2DRenderer;
import com.macbury.fabula.utils.PNG;

public class AutoTileBrush extends Brush {
  private static final String TAG = "AutoTileBrush";
  private HashMap<String, ImageIcon> autoTileIcons;
  private ArrayList<String> autoTileNames;
  public AutoTileBrush(Terrain terrain) {
    super(terrain);
    this.autoTileIcons = new HashMap<String, ImageIcon>();
    this.autoTileNames = new ArrayList<String>();
  }
  
  public void buildPreviews() {
    Tileset tileset = terrain.getTileset();
    
    for (AutoTile at : tileset.getIcons()) {
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
      this.autoTileNames.add(at.getName());
      this.autoTileIcons.put(at.getName(), icon);
    }
  }
  
  @Override
  public void onApply() {
    
  }

  public HashMap<String, ImageIcon> getAutoTileIcons() {
    return autoTileIcons;
  }
  
  public ArrayList<String> getOrderedTileNames() {
    return autoTileNames;
  }
  
  private class AutoTilePreviewRenderer extends OffScreen2DRenderer {
    private AutoTile at;

    public AutoTilePreviewRenderer(AutoTile at) {
      super(AutoTiles.TILE_SIZE, AutoTiles.TILE_SIZE);
      this.at = at;
    }

    @Override
    public void onRender(SpriteBatch batch) {
      //Texture texture = new ResourceManager().shared().getTexture("TEXTURE_GRASS");
      //batch.draw(texture, 0, 0, 32, 32);
      batch.draw(at.getRegion(), 0, 0);
    }
  }
}
