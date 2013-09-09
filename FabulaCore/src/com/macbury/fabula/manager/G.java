package com.macbury.fabula.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.macbury.fabula.db.GameDatabase;
import com.macbury.fabula.game_objects.GameObjectFactory;
import com.thesecretpie.shader.ShaderManager;

public class G {
  public static GameManager game;
  public static ShaderManager shaders;
  public static GameDatabase db;
  public static GameObjectFactory factory;
  public static FileHandle fs(String path) {
    return Gdx.files.absolute(GameManager.shared().getStorePath() + path);
  }
}
