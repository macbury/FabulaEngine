package com.macbury.fabula.screens;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.editor.ShaderFileChangeListener;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;

public abstract class BaseScreenWithAutoReloadShaders extends BaseScreen {
  
  private static final String TAG = "BaseScreenWithAutoReloadShaders";
  private int shaderWatchID;

  public BaseScreenWithAutoReloadShaders(GameManager manager) {
    super(manager);
    try {
      String path = G.fs("shaders/").file().getAbsolutePath();
      Gdx.app.log(TAG, "Watching: " + path);
      this.shaderWatchID = JNotify.addWatch(path, JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED, true, new ShaderFileChangeListener());
    } catch (JNotifyException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void dispose() {
    try {
      JNotify.removeWatch(shaderWatchID);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
