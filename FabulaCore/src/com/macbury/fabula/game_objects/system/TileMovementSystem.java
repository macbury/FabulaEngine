package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.TileMovementComponent;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class TileMovementSystem extends EntityProcessingSystem {
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<TileMovementComponent> vm;
  
  private Terrain terrain;
  
  public TileMovementSystem(Terrain terrain) {
    super(Aspect.getAspectForAll(PositionComponent.class, TileMovementComponent.class));
    this.terrain = terrain;
  }

  @Override
  protected void process(Entity e) {
    PositionComponent pc     = pm.get(e);
    TileMovementComponent mc = vm.get(e);
    
    Tile currentTile         = terrain.getTile(pc.getTileX(), pc.getTileZ());
    if (currentTile != null) {
      pc.setY(currentTile.getY());
    }
    
    if (mc.isMoving()) {
      mc.addDelta(world.getDelta());
      pc.setVector(mc.getCurrentPosition());
      if (mc.moveFinished()) {
        mc.setMoving(false);
      }
    }
  }
}
