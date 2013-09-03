package com.macbury.fabula.editor.brushes;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.terrain.Terrain;

public class EventBrush extends Brush {
  private static final String TAG = "EventBrush";

  public EventBrush(Terrain terrain) {
    super(terrain);
    setSize(0);
  }

  @Override
  public String getStatusBarInfo() {
    return null;
  }
  
  @Override
  public void onApply() {
    screen.getContainerFrame();
  }

}
