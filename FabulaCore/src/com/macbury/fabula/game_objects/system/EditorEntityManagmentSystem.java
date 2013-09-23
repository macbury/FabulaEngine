package com.macbury.fabula.game_objects.system;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.macbury.fabula.db.PlayerStartPosition;
import com.macbury.fabula.game_objects.components.BoundingBoxComponent;
import com.macbury.fabula.game_objects.components.PositionComponent;
import com.macbury.fabula.game_objects.components.StartPositionComponent;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.Sector;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.tile.Tile;

public class EditorEntityManagmentSystem extends EntitySystem {
  private static final Color TILE_DEBUG_COLOR             = new Color(0f, 0f, 0f, 0.2f);
  private static final float WIREFRAME_LINE_RENDER_OFFSET = 0.001f;
  private static final Color TILE_DEBUG_BLOCK_COLOR       = new Color(1f, 0f, 0f, 1f);
  @Mapper ComponentMapper<PositionComponent> pm;
  @Mapper ComponentMapper<BoundingBoxComponent> bbm;
  @Mapper ComponentMapper<StartPositionComponent> spm;
  
  private ShapeRenderer shapeRenderer;
  private Terrain terrain;
  private boolean clicked;
  private EditorEntityManagmentSystemListener callback;
  private Vector2 position;
  private boolean showWireframe = true;
  private boolean showColliders = true;
  
  public EditorEntityManagmentSystem(ShapeRenderer shapeRenderer, Terrain terrain) {
    super(Aspect.getAspectForAll(PositionComponent.class));
    this.shapeRenderer = shapeRenderer;
    this.terrain       = terrain;
  }
  
  @Override
  protected void processEntities(ImmutableBag<Entity> entities) {
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glDepthMask(true);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    
    for (int i = 0; i < entities.size(); i++) {
      Entity e = entities.get(i);
      process(e);
      
      if (this.clicked && pm.has(e)) {
        PositionComponent pc = pm.get(e);
        
        if (pc.equalsTile(position)){
          this.clicked = false;
          callback.onEntitySelect(e);
        }
      }
    }
    
    if (this.clicked) {
      callback.onEntitySelect(null);
    }
    
    this.clicked = false;
    
    renderDebug();
  }

  private void renderDebug() {
    shapeRenderer.begin(ShapeType.Line);
      for (Sector sector : this.terrain.getVisibleSectors()) {
        for (int x = sector.getStartX(); x < sector.getEndX(); x++) {
          for (int z = sector.getStartZ(); z < sector.getEndZ(); z++) {
            Tile tile = this.terrain.getTile(x, z);
            
            if (showWireframe) {
              shapeRenderer.setColor(TILE_DEBUG_COLOR);
              shapeRenderer.line(x, tile.getY1()+WIREFRAME_LINE_RENDER_OFFSET, z, x+1, tile.getY3()+WIREFRAME_LINE_RENDER_OFFSET, z); // top
              shapeRenderer.line(x+1, tile.getY3()+WIREFRAME_LINE_RENDER_OFFSET, z, x+1, tile.getY4()+WIREFRAME_LINE_RENDER_OFFSET, z+1); // right
              shapeRenderer.line(x, tile.getY1()+WIREFRAME_LINE_RENDER_OFFSET, z, x+1, tile.getY4()+WIREFRAME_LINE_RENDER_OFFSET, z+1);
            }
            
            if (showColliders) {
              if (!tile.isPassable()) {
                shapeRenderer.setColor(TILE_DEBUG_BLOCK_COLOR);
                shapeRenderer.box(x, tile.getMinY(), z+1, 1, Math.max(tile.getHeight(), 1.0f),1);
              }
            }
          }
          
          shapeRenderer.flush();
        }
      }
    shapeRenderer.end();

  }

  protected void process(Entity entity) {
    PositionComponent pc = pm.get(entity);
    updatePlayerPosition(entity);
    
    Tile currentTile = terrain.getTile(pc.getTileX(), pc.getTileZ());
    if (currentTile != null) {
      pc.setY(currentTile.getY());
    }
    
    if (bbm.has(entity)) {
      throw new GdxRuntimeException("Fuck not implemented");
    } else {
      shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.box(pc.getTileX(), pc.getY(), pc.getTileZ()+1, 1, 1, 1);
      shapeRenderer.end();
    }
  }

  private void updatePlayerPosition(Entity entity) {
    if (spm.has(entity)) {
      PositionComponent pc   = pm.get(entity);
      PlayerStartPosition psp = G.db.getPlayerStartPosition();
      pc.setVector(psp.getTileX(), psp.getTileY());
    }
  }

  public void onClick(EditorEntityManagmentSystemListener c, Vector2 position) {
    this.clicked  = true;
    this.callback = c;
    this.position = position;
  }

  
  public static interface EditorEntityManagmentSystemListener {
    public void onEntitySelect(Entity entity);
  }

  @Override
  protected boolean checkProcessing() {
    return true;
  }

  public boolean isShowWireframe() {
    return showWireframe;
  }

  public void setShowWireframe(boolean showWireframe) {
    this.showWireframe = showWireframe;
  }

  public boolean isShowColliders() {
    return showColliders;
  }

  public void setShowColliders(boolean showColliders) {
    this.showColliders = showColliders;
  }
}
