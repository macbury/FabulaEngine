package com.macbury.fabula.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.macbury.fabula.graphics.CubeMap;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.geometry.TriangleGrid;
import com.macbury.fabula.terrain.geometry.TriangleGrid.AttributeType;
import com.macbury.fabula.test.Shapes;

public class CubeMapScreen extends BaseScreen {
  private CubeMap cubemap;
  private Mesh cube;
  private PerspectiveCamera camera;
  private Mesh mesh;
  private float         amplitudeWave   = 0.1f;
  private float         angleWave       = 0.0f;
  private float         angleWaveSpeed  = 2.0f;
  private Animation     animation;
  
  public CubeMapScreen(GameManager manager) {
    super(manager);
    
    this.cubemap   = new CubeMap("textures/skybox/day");
    this.cube      = Shapes.genCube();
    this.animation = new Animation(0.05f, G.db.getAtlas("liquid").findRegions("water"));
    this.animation.setPlayMode(Animation.LOOP);
    TriangleGrid triangleGrid = new TriangleGrid(5, 5, true);
    triangleGrid.using(AttributeType.Position);
    triangleGrid.using(AttributeType.Normal);
    triangleGrid.begin();
    
    short n1, n2, n3 = 0;
    
    for (int x = 0; x < triangleGrid.getColumns(); x++) {
      for (int z = 0; z < triangleGrid.getRows(); z++) {
        /* Top right Vertex */
        n1 = triangleGrid.addVertex(x+1f, 0, z);
        triangleGrid.addUVMap(1, 0);
        triangleGrid.addNormal();
        /* top left Vertex */
        n2 = triangleGrid.addVertex(x , 0, z);
        triangleGrid.addUVMap(1, 1);
        triangleGrid.addNormal();
        /* bottom Right Vertex */
        n3 = triangleGrid.addVertex(x+1f, 0, z+1f);
        triangleGrid.addUVMap(0, 0);
        triangleGrid.addNormal();
        
        triangleGrid.addIndices(n1,n2,n3);
        /* Bottom left Vertex */
        n1 = triangleGrid.addVertex(x, 0, z+1f);
        triangleGrid.addUVMap(1, 0);
        triangleGrid.addNormal();
        
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
  public void render(float arg0) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    
    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
    Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, cubemap.getTextureId());
    
    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
    final float dt = Gdx.graphics.getRawDeltaTime();
    
    angleWave += dt;
    TextureRegion region = this.animation.getKeyFrame(angleWave);
    
    region.getTexture().bind(1);
    this.camera.update();
    
    G.shaders.begin("water");
      G.shaders.setUniformMatrix("u_model_view", this.camera.combined);
      G.shaders.setUniformf("u_wave_data", angleWave, amplitudeWave);
      G.shaders.setUniformi("u_texture", 1);
      G.shaders.setUniformf("u_texture_cordinates", region.getU(), region.getV(), region.getU2(), region.getV2());
      G.shaders.setUniformf("u_camera_position", this.camera.position.x, this.camera.position.y, this.camera.position.z);
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
