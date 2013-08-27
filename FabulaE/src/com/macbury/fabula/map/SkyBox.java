package com.macbury.fabula.map;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.macbury.fabula.manager.ResourceManager;
import com.thesecretpie.shader.ShaderManager;

public class SkyBox implements Disposable {
  private String name;
  private Texture textureXNEG;
  private Texture textureXPOS;
  private Texture textureYNEG;
  private Texture textureYPOS;
  private Texture textureZNEG;
  private Texture textureZPOS;
  
  private static final int SKYBOX_TEXTURE_UNIT = 1;
  private static final int SKYBOX_TEXTURE_ACTIVE_UNIT = GL20.GL_TEXTURE0
      + SKYBOX_TEXTURE_UNIT;

  private static final float ONE = 1f;

  private final static int NB_FACES = 6;
  private final static int QUAD_LENGTH = 4;

  // x, y, z
  private final static float[] VERTICES = { //
      -ONE, -ONE, -ONE, //
      ONE, -ONE, -ONE, //
      -ONE, ONE, -ONE, //
      ONE, ONE, -ONE, //
      -ONE, -ONE, ONE, //
      ONE, -ONE, ONE, //
      -ONE, ONE, ONE, //
      ONE, ONE, ONE };

  // QUADS drawn with TRIANGLE_FAN
  private final static int[] INDICES = { 
    5, 1, 7, 3, // positive x
    4, 0, 6, 2, // 0, 4, 2, 6, // negative x
    4, 5, 6, 7, // positive y
    1, 0, 3, 2, // negative y
    0, 1, 4, 5, // positive z
    3, 2, 7, 6 // negative z
  };
  
  private final static short[] ORDERED_INDICES = { 0, 1, 2, 3 };
  
  private Mesh[] meshes;
  private Matrix4 model;
  private Texture[] skyBoxTextures;
  private int textureId;
  
  public SkyBox() {
    this.meshes = new Mesh[NB_FACES];
    for (int i = 0; i < NB_FACES; i++) {
      Mesh mesh = new Mesh(true, QUAD_LENGTH * 3, QUAD_LENGTH,
          VertexAttribute.Position());
      mesh.setVertices(getVertices(i));
      mesh.setIndices(ORDERED_INDICES);
      meshes[i] = mesh;
    }
    this.skyBoxTextures = new Texture[6];
    
    model = new Matrix4();
    
    IntBuffer buffer = BufferUtils.newIntBuffer(1);
    buffer.position(0);
    buffer.limit(buffer.capacity());
    this.textureId = buffer.get(0);
  }
  
  public void disable() {
  }
  
  public void setName(String id) {
    this.name = id;
  }
  
  public void setTextureXPOS(Texture skyBoxTexture) {
    this.textureXPOS = skyBoxTexture;
    skyBoxTextures[0] = skyBoxTexture;
  }
  
  public void setTextureXNEG(Texture skyBoxTexture) {
    this.textureXNEG = skyBoxTexture;
    skyBoxTextures[1] = skyBoxTexture;
  }
  
  public void setTextureYPOS(Texture skyBoxTexture) {
    this.textureYPOS = skyBoxTexture;
    skyBoxTextures[2] = skyBoxTexture;
  }
  
  public void setTextureYNEG(Texture skyBoxTexture) {
    this.textureYNEG = skyBoxTexture;
    skyBoxTextures[3] = skyBoxTexture;
  }
  
  public void setTextureZPOS(Texture skyBoxTexture) {
    this.textureZPOS = skyBoxTexture;
    skyBoxTextures[4] = skyBoxTexture;
  }
  
  public void setTextureZNEG(Texture skyBoxTexture) {
    this.textureZNEG       = skyBoxTexture;
    skyBoxTextures[5] = skyBoxTexture;
  }

  public void render(Camera camera) {
    /*GL20 gl = Gdx.gl20;
    
    model.setToTranslation(camera.position);
    model.rotate(1, 0, 0, 90);
    
    gl.glDisable(GL20.GL_DEPTH_TEST);
    gl.glEnable(GL20.GL_TEXTURE_2D);
    
    gl.glActiveTexture(SKYBOX_TEXTURE_ACTIVE_UNIT);
    
    sm.begin("SHADER_SKYBOX");
      sm.setUniformMatrix("u_model_view", model);
      sm.setUniformMatrix("u_world_view", camera.combined);
      sm.setUniformi("u_texture"+SKYBOX_TEXTURE_UNIT, SKYBOX_TEXTURE_ACTIVE_UNIT);
      for (int i = 0; i < meshes.length; i++) {
        skyBoxTextures[i].bind(SKYBOX_TEXTURE_ACTIVE_UNIT);
        meshes[i].render(sm.getCurrent(), GL20.GL_TRIANGLE_STRIP);
      }
    sm.end();
    
    gl.glDisable(GL20.GL_TEXTURE_2D);
    gl.glEnable(GL20.GL_DEPTH_TEST);*/
  }


  private float[] getVertices(int indexNb) {
    float[] vertices = new float[3 * QUAD_LENGTH];
    for (int i = 0; i < QUAD_LENGTH; i++) {
      int offset = INDICES[QUAD_LENGTH * indexNb + i];
      vertices[3 * i] = VERTICES[3 * offset]; // X
      vertices[3 * i + 1] = VERTICES[3 * offset + 1]; // Y
      vertices[3 * i + 2] = VERTICES[3 * offset + 2]; // Z
    }
    return vertices;
  }

  public int getCubeMapTextureUnit() {
    return SKYBOX_TEXTURE_UNIT;
  }
  
  @Override
  public void dispose() {
    for (Mesh mesh : meshes) {
      mesh.dispose();
    }
    
    for (int i = 0; i < skyBoxTextures.length; i++) {
      skyBoxTextures[i].dispose();
    }
  }
}
