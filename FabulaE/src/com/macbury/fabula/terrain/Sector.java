package com.macbury.fabula.terrain;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Sector {
  public final static int ROW_COUNT               = 10;
  public final static int COLUMN_COUNT            = 10;
  public final static int VERTEX_PER_BOX_COUNT    = 4;
  public final static int VERTEX_ATTRIBUTE_COUNT  = 6;
  public final static int TOTAL_ATTRIBUTES_COUNT  = VERTEX_PER_BOX_COUNT * VERTEX_ATTRIBUTE_COUNT;
  public final static int VERTEX_PER_ROW          = VERTEX_PER_BOX_COUNT * COLUMN_COUNT;
  
  private Vector3 position;
  private Array<Mesh> meshes;
  private int currentRow = 0;
  private Terrain terrain;
  
  public Sector(Vector3 pos, Terrain terrain) {
    this.terrain  = terrain;
    this.position = pos;
    this.meshes   = new Array<Mesh>(Sector.ROW_COUNT);
    this.currentRow  = 0;
  }
  
  public void clearSector() {
    this.currentRow = 0;
    this.meshes.clear();
  }
  
  public void buildRow() {
    int verticiesElementsCount = COLUMN_COUNT * TOTAL_ATTRIBUTES_COUNT;
    float[] verticies = new float[verticiesElementsCount];
    
    int i = 0;
    int x = (int) position.x;
    int z = (int) position.z + currentRow++;
    
    while(i < verticiesElementsCount) {
      Tile tile                   = terrain.getTile(x, z);
      TextureRegion textureRegion = tile.getTextureRegion();
      Gdx.app.log("S", "ID: " + tile.getId() +" X: "+x + "Z: " + z +"  Y: " + tile.getY());
      /* Vertex 1 */
      verticies[i++] = x; // X
      verticies[i++] = tile.getY(); // Y
      verticies[i++] = z; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU(); // U
      verticies[i++] = textureRegion.getV(); // V
      
      /* Vertex 2 */
      verticies[i++] = x; // X
      verticies[i++] = tile.getY(); // Y
      verticies[i++] = z + 1f; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU(); // U
      verticies[i++] = textureRegion.getV2(); // V
      
      /* Vertex 3 */
      verticies[i++] = x + 1f; // X
      verticies[i++] = tile.getY(); // Y
      verticies[i++] = z; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU2(); // U
      verticies[i++] = textureRegion.getV(); // V
      
      /* Vertex 4 */
      verticies[i++] = x + 1f; // X
      verticies[i++] = tile.getY(); // Y
      verticies[i++] = z+1f; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU2(); // U
      verticies[i++] = textureRegion.getV2(); // V
      
      x++;
    }
    
    Mesh mesh = new Mesh(true, VERTEX_PER_ROW, 0, 
      new VertexAttribute(Usage.Position, 3, "a_position"),
      new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
      new VertexAttribute(Usage.TextureCoordinates, 2, "a_textCords")
    );
    mesh.setVertices(verticies);
    this.meshes.add(mesh);
  }

  public void build() {
    this.clearSector();
    for (int i = 0; i < Sector.ROW_COUNT; i++) {
      this.buildRow();
    }
  }

  public void render(ShaderProgram terrainShader) { //TODO multi texture terrain shit should go here
    Tile tile = terrain.getTile(0, 0);
    //tile.getTextureRegion().getTexture().i
    tile.getTextureRegion().getTexture().bind(0);
    for (int i = 0; i < meshes.size; i++) {
      Mesh mesh = meshes.get(i);
      mesh.render(terrainShader, GL20.GL_TRIANGLE_STRIP);
    }
  }
}
