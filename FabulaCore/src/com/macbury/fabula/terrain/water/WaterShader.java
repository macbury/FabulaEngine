package com.macbury.fabula.terrain.water;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.macbury.fabula.manager.G;

public class WaterShader implements Shader {
  private static final String SHADER_NAME                = "water";
  private static final String UNIFORM_MODEL_VIEW         = "u_model_view";
  private static final String UNIFORM_WAVE_DATA          = "u_wave_data";
  private static final String UNIFORM_TEXTURE_ID         = "u_texture";
  private static final String UNIFORM_TEXTURE_CORDINATES = "u_texture_cordinates";
  private static final String UNIFORM_CAMERA_POSITION    = "u_camera_position";
  private static final String UNIFORM_WATER_ALPHA        = "u_water_alpha";
  private static final String UNIFORM_WATER_MIX          = "u_water_mix";
  private Water water;

  public WaterShader(Water water) {
    this.water = water;
  }

  @Override
  public void dispose() {
    this.water = null;
  }
  
  @Override
  public void begin(Camera camera, RenderContext context) {
    context.setBlending(true, GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    context.setDepthTest(GL20.GL_LEQUAL);
    //context.setCullFace(GL20.GL_BACK);
    if (water.getCubeMap() != null) {
      Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
      Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, water.getCubeMap().getTextureId());
    }
    G.shaders.begin(SHADER_NAME);
    
    TextureRegion region = water.getCurrentRegion();
    
    G.shaders.setUniformMatrix(UNIFORM_MODEL_VIEW, camera.combined);
    G.shaders.setUniformf(UNIFORM_WAVE_DATA, water.getAngleWave(), water.getAmplitudeWave());
    G.shaders.setUniformi(UNIFORM_TEXTURE_ID, context.textureBinder.bind(water.getWaterTextureId()));
    G.shaders.setUniformf(UNIFORM_TEXTURE_CORDINATES, region.getU(), region.getV(), region.getU2(), region.getV2());
    G.shaders.setUniformf(UNIFORM_CAMERA_POSITION, camera.position.x, camera.position.y, camera.position.z);
    G.shaders.setUniformf(UNIFORM_WATER_ALPHA, water.getAlpha());
    G.shaders.setUniformf(UNIFORM_WATER_MIX, water.getMix());
  }
  
  @Override
  public boolean canRender(Renderable renderable) {
    return WaterRenderable.class.isInstance(renderable);
  }
  
  @Override
  public void render(Renderable renderable) {
    renderable.mesh.render(G.shaders.getCurrent(), renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize, true);
  }
  
  @Override
  public void end() {
    G.shaders.end();
  }
  
  @Override
  public int compareTo(Shader other) {
    if (other == null) return -1;
    if (other == this) return 0;
    return 0;
  }
  
  @Override
  public void init() {
    
  }
  
}
