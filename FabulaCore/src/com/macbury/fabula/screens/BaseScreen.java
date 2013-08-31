package com.macbury.fabula.screens;

import com.badlogic.gdx.Screen;
import com.macbury.fabula.manager.GameManager;

public abstract class BaseScreen implements Screen {
  protected GameManager gameManager;
  
  public BaseScreen(GameManager manager) {
    this.gameManager = manager;
  }
  
  
}
