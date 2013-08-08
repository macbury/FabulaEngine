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
  
  private Vector3 topLeftCorner;
  private Array<MeshRow> meshes;
  private Array<BoundingBox> boundingBoxes;
  private int currentRow = 0;
  private Terrain terrain;
  private Vector3 bottomRightCorner;
  private BoundingBox boundingBox;
  private float height = 0.0f;
  
  public Sector(Vector3 pos, Terrain terrain) {
    this.terrain           = terrain;
    this.topLeftCorner     = pos;
    this.meshes            = new Array<MeshRow>(Sector.ROW_COUNT);
    this.currentRow        = 0;
    this.bottomRightCorner = pos.cpy().add(COLUMN_COUNT, 0, ROW_COUNT);
    this.boundingBoxes     = new Array<BoundingBox>(Sector.ROW_COUNT);
  }
  
  public void clearSector() {
    this.currentRow = 0;
    this.meshes.clear();
  }
  
  public float buildRow() {
    int verticiesElementsCount = COLUMN_COUNT * TOTAL_ATTRIBUTES_COUNT;
    
    float y = 0;
    int i = 0;
    int x = (int) topLeftCorner.x;
    int z = (int) topLeftCorner.z + currentRow++;
    
    MeshRow meshRow = new MeshRow(verticiesElementsCount);
    
    while(meshRow.isBuilding(verticiesElementsCount)) {
      Tile tile                   = terrain.getTile(x, z);
      TextureRegion textureRegion = tile.getTextureRegion();
      if (tile.getY() > y) {
        y = tile.getY();
      }
      //Gdx.app.log("S", "ID: " + tile.getId() +" X: "+x + "Z: " + z +"  Y: " + tile.getY());
      /* Vertex 1 */
      meshRow.addVertex(x, tile.getY1(), z); // Vertext position
      meshRow.addColor(255, 255, 255, 0); // Color
      meshRow.addUVMap(textureRegion.getU(), textureRegion.getV()); // Texture Cords
      
      /* Vertex 2 */
      meshRow.addVertex(x, tile.getY2(), z+1f); // Vertext position
      meshRow.addColor(255, 255, 255, 0); // Color
      meshRow.addUVMap(textureRegion.getU(), textureRegion.getV2()); // Texture Cords
      
      /* Vertex 3 */
      meshRow.addVertex(x+1f, tile.getY3(), z); // Vertext position
      meshRow.addColor(255, 255, 255, 0); // Color
      meshRow.addUVMap(textureRegion.getU2(), textureRegion.getV()); // Texture Cords
      
      /* Vertex 4 */
      meshRow.addVertex(x+1f, tile.getY4(), z+1f); // Vertext position
      meshRow.addColor(255, 255, 255, 0); // Color
      meshRow.addUVMap(textureRegion.getU2(), textureRegion.getV2()); // Texture Cords
      x++;
    }
    
    meshRow.finish();
    this.boundingBoxes.add(meshRow.getMesh().calculateBoundingBox());
    this.meshes.add(meshRow);
    
    return y;
  }

  public void build() {
    this.clearSector();
    height = 0;
    for (int i = 0; i < Sector.ROW_COUNT; i++) {
      height = Math.max(height, this.buildRow());
    }
    
    this.boundingBox = new BoundingBox(this.topLeftCorner, this.bottomRightCorner.cpy().add(0, height, 0));
  }

  public void render(ShaderProgram terrainShader) { //TODO multi texture terrain shit should go here
    Tile tile = terrain.getTile(0, 0);
    //tile.getTextureRegion().getTexture().i
    tile.getTextureRegion().getTexture().bind(0);
    for (int i = 0; i < meshes.size; i++) {
      Mesh mesh = meshes.get(i).getMesh();
      mesh.render(terrainShader, GL20.GL_TRIANGLE_STRIP);
      //mesh.render(terrainShader, GL20.GL_LINE_STRIP);
    }
  }

  public boolean visibleInCamera(Camera camera) {
    return camera.frustum.boundsInFrustum(boundingBox);
  }

  public Vector3 getPositionForRay(Ray ray) {
    if (Intersector.intersectRayBoundsFast(ray, this.boundingBox)) {
      Vector3 intersectedVector = new Vector3();
      for (BoundingBox boundingBox : boundingBoxes) {
        if (Intersector.intersectRayBounds(ray, boundingBox, intersectedVector)) { //TODO check which mesh is to intersect wit thirangles
          return intersectedVector;
        }
      }
      
      /*for (MeshRow row : meshes) {
        if (Intersector.intersectRayTriangles(ray, row.getTriangles(), intersectedVector)) {
          return intersectedVector;
        }
      }*/
    }
    
    return null;
  }
}
