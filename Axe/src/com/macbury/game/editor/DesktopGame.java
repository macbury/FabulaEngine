package com.macbury.game.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.macbury.fabula.manager.DesktopGameManager;

public class DesktopGame {

  public static void main(String[] args) {
    DesktopGameManager manager = new DesktopGameManager();
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.resizable  = true;
    config.width      = 860;
    config.height     = 640;
    config.useGL20    = true;
    config.vSyncEnabled = true;
    LwjglApplication application = new LwjglApplication(manager, config);
  }
  
}
