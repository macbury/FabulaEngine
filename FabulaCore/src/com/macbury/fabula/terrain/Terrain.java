package com.macbury.fabula.terrain;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Complete;
import org.simpleframework.xml.core.Persist;

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
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.manager.G;
import com.thesecretpie.shader.ShaderManager;

@Root
public class Terrain implements Disposable {
  private Sector[][] sectors;
  private Tile[][] tiles;
  
  @Attribute
  private int columns;
  @Attribute
  private int rows;
  @Attribute
  private String tilesetName;
  
  @Element
  private String terrainData;
  
  private int horizontalSectorCount;
  private int veriticalSectorCount;
  private int totalSectorCount;
  private int visibleSectorCount;
  
  private Stack<Sector> visibleSectors;
  private ArrayList<Sector> rebuildSectorsArray = new ArrayList<Sector>();
  private Vector3 intersection = new Vector3();
  private boolean debug = false;
  
  private Tileset tileset;
  private TerrainDebugListener debugListener;
  
  public Terrain(int columns, int rows) {
    initialize(columns, rows);
  }

  /*public Terrain(@Attribute int columns, @Attribute int rows, @Attribute String tilesetName) {
    initialize(columns, rows);
    setTileset(tilesetName);
  }*/
  
  private void initialize(int columns, int rows) {
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
  }
  
  public void setTileset(String name) {
    tilesetName = name;
    tileset     = G.db.getTileset(tilesetName);
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
    tile.setX(x);
    tile.setZ(z);
    this.tiles[x][z] = tile;
  }
  
  public void setTile(float x, float z, Tile tile) {
    setTile((int)x, (int)z, tile);
  }
  
  public void render(Camera camera, Lights lights) {
    ShaderManager sm = G.shaders;
    GL20 gl          = Gdx.graphics.getGL20();
    
    gl.glEnable(GL10.GL_DEPTH_TEST);
    gl.glEnable(GL20.GL_TEXTURE_2D);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glActiveTexture(GL20.GL_TEXTURE1);
    
    visibleSectorCount  = 0;
    final int textureId = 1;
    tileset.getTexture().bind(textureId);
    sm.begin("terrain-editor");
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

  @Override
  public void dispose() {
    for (int x = 0; x < horizontalSectorCount; x++) {
      for (int z = 0; z < veriticalSectorCount; z++) {
        Sector sector = this.sectors[x][z]; 
        sector.dispose();
      }
    }
  }

  public Tile getTileByTilePosition(Tile t) {
    return getTile(t.getX(), t.getZ());
  }

  private Tile getTile(float x, float z) {
    return getTile((int)x, (int)z);
  }
  
  @Commit
  public void commit() {
     //decoded = encoder.decode(encoded, format);
     //encoded = null;
  }

  @Persist
  public void prepare() {
    Deflater deflater = new Deflater();
    deflater.setStrategy(Deflater.BEST_SPEED);
    
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DeflaterOutputStream dout              = new DeflaterOutputStream(byteOutputStream, deflater);
    DataOutputStream dos                   = new DataOutputStream(dout);
    
    try {
      for (int z = 0; z < rows; z++) {
        for (int x = 0; x < columns; x++) {
          Tile tile = getTile(x, z);
          dos.writeInt(tile.getGid());
          dos.writeFloat(tile.getX());
          dos.writeFloat(tile.getZ());
          dos.writeFloat(tile.getY());
          dos.writeFloat(tile.getY1());
          dos.writeFloat(tile.getY2());
          dos.writeFloat(tile.getY3());
          dos.writeFloat(tile.getY4());
          dos.writeChars(tile.getAutoType().toString());
        }
      }
      
      dos.close();
      dout.close();
      byteOutputStream.close();
      terrainData = new String(Base64Coder.encode(byteOutputStream.toByteArray()));
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Complete
  public void release() {
    terrainData = null;
  }
  
}
