package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.foliage.FoliageDescriptor;
import com.macbury.fabula.terrain.foliage.FoliageSet;
import com.macbury.fabula.terrain.geometry.TriangleGrid;
import com.macbury.fabula.terrain.geometry.TriangleGrid.AttributeType;
import com.macbury.fabula.terrain.tile.Tile;

public class GrassTestScreen extends BaseScreenWithAutoReloadShaders {
  
  private PerspectiveCamera camera;
  
  private Mesh mesh;
  
  private float time      = 0.0f;
  private float amplitude = 0.04f;
  private float speed     = 6f;
  private FoliageSet foliageSet;
  private FoliageDescriptor foliageDescriptor;

  private Vector3 position;
  
  public GrassTestScreen(GameManager manager) {
    super(manager);
    this.foliageSet = G.db.getFoliageSet("outside");
    this.foliageDescriptor = this.foliageSet.findDescriptor("crops7");
    
    TextureRegion uvMap = this.foliageDescriptor.getRegion();
    
    TriangleGrid triangleGrid = new TriangleGrid(10, 10, true);
    triangleGrid.using(AttributeType.Position);
    triangleGrid.using(AttributeType.TextureCord);
    triangleGrid.using(AttributeType.Color);
    
    this.position = new Vector3();
    short n1, n2, n3 = 0;
    float y = 0.0f;
    float animated = foliageDescriptor.isAnimated() ? 1.0f : 0.0f;
    triangleGrid.begin();
      for (int x = 0; x < 5; x++) {
        for (int z = 0; z < 5; z++) {
          
          float w  = uvMap.getRegionWidth() / Tile.TILE_SIZE_IN_PIXELS;
          float sz = z + 0.5f - w/2;
          float ez = z + 0.5f + w/2;
          
          float lx = x + 0.5f - w/2;
          float rx = x + 0.5f + w/2;
          float h  = uvMap.getRegionHeight() / Tile.TILE_SIZE_IN_PIXELS;
          float sy = y;
          float ey = y + h;
          /* Bottom Left Vertex */
          n1 = triangleGrid.addVertex(lx, sy, sz);
          triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
          triangleGrid.addColorToVertex(0, 0, 0, 0);
          /* Top left Vertex */
          n2 = triangleGrid.addVertex(lx, ey, sz);
          triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
          triangleGrid.addColorToVertex(animated, 0, 0, 0);
          /* Bottom right Vertex */
          n3 = triangleGrid.addVertex(rx, sy, sz);
          triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
          triangleGrid.addColorToVertex(0, 0, 0, 0);
          triangleGrid.addIndices(n1,n2,n3);

          /* Top Right Vertex */
          n1 = triangleGrid.addVertex(rx, ey, sz);
          triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
          triangleGrid.addColorToVertex(animated, 0, 0, 0);
          triangleGrid.addIndices(n3,n2,n1);
          
          // next gex
          
          /* Bottom Left Vertex */
          //n1 = triangleGrid.addVertex(lx, sy, ez);
          //triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());
          //triangleGrid.addColorToVertex(0, 0, 0, 0);
          /* Top left Vertex */
          //n2 = triangleGrid.addVertex(lx, ey, ez);
          //triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());
          //triangleGrid.addColorToVertex(animated, 0, 0, 0);
          /* Bottom right Vertex */
          //n3 = triangleGrid.addVertex(rx, sy, sz);
          //triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());
          //triangleGrid.addColorToVertex(0, 0, 0, 0);
          //triangleGrid.addIndices(n1,n2,n3);

          /* Top Right Vertex */
          //n1 = triangleGrid.addVertex(rx, ey, sz);
          //triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());
          //triangleGrid.addColorToVertex(animated, 0, 0, 0);
          //triangleGrid.addIndices(n3,n2,n1);
        }
      }
    triangleGrid.end();
    
    this.mesh = triangleGrid.getMesh();
    
    camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    
    camera.position.x = 0.0f;
    camera.position.y = 4.0f;
    camera.position.z = 10.0f;
    camera.near = 0.1f;
    camera.far = 500.0f;
    camera.lookAt(0, 0, 0);
    camera.update(true);
    
    Gdx.input.setInputProcessor(new CameraInputController(camera));
  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void pause() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    
    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
    this.foliageSet.getTexture().bind();
    
    time += delta * speed;
    camera.update();
    
    position.set(this.camera.up).nor();
    G.shaders.begin("grass");
      G.shaders.setUniformMatrix("u_model_view", this.camera.combined);
      G.shaders.setUniformi("u_texture", 0);
      G.shaders.setUniformf("u_wave_data", time, amplitude);
      G.shaders.setUniformf("u_camera_up", position.x, position.y, position.z);
      G.shaders.setUniformf("u_camera_pos", this.camera.position.x, this.camera.position.y, this.camera.position.z);
      mesh.render(G.shaders.getCurrent(), GL10.GL_TRIANGLES);
    G.shaders.end();
  }
  
  @Override
  public void resize(int arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void show() {
    // TODO Auto-generated method stub
    
  }
  
}
