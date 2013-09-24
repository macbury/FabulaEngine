package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.terrain.foliage.Foliage;
import com.macbury.fabula.terrain.foliage.FoliageDescriptor;
import com.macbury.fabula.terrain.foliage.FoliageRenderable;
import com.macbury.fabula.terrain.geometry.TriangleGrid;
import com.macbury.fabula.terrain.geometry.TriangleGrid.AttributeType;
import com.macbury.fabula.terrain.tile.Tile;
import com.macbury.fabula.terrain.tile.TileTransformer;
import com.macbury.fabula.terrain.water.Water;
import com.macbury.fabula.terrain.water.WaterRenderable;

public class Sector implements Disposable {
  public final static int ROW_COUNT               = 5;
  public final static int COLUMN_COUNT            = 5;
  public final static int VERTEX_PER_BOX_COUNT    = 4;
  public final static int VERTEX_ATTRIBUTE_COUNT  = 6;
  public final static int TOTAL_ATTRIBUTES_COUNT  = VERTEX_PER_BOX_COUNT * VERTEX_ATTRIBUTE_COUNT;
  public final static int VERTEX_PER_ROW          = VERTEX_PER_BOX_COUNT * COLUMN_COUNT;
  
  private Vector3 bottomRightCorner;
  private Vector3 topLeftCorner;
  private Terrain terrain;
  private BoundingBox boundingBox;
  
  private TriangleGrid terrainTriangleGrid;
  private TerrainRenderable terrainRenderable;
  private TriangleGrid waterTriangleGrid;
  private WaterRenderable waterRenderable;
  private FoliageRenderable foliageRenderable;
  private TriangleGrid foliageTriangleGrid;
  
  public Sector(Vector3 pos, Terrain terrain) {
    this.terrain                  = terrain;
    this.topLeftCorner            = pos;
    this.bottomRightCorner        = pos.cpy().add(COLUMN_COUNT, 0, ROW_COUNT);
    this.terrainTriangleGrid      = new TriangleGrid(COLUMN_COUNT, ROW_COUNT, false); //TODO: change to static for non world edit
    this.waterTriangleGrid        = new TriangleGrid(COLUMN_COUNT, ROW_COUNT, false);
    this.foliageTriangleGrid      = new TriangleGrid(COLUMN_COUNT*2, ROW_COUNT*2, false);
  }

  public int getStartX() {
    return (int)this.topLeftCorner.x;
  }
  
  public int getStartZ() {
    return (int)this.topLeftCorner.z;
  }
  
  public int getEndX() {
    return (int)this.bottomRightCorner.x;
  }
  
  public int getEndZ() {
    return (int)this.bottomRightCorner.z;
  }
  
  public void build() {
    float minHeight = 0.0f;
    float maxHeight = 1.0f;
    
    short rowEnd    = (short) (ROW_COUNT + topLeftCorner.z);
    short columnEnd = (short) (COLUMN_COUNT+topLeftCorner.x);
    
    TileTransformer transformer = new TileTransformer();
    this.terrainRenderable      = null;
    this.waterRenderable        = null;
    this.foliageRenderable      = null;
    
    terrainTriangleGrid.using(AttributeType.Position);
    terrainTriangleGrid.using(AttributeType.TextureCord);
    
    waterTriangleGrid.using(AttributeType.Position);
    waterTriangleGrid.using(AttributeType.Color);
    waterTriangleGrid.using(AttributeType.Normal);
    
    foliageTriangleGrid.using(AttributeType.Position);
    foliageTriangleGrid.using(AttributeType.TextureCord);
    foliageTriangleGrid.using(AttributeType.Color);
    
    if (terrain.isDebuging()) {
      terrainTriangleGrid.using(AttributeType.TilePosition);
    }
    
    waterTriangleGrid.begin();
    terrainTriangleGrid.begin();
    foliageTriangleGrid.begin();
      for (int z = (int) topLeftCorner.z; z < rowEnd; z++) {
        for (int x = (int) topLeftCorner.x; x < columnEnd; x++) {
          Tile tile = terrain.getTile(x, z);
          transformer.setTile(tile);
          tile.calculateHeight();
          maxHeight    = Math.max(tile.getY(), maxHeight);
          minHeight    = Math.min(tile.getY(), minHeight);
          
          createTerrainTileGeometry(tile);
          if (tile.isLiquid()) {
            createLiquidTileGeometry(tile);
          }
          
          if (tile.haveFoliage()) {
            createFoliageGeometry(tile);
          }
        }
      }
    
    
    Vector3 firstCorner = this.topLeftCorner.cpy();
    firstCorner.y       = maxHeight;
    this.boundingBox    = new BoundingBox(firstCorner, this.bottomRightCorner.cpy().add(0, minHeight, 0));
    foliageTriangleGrid.end();
    terrainTriangleGrid.end();
    waterTriangleGrid.end();

  }

  private void createFoliageGeometry(Tile tile) {
    FoliageDescriptor descriptor   = tile.getFoliage();
    short n1, n2, n3 = 0;
    float y = tile.getY();
    float z = tile.getZ();
    float x = tile.getX();
    float animated = descriptor.isAnimated() ? 1.0f : 0.0f;
    
    TextureRegion uvMap = descriptor.getRegion();
    
    float w  = uvMap.getRegionWidth() / Tile.TILE_SIZE_IN_PIXELS;
    float sz = z + 0.5f - w/2;
    float ez = z + 0.5f + w/2;
    
    float lx = x + 0.5f - w/2;
    float rx = x + 0.5f + w/2;
    float h  = uvMap.getRegionHeight() / Tile.TILE_SIZE_IN_PIXELS;
    float sy = y;
    float ey = y + h;
    
    /* Bottom Left Vertex */
    n1 = foliageTriangleGrid.addVertex(lx, sy, sz);
    foliageTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    foliageTriangleGrid.addColorToVertex(0, 0, 0, 0);
    /* Top left Vertex */
    n2 = foliageTriangleGrid.addVertex(lx, ey, sz);
    foliageTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    foliageTriangleGrid.addColorToVertex(animated, 0, 0, 0);
    /* Bottom right Vertex */
    n3 = foliageTriangleGrid.addVertex(rx, sy, ez);
    foliageTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    foliageTriangleGrid.addColorToVertex(0, 0, 0, 0);
    foliageTriangleGrid.addIndices(n1,n2,n3);

    /* Top Right Vertex */
    n1 = foliageTriangleGrid.addVertex(rx, ey, ez);
    foliageTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    foliageTriangleGrid.addColorToVertex(animated, 0, 0, 0);
    foliageTriangleGrid.addIndices(n3,n2,n1);
    
    // next gex
    
    /* Bottom Left Vertex */
    n1 = foliageTriangleGrid.addVertex(lx, sy, ez);
    foliageTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    foliageTriangleGrid.addColorToVertex(0, 0, 0, 0);
    /* Top left Vertex */
    n2 = foliageTriangleGrid.addVertex(lx, ey, ez);
    foliageTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    foliageTriangleGrid.addColorToVertex(animated, 0, 0, 0);
    /* Bottom right Vertex */
    n3 = foliageTriangleGrid.addVertex(rx, sy, sz);
    foliageTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    foliageTriangleGrid.addColorToVertex(0, 0, 0, 0);
    foliageTriangleGrid.addIndices(n1,n2,n3);

    /* Top Right Vertex */
    n1 = foliageTriangleGrid.addVertex(rx, ey, sz);
    foliageTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    foliageTriangleGrid.addColorToVertex(animated, 0, 0, 0);
    foliageTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createLiquidTileGeometry(Tile tile) {
    short n1, n2, n3 = 0;
    
    /* Top right Vertex */
    n1 = waterTriangleGrid.addVertex(tile.getX()+1f, tile.getLiquidHeight(), tile.getZ());
    waterTriangleGrid.addColorToVertex(1, 0, 0, 1);
    waterTriangleGrid.addNormal();
    /* top left Vertex */
    n2 = waterTriangleGrid.addVertex(tile.getX(), tile.getLiquidHeight(), tile.getZ());
    waterTriangleGrid.addColorToVertex(0, 1, 0, 1);
    waterTriangleGrid.addNormal();
    /* bottom Right Vertex */
    n3 = waterTriangleGrid.addVertex(tile.getX()+1f, tile.getLiquidHeight(), tile.getZ()+1f);
    waterTriangleGrid.addColorToVertex(0, 0, 1, 1);
    waterTriangleGrid.addNormal();
    
    waterTriangleGrid.addIndices(n1,n2,n3);
    /* Bottom left Vertex */
    n1 = waterTriangleGrid.addVertex(tile.getX(), tile.getLiquidHeight(), tile.getZ()+1f);
    waterTriangleGrid.addColorToVertex(0, 0, 0, 0);
    waterTriangleGrid.addNormal();
    
    waterTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createTerrainTileGeometry(Tile tile) {
    if (tile.getAutoTiles().isSlope()) {
      switch (tile.getSlope()) {
        case CornerTopLeft:
          createCornerTopLeftMeshTile(tile);
        break;
        
        case CornerTopRight:
          createCornerTopRightMeshTile(tile);
        break;
        
        case CornerBottomLeft:
          createCornerBottomLeftMeshTile(tile);
        break;
        
        case CornerBottomRight:
          createCornerBottomRightMeshTile(tile);
        break;
        
        case EdgeBottomRight:
          createEdgeBottomRightMeshTile(tile);
        break;
        
        case EdgeBottomLeft:
          createEdgeBottomLeftMeshTile(tile);
        break;
        
        case EdgeTopLeft:
          createEdgeTopLeftMeshTile(tile);
        break;
        
        case EdgeTopRight:
          createEdgeTopRightMeshTile(tile);
        break;
        
        case Up:
          createTopMeshTile(tile);
        break;
        
        case Down:
          createBottomMeshTile(tile);
        break;
        
        case Left:
          createLeftMeshTile(tile);
        break;
        
        case Right:
          createRightMeshTile(tile);
        break;
        
        default:
          createBottomMeshTile(tile);
        break;
      }
    } else {
      createBottomMeshTile(tile);
    }
  }

  private void createEdgeTopRightMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    //bottom
    /* Top Right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    ////terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    ////terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Top left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      ////terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Bottom right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      ////terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createEdgeBottomLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top right Vertex */
    n2 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom left Vertex */
    n3 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createEdgeTopLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top right Vertex */
    n2 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom left Vertex */
    n3 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createEdgeBottomRightMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    //bottom
    /* Top Right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Top left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Bottom right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerBottomRightMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    //bottom
    /* Top Right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Top left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Bottom right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerBottomLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top right Vertex */
    n2 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom left Vertex */
    n3 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerTopRightMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top right Vertex */
    n2 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom left Vertex */
    n3 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerTopLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* Top right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom Right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
 // horizontal left
    /* bottom right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top right Vertex */
    n2 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom left Vertex */
    n3 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* top left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n2,n1,n3);
  }

  private void createRightMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
 // horizontal right
    /* Top left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* bottom left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* top right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createTopMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    // Top
    /* Bottom Right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Bottom left Vertex */
    n3 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Top right Vertex */
    n2 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Top left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createBottomMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    //bottom
    /* Top Right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Top left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Bottom right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerTop(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* Top left Vertex */
    n1 = terrainTriangleGrid.addVertex(x, tile.getY1(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Bottom left Vertex */
    n2 = terrainTriangleGrid.addVertex(x, tile.getY2(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    /* Top Right Vertex */
    n3 = terrainTriangleGrid.addVertex(x+1f, tile.getY3(), z);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom right Vertex */
    n1 = terrainTriangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    //terrainTriangleGrid.addColorToVertex(255, 255, 255, 255);
    terrainTriangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    //terrainTriangleGrid.addNormal();
    if (terrain.isDebuging()) {
      terrainTriangleGrid.addTilePos(tile.getX(), tile.getZ());
      //terrainTriangleGrid.addPassableInfo(tile.isPassable());
    }
    
    terrainTriangleGrid.addIndices(n3,n2,n1);
  }


  public boolean visibleInCamera(Camera camera) {
    return camera.frustum.boundsInFrustum(boundingBox);
  }

  public Vector3 getPositionForRay(Ray ray, Vector3 mouseTilePosition) {
    if (Intersector.intersectRayTriangles(ray, terrainTriangleGrid.getVerties(), terrainTriangleGrid.getIndices(), terrainTriangleGrid.getVertexSize(), mouseTilePosition)) {
      return mouseTilePosition;
    } else {
      return null;
    }
  }
  
  public TerrainRenderable getTerrainRenderable(TerrainShader terrainShader) {
    if (terrainRenderable == null) {
      this.terrainRenderable                = new TerrainRenderable();
      this.terrainRenderable.mesh           = terrainTriangleGrid.getMesh();
      this.terrainRenderable.meshPartOffset = 0;
      this.terrainRenderable.meshPartSize   = this.terrainRenderable.mesh.getNumIndices();
      this.terrainRenderable.primitiveType  = GL20.GL_TRIANGLES;
    }
    this.terrainRenderable.material       = terrainShader.getMaterial();
    this.terrainRenderable.shader         = terrainShader;
    
    return terrainRenderable;
  }
  
  public WaterRenderable getWaterRenderable(Water water) {
    if (waterRenderable == null && waterTriangleGrid.haveMeshData()) {
      this.waterRenderable                = new WaterRenderable();
      this.waterRenderable.mesh           = waterTriangleGrid.getMesh();
      this.waterRenderable.meshPartOffset = 0;
      this.waterRenderable.meshPartSize   = this.waterRenderable.mesh.getNumIndices();
      this.waterRenderable.primitiveType  = GL20.GL_TRIANGLES;
    }
    
    if (waterRenderable != null) {
      this.waterRenderable.shader   = water.getShader();
      this.waterRenderable.material = water.getMaterial();
    }
    
    return waterRenderable;
  }
  
  public FoliageRenderable getFoliageRenderable(Foliage foliage) {
    if (foliageRenderable == null && foliageTriangleGrid.haveMeshData()) {
      this.foliageRenderable                = new FoliageRenderable();
      this.foliageRenderable.mesh           = foliageTriangleGrid.getMesh();
      this.foliageRenderable.meshPartOffset = 0;
      this.foliageRenderable.meshPartSize   = this.foliageRenderable.mesh.getNumIndices();
      this.foliageRenderable.primitiveType  = GL20.GL_TRIANGLES;
    }
    
    if (foliageRenderable != null) {
      this.foliageRenderable.shader   = foliage.getShader();
      this.foliageRenderable.material = foliage.getMaterial();
    }
    
    return foliageRenderable;
  }

  @Override
  public void dispose() {
    this.terrainTriangleGrid.dispose();
    this.waterTriangleGrid.dispose();
    this.foliageTriangleGrid.dispose();
    if (terrainRenderable != null) {
      terrainRenderable.mesh.dispose();
    }
    
    if (this.waterRenderable != null) {
      waterRenderable.mesh.dispose();
    }
    
    if (this.foliageRenderable != null) {
      foliageRenderable.mesh.dispose();
    }
  }
  
  public BoundingBox getBounds() {
    return boundingBox;
  }

  public boolean containsTilePosition(float x, float z) {
    return (x >= getStartX() && x <= getEndX() && z >= getStartZ() && z <= getEndZ());
  }

  public void cleanRenderableData() {
    this.terrainRenderable = null;
  }
}
