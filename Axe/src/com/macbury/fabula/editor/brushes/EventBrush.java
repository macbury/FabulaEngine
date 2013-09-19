package com.macbury.fabula.editor.brushes;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.game_objects.Tags;
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
    if (selectedPlayerPosition(Gdx.input.getX(), Gdx.input.getY())) {
      
    } else {
      screen.getContainerFrame().eventPopupMenu.show(screen.getContainerFrame().canvas, Gdx.input.getX(), Gdx.input.getY());
    }
  }

  private boolean selectedPlayerPosition(int x, int y) {

    return false;
  }

  public void placeStartPosition() {
    PlayerStartPosition playerStartPosition = new PlayerStartPosition((int)this.getPosition().x, (int)this.getPosition().y, screen.getScene().getUID());
    G.db.setPlayerStartPosition(playerStartPosition);
    G.db.save();
    
    Entity entity = G.factory.getWorld().getManager(TagManager.class).getEntity(Tags.START_POSITION);
    
    if (entity == null) {
      entity = G.factory.buildStartPosition(playerStartPosition);
      entity.addToWorld();
    }
  }

}
