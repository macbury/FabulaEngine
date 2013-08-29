package com.macbury.fabula.manager;

import java.awt.EventQueue;

import com.badlogic.gdx.Screen;
import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.screens.WorldEditScreen;

public class EditorGameManager extends GameManager {
  protected WorldEditScreen worldEditScreen;
  
  public WorldEditScreen getWorldEditScreen() {
    if (worldEditScreen == null) {
      worldEditScreen = new WorldEditScreen(this);
    }
    
    return worldEditScreen;
  }
  
  public void startEditor() {
    final EditorGameManager self = this;
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          WorldEditorFrame frame = new WorldEditorFrame(self);
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  protected Screen getInitialScreen() {
    return getWorldEditScreen();
  }
}
