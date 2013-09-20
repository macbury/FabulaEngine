package com.macbury.fabula.editor.brushes;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.game_objects.Tags;
import com.macbury.fabula.game_objects.system.EditorEntityManagmentSystem.EditorEntityManagmentSystemListener;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.Terrain;

public class EventBrush extends Brush implements EditorEntityManagmentSystemListener {
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
    screen.getScene().getEditorEntityManagmentSystem().onClick(this, this.getPosition());
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

  @Override
  public void onEntitySelect(Entity entity) {
    if (entity == null) {
      screen.getContainerFrame().eventPopupMenu.show(screen.getContainerFrame().canvas, Gdx.input.getX(), Gdx.input.getY());
    } else {
      screen.getContainerFrame().editEventPopupMenu.show(screen.getContainerFrame().canvas, Gdx.input.getX(), Gdx.input.getY());
    }
    
  }

}
