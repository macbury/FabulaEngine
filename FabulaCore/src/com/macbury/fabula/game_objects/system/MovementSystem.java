package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.SolidColliderComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.Tile;

public class MovementSystem extends EntityProcessingSystem {
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<VelocityComponent> vm;
  @Mapper ComponentMapper<SolidColliderComponent> scm;
  @Mapper ComponentMapper<BoundingBoxComponent> bbm;
  
  private Terrain terrain;
  private Vector3 futureVector = new Vector3();
  
  public MovementSystem(Terrain terrain) {
    super(Aspect.getAspectForAll(PositionComponent.class, VelocityComponent.class));
    this.terrain = terrain;
  }

  @Override
  protected void process(Entity e) {
    PositionComponent pc       = pm.get(e);
    Vector3 position           = pc.getVector();
    VelocityComponent velocity = vm.get(e);
    
    if (!velocity.getVector().equals(Vector3.Zero)) {
      futureVector.set(position).add(velocity.getScaledVector(world.delta));
      
      if (scm.has(e) && bbm.has(e)) {
        BoundingBox box  = bbm.get(e).getBoundingBox(futureVector);
        Tile currentTile = this.terrain.getTile(pc.getTileX(), pc.getTileZ());
        Tile futureTile  = this.terrain.getTile(Math.round(futureVector.x), Math.round(futureVector.z));
        
        if (currentTile.getY() == futureTile.getY()) {
          position.set(futureVector);
        }
      } else {
        position.set(futureVector);
      }
    }
  }
}
