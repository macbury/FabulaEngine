package com.macbury.fabula.editor;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.manager.G;

import net.contentobjects.jnotify.JNotifyListener;

public class ShaderFileChangeListener implements JNotifyListener {
  private static final String TAG = "ShaderFileChangeListener";

  public void fileRenamed(int wd, String rootPath, String oldName,
      String newName) {
    print("renamed " + rootPath + " : " + oldName + " -> " + newName);
  }
  public void fileModified(int wd, String rootPath, String name) {
    print("modified " + rootPath + " : " + name);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        G.shaders.reload();
      }
    });
  }
  public void fileDeleted(int wd, String rootPath, String name) {
    print("deleted " + rootPath + " : " + name);
  }
  public void fileCreated(int wd, String rootPath, String name) {
    print("created " + rootPath + " : " + name);
  }
  void print(String msg) {
    Gdx.app.log(TAG, msg);
  }
  
}
