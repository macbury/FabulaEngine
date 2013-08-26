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
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.macbury.fabula.manager.ResourceManager;
import com.thesecretpie.shader.ShaderManager;

public class Terrain {
  private Sector[][] sectors;
  private Tile[][] tiles;

  private int columns;
  private int rows;
  private int horizontalSectorCount;
  private int veriticalSectorCount;
  private int totalSectorCount;
  private int visibleSectorCount;
  
  private Stack<Sector> visibleSectors;
  private ArrayList<Sector> rebuildSectorsArray = new ArrayList<>();
  private Vector3 intersection = new Vector3();
  private boolean debug = false;
  
  private Tileset tileset;
  private TerrainDebugListener debugListener;
  
  public Terrain(int columns, int rows) {
    Tile.GID_COUNTER  = 1;
    this.debug        = debug;
    this.columns      = columns;
    this.rows         = rows;
    this.tiles        = new Tile[columns][rows];
    
    if (columns % Sector.COLUMN_COUNT != 0 || rows%Sector.ROW_COUNT != 0) {
      throw new RuntimeException("Map size must be proper!");
    }
    
    this.horizontalSectorCount = columns/Sector.COLUMN_COUNT;
    this.veriticalSectorCount  = rows/Sector.ROW_COUNT;
    this.totalSectorCount      = horizontalSectorCount * veriticalSectorCount;
    
    this.sectors        = new Sector[horizontalSectorCount][veriticalSectorCount];
    this.visibleSectors = new Stack<Sector>();
    
    tileset = ResourceManager.shared().getTileset("OUTSIDE_TILESET");
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
    Tileset tileset = ResourceManager.shared().getTileset("OUTSIDE_TILESET");
    for (int z = 0; z < rows; z++) {
      for (int x = 0; x < columns; x++) {
        if (!haveTile(x,z)) {
          Tile tile = new Tile(x, 0, z);
          tile.setAutoTile(tileset.getDefaultAutoTile());
          setTile(x, z, tile);
        }
      }
    }
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

  public void render(Camera camera, Lights lights) {
    ShaderManager sm = ResourceManager.shared().getShaderManager();
    GL20 gl          = Gdx.graphics.getGL20();
    
    gl.glEnable(GL10.GL_DEPTH_TEST);
    gl.glEnable(GL20.GL_TEXTURE_2D);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glActiveTexture(GL20.GL_TEXTURE1);
    
    visibleSectorCount  = 0;
    final int textureId = 1;
    tileset.getTexture().bind(textureId);
    sm.begin("SHADER_TERRAIN_EDITOR");
      sm.setUniformMatrix("u_projectionViewMatrix", camera.combined);
      sm.setUniformi("u_texture0", textureId);
      sm.getCurrent().setUniformf("u_ambient_color", lights.ambientLight);
      sm.getCurrent().setUniformf("u_light_color", lights.directionalLights.get(0).color);
      sm.getCurrent().setUniformf("u_light_direction", lights.directionalLights.get(0).direction);
  
      if (debugListener != null) {
        debugListener.onDebugTerrainConfigureShader(sm.getCurrent());
      }
      
      visibleSectors.clear();
      
      for (int x = 0; x < horizontalSectorCount; x++) {
        for (int z = 0; z < veriticalSectorCount; z++) {
          Sector sector = this.sectors[x][z]; 
          if (sector.visibleInCamera(camera)) {
            sector.getMesh().render(sm.getCurrent(), GL20.GL_TRIANGLES); // GL20.GL_LINES wireframe
            visibleSectors.add(sector);
            visibleSectorCount++;
          }
        }
      }
    sm.end();
    
    gl.glDisable(GL10.GL_CULL_FACE); // TODO: this must to be disabled to show sprite batch duh
    gl.glDisable(GL10.GL_DEPTH_TEST);
   // gl.glDisable(GL20.GL_TEXTURE_2D);
  }
  
  public int getTotalSectorCount() {
    return totalSectorCount;
  }

  public int getVisibleSectorCount() {
    return this.visibleSectorCount;
  }

  public Vector3 getPositionForRay(Ray ray, Vector3 mouseTilePosition) {
    Vector3 intersectedVector = null;
    for (Sector sector : visibleSectors) {
      intersectedVector = sector.getPositionForRay(ray, mouseTilePosition);
      
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

  public Vector3 getSnappedPositionForRay(Ray ray, Vector3 mouseTilePosition) {
    Vector3 pos = getPositionForRay(ray, mouseTilePosition);
    if (pos != null) {
      Tile tile   = getTile(pos);
      
      float y = Math.max(0, pos.y);
      if (tile != null) {
        y = tile.getY();
      }
      pos.set((float)Math.floor(pos.x), y, (float)Math.floor(pos.z));
    }
    
    return pos;
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
  
  public void applySlope(Tile currentTile) {
    int x = (int)currentTile.getX();
    int z = (int)currentTile.getZ();
    
    Tile topTile           = getTile(x, z-1);
    Tile bottomTile        = getTile(x, z+1);
    
    Tile leftTile          = getTile(x-1, z);
    Tile rightTile         = getTile(x+1, z);
    
    Tile topLeftTile       = getTile(x-1, z-1);
    Tile topRightTile      = getTile(x+1, z-1);
    
    Tile bottomLeftTile    = getTile(x-1, z+1);
    Tile bottomRightTile   = getTile(x+1, z+1);
    
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
  }
  
  public void rebuildUsedSectors() {
    for (Sector sector : rebuildSectorsArray) {
      sector.build();
    }
    rebuildSectorsArray.clear();
  }
  
  public boolean isDebuging() {
    return debug;
  }
  

  public int getTileIdByPos(Vector3 pos) {
    return (int)((pos.z - 1) * this.columns + pos.x);
  }
  
  public TerrainDebugListener getDebugListener() {
    return debugListener;
  }

  public void setDebugListener(TerrainDebugListener debugListener) {
    this.debugListener = debugListener;
    this.debug         = true;
    //terrainShader = ResourceManager.shared().getShaderProgram(debug ? "SHADER_TERRAIN_EDITOR" : "SHADER_TERRAIN");
  }

  public interface TerrainDebugListener {
    public void onDebugTerrainConfigureShader(ShaderProgram shader);
  }

  public Tileset getTileset() {
    return this.tileset;
  }
  
  public Tile[][] getTiles() {
    return tiles;
  }

  public int getColumns() {
    return columns;
  }

  public int getRows() {
    return rows;
  }
}
