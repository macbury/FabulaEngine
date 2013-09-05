package com.macbury.fabula.terrain;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.Terrain.TerrainDebugListener;

public class TerrainShader implements Shader {
  private String shaderName;
  private ShaderProgram shader;
  private Lights lights;
  private Material terrainMaterial;
  private RenderContext context;
  private TerrainDebugListener debugListener;
  
  public TerrainShader(String shaderName) {
    setShaderName(shaderName);
  }
  
  @Override
  public void init() {
    
  }
  
  @Override
  public void dispose() {
    shaderName = null;
  }
  
  @Override
  public void begin(Camera camera, RenderContext context) {
    this.shader  = G.shaders.begin(shaderName);
    this.context = context;
    context.setDepthTest(GL20.GL_LEQUAL);
    context.setCullFace(GL20.GL_BACK);
    shader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
    shader.setUniformf("u_ambient_color", lights.ambientLight);
    shader.setUniformf("u_light_color", lights.directionalLights.get(0).color);
    shader.setUniformf("u_light_direction", lights.directionalLights.get(0).direction);
    
    TextureAttribute textureAttr = (TextureAttribute) terrainMaterial.get(TextureAttribute.Diffuse);
    int textureId                = context.textureBinder.bind(textureAttr.textureDescription);
    
    shader.setUniformi("u_texture0", textureId);
    
    
    if (debugListener != null) {
      debugListener.onDebugTerrainConfigureShader(shader);
    }
  }
  
  @Override
  public void render(Renderable renderable) {
    renderable.mesh.render(shader, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize, true);
  }
  
  @Override
  public void end() {
    G.shaders.end();
  }
  
  @Override
  public boolean canRender(Renderable renderable) {
    return Sector.class.isInstance(renderable);
  }
  
  @Override
  public int compareTo(Shader other) {
    if (other == null) return -1;
    if (other == this) return 0;
    return 0;
  }

  public void setShaderName(String shaderName) {
    this.shaderName = shaderName;
  }
  
  public void setMaterial(Material terrainMaterial) {
    this.terrainMaterial = terrainMaterial;
  }

  public Lights getLights() {
    return lights;
  }

  public void setLights(Lights lights) {
    this.lights = lights;
  }

  public void setDebugListener(TerrainDebugListener listener) {
    this.debugListener = listener;
  }
  
}
