package com.macbury.fabula.manager;

import java.awt.EventQueue;

import com.badlogic.gdx.Screen;
import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.screens.WorldEditScreen;

public class EditorGameManager extends GameManager {
  public EditorGameManager() {
    super(System.getProperty("user.dir")+"/assets/data/");
  }

  protected WorldEditScreen worldEditScreen;
  
  public WorldEditScreen getWorldEditScreen() {
    if (worldEditScreen == null) {
      worldEditScreen = new WorldEditScreen(this);
    }
    
    return worldEditScreen;
  }
  
  @Override
  public void create() {
    super.create();
    G.db.reloadMapData();
  }

  public void startEditor() {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          WorldEditorFrame frame = new WorldEditorFrame(EditorGameManager.this);
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  public Screen getInitialScreen() {
    return getWorldEditScreen();
  }

  @Override
  public void onNoGameData() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onNoPlayerStartPosition() {
    setScreen(getInitialScreen());
  }
}
