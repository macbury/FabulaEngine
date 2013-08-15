package com.macbury.fabula.screens;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.utils.OffScreen2DRenderer;
import com.macbury.fabula.utils.PNG;

public class FrameBufferTest extends BaseScreen {
  private static final String TAG = "FrameBufferTest";
  Texture texture;
  SpriteBatch spriteBatch;
  private TestRender renderer;
  private Texture test;
  public FrameBufferTest(GameManager manager) {
    super(manager);
    texture = new ResourceManager().shared().getTexture("TEXTURE_GRASS");
    renderer = new TestRender(32, 32);
    spriteBatch = new SpriteBatch();
    
    Pixmap iconPixmap   = renderer.render();
    String filePath     = "./preprocessed/previews/test_brush_icon.png";
    FileHandle image    = Gdx.files.absolute(filePath);
    PixmapIO.writePNG(image, iconPixmap);
    iconPixmap.dispose();

  }

  @Override
  public void dispose () {
    texture.dispose();
    spriteBatch.dispose();
    renderer.dispose();
  }
  @Override
  public void hide() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void pause() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void render (float delta) {
    
    
    Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.graphics.getGL20().glClearColor(0.2f, 0.2f, 0.2f, 1);
    Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

    /*spriteBatch.begin();
    spriteBatch.draw(test, 0, 0);
    spriteBatch.end();*/
  }
  
  @Override
  public void resize(int arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void show() {
    // TODO Auto-generated method stub
    
  }

  
  private class TestRender extends OffScreen2DRenderer {

    public TestRender(int width, int height) {
      super(width, height);
    }

    @Override
    public void onRender(SpriteBatch batch) {
      batch.draw(FrameBufferTest.this.texture, 0, 0, 32, 32);
    }
    
  }
}
