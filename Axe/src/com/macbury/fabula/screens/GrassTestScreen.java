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
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.geometry.TriangleGrid;
import com.macbury.fabula.terrain.geometry.TriangleGrid.AttributeType;

public class GrassTestScreen extends BaseScreen {
  
  private PerspectiveCamera camera;
  private AtlasRegion uvMap;
  private float time;
  private Mesh mesh;

  public GrassTestScreen(GameManager manager) {
    super(manager);
    TextureAtlas atlas = G.db.getAtlas("foliage");
    this.uvMap = atlas.findRegion("carrots");
    TriangleGrid triangleGrid = new TriangleGrid(5, 5, true);
    triangleGrid.using(AttributeType.Position);
    triangleGrid.using(AttributeType.TextureCord);
    
    short n1, n2, n3 = 0;
    
    triangleGrid.begin();
      for (int x = 0; x < triangleGrid.getColumns(); x++) {
        for (int z = 0; z < triangleGrid.getRows(); z++) {
          /* Top Right Vertex */
          n1 = triangleGrid.addVertex(x+1f, 0, z);
          triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV());

          /* Top left Vertex */
          n2 = triangleGrid.addVertex(x, 0, z);
          triangleGrid.addUVMap(uvMap.getU(), uvMap.getV());

          /* Bottom right Vertex */
          n3 = triangleGrid.addVertex(x+1f, 0, z+1f);
          triangleGrid.addUVMap(uvMap.getU2(), uvMap.getV2());

          triangleGrid.addIndices(n1,n2,n3);

          /* Bottom left Vertex */
          n1 = triangleGrid.addVertex(x, 0, z+1f);
          triangleGrid.addUVMap(uvMap.getU(), uvMap.getV2());

          triangleGrid.addIndices(n3,n2,n1);
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
  public void dispose() {
    // TODO Auto-generated method stub
    
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
    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    
    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
    uvMap.getTexture().bind();
    
    time += delta;
    camera.update();
    
    G.shaders.begin("grass");
      G.shaders.setUniformMatrix("u_model_view", this.camera.combined);
      G.shaders.setUniformi("u_texture", 0);
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
