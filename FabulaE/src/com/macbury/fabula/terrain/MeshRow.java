package com.macbury.fabula.terrain;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MeshRow {
  private Mesh mesh;
  private List<Vector3> triangles;
  private float[] verticies;
  private short cursor;
  
  public MeshRow(int verticiesElementsCount) {
    this.triangles              = new ArrayList<Vector3>();
    this.verticies              = new float[verticiesElementsCount];
    this.cursor                 = 0;
    this.mesh                   = new Mesh(true, Sector.VERTEX_PER_ROW, 0, 
      new VertexAttribute(Usage.Position, 3, "a_position"),
      new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
      new VertexAttribute(Usage.TextureCoordinates, 2, "a_textCords")
    );
  }
  
  public Mesh getMesh() {
    return mesh;
  }

  public void setMesh(Mesh mesh) {
    this.mesh = mesh;
  }

  public void addVertex(float x, float y, float z) {
    triangles.add(new Vector3(x,y,z)); 
    this.verticies[cursor++] = x;
    this.verticies[cursor++] = y;
    this.verticies[cursor++] = z;
  }
  
  public void addColor(int r, int g, int b, int a) {
    this.verticies[cursor++] = Color.toFloatBits(r, g, b, a);
  }
  
  public void addUVMap(float u, float v) {
    this.verticies[cursor++] = u;
    this.verticies[cursor++] = v;
  }

  public boolean isBuilding(int verticiesElementsCount) {
    return cursor < verticiesElementsCount;
  }

  public void finish() {
    mesh.setVertices(verticies);
    verticies = null;
    cursor = 0;
  }
  
  public List<Vector3> getTriangles() {
    return triangles;
  }
}
