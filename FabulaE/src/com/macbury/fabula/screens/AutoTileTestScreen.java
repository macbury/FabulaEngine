package com.macbury.fabula.screens;

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
    TexturePacker2.process("preprocessed/parts/", "preprocessed/whole/", "autotile.atlas");
    this.rawTiles   = new TextureAtlas(Gdx.files.internal("preprocessed/whole/autotile.atlas"));
    this.autoTiles = new AutoTiles(rawTiles, "road");
    
    //new AutoTiles(rawTiles, "grass");
    //new AutoTiles(rawTiles, "sidewalk");
    //new AutoTiles(rawTiles, "city_sidewalk");
    //new AutoTiles(rawTiles, "sand");
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
