package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.game_objects.components.PlayerComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;

public class PlayerSystem extends EntityProcessingSystem {
  private static final float PLAYER_SPEED = 2.2f;
  @Mapper ComponentMapper<VelocityComponent> vm;
  @Mapper ComponentMapper<PositionComponent> pm;
  private PerspectiveCamera camera;
  private Vector2 velocity;

  public PlayerSystem(PerspectiveCamera camera) {
    super(Aspect.getAspectForAll(VelocityComponent.class, PlayerComponent.class, PositionComponent.class));
    this.camera   = camera;
    this.velocity = new Vector2();
  }
  
  @Override
  protected void process(Entity e) {
    PositionComponent positionComponent = pm.get(e);
    VelocityComponent velocityComponent = vm.get(e);
    
    velocityComponent.setVector(velocity.x * PLAYER_SPEED, velocity.y * PLAYER_SPEED);
    
    this.camera.position.set(positionComponent.getX(), positionComponent.getY()+8.4f, positionComponent.getZ()+4);
    this.camera.lookAt(positionComponent.getVector());
  }

  public void setVelocity(float x, float y) {
    this.velocity.set(x, y);
  }
}
