package com.macbury.fabula.terrain;

import java.io.IOException;
import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.utils.PNG;
import com.badlogic.gdx.graphics.PixmapIO;

public class AutoTiles {
  public static final int TILE_SIZE      = 32;
  private static final Format TILE_FORMAT = Format.RGBA8888;
  private Array<AtlasRegion> tileParts;
  public TextureRegion debugTexture;
  private Array<AutoTile> list;
  private String name;
  
  public static enum Types {
    Start, InnerReapeating, CornerTopLeft, CornerTopRight, CornerBottomLeft, CornerBottomRight, EdgeLeft, EdgeRight, EdgeTop, EdgeBottom, PathHorizontal, PathVertical, PathVerticalTop, PathVerticalBottom, PathHorizontalLeft, PathHorizontalRight, PathCornerBottomLeft, PathCornerBottomRight, PathCornerTopRight, PathCornerTopLeft, PathCross
  };
  
  private final static byte[] tileCombinations = {
    0,1,4,5,
    2,3,6,7,
    13,14,17,18,
    8,9,12,13,
    10,11,14,15,
    16,17,20,21,
    18,19,22,23,
    12,13,16,17,
    14,15,18,19,
    9,10,13,14,
    17,18,21,22,
    9,10,21,22,
    12,15,16,19,
    8,11,12,15,
    16,19,20,23,
    8,9,20,21,
    10,11,22,23,
    2,19,22,23,
    16,3,20,21,
    8,9, 12,7,
    10,11,6,15
  };
  
  private final static Types[] tileTypes = {
    Types.Start,
    Types.PathCross,
    Types.InnerReapeating,
    Types.CornerTopLeft,
    Types.CornerTopRight,
    Types.CornerBottomLeft,
    Types.CornerBottomRight,
    Types.EdgeLeft,
    Types.EdgeRight,
    Types.EdgeTop,
    Types.EdgeBottom,
    Types.PathHorizontal,
    Types.PathVertical,
    Types.PathVerticalTop,
    Types.PathVerticalBottom,
    Types.PathHorizontalLeft,
    Types.PathHorizontalRight,
    Types.PathCornerBottomRight,
    Types.PathCornerBottomLeft,
    Types.PathCornerTopRight,
    Types.PathCornerTopLeft,
  };
  private static final String TAG = "AutoTiles";
  
  
  /**
   * Genereating new autotile based on tilemap
   * @param baseAtlas
   * @param name
   */
  //http://www.badlogicgames.com/forum/viewtopic.php?p=8358#p8358
  public AutoTiles(TextureAtlas baseAtlas, String name) {
    this.name         = name;
    this.tileParts    = baseAtlas.findRegions(name);
    this.debugTexture = null;
    this.list         = new Array<AutoTile>();
    
    for (int i = 0; i < tileCombinations.length; i+=4) {
      TextureRegion tileRegion = generateTileForCombination(i);
      AutoTile      autoTile   = new AutoTile(tileRegion, tileTypes[i/4]);
      debugTexture             = tileRegion;
      autoTile.setIndex(i/4);
      list.add(autoTile);
    }
  }
  
  private TextureRegion generateTileForCombination(int i) {
    TextureRegion topLeftRegion     = this.tileParts.get(tileCombinations[i]);
    TextureRegion topRightRegion    = this.tileParts.get(tileCombinations[i+1]);
    TextureRegion bottomLeftRegion  = this.tileParts.get(tileCombinations[i+2]);
    TextureRegion bottomRightRegion = this.tileParts.get(tileCombinations[i+3]);
    
    Pixmap tilePixmap = new Pixmap(TILE_SIZE, TILE_SIZE, TILE_FORMAT);
    
    drawRegionOnPixmap(tilePixmap, topLeftRegion,     0, 0);
    drawRegionOnPixmap(tilePixmap, topRightRegion,    TILE_SIZE/2, 0);
    drawRegionOnPixmap(tilePixmap, bottomLeftRegion,  0, TILE_SIZE/2);
    drawRegionOnPixmap(tilePixmap, bottomRightRegion, TILE_SIZE/2, TILE_SIZE/2);
    
    Texture tileTexture = new Texture(tilePixmap);

    String filePath     = "./preprocessed/expanded/" + name+"_"+i+".png";
    FileHandle image    = Gdx.files.absolute(filePath);
    PixmapIO.writePNG(image, tilePixmap);
    
    tilePixmap.dispose();
    return new TextureRegion(tileTexture);
  }

  private void drawRegionOnPixmap(Pixmap tilePixmap, TextureRegion region, int x, int y) {
    TextureData baseTextureData = region.getTexture().getTextureData();
    baseTextureData.prepare();
    Pixmap basePixmap           = baseTextureData.consumePixmap();
    tilePixmap.drawPixmap(basePixmap, x, y, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
    baseTextureData.disposePixmap();
    basePixmap.dispose();
  }
  
  public Array<AutoTile> all() {
    return list;
  }
  

}
