package com.macbury.fabula.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class Sector {
  public final static int ROW_COUNT               = 10;
  public final static int COLUMN_COUNT            = 10;
  public final static int VERTEX_PER_BOX_COUNT    = 4;
  public final static int VERTEX_ATTRIBUTE_COUNT  = 6;
  public final static int TOTAL_ATTRIBUTES_COUNT  = VERTEX_PER_BOX_COUNT * VERTEX_ATTRIBUTE_COUNT;
  public final static int VERTEX_PER_ROW          = VERTEX_PER_BOX_COUNT * COLUMN_COUNT;
  
  private Vector3 bottomRightCorner;
  private Vector3 topLeftCorner;
  private Terrain terrain;
  private BoundingBox boundingBox;
  private float height = 0.0f;
  private TriangleGrid triangleGrid;
  
  public Sector(Vector3 pos, Terrain terrain) {
    this.terrain           = terrain;
    this.topLeftCorner     = pos;
    this.bottomRightCorner = pos.cpy().add(COLUMN_COUNT, 0, ROW_COUNT);
    this.triangleGrid      = new TriangleGrid(COLUMN_COUNT, ROW_COUNT, false); //TODO: change to static for non world edit
  }

  public void build() {
    height = 0;
 
    
    short rowEnd    = (short) (ROW_COUNT + topLeftCorner.z);
    short columnEnd = (short) (COLUMN_COUNT+topLeftCorner.x);
    
    TileTransformer transformer = new TileTransformer();
    //TileUVMap uvMap = new TileUVMap();
    triangleGrid.begin();
      for (int z = (int) topLeftCorner.z; z < rowEnd; z++) {
        for (int x = (int) topLeftCorner.x; x < columnEnd; x++) {
          Tile tile = terrain.getTile(x, z);
          transformer.setTile(tile);
          tile.calculateHeight();
          height    = Math.max(tile.getY(), height);
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
          }/*
          if (tile.getType() == Tile.Type.CornerTopRight || tile.getType() == Tile.Type.CornerBottomLeft) {
            createCornerTop(tile);
          } else if (true) {
            createBottomMeshTile(tile);
          } else if (false) {
            createTopMeshTile(tile);
          } else if (false) {
            createRightMeshTile(tile);
          } else {
            createLeftMeshTile(tile);
          }*/
        }
      }
    triangleGrid.end();
    
    this.boundingBox = new BoundingBox(this.topLeftCorner, this.bottomRightCorner.cpy().add(0, height, 0));
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
    n1 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Top left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Bottom right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }

  private void createEdgeBottomLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top right Vertex */
    n2 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom left Vertex */
    n3 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }

  private void createEdgeTopLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top right Vertex */
    n2 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom left Vertex */
    n3 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
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
    n1 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Top left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Bottom right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
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
    n1 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Top left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Bottom right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerBottomLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top right Vertex */
    n2 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom left Vertex */
    n3 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerTopRightMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* bottom right Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top right Vertex */
    n2 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom left Vertex */
    n3 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerTopLeftMeshTile(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* Top right Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom Right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
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
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top right Vertex */
    n2 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom left Vertex */
    n3 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* top left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n2,n1,n3);
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
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* bottom left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* top right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* bottom left Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
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
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Bottom left Vertex */
    n3 = triangleGrid.addVertex(x, tile.getY2(), z+1);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Top right Vertex */
    n2 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Top left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
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
    n1 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Top left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Bottom right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }

  private void createCornerTop(Tile tile) {
    TextureRegion uvMap = tile.getTextureRegion();
    float x  = tile.getX();
    float z  = tile.getZ();
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    /* Top left Vertex */
    n1 = triangleGrid.addVertex(x, tile.getY1(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Bottom left Vertex */
    n2 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    /* Top Right Vertex */
    n3 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n1,n2,n3);
    
    /* Bottom right Vertex */
    n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
    triangleGrid.addColorToVertex(255, 255, 255, 255);
    triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
    triangleGrid.addNormal();
    if (terrain.isDebuging()) {
      triangleGrid.addTilePos(tile.getX(), tile.getZ());
    }
    
    triangleGrid.addIndices(n3,n2,n1);
  }


  public boolean visibleInCamera(Camera camera) {
    return camera.frustum.boundsInFrustum(boundingBox);
  }

  public Vector3 getPositionForRay(Ray ray, Vector3 mouseTilePosition) {
    if (Intersector.intersectRayTriangles(ray, triangleGrid.getVerties(), triangleGrid.getIndices(), triangleGrid.getVertexSize(), mouseTilePosition)) {
      return mouseTilePosition;
    } else {
      return null;
    }
  }

  public Mesh getMesh() {
    return this.triangleGrid.getMesh();
  }
}
