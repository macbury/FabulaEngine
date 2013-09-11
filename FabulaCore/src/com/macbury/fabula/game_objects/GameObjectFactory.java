package com.macbury.fabula.game_objects;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PlayerComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.TileInteractionComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;
import com.macbury.fabula.manager.G;

public class GameObjectFactory {
  private World world;

  public World getWorld() {
    return world;
  }

  public void setWorld(World world) {
    this.world = world;
  }
  
  public Entity buildPlayer(Vector2 position) {
    Entity e = world.createEntity();
    
    TextureAtlas ta = G.db.getAtlas("robot");

    e.addComponent(new PositionComponent(position));
    e.addComponent(new VelocityComponent(new Vector3(0, 0, 0.0f)));
    e.addComponent(new PlayerComponent());
    e.addComponent(new TileInteractionComponent());
    e.addComponent(new DecalComponent(ta.findRegion("franklin")));
    e.addComponent(new BoundingBoxComponent(new Vector3(1.0f,1.0f,1.0f)));
    return e;
  }
}
