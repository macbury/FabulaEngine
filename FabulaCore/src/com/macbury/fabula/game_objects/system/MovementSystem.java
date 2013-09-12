package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector3;
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
      
      // get all tiles around me in future
      // check if i im colliding with one of the tiles
      // if height of one tile is not equal to my height then stop
      
      if (scm.has(e) && bbm.has(e)) {
        //BoundingBox futureBox  = bbm.get(e).getBoundingBox(futureVector);
        //BoundingBox currentBox = bbm.get(e).getBoundingBox(pc.getVector());
        
        Tile currentTile       = this.terrain.getTile(pc.getTileX(), pc.getTileZ());
        Tile futureTile        = this.terrain.getTile(Math.round(futureVector.x), Math.round(futureVector.z));
        
        boolean passable       = currentTile.getY() == futureTile.getY();
        
        if (passable) {
          position.set(futureVector);
        }
      } else {
        position.set(futureVector);
      }
    }
  }
}
