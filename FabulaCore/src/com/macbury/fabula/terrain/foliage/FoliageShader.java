package com.macbury.fabula.terrain.foliage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.macbury.fabula.manager.G;

public class FoliageShader implements Shader {
  private static final String SHADER_NAME                = "grass";
  private static final String UNIFORM_MODEL_VIEW         = "u_model_view";
  private static final String UNIFORM_TEXTURE_ID         = "u_texture";
  private static final String UNIFORM_WAVE_DATA          = "u_wave_data";
  private Foliage foliage;

  public FoliageShader(Foliage foliage) {
    this.foliage = foliage;
  }

  @Override
  public void dispose() {
    this.foliage = null;
  }
  
  @Override
  public void begin(Camera camera, RenderContext context) {
    context.setBlending(true, GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    context.setDepthTest(GL20.GL_LEQUAL);
    //context.setCullFace(GL20.GL_FRONT);
    Gdx.gl.glDisable(GL10.GL_CULL_FACE);
    G.shaders.begin(SHADER_NAME);
    G.shaders.setUniformMatrix(UNIFORM_MODEL_VIEW, camera.combined);
    G.shaders.setUniformi(UNIFORM_TEXTURE_ID, context.textureBinder.bind(foliage.getTextureId()));
    G.shaders.setUniformf(UNIFORM_WAVE_DATA, foliage.getTime(), foliage.getAmplitude());
  }
  
  @Override
  public boolean canRender(Renderable renderable) {
    return FoliageRenderable.class.isInstance(renderable);
  }
  
  @Override
  public int compareTo(Shader other) {
    if (other == null) return -1;
    if (other == this) return 0;
    return 0;
  }
  
  @Override
  public void end() {
    G.shaders.end();
    Gdx.gl.glEnable(GL10.GL_CULL_FACE);
  }
  
  @Override
  public void init() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void render(Renderable renderable) {
    renderable.mesh.render(G.shaders.getCurrent(), renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize, true);
  }
  
}
