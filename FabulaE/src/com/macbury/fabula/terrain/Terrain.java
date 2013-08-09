package com.macbury.fabula.terrain;

import java.util.ArrayList;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
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
  private ArrayList<Sector> rebuildSectorsArray = new ArrayList<>();
  private Vector3 intersection = new Vector3();
  
  public Terrain(WorldScreen screen, int columns, int rows) {
    this.columns = columns;
    this.rows    = rows;
    
    this.tiles    = new Tile[columns][rows];
    terrainShader = ResourceManager.shared().getShaderProgram("SHADER_TERRAIN");
    
    if (columns % Sector.COLUMN_COUNT != 0 || rows%Sector.ROW_COUNT != 0) {
      throw new RuntimeException("Map size must be proper!");
    }
    
    this.horizontalSectorCount = columns/Sector.COLUMN_COUNT;
    this.veriticalSectorCount  = rows/Sector.ROW_COUNT;
    this.totalSectorCount      = horizontalSectorCount * veriticalSectorCount;
    
    this.sectors        = new Sector[horizontalSectorCount][veriticalSectorCount];
    this.visibleSectors = new Stack<Sector>();
    
  }

  public void buildSectors() {
    for (int x = 0; x < horizontalSectorCount; x++) {
      for (int z = 0; z < veriticalSectorCount; z++) {
        Sector sector = new Sector(new Vector3(x * Sector.COLUMN_COUNT, 0, z * Sector.ROW_COUNT), this);
        sector.build();
        this.sectors[x][z] = sector;
      }
    }
    
    System.gc();
  }
  
  public void fillEmptyTilesWithDebugTile() {
    for (int z = 0; z < rows; z++) {
      for (int x = 0; x < columns; x++) {
        if (!haveTile(x,z)) {
          setTile(x, z, new Tile(x, 0, z));
        }
      }
    }
    
    /*this.tiles[0][0].setY(1);
    this.tiles[0][1].setY(1);
    this.tiles[1][0].setY(1);
    this.tiles[1][1].setY(1);
    */
  }

  private boolean haveTile(int x, int z) {
    return getTile(x,z) != null;
  }

  public Tile getTile(int x, int z) {
    try {
      return this.tiles[x][z];
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }
  
  public void setTile(int x, int z, Tile tile) {
    this.tiles[x][z] = tile;
  }

  public void render(Camera camera) {
    GL20 gl = Gdx.graphics.getGL20();
    gl.glEnable(GL10.GL_DEPTH_TEST);
    gl.glEnable(GL20.GL_TEXTURE_2D);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glActiveTexture(GL20.GL_TEXTURE0);
    
    visibleSectorCount = 0;
    
    terrainShader.begin();
    terrainShader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
    terrainShader.setUniformi("u_texture0", 0);
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
    gl.glDisable(GL10.GL_DEPTH_TEST);
    gl.glDisable(GL20.GL_TEXTURE_2D);
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
  
  public void buildTerrainUsingImageHeightMap(String pathToHeightMap) {
    FileHandle file       = Gdx.files.local(pathToHeightMap);
    Pixmap heightmapImage = new Pixmap(file);
    Color color           = new Color();
    
    for (int z = 0; z < rows; z++) {
      for (int x = 0; x < columns; x++) {
        Color.rgba8888ToColor(color, heightmapImage.getPixel(x, z));
        setTile(x, z, new Tile(x, color.r*30, z));
      }
    }
  }

  public Vector3 getSnappedPositionForRay(Ray ray) {
    Vector3 pos = getPositionForRay(ray);
    if (pos != null) {
      Tile tile = getTile(pos);
      
      float y = Math.max(0, pos.y);
      if (tile != null) {
        y = tile.getY();
      }
      pos.set((float)Math.floor(pos.x)+1, y, (float)Math.floor(pos.z)+1);
      return pos;
    } else {
      return null;
    }
  }

  public Tile getTile(Vector3 pos) {
    return getTile((int)pos.x, (int)pos.z);
  }
  
  public Sector getSectorForTile(Tile tile) {
    int x = (int) Math.floor(tile.getX() / Sector.COLUMN_COUNT);
    int z = (int) Math.floor(tile.getZ() / Sector.ROW_COUNT);
    return sectors[x][z];
  }
  
  public void addSectorToRebuildFromTile(Tile tile) {
    Sector sector = getSectorForTile(tile);
    if (sector != null && this.rebuildSectorsArray.indexOf(sector) == -1) {
      this.rebuildSectorsArray.add(sector);
    }
  }
  
  public void applyHill(Vector3 pos, float power) {
    int x = (int)pos.x;
    int z = (int)pos.z;
    
    Tile currentTile = getTile(x, z);
    
    if (currentTile != null) {
      Tile topTile      = getTile(x, z-1);
      Tile bottomTile   = getTile(x, z+1);
      
      Tile leftTile     = getTile(x-1, z);
      Tile rightTile    = getTile(x+1, z);
      
      Tile topLeftTile     = getTile(x-1, z-1);
      Tile topRightTile     = getTile(x+1, z-1);
      
      Tile bottomLeftTile   = getTile(x-1, z+1);
      Tile bottomRightTile   = getTile(x+1, z+1);
      
      currentTile.setY(currentTile.getY()+power);
      addSectorToRebuildFromTile(currentTile);
      
      if (topTile != null) {
        topTile.setY2(currentTile.getY1());
        topTile.setY4(currentTile.getY3());
        addSectorToRebuildFromTile(topTile);
      }
      
      if (bottomTile != null) {
        bottomTile.setY1(currentTile.getY2());
        bottomTile.setY3(currentTile.getY4());
        addSectorToRebuildFromTile(bottomTile);
      }
      
      if (leftTile != null) {
        leftTile.setY3(currentTile.getY1());
        leftTile.setY4(currentTile.getY2());
        addSectorToRebuildFromTile(leftTile);
      }
      
      if (rightTile != null) {
        rightTile.setY1(currentTile.getY3());
        rightTile.setY2(currentTile.getY4());
        addSectorToRebuildFromTile(rightTile);
      }
      
      if (topLeftTile != null) {
        topLeftTile.setY4(currentTile.getY1());
        topLeftTile.setType(Tile.Type.CornerTopLeft);
        //topLeftTile.setY2(currentTile.getY1());
        addSectorToRebuildFromTile(topLeftTile);
      }
      
      if (topRightTile != null) {
        topRightTile.setY2(currentTile.getY3());
        topRightTile.setType(Tile.Type.CornerTopRight);
        addSectorToRebuildFromTile(topRightTile);
      }
      
      if (bottomLeftTile != null) {
        bottomLeftTile.setY3(currentTile.getY2());
        bottomLeftTile.setType(Tile.Type.CornerBottomLeft);
        addSectorToRebuildFromTile(bottomLeftTile);
      }
      
      if (bottomRightTile != null) {
        bottomRightTile.setY1(currentTile.getY4());
        bottomRightTile.setType(Tile.Type.CornerBottomRight);
        addSectorToRebuildFromTile(bottomRightTile);
      }
      
      for (Sector sector : rebuildSectorsArray) {
        sector.build();
      }
      rebuildSectorsArray.clear();
      //this.buildSectors();
    }
  }
}
