package com.macbury.fabula.db;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.macbury.fabula.manager.G;

public class GameFileResolver implements FileHandleResolver {
  
  @Override
  public FileHandle resolve(String fileName) {
    return G.fs(fileName);
  }
  
}
