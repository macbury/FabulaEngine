package com.macbury.fabula.game_objects.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.TileMovementComponent;
import com.macbury.fabula.terrain.Terrain;

public class PlayerSystem extends VoidEntitySystem {
  private static final float PLAYER_SPEED = 2.2f;
  public static final String TAG_PLAYER   = "PLAYER";
  private static final float CAMERA_HEIGHT = 5.4f;
  private static final float CAMERA_OFFSET = 6;
  @Mapper ComponentMapper<TileMovementComponent> tmm;
  @Mapper ComponentMapper<PositionComponent> pm;
  private PerspectiveCamera camera;
  private Vector3 direction;
  private Entity playerEntity;
  private boolean moving = false;

  public PlayerSystem(PerspectiveCamera camera) {
    super();
    this.camera   = camera;
  }
  
  protected void process(Entity e) {
    PositionComponent     pc  = pm.get(e);
    TileMovementComponent tmc = tmm.get(playerEntity);
    
    this.camera.position.set(pc.getX(), pc.getY()+CAMERA_HEIGHT, pc.getZ()+CAMERA_OFFSET);
    this.camera.lookAt(pc.getVector());
    
    if (moving) {
      tmc.startMoving(pc.getVector());
    }
  }

  @Override
  protected void processSystem() {
    if (playerEntity == null) {
      playerEntity = world.getManager(TagManager.class).getEntity(TAG_PLAYER);
    } else {
      process(playerEntity);
    }
  }

  public void moveIn(TileMovementComponent.Direction direction) {
    if (playerEntity != null) {
      TileMovementComponent tmc = tmm.get(playerEntity);
      tmc.setDirection(direction);
      moving = true;
    }
  }

  public void stopMove() {
    moving = false;
  }
}
