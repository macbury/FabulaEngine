package com.macbury.fabula.screens;

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
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Library;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.Terrain;
import com.macbury.fabula.terrain.TriangleGridBuilder;
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
    
    String vertexShader = Gdx.files.internal("data/shaders/simple.vert").readString();
    String fragmentShader = Gdx.files.internal("data/shaders/simple.frag").readString();
    meshShader = new ShaderProgram(vertexShader, fragmentShader);
    if (!meshShader.isCompiled())
      throw new IllegalStateException(meshShader.getLog());
    
    this.camera = new TopDownCamera();
    camera.position.set(0, 3, 0);
    camera.lookAt(0, 0, 0);
    
    CameraInputController cont = new CameraInputController(camera);
    Gdx.input.setInputProcessor(cont);
    
    texture = ResourceManager.shared().getTexture("TEXTURE_DEBUG");
    //texture.setFilter(Filter.NearestNeighbour, Filter.NearestNeighbour);
    textureRegion = new TextureRegion(texture, 0, 0, 32, 32);
    // U == X
    // V == Y
    
    
    TriangleGridBuilder builder = new TriangleGridBuilder(1, 1);
    
    short n1 = 0;
    short n2 = 0;
    short n3 = 0;
    
    builder.begin();
      n1 = builder.addVertex(0f, 0f, 0f);
      builder.addColorToVertex(255, 255, 255, 255);
      builder.addUVMap(0, 0);
      
      n2 = builder.addVertex(0f, 0f, 1f);
      builder.addColorToVertex(255, 255, 255, 255);
      builder.addUVMap(0, 0);
      
      n3 = builder.addVertex(1f, 0f, 0f);
      builder.addColorToVertex(255, 255, 255, 255);
      builder.addUVMap(0, 0);
      
      builder.addIndices(n1,n2,n3);
      
      n1 = builder.addVertex(1f, 0, 1f);
      builder.addColorToVertex(255, 255, 255, 255);
      builder.addUVMap(0, 0);
      
      builder.addIndices(n3,n2,n1);
    builder.end();
    mesh = builder.getMesh();
    
    
    //Gdx.app.log(TAG, "U: "+ textureRegion.getU() + " V: "+textureRegion.getV());
    //Gdx.app.log(TAG, "U: "+ textureRegion.getU2() + " V: "+textureRegion.getV2());
    
    /*mesh = new Mesh(true, rows * columns * vertexPerBoxCount, indices.length, 
      new VertexAttribute(Usage.Position, 3, "a_position")
    );
    mesh.setVertices(vertices);
    mesh.setIndices(indices);*/    
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
