package com.macbury.fabula.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.macbury.fabula.manager.ResourceManager;
import com.thesecretpie.shader.ShaderManager;

public class SkyBox {
  private String name;
  private Texture textureXNEG;
  private Texture textureXPOS;
  private Texture textureYNEG;
  private Texture textureYPOS;
  private Texture textureZNEG;
  private Texture textureZPOS;
  private Mesh quad;
  private Renderable renderable;
  private ShaderManager sm;
  
  public SkyBox() {
    quad = new Mesh(true, 4, 0, new VertexAttribute(Usage.Position, 3, "a_position"), new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
    quad.setVertices(new float[] {
        -5f, -5f, 0, 0, 1,
        5f, -5f, 0, 1, 1,
        5f, 5f, 0, 1, 0,
        -5f, 5f, 0, 0, 0});
    this.sm = ResourceManager.shared().getShaderManager();
  }
  
  public void setName(String id) {
    this.name = id;
  }
  
  public void setTextureXNEG(Texture texture) {
    this.textureXNEG = texture;
  }
  
  public void setTextureXPOS(Texture skyBoxTexture) {
    this.textureXPOS = skyBoxTexture;
  }
  
  public void setTextureYNEG(Texture skyBoxTexture) {
    this.textureYNEG = skyBoxTexture;
  }
  
  public void setTextureYPOS(Texture skyBoxTexture) {
    this.textureYPOS = skyBoxTexture;
  }
  
  public void setTextureZNEG(Texture skyBoxTexture) {
    this.textureZNEG = skyBoxTexture;
  }
  
  public void setTextureZPOS(Texture skyBoxTexture) {
    this.textureZPOS = skyBoxTexture;
  }
  
  public void render(Camera camera) {
    GL20 gl = Gdx.gl20;
    
    gl.glDisable(GL20.GL_DEPTH_TEST);
    gl.glEnable(GL20.GL_TEXTURE_2D);
    
    sm.begin("empty");
      quad.render(sm.getCurrent(), gl.GL_TRIANGLE_FAN);
    sm.end();
  }
}
