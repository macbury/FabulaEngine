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
    
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    short rowEnd    = (short) (ROW_COUNT + topLeftCorner.z);
    short columnEnd = (short) (COLUMN_COUNT+topLeftCorner.x);
    
    triangleGrid.begin();
      for (int z = (int) topLeftCorner.z; z < rowEnd; z++) {
        for (int x = (int) topLeftCorner.x; x < columnEnd; x++) {
          Tile tile = terrain.getTile(x, z);
          height    = Math.max(tile.getY(), height);
          TextureRegion textureRegion = tile.getTextureRegion();
          if (tile.getType() == Tile.Type.CornerTopRight || tile.getType() == Tile.Type.CornerBottomLeft) {
            /* Top left Vertex */
            n1 = triangleGrid.addVertex(x, tile.getY1(), z);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU(), textureRegion.getV());
            triangleGrid.addTextureIndex(255);
            
            /* Bottom left Vertex */
            n2 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU(), textureRegion.getV2());
            triangleGrid.addTextureIndex(0);
            
            /* Top Right Vertex */
            n3 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU2(), textureRegion.getV());
            triangleGrid.addTextureIndex(0);
            
            triangleGrid.addIndices(n1,n2,n3);
            
            /* Bottom right Vertex */
            n1 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU2(), textureRegion.getV2());
            triangleGrid.addTextureIndex(0);
            
            triangleGrid.addIndices(n3,n2,n1);
          } else {
            /* Top Right Vertex */
            n1 = triangleGrid.addVertex(x+1f, tile.getY3(), z);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU2(), textureRegion.getV());
            triangleGrid.addTextureIndex(0);
            
            /* Top left Vertex */
            n2 = triangleGrid.addVertex(x, tile.getY1(), z);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU(), textureRegion.getV());
            triangleGrid.addTextureIndex(0);
            
            /* Bottom right Vertex */
            n3 = triangleGrid.addVertex(x+1f, tile.getY4(), z+1f);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU2(), textureRegion.getV2());
            triangleGrid.addTextureIndex(0);
            
            triangleGrid.addIndices(n1,n2,n3);
            
            /* Bottom left Vertex */
            n1 = triangleGrid.addVertex(x, tile.getY2(), z+1f);
            triangleGrid.addColorToVertex(255, 255, 255, 255);
            triangleGrid.addUVMap(textureRegion.getU(), textureRegion.getV2());
            triangleGrid.addTextureIndex(0);
            
            triangleGrid.addIndices(n3,n2,n1);
          }
        }
      }
    triangleGrid.end();
    
    this.boundingBox = new BoundingBox(this.topLeftCorner, this.bottomRightCorner.cpy().add(0, height, 0));
  }

  public void render(ShaderProgram terrainShader) { //TODO multi texture terrain shit should go here
    Tile tile = terrain.getTile(0, 0);
    //tile.getTextureRegion().getTexture().i
    tile.getTextureRegion().getTexture().bind(0);
    triangleGrid.getMesh().render(terrainShader, GL20.GL_TRIANGLES);
  }

  public boolean visibleInCamera(Camera camera) {
    return camera.frustum.boundsInFrustum(boundingBox);
  }

  public Vector3 getPositionForRay(Ray ray) {
    Vector3 intersectedVector = new Vector3();
    /*if (Intersector.intersectRayBoundsFast(ray, this.boundingBox)) {
      Vector3 intersectedVector = new Vector3();
      /*for (BoundingBox boundingBox : boundingBoxes) {
        if (Intersector.intersectRayBounds(ray, boundingBox, intersectedVector)) { //TODO check which mesh is to intersect wit thirangles
          return intersectedVector;
        }
      }*/
      
      /*for (MeshRow row : meshes) {
        if (Intersector.intersectRayTriangles(ray, row.getTriangles(), intersectedVector)) {
          return intersectedVector;
        }
      }
    }*/
    
    if (Intersector.intersectRayTriangles(ray, triangleGrid.getVerties(), triangleGrid.getIndices(), triangleGrid.getVertexSize(), intersectedVector)) {
      return intersectedVector;
    } else {
      return null;
    }
  }
}
