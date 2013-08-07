package com.macbury.fabula;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.macbury.fabula.manager.GameManager;

public class DesktopGame {
  public static void main(String[] args) {
    GameManager game             = new GameManager();
    LwjglApplication application = new LwjglApplication(game, "FabulaEngine", 1366, 768, true);
  }
}
