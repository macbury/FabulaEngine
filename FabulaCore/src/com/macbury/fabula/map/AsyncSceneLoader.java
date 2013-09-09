package com.macbury.fabula.map;

import java.io.File;

import org.simpleframework.xml.Serializer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.persister.ScenePersister;

public class AsyncSceneLoader implements Runnable {
  private static final String TAG = "AsyncSceneLoader";
  private File file;
  private AsyncSceneLoaderListener listener;
  private Vector2 spawnPosition;
  public AsyncSceneLoader(File file, Vector2 spawnPosition, AsyncSceneLoaderListener list) {
    this.file          = file;
    this.listener      = list;
    this.spawnPosition = spawnPosition;
  }

  @Override
  public void run() {
    Gdx.app.log(TAG, "Loading file: " + file.getAbsolutePath());
    Serializer serializer = GameDatabase.getDefaultSerializer();
    try {
      ScenePersister scenePersister = serializer.read(ScenePersister.class, file);
      final Scene scene = scenePersister.getScene();
      Gdx.app.log(TAG, "Loaded " + scene.getName());
      scene.getTerrain().buildSectors();
      Gdx.app.log(TAG, "Builded sectors");
      Gdx.app.postRunnable(new Runnable() {
        @Override
        public void run() {
          listener.onSceneDidLoad(scene, spawnPosition);
        }
      });
      
    } catch (final Exception e) {
      Gdx.app.error(TAG, "Scene load error!", e);
      Gdx.app.postRunnable(new Runnable() {
        @Override
        public void run() {
          listener.onSceneLoadError(e);
        }
      });
    }
  }
  
  public interface AsyncSceneLoaderListener {
    public void onSceneDidLoad(Scene scene, Vector2 spawnPosition);
    public void onSceneLoadError(Exception e);
  }
}
