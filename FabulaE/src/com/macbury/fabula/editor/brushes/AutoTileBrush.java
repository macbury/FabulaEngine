package com.macbury.fabula.editor.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tileset;

public class AutoTileBrush extends Brush {
  
  public AutoTileBrush(Terrain terrain) {
    super(terrain);
  }
  
  public void buildPreviews() {
    Tileset tileset = terrain.getTileset();
    
    for (AutoTile at : tileset.getIcons()) {
      Texture texture = buildPreviewFor(at);
    }
  }
  
  public Texture buildPreviewFor(AutoTile at) {
    FrameBuffer frameBuffer = new FrameBuffer(Format.RGBA8888, AutoTiles.TILE_SIZE, AutoTiles.TILE_SIZE, true);
    SpriteBatch spriteBatch = new SpriteBatch();
    frameBuffer.begin();
    Gdx.graphics.getGL20().glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
    Gdx.graphics.getGL20().glClearColor(0f, 1f, 0f, 1);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.graphics.getGL20().glEnable(GL20.GL_TEXTURE_2D);
    
    spriteBatch.begin();
    spriteBatch.draw(at.getRegion(), 0, 0);
    spriteBatch.end();
    
    frameBuffer.end();
    return frameBuffer.getColorBufferTexture();
  }

  @Override
  public void onApply() {
    
  }
  
}
