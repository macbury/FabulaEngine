package com.macbury.fabula.game_objects.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;

public class CollisionRenderingSystem extends EntityProcessingSystem {
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<BoundingBoxComponent> bbm;
  private ShapeRenderer renderer;

  public CollisionRenderingSystem(ShapeRenderer renderer) {
    super(Aspect.getAspectForAll(BoundingBoxComponent.class, PositionComponent.class));
    this.renderer = renderer;
  }

  @Override
  protected void process(Entity entity) {
    PositionComponent pc    = pm.get(entity);
    BoundingBox boundingBox = bbm.get(entity).getBoundingBox(pc.getVector());
    
    renderer.begin(ShapeType.Line);
      renderer.setColor(Color.WHITE);
      renderer.box(boundingBox.getCorners()[4].x, boundingBox.getCorners()[4].y, boundingBox.getCorners()[4].z, boundingBox.getDimensions().x, boundingBox.getDimensions().y, boundingBox.getDimensions().z);
    renderer.end();
  }
  
}
