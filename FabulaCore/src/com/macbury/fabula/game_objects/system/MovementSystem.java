package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;

public class MovementSystem extends EntityProcessingSystem {
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<VelocityComponent> vm;
  
  public MovementSystem() {
    super(Aspect.getAspectForAll(PositionComponent.class, VelocityComponent.class));
  }

  @Override
  protected void process(Entity e) {
    Vector3 position = pm.get(e).getVector();
    VelocityComponent velocity = vm.get(e);
    position.add(velocity.getScaledVector(world.delta));
  }
}
