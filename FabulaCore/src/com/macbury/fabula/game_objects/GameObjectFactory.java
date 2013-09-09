package com.macbury.fabula.game_objects;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.macbury.fabula.game_objects.components.PlayerComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.VelocityComponent;

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
    e.addComponent(new PositionComponent(position));
    e.addComponent(new VelocityComponent());
    e.addComponent(new PlayerComponent());
    
    return e;
  }
}
