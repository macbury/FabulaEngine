package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class TriangleGrid {
  ///private static final int ATTRIBUTES_PER_VERTEXT = 9; // Only Position
  private static final int VERTEXT_PER_COL        = 4;
  private int rows;
  private int columns;
  private short vertexCursor;
  private short vertexIndex;
  private short indicesCursor;
  private float[] verties;
  private short[] indices;
  private Mesh mesh;
  private int attributes_per_vertex;
  
  public TriangleGrid(int width, int height, boolean isStatic, int attr_count) {
    this.attributes_per_vertex = attr_count;
    this.rows          = height;
    this.columns       = width;
    int vertextCount   = rows*columns* VERTEXT_PER_COL;
    this.verties       = new float[vertextCount * getAttributesPerVertex()];
    this.indices       = new short[vertextCount * 3];
    this.mesh = new Mesh(isStatic, verties.length, indices.length, 
      new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
      new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
      new VertexAttribute(Usage.TextureCoordinates, 2, "a_textCords"),
      new VertexAttribute(Usage.Generic, 1, "a_textureNumber"),
      new VertexAttribute(Usage.Generic, 2, "a_tile_position")
    );
  }
  
  public int getAttributesPerVertex() {
    return this.attributes_per_vertex;
  }
  
  public int getVertexSize() {
    return getAttributesPerVertex()-1;
  }
  
  public void begin() {
    this.vertexCursor  = 0;
    this.indicesCursor = 0;
    this.vertexIndex   = 0;
  }
  
  public short addVertex(float x, float y, float z) {
    this.verties[vertexCursor++] = x;
    this.verties[vertexCursor++] = y;
    this.verties[vertexCursor++] = z;
    return vertexIndex++;
  }
  
  public void addTextureIndex(float i) {
    this.verties[vertexCursor++] = i;
  }
  
  public void addTilePos(float x, float z) {
    this.verties[vertexCursor++] = x;
    this.verties[vertexCursor++] = z;
  }
  
  public void addColorToVertex(int r, int g, int b, int a) {
    this.verties[vertexCursor++] = Color.toFloatBits(r, g, b, a);
  }
  
  public void addUVMap(float u, float v) {
    this.verties[vertexCursor++] = u;
    this.verties[vertexCursor++] = v;
  }
  
  public void addRectangle(float x, float y, float z, float width, float height) {
    short n1 = this.addVertex(x, y, z); // top left corner
    short n2 = this.addVertex(x, y, z+1f); // bottom left corner
    short n3 = this.addVertex(x+1f, y, z); // top right corner
    addIndices(n1,n2,n3);
    
    n1 = this.addVertex(x+1f, y, z+1f);
    addIndices(n3,n2,n1);
  }
  
  public void addIndices(short n1, short n2, short n3) {
    this.indices[indicesCursor++] = n1;
    this.indices[indicesCursor++] = n2;
    this.indices[indicesCursor++] = n3;
  }

  public void end() {
    mesh.setVertices(verties);
    mesh.setIndices(indices);
  }

  public float[] getVerties() {
    return verties;
  }

  public short[] getIndices() {
    return indices;
  }

  
  public Mesh getMesh() {
    return mesh;
  }

  
}
