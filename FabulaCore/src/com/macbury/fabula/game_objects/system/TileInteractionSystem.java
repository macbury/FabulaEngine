package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.macbury.fabula.game_objects.components.PlayerComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.TileInteractionComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class TileInteractionSystem extends EntityProcessingSystem {
  @Mapper ComponentMapper<PositionComponent> pm;
  private Terrain terrain;
  
  public TileInteractionSystem(Terrain terrain) {
    super(Aspect.getAspectForAll(TileInteractionComponent.class, PositionComponent.class));
    this.terrain = terrain;
  }

  @Override
  protected void process(Entity e) {
    PositionComponent positionComponent = pm.get(e);
    Tile tile                           = terrain.getTile(positionComponent.getTileX(), positionComponent.getTileZ());
    positionComponent.setY(tile.getY());
  }
  
}
