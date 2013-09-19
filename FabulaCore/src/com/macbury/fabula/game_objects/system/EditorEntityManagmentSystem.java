package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.StartPositionComponent;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class EditorEntityManagmentSystem extends EntityProcessingSystem {
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<BoundingBoxComponent> bbm;
  @Mapper ComponentMapper<StartPositionComponent> spm;
  
  private ShapeRenderer shapeRenderer;
  private Terrain terrain;
  
  public EditorEntityManagmentSystem(ShapeRenderer shapeRenderer, Terrain terrain) {
    super(Aspect.getAspectForAll(PositionComponent.class));
    this.shapeRenderer = shapeRenderer;
    this.terrain       = terrain;
  }

  @Override
  protected void process(Entity entity) {
    PositionComponent pc = pm.get(entity);
    updatePlayerPosition(entity);
    
    Tile currentTile = terrain.getTile(pc.getTileX(), pc.getTileZ());
    if (currentTile != null) {
      pc.setY(currentTile.getY());
    }
    
    if (bbm.has(entity)) {
      throw new GdxRuntimeException("Fuck not implemented");
    } else {
      shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.box(pc.getTileX(), pc.getY(), pc.getTileZ()+1, 1, 1, 1);
      shapeRenderer.end();
    }
  }

  private void updatePlayerPosition(Entity entity) {
    if (spm.has(entity)) {
      PositionComponent pc   = pm.get(entity);
      PlayerStartPosition psp = G.db.getPlayerStartPosition();
      pc.setVector(psp.getTileX(), psp.getTileY());
    }
  }
  
}
