package com.macbury.fabula.editor.tiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.macbury.fabula.terrain.tileset.AutoTiles;
import com.macbury.fabula.terrain.tileset.Tileset;

public class TilesetGenerator {
  private static final String TAG = "TilesetGenerator";
  private TileGeneratorListener listener;
  private boolean running;
  private ArrayList<AutoTiles> autoTiles;
  
  public TilesetGenerator(TileGeneratorListener listener) {
    this.listener = listener;
  }
  
  public String getTempAbsolutePath(String appendPath) {
    return Gdx.files.internal("preprocessed/"+appendPath).file().getAbsolutePath();
  }
  
  public void clearPngFilesIn(String path) {
    File file = Gdx.files.internal(path).file();
    
    for (File af : file.listFiles()) {
      if (!af.isDirectory() && af.getName().contains(".png")) {
        af.delete();
      }
    }
  }
  
  public void createParts(ArrayList<AutoTiles> autotiles, String path) {
    ConvertCmd cmd = new ConvertCmd();
    cmd.setSearchPath("C:\\Program Files (x86)\\ImageMagick-6.8.6-Q16");
    IMOperation op = new IMOperation();
    op.addImage();
    op.crop(16, 16);
    op.addImage();
    
    for (int i = 0; i < autotiles.size(); i++) {
      AutoTiles at = autotiles.get(i);
      listener.onProgress(i, autotiles.size()-1);
      
      String fromPartsName  = Gdx.files.internal(path+"/"+at.getName()+".png").file().getAbsolutePath();
      String targetPartName = Gdx.files.internal("preprocessed/parts/"+at.getName()+"_%02d.png").file().getAbsolutePath();
      listener.onLog("Found " + at.getName());
      
      try {
        cmd.run(op,fromPartsName,targetPartName);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (IM4JavaException e) {
        e.printStackTrace();
      }
    }
  }

  public void build(Tileset tileset, String autotilePath, String texturePath) {
    listener.onProgress(0, 0);
    clearPngFilesIn(getTempAbsolutePath("expanded/"));
    clearPngFilesIn(getTempAbsolutePath("parts/"));
    clearPngFilesIn(getTempAbsolutePath("whole/"));
    clearPngFilesIn(getTempAbsolutePath("previews/"));
    
    this.autoTiles = tileset.getAutoTiles();
    createParts(autoTiles, autotilePath);
    
    listener.onProgress(0, 0);
    listener.onLog("Processing parts...");
    TexturePacker2.process("preprocessed/parts/", "preprocessed/whole/", "temp.atlas");
    
    this.running = true;
    
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        TilesetGenerator.this.buildAutoTiles(); // This must be running on thread With opengl!
        TilesetGenerator.this.running = false;
      }
    });
    
    while(running) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        running = false;
      }
    }
    
    listener.onLog("Packing...");
    listener.onProgress(0, 0);
    TexturePacker2.process("preprocessed/expanded/", "assets/data/textures/", tileset.getAtlasName()+".atlas");
    listener.onLog("Finished: "+tileset.getAtlasName());
    listener.onFinish();
  }
  
  protected void buildAutoTiles() {
    TextureAtlas rawTiles = new TextureAtlas(Gdx.files.internal("preprocessed/whole/temp.atlas"));
    listener.onLog("Expanding parts...");
    for (int i = 0; i < autoTiles.size(); i++) {
      AutoTiles at = autoTiles.get(i);
      listener.onProgress(0, autoTiles.size()-1);
      new AutoTiles(rawTiles, at.getName());
      
      listener.onLog("Expanded: "+at.getName());
    }
  }

  public interface TileGeneratorListener {
    public void onProgress(int progress, int max);
    public void onLog(String line);
    public void onFinish();
  }
}
