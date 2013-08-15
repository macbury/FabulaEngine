package com.macbury.fabula.utils;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public abstract class OffScreen2DRenderer {
  protected FrameBuffer fbo;
  protected SpriteBatch fboSpriteBatch;
  protected OrthographicCamera cam;
  
  public OffScreen2DRenderer(int width, int height) {
    fboSpriteBatch = new SpriteBatch();
    fbo            = new FrameBuffer(Format.RGBA8888, width, height, false);
    
    this.cam = new OrthographicCamera(fbo.getWidth(), fbo.getHeight());
    cam.position.set(fbo.getWidth() / 2, fbo.getHeight() / 2, 0);
    cam.update();
  }
  
  public Pixmap render() {
    fbo.begin();
    Gdx.gl20.glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
    Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
    fboSpriteBatch.setProjectionMatrix(cam.combined);
    fboSpriteBatch.begin();
    onRender(fboSpriteBatch);
    fboSpriteBatch.end();
    byte[] data = this.readData(fbo.getWidth(), fbo.getHeight());
    fbo.end();
    
    Pixmap picture = new Pixmap(fbo.getWidth(), fbo.getHeight(), Format.RGBA8888);
    picture.getPixels().put(data, 0, data.length);
    
    return picture;
  }


  // Adapted from ScreenUtil class
  public byte[] readData(final int width, final int height) {
    final int numBytes = width * height * 4;
    final ByteBuffer pixels = BufferUtils.newByteBuffer(numBytes);
    Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
    Gdx.gl.glReadPixels(0, 0, width, height, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);

    final byte[] lines = new byte[numBytes];
    final int numBytesPerLine = width * 4;
    for (int i = 0; i < height; i++) {
        pixels.position((height - i - 1) * numBytesPerLine);
        pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
    }

    return lines;
  }
  
  public abstract void onRender(SpriteBatch batch);
  
  public void dispose() {
    fbo.dispose();
    fboSpriteBatch.dispose();
  }
}
