package com.macbury.fabula.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CorruptedGameScreen implements Screen {
  private OrthographicCamera camera;
  private SpriteBatch batch;
  private Texture texture;
  private Sprite sprite;
  
  @Override
  public void dispose() {
    batch.dispose();
    texture.dispose();
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
  public void render(float arg0) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    sprite.draw(batch);
    batch.end();
  }
  
  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void show() {
    camera = new OrthographicCamera();
    camera.setToOrtho(false);
    batch = new SpriteBatch();
    
    texture = new Texture(Gdx.files.internal("corrupted_data.png"));
    
    TextureRegion region = new TextureRegion(texture);
    
    sprite = new Sprite(region);
    sprite.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
  }

  
}
