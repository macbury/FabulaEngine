package com.macbury.fabula.graphics;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.manager.G;
public class CubeMap implements Disposable {
  private Pixmap[] textures;
  private int g_cubeTexture;
  
  public CubeMap(String name) {
    textures = new Pixmap[6];
    loadImages(name);
  }

  private void loadImages(String name) {
    g_cubeTexture = Texture.createGLHandle();
    Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, g_cubeTexture);
    
    textures[0] = loadPixmap(name, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X);
    textures[1] = loadPixmap(name, GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
    textures[2] = loadPixmap(name, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
    textures[3] = loadPixmap(name, GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
    textures[4] = loadPixmap(name, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
    textures[5] = loadPixmap(name, GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
    
    Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, g_cubeTexture);
    
    Gdx.gl20.glTexParameterf(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
    Gdx.gl20.glTexParameterf(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
    Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
    Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
  }

  private Pixmap loadPixmap(String name, int position) {
    Pixmap temp = new Pixmap(G.fs(name+"_" + prefixByType(position) + ".png"));
    Gdx.gl.glTexImage2D(position, 0, temp.getGLInternalFormat(), temp.getWidth(), temp.getHeight(), 0, temp.getGLFormat(), temp.getGLType(), temp.getPixels());
    return temp;
  }

  private String prefixByType(int position) {
    switch(position) {
      case GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X:
        return "xpos";
      case GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X:
        return "xneg";
      case GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y:
        return "ypos";
      case GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y:
        return "yneg";
      case GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z:
        return "zpos";
      case GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z:
        return "zneg";
    }
    return null;
  }

  @Override
  public void dispose() {
    IntBuffer buffer = BufferUtils.newIntBuffer(1);
    buffer.put(0, g_cubeTexture);
    Gdx.gl.glDeleteTextures(1, buffer);
    for (int i = 0; i < textures.length; i++) {
      textures[i].dispose();
    }
  }

  public int getTextureId() {
    return g_cubeTexture;
  }
}
