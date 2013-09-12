package com.macbury.fabula.game_objects;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.DecalComponent;
import com.macbury.fabula.game_objects.components.PlayerComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.SolidColliderComponent;
import com.macbury.fabula.game_objects.components.TileMovementComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;
import com.macbury.fabula.manager.G;

public class GameObjectFactory {
  public static float NPC_HEIGHT = 0.2f;
  private World world;

  public World getWorld() {
    return world;
  }

  public void setWorld(World world) {
    this.world = world;
  }
  
  public Entity buildPlayer(Vector2 position) {
    Entity e = world.createEntity();
    
    TextureAtlas ta               = G.db.getAtlas("robot");
    TextureRegion region          =  ta.findRegion("franklin");
    DecalComponent decalComponent = new DecalComponent(region);
    
    e.addComponent(new PositionComponent(position));
    e.addComponent(new VelocityComponent(new Vector3(0, 0, 0.0f)));
    e.addComponent(new PlayerComponent());
    e.addComponent(decalComponent);
    e.addComponent(new TileMovementComponent());
    //e.addComponent(new BoundingBoxComponent(new Vector3(decalComponent.getDecal().getWidth(),NPC_HEIGHT,NPC_HEIGHT)));
    e.addComponent(new SolidColliderComponent());
    return e;
  }
}
