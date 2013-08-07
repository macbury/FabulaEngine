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
    camera.position.set(0, 15, 0);
    camera.lookAt(0, 0, 0);
    
    CameraInputController cont = new CameraInputController(camera);
    Gdx.input.setInputProcessor(cont);
    
    texture = ResourceManager.shared().getTexture("TEXTURE_DEBUG");
    //texture.setFilter(Filter.NearestNeighbour, Filter.NearestNeighbour);
    textureRegion = new TextureRegion(texture, 416, 0, 32, 32);
    // U == X
    // V == Y
    
    int columns               = 10;
    int rows                  = 1;
    int vertexPerBoxCount     = 4;
    int vertexAttributesCount = 6;
    
    int totalAttributesPerRow  = vertexPerBoxCount * vertexAttributesCount;
    int verticiesElementsCount = columns * rows * totalAttributesPerRow;
    
    float[] verticies = new float[verticiesElementsCount]; /*{ 
      0f, 0f, 0, Color.toFloatBits(255, 0, 0, 0), textureRegion.getU(), textureRegion.getV(), // top left 1
      0f, -1f, 0, Color.toFloatBits(0, 255, 0, 0), textureRegion.getU(), textureRegion.getV2(), // bottom left 2
      1f, 0f, 0, Color.toFloatBits(0, 0, 255, 0), textureRegion.getU2(), textureRegion.getV(), // top right 3 
      1, -1f, 0, Color.toFloatBits(255, 0, 0, 0), textureRegion.getU2(), textureRegion.getV2(), // bottom right 4
    };*/
    int i = 0;
    int x = 0;
    int y = 0;
    while(i < verticiesElementsCount) {
      /* Vertex 1 */
      verticies[i++] = x; // X
      verticies[i++] = 0; // Y
      verticies[i++] = y; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU(); // U
      verticies[i++] = textureRegion.getV(); // V
      
      /* Vertex 2 */
      verticies[i++] = x; // X
      verticies[i++] = 0; // Y
      verticies[i++] = y+1f; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU(); // U
      verticies[i++] = textureRegion.getV2(); // V
      
      /* Vertex 3 */
      verticies[i++] = x + 1f; // X
      verticies[i++] = 0; // Y
      verticies[i++] = y; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU2(); // U
      verticies[i++] = textureRegion.getV(); // V
      
      /* Vertex 4 */
      verticies[i++] = x + 1f; // X
      verticies[i++] = 0; // Y
      verticies[i++] = y+1f; // Z
      
      verticies[i++] = Color.toFloatBits(255, 255, 255, 255); // Color
      
      // Texture Cords
      verticies[i++] = textureRegion.getU2(); // U
      verticies[i++] = textureRegion.getV2(); // V
      
      if (x < columns) {
        x++;
      } else {
        x = 0;
        y++;
      }
      
    }
    
    
    //Gdx.app.log(TAG, "U: "+ textureRegion.getU() + " V: "+textureRegion.getV());
    //Gdx.app.log(TAG, "U: "+ textureRegion.getU2() + " V: "+textureRegion.getV2());
    
    mesh = new Mesh(true, rows * columns * vertexPerBoxCount, 0, 
      new VertexAttribute(Usage.Position, 3, "a_position"),
      new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
      new VertexAttribute(Usage.TextureCoordinates, 2, "a_textCords")
    );
    mesh.setVertices(verticies);
    
    //mesh.setIndices(new short[] { 0, 1, 2 });     
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
    meshShader.setUniformi("u_texture", 0);
    texture.bind(0);
    
    mesh.render(meshShader, GL20.GL_TRIANGLE_STRIP);
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
