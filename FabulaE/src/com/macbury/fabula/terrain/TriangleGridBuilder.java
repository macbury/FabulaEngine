package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TriangleGridBuilder {
  private static final int ATTRIBUTES_PER_VERTEXT = 7; // Only Position
  private static final int VERTEXT_PER_COL        = 4;
  private int rows;
  private int columns;
  private short vertexCursor;
  private short vertexIndex;
  private short indicesCursor;
  private float[] verties;
  private short[] indices;
  
  public TriangleGridBuilder(int width, int height) {
    this.rows    = height;
    this.columns = width;
  }
  
  public void begin() {
    int vertextCount   = rows*columns* VERTEXT_PER_COL;
    this.verties       = new float[vertextCount * ATTRIBUTES_PER_VERTEXT];
    this.indices       = new short[vertextCount * 3];
    this.vertexCursor  = 0;
    this.indicesCursor = 0;
  }
  
  public short addVertex(float x, float y, float z) {
    this.verties[vertexCursor++] = x;
    this.verties[vertexCursor++] = y;
    this.verties[vertexCursor++] = z;
    return vertexIndex++;
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
    
  }

  public Mesh getMesh() {
    Mesh mesh = new Mesh(true, verties.length, indices.length, 
      new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
      new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
      new VertexAttribute(Usage.TextureCoordinates, 2, "a_textCords")
    );
    mesh.setVertices(verties);
    mesh.setIndices(indices);
    return mesh;
  }
}
