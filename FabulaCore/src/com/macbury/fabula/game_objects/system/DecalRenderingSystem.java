package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.TileMovementComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;
import com.macbury.fabula.game_objects.components.WalkingAnimationComponent;
import com.macbury.fabula.terrain.Terrain;

public class DecalRenderingSystem extends EntitySystem {
  @Mapper ComponentMapper<DecalComponent> dm;
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<WalkingAnimationComponent> wam;
  @Mapper ComponentMapper<TileMovementComponent> tm;
  @Mapper ComponentMapper<VelocityComponent> vm;
  @Mapper ComponentMapper<BoundingBoxComponent> bbm;
  private DecalBatch decalBatch;
  private PerspectiveCamera camera;
  private Vector3 normalizedCameraVector;
  private Terrain terrain;
  
  public DecalRenderingSystem(DecalBatch batch, PerspectiveCamera perspectiveCamera, Terrain terrain) {
    super(Aspect.getAspectForAll(DecalComponent.class, PositionComponent.class));
    this.decalBatch             = batch;
    this.camera                 = perspectiveCamera;
    this.normalizedCameraVector = new Vector3();
    this.terrain                = terrain;
  }

  @Override
  protected boolean checkProcessing() {
    return true;
  }
  
  @Override
  protected void processEntities(ImmutableBag<Entity> entities) {
    this.normalizedCameraVector.set(camera.up).nor();
    for (int i = 0; i < entities.size(); i++) {
      process(entities.get(i));
    }
  }

  private void process(Entity entity) {
    if (pm.has(entity)) {
      PositionComponent position = pm.get(entity);
      
      if(bbm.has(entity) && terrain.isVisible(bbm.get(entity).getBoundingBox(position.getVector()))) {
        renderEntity(entity);
      } else if (terrain.isVisibleTile(position.getTileX(), position.getTileZ())) {
        renderEntity(entity);
      }
    }
  }

  private void renderEntity(Entity entity) {
    PositionComponent position    = pm.get(entity);
    DecalComponent decalComponent = dm.get(entity);
    
    if (wam.has(entity) && vm.has(entity) && tm.has(entity)) {
      WalkingAnimationComponent wac = wam.get(entity);
      TileMovementComponent     tc  = tm.get(entity);
      
      Animation animation       = null;
      if (tc.isMoving()) {
        wac.updateTime(world.getDelta());
      } else {
        wac.resetTime();
      }
      if(tc.isLeft()) {
        animation = wac.getLeftAnimation();
      } else if(tc.isRight()) {
        animation = wac.getRightAnimation();
      } else if(tc.isUp()) {
        animation = wac.getUpAnimation();
      } else {
        animation = wac.getDownAnimation();
      }
      
      decalComponent.setTextureRegion(animation.getKeyFrame(wac.getStateTime()));
    }
    
    Decal decal = decalComponent.getDecal();
    float w = decal.getWidth()/2;
    float h = decal.getHeight()/2;
    decal.setPosition(position.getX()+w, position.getY()+h, position.getZ()+h );
    decal.setRotationX(-25.0f);
    //decal.lookAt(camera.position, this.normalizedCameraVector);
    decalBatch.add(decal);
  }

  @Override
  protected void end() {
    decalBatch.flush();
  }
}
