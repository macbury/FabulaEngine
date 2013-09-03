package com.macbury.fabula.test;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.screens.BaseScreen;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.TriangleGrid;
import com.macbury.fabula.utils.TopDownCamera;

public class MeshScreen extends BaseScreen {
  
  private static final String TAG = "MeshScreen";
  private VertexAttribute vAttr;
  private Mesh mesh;
  private ShaderProgram meshShader;
  private TopDownCamera camera;
  private Texture texture;
  private FPSLogger fps;
  private TextureRegion textureRegion;
  
  public MeshScreen(GameManager manager) {
    super(manager);
    
    fps = new FPSLogger();
    
    String vertexShader = Gdx.files.internal("data/shaders/mesh.vert").readString();
    String fragmentShader = Gdx.files.internal("data/shaders/mesh.frag").readString();
    meshShader = new ShaderProgram(vertexShader, fragmentShader);
    if (!meshShader.isCompiled())
      throw new IllegalStateException(meshShader.getLog());
    
    this.camera = new TopDownCamera();
    camera.position.set(0, 3, 0);
    camera.lookAt(0, 0, 0);
    
    CameraInputController cont = new CameraInputController(camera);
    Gdx.input.setInputProcessor(cont);
    
    //texture = ResourceManager.shared().getTexture("TEXTURE_DEBUG");
    //texture.setFilter(Filter.NearestNeighbour, Filter.NearestNeighbour);
    textureRegion = new TextureRegion(texture, 64, 64, 64, 64);
    // U == X
    // V == Y
    
    
    TriangleGrid builder = new TriangleGrid(10, 10, true);
    
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    builder.begin();
      for (int z = 0; z < 10; z++) {
        for (int x = 0; x < 10; x++) {
          if (x % 2 == 0) {
            /* Top left Vertex */
            n1 = builder.addVertex(x, 0f, z);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU(), textureRegion.getV());
            
            /* Bottom left Vertex */
            n2 = builder.addVertex(x, 0f, z+1f);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU(), textureRegion.getV2());
            
            /* Top Right Vertex */
            n3 = builder.addVertex(x+1f, 0f, z);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU2(), textureRegion.getV());
            
            builder.addIndices(n1,n2,n3);
            
            /* Bottom right Vertex */
            n1 = builder.addVertex(x+1f, 0, z+1f);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU2(), textureRegion.getV2());
            
            builder.addIndices(n3,n2,n1);
          } else {
            /* Top Right Vertex */
            n1 = builder.addVertex(x+1f, 0f, z);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU2(), textureRegion.getV());
            
            /* Top left Vertex */
            n2 = builder.addVertex(x, 0f, z);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU(), textureRegion.getV());
            
            /* Bottom right Vertex */
            n3 = builder.addVertex(x+1f, 0, z+1f);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU2(), textureRegion.getV2());
            
            builder.addIndices(n1,n2,n3);
            
            /* Bottom left Vertex */
            n1 = builder.addVertex(x, 0f, z+1f);
            builder.addColorToVertex(255, 255, 255, 255);
            builder.addUVMap(textureRegion.getU(), textureRegion.getV2());
            builder.addIndices(n3,n2,n1);
          }
        }
      }
    builder.end();
    
    mesh = builder.getMesh();  
  }


  @Override
  public void render(float arg0) {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    this.camera.update();
    
    GL20 gl = Gdx.graphics.getGL20();
    gl.glEnable(GL20.GL_TEXTURE_2D);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glActiveTexture(GL20.GL_TEXTURE0);
    
    meshShader.begin();
    meshShader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
    //meshShader.setUniformi("u_texture", 0);
    texture.bind(0);
    
    mesh.render(meshShader, GL20.GL_TRIANGLES);
    meshShader.end();
    fps.log();
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
