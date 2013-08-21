package com.macbury.fabula.screens;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;

public class AutoTileTestScreen extends BaseScreen {

  private static final String TAG = "AutoTileTestScreen";
  private SpriteBatch debugBatch;
  private TextureAtlas rawTiles;
  private Array<AtlasRegion> tileParts;
  
  
  private Texture tileTexture;
  private AutoTiles autoTiles;
  
  public AutoTileTestScreen(GameManager manager) {
    super(manager);
    debugBatch = new SpriteBatch();
    File file = Gdx.files.internal("preprocessed/autotiles").file();
    
    for (File af : file.listFiles()) {
      if (!af.isDirectory() && af.getName().contains(".png")) {
        String workingDir     = Gdx.files.internal("preprocessed/autotiles").file().getAbsolutePath();
        String baseName       = af.getName().replaceFirst(".png", "");
        String targetPartName = Gdx.files.internal("preprocessed/parts/"+baseName+"_%02d.png").file().getAbsolutePath();
        String cmd            = "convert "+af.getAbsolutePath()+" -crop 16x16 " + targetPartName;
        //Gdx.app.log(TAG, "Found: " + af.getName() + " => " + targetPartName);
        try {
          Gdx.app.log(TAG, cmd);
          Process proc = Runtime.getRuntime().exec(cmd);
          proc.waitFor();
          
          BufferedReader buf = new BufferedReader( new InputStreamReader( proc.getErrorStream() ) ) ;
          String line = null;
          while (true)  {
            line = buf.readLine();
            
            if (line == null) {
              break;
            } else {
              Gdx.app.log(TAG, "Console: " + line);
            }
          }

        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    
    TexturePacker2.process("preprocessed/parts/", "preprocessed/whole/", "autotile.atlas");
    this.rawTiles   = new TextureAtlas(Gdx.files.internal("preprocessed/whole/autotile.atlas"));
    
    
    for (AtlasRegion region : this.rawTiles.getRegions()) {
      //Gdx.app.log(TAG, region.get);
    }
    
    new AutoTiles(rawTiles, "grass_sand_road");
    
    new AutoTiles(rawTiles, "desert_grass_road");
    new AutoTiles(rawTiles, "desert_rock_road");
    new AutoTiles(rawTiles, "desert_simple");
    new AutoTiles(rawTiles, "grass_rock_road");
    new AutoTiles(rawTiles, "grass_simple");
    this.autoTiles = new AutoTiles(rawTiles, "rock_road");
    new AutoTiles(rawTiles, "rock_road_second");
    new AutoTiles(rawTiles, "sand_grass_road");
    new AutoTiles(rawTiles, "sand_rock_road");
    new AutoTiles(rawTiles, "sand_simple");
    new AutoTiles(rawTiles, "snow_rock_road");
    new AutoTiles(rawTiles, "snow_simple");
    
    TexturePacker2.process("preprocessed/expanded/", "data/textures/", "outside.atlas");
    /*this.tileParts = rawTiles.findRegions("road");
    Pixmap tilePixmap = new Pixmap(32, 32, Format.RGBA8888);
    
    for (AtlasRegion atlasRegion : tileParts) {
      Gdx.app.log(TAG, atlasRegion.name);
      TextureData baseTextureData = atlasRegion.getTexture().getTextureData();
      baseTextureData.prepare();
      Pixmap basePixmap = baseTextureData.consumePixmap();
      tilePixmap.drawPixmap(basePixmap, 0, 0, atlasRegion.getRegionX(), atlasRegion.getRegionY(), atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight());
      basePixmap.dispose();
      baseTextureData.disposePixmap();
    }
    
    this.tileTexture = new Texture(tilePixmap);
    
    tilePixmap.dispose();*/
  }

  @Override
  public void dispose() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void render(float arg0) {
    debugBatch.begin();
    int i = 0;
    for (AutoTile autoTile : autoTiles.all()) {
      debugBatch.draw(autoTile.getRegion(), i * AutoTiles.TILE_SIZE, 500);
      i++;
    }
    debugBatch.end();
  }

  @Override
  public void resize(int arg0, int arg1) {
  }
  @Override
  public void resume() {
  }
  @Override
  public void show() {
  }
}
