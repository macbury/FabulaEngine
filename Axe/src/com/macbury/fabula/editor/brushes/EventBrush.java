package com.macbury.fabula.editor.brushes;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.manager.G;
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
    screen.getContainerFrame().eventPopupMenu.show(screen.getContainerFrame().canvas, Gdx.input.getX(), Gdx.input.getY());
  }

  public void placeStartPosition() {
    PlayerStartPosition playerStartPosition = new PlayerStartPosition((int)this.getPosition().x, (int)this.getPosition().y, screen.getScene().getUID());
    G.db.setPlayerStartPosition(playerStartPosition);
    G.db.save();
  }

}
