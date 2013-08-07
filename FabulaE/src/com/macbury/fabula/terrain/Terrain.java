package com.macbury.fabula.terrain;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.screens.WorldScreen;

public class Terrain {
  private Sector[][] sectors;
  private Tile[][] tiles;
  private int columns;
  private int rows;
  private ShaderProgram terrainShader;
  private int horizontalSectorCount;
  private int veriticalSectorCount;
  private int totalSectorCount;
  private int visibleSectorCount;
  
  private Stack<Sector> visibleSectors;
  
  private Vector3 intersection = new Vector3();
  
  public Terrain(WorldScreen screen, int columns, int rows) {
    this.columns = columns;
    this.rows    = rows;
    
    this.tiles   = new Tile[columns][rows];
    fillEmptyTilesWithDebugTile();
    terrainShader = ResourceManager.shared().getShaderProgram("SHADER_TERRAIN");
    
    if (columns % Sector.COLUMN_COUNT != 0 || rows%Sector.ROW_COUNT != 0) {
      throw new RuntimeException("Map size must be proper!");
    }
    
    this.horizontalSectorCount = columns/Sector.COLUMN_COUNT;
    this.veriticalSectorCount  = rows/Sector.ROW_COUNT;
    this.totalSectorCount      = horizontalSectorCount * veriticalSectorCount;
    
    this.sectors        = new Sector[horizontalSectorCount][veriticalSectorCount];
    this.visibleSectors = new Stack<Sector>();
    
    for (int x = 0; x < horizontalSectorCount; x++) {
      for (int z = 0; z < veriticalSectorCount; z++) {
        Sector sector = new Sector(new Vector3(x * Sector.COLUMN_COUNT, 0, z * Sector.ROW_COUNT), this);
        sector.build();
        this.sectors[x][z] = sector;
      }
    }
  }

  private void fillEmptyTilesWithDebugTile() {
    for (int z = 0; z < rows; z++) {
      for (int x = 0; x < columns; x++) {
        if (!haveTile(x,z)) {
          setTile(x, z, new Tile(x, 0, z));
        }
      }
    }
    
    this.tiles[0][0].setY(1);
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
    visibleSectors.clear();
    
    for (int x = 0; x < horizontalSectorCount; x++) {
      for (int z = 0; z < veriticalSectorCount; z++) {
        Sector sector = this.sectors[x][z]; 
        if (sector.visibleInCamera(camera)) {
          sector.render(terrainShader);
          visibleSectors.add(sector);
          visibleSectorCount++;
        }
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

  public Vector3 getPositionForRay(Ray ray) {
    Vector3 intersectedVector = null;
    for (Sector sector : visibleSectors) {
      intersectedVector = sector.getPositionForRay(ray);
      
      if (intersectedVector != null) {
        break;
      }
    }
    return intersectedVector;
  }
}
