package com.macbury.fabula.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.screens.WorldScreen;

public class Terrain {
  private Sector[][] sectors;
  private Tile[][] tiles;
  private WorldScreen screen;
  private int columns;
  private int rows;
  private ShaderProgram terrainShader;
  private Sector sector;
  private int horizontalSectorCount;
  private int veriticalSectorCount;
  private int totalSectorCount;
  private int visibleSectorCount;
  public Terrain(WorldScreen screen, int columns, int rows) {
    this.columns = columns;
    this.rows    = rows;
    
    this.tiles   = new Tile[columns][rows];
    fillEmptyTilesWithDebugTile();
    loadShaders();
    
    if (columns % Sector.COLUMN_COUNT != 0 || rows%Sector.ROW_COUNT != 0) {
      throw new RuntimeException("Map size must be proper!");
    }
    
    this.horizontalSectorCount = columns/Sector.COLUMN_COUNT;
    this.veriticalSectorCount  = rows/Sector.ROW_COUNT;
    this.totalSectorCount      = horizontalSectorCount * veriticalSectorCount;
    
    this.sectors = new Sector[horizontalSectorCount][veriticalSectorCount];
    
    for (int x = 0; x < horizontalSectorCount; x++) {
      for (int z = 0; z < veriticalSectorCount; z++) {
        Sector sector = new Sector(new Vector3(x * Sector.COLUMN_COUNT, 0, z * Sector.ROW_COUNT), this);
        sector.build();
        this.sectors[x][z] = sector;
      }
    }
  }

  private void loadShaders() {
    String vertexShader = Gdx.files.internal("data/shaders/mesh.vert").readString();
    String fragmentShader = Gdx.files.internal("data/shaders/mesh.frag").readString();
    terrainShader = new ShaderProgram(vertexShader, fragmentShader);
    if (!terrainShader.isCompiled())
      throw new IllegalStateException(terrainShader.getLog());
  }

  private void fillEmptyTilesWithDebugTile() {
    for (int x = 0; x < columns; x++) {
      for (int z = 0; z < rows; z++) {
        if (!haveTile(x,z)) {
          setTile(x, z, new Tile(x, 0, z));
        }
      }
    }
  }

  private boolean haveTile(int x, int z) {
    return getTile(x,z) != null;
  }

  public Tile getTile(int x, int z) {
    return this.tiles[x][z];
  }
  
  public void setTile(int x, int z, Tile tile) {
    this.tiles[x][z] = tile;
  }

  public void render(Camera camera) {
    GL20 gl = Gdx.graphics.getGL20();
    gl.glEnable(GL20.GL_TEXTURE_2D);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glActiveTexture(GL20.GL_TEXTURE0);
    
    visibleSectorCount = 0;
    
    terrainShader.begin();
    terrainShader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
    terrainShader.setUniformi("u_texture", 0);
    
    for (int x = 0; x < horizontalSectorCount; x++) {
      for (int z = 0; z < veriticalSectorCount; z++) {
        this.sectors[x][z].render(terrainShader);
        visibleSectorCount++;
      }
    }
    terrainShader.end();
    
    gl.glDisable(GL10.GL_CULL_FACE); // TODO: this must to be disabled to show sprite batch duh
  }
  
  public int getTotalSectorCount() {
    return totalSectorCount;
  }

  public int getVisibleSectorCount() {
    return this.visibleSectorCount;
  }
}

// Terrain -hm-> Sectors -hm-> Rows -hm-> Columns