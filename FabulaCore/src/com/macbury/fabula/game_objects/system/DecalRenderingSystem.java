package com.macbury.fabula.game_objects.system;

import java.util.Vector;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;

public class DecalRenderingSystem extends EntitySystem {
  @Mapper ComponentMapper<DecalComponent> dm;
  @Mapper ComponentMapper<PositionComponent> pm;
  private DecalBatch decalBatch;
  private PerspectiveCamera camera;
  private Vector3 normalizedCameraVector;
  
  public DecalRenderingSystem(DecalBatch batch, PerspectiveCamera perspectiveCamera) {
    super(Aspect.getAspectForAll(PositionComponent.class, DecalComponent.class));
    this.decalBatch = batch;
    this.camera     = perspectiveCamera;
    this.normalizedCameraVector = new Vector3();
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
    if (dm.has(entity) && pm.has(entity)) {
      DecalComponent decalComponent = dm.get(entity);
      PositionComponent position    = pm.get(entity);
      Decal decal                   = decalComponent.getDecal();
      
      decal.setPosition(position.getX()+decal.getWidth()/2, position.getY()+decal.getHeight()/2, position.getZ()+decal.getHeight()/2);
      //decal.setRotationX(-35);
      decal.lookAt(camera.position, this.normalizedCameraVector);
      
      decalBatch.add(decal);
    }
  }

  @Override
  protected void end() {
    decalBatch.flush();
  }
  
}
