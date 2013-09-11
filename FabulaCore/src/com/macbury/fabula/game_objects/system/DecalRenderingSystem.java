package com.macbury.fabula.game_objects.system;

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
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.terrain.Terrain;

public class DecalRenderingSystem extends EntitySystem {
  @Mapper ComponentMapper<DecalComponent> dm;
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<BoundingBoxComponent> bbm;
  private DecalBatch decalBatch;
  private PerspectiveCamera camera;
  private Vector3 normalizedCameraVector;
  private Terrain terrain;
  
  public DecalRenderingSystem(DecalBatch batch, PerspectiveCamera perspectiveCamera, Terrain terrain) {
    super(Aspect.getAspectForAll(PositionComponent.class, DecalComponent.class));
    this.decalBatch = batch;
    this.camera     = perspectiveCamera;
    this.normalizedCameraVector = new Vector3();
    this.terrain    = terrain;
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
      
      if(bbm.has(entity)) {
        if (terrain.isVisible(bbm.get(entity).getBoundingBox(position.getVector()))) {
          renderEntity(entity);
        }
      } else if (position.isVisible(terrain)) {
        renderEntity(entity);
      }
    }
    
  }

  private void renderEntity(Entity entity) {
    PositionComponent position    = pm.get(entity);
    DecalComponent decalComponent = dm.get(entity);
    
    Decal decal                   = decalComponent.getDecal();
    decal.setPosition(position.getX()+decal.getWidth()/2, position.getY()+decal.getHeight()/2, position.getZ()+decal.getHeight()/2);
    //decal.setRotationX(-35);
    decal.lookAt(camera.position, this.normalizedCameraVector);
    
    decalBatch.add(decal);
  }

  @Override
  protected void end() {
    decalBatch.flush();
  }
}
