package com.macbury.fabula.terrain;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import javax.management.RuntimeErrorException;

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
import com.macbury.fabula.terrain.AutoTiles.Types;
import com.macbury.fabula.utils.PNG;
import com.badlogic.gdx.graphics.PixmapIO;

public class AutoTiles {
  public static final int CORNER_TOP_LEFT     = 0;
  public static final int CORNER_TOP_RIGHT    = 1;
  public static final int CORNER_BOTTOM_RIGHT = 3;
  public static final int CORNER_BOTTOM_LEFT  = 2;
  public static int GID                       = 0;
  public static final int TILE_SIZE           = 32;
  private static final Format TILE_FORMAT     = Format.RGBA8888;
  private Array<AtlasRegion> tileParts;
  private Tileset tileset;
  public TextureRegion debugTexture;
  private Array<AutoTile> list;
  private String name;
  private int id;
  
  public static enum Types {
    Start, InnerReapeating, CornerTopLeft, CornerTopRight, CornerBottomLeft, CornerBottomRight, EdgeLeft, EdgeRight, EdgeTop, EdgeBottom, PathHorizontal, PathVertical, PathVerticalTop, PathVerticalBottom, PathHorizontalLeft, PathHorizontalRight, PathCornerBottomLeft, PathCornerBottomRight, PathCornerTopRight, PathCornerTopLeft, PathCross, InnerEdgeBottomRight, InnerEdgeBottomLeft, InnerEdgeTopLeft, InnerEdgeTopRight
  };
  
  public static HashMap<String, AutoTiles.Types> CORNER_MAP;
  
  // 1 = equals border
  // 0 = equals none
  
  public final static byte[] TILE_MASK = {
    14, //0
    13, //1
    8,  //2
    4,  //3
    11, //4
    7,  //5
    2,  //6
    1,  //7
    14, //8
    12, //9
    12, //10
    13, //11,
    10, //12,
    0, //13,
    0, //14,
    5, //15,
    10, //16,
    0, //17,
    0, //18,
    5, //19,
    11, //20
    3,  //21
    3, //22
    7, //23
  };
  
  public final static byte[] TILE_COMBINATIONS = {
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
    10,11,6,15,
    13,14,17,7,
    13,14,6,18,
    2,14,17,18,
    13,3,17,18
  };
  
  public final static int OTHER_AUTOTILE_MASK = 65535;
  
  public final static Types[] TILE_TYPES = {
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
    Types.InnerEdgeBottomRight,
    Types.InnerEdgeBottomLeft,
    Types.InnerEdgeTopLeft,
    Types.InnerEdgeTopRight,
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
    
    for (int i = 0; i < TILE_COMBINATIONS.length; i+=4) {
      TextureRegion tileRegion = generateTileForCombination(i);
      //Gdx.app.log(TAG, "Id: "+ computeIndex(i));
      AutoTile      autoTile   = new AutoTile(tileRegion, TILE_TYPES[i/4]);
      debugTexture             = tileRegion;
      autoTile.setIndex(i/4);
      list.add(autoTile);
    }
    
    
   
  }
  
  public static HashMap<String, AutoTiles.Types> getCornerMap() {
    if (CORNER_MAP == null) {
      buildCornerMap();
    }
    
    return CORNER_MAP;
  }
  
  private static void buildCornerMap() {
    CORNER_MAP = new HashMap<String, AutoTiles.Types>();
    /*CORNER_MAP.put("4C80EDFE", Types.InnerReapeating);
    CORNER_MAP.put("0", Types.Start);
    CORNER_MAP.put("1C00F0", Types.PathVerticalTop);
    //CORNER_MAP.put("DDC4EC0E", Types.PathVerticalTop);
    /*CORNER_MAP.put("180000", Types.PathVerticalBottom);
    CORNER_MAP.put("F1C0010", Types.PathVerticalBottom);
    CORNER_MAP.put("F1C00F0", Types.PathVertical);
    CORNER_MAP.put("1CF0A0", Types.CornerTopLeft);
    CORNER_MAP.put("1C0050", Types.CornerTopRight);
    CORNER_MAP.put("FBC3010", Types.CornerBottomLeft);
    CORNER_MAP.put("7F1C0010", Types.CornerBottomRight);
    CORNER_MAP.put("7FBC3010", Types.EdgeBottom);
    CORNER_MAP.put("FBCF0A0", Types.EdgeLeft);
    CORNER_MAP.put("1CF0AE", Types.CornerTopLeft);
    CORNER_MAP.put("1CF0FE", Types.EdgeTop);
    CORNER_MAP.put("1C0DF0", Types.CornerTopRight);
    CORNER_MAP.put("51C0050", Types.EdgeRight);
    CORNER_MAP.put("F1C0050", Types.InnerReapeating);*/
  }

  public AutoTiles(Tileset tileset, String name) {
    GID+=100;
    this.id           = GID;
    this.tileset      = tileset;
    this.name         = name;
    this.tileParts    = tileset.getAtlas().findRegions(name);
    this.debugTexture = null;
    this.list         = new Array<AutoTile>();
    
    for (int i = 0; i < TILE_TYPES.length; i++) {
      TextureRegion region     = tileParts.get(i);
      AutoTile      autoTile   = new AutoTile(region, TILE_TYPES[i]);
      //Gdx.app.log(TAG, "X:" + region.getRegionX() + " Y: " + region.getRegionY());
      autoTile.setIndex(i);
      autoTile.setAutoTiles(this);
      list.add(autoTile);
    }
  }
  
  private int computeIndex(int i) {
    int i0 = TILE_COMBINATIONS[i];
    int i1 = TILE_COMBINATIONS[i+1];
    int i2 = TILE_COMBINATIONS[i+2];
    int i3 = TILE_COMBINATIONS[i+3];
    return i0 + i1 + i2 + i3; 
  }
  
  private TextureRegion generateTileForCombination(int i) {
    TextureRegion topLeftRegion     = this.tileParts.get(TILE_COMBINATIONS[i]);
    TextureRegion topRightRegion    = this.tileParts.get(TILE_COMBINATIONS[i+1]);
    TextureRegion bottomLeftRegion  = this.tileParts.get(TILE_COMBINATIONS[i+2]);
    TextureRegion bottomRightRegion = this.tileParts.get(TILE_COMBINATIONS[i+3]);
    
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

  public int getMaskForAutoTile(Types type) {
    int cursor = getIndexForType(type);
    
    int index1 = TILE_COMBINATIONS[cursor];
    int index2 = TILE_COMBINATIONS[cursor+1];
    int index3 = TILE_COMBINATIONS[cursor+2];
    int index4 = TILE_COMBINATIONS[cursor+3];
    
    int mask1  = TILE_MASK[index1] << 12;
    int mask2  = TILE_MASK[index2] << 8;
    int mask3  = TILE_MASK[index3] << 4;
    int mask4  = TILE_MASK[index4];
    
    return mask1 | mask2 | mask3 | mask4;
  }
  
  public int getIndexForType(Types type) {
    int cursor = -1;
    
    for (int i = 0; i < TILE_TYPES.length; i++) {
      if (TILE_TYPES[i].equals(type)) {
        cursor = i;
        break;
      }
    }
   
    if (cursor == -1) {
      throw new RuntimeException("Could not found "+type.toString() + " in tile TILE_FORMAT");
    }
    
    cursor *= 4;
    return cursor;
  }
  
  public long getMaskForTypeAndIndex(Types type, int offset) {
    int cursor = getIndexForType(type)+offset;
    int index = TILE_COMBINATIONS[cursor];
    
    try {
      return TILE_MASK[index];
    } catch (ArrayIndexOutOfBoundsException e) {
      Gdx.app.log(TAG, "Error: " + e.toString());
      return 0;
    }
  }

  public AutoTile getAutoTile(Types type) {
    int id = Arrays.asList(TILE_TYPES).indexOf(type);
    return list.get(id);
  }

  public String getName() {
    return this.name;
  }
}
