package com.macbury.fabula;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.macbury.fabula.utils.TopDownCamera;

public class GameBase implements ApplicationListener {
  private static final String TAG = "TEST:";
  private TopDownCamera camera;
  private Lights lights;
  private CameraInputController camController;
  public Array<Decal> instances = new Array<Decal>();
  public Array<Decal> objects = new Array<Decal>();
  private DecalBatch decalBatch;
  private Texture debugTexture;
  private Decal treeDecal;
  private Texture treeTexture;
  private Texture characterTexture;
  private Decal characterDecal;

  
  @Override
  public void create() {
    Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
    Gdx.gl.glDepthFunc(GL10.GL_LESS);
    
    instances = new Array<Decal>();
    objects   = new Array<Decal>();
    lights = new Lights();
    lights.ambientLight.set(0.4f, 0.4f, 0.4f, 1f);
    lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
    camera       = new TopDownCamera();
    decalBatch   = new DecalBatch(new CameraGroupStrategy(camera));
    
    treeTexture      = new Texture(Gdx.files.internal("data/textures/tree.png"));
    characterTexture = new Texture(Gdx.files.internal("data/textures/character.png")); 
    debugTexture     = new Texture(Gdx.files.internal("data/textures/grass.png"));
    debugTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    debugTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    int tileX = 57; 
    int tileZ = 28;
    
    for (int x = 0; x < tileX; x++) {
      for (int z = 0; z < tileZ; z++) {
        Decal decal = Decal.newDecal(new TextureRegion(debugTexture,0,0,32,32));
        decal.setPosition(x, 0, z);
        decal.setRotationX(90);
        decal.setWidth(1);
        decal.setHeight(1);
        instances.add(decal);
      }
    }
    
    camera.position.set(tileX/2, 17, 23);
    camera.rotate(70, -1, 0, 0);
    //camera.lookAt(0, 0, -12);
    camera.near = 0.1f;
    camera.far  = 300f;
    camera.update();
    
    camController = new CameraInputController(camera);
    //Gdx.input.setInputProcessor(camController);
    characterDecal = Decal.newDecal(new TextureRegion(characterTexture,32,0,32,32), true);
    characterDecal.setPosition(tileX/2+0.7f, 0.5f, tileZ/2+1.3f);
    characterDecal.setWidth(1f);
    characterDecal.setHeight(1f);
    characterDecal.lookAt(camera.position.cpy(), camera.up.cpy().nor());
    //treeDecal = Decal.newDecal(new TextureRegion(treeTexture), true);
    //treeDecal.setPosition(tileX/2, 2f, tileZ/2);
    //treeDecal.setWidth(4);
    //treeDecal.setRotationX(-70f);
    
    for (int x = 0; x < 30; x++) {
      for (int y = 0; y < 15; y++) {
        Decal tmpDecal = Decal.newDecal(new TextureRegion(treeTexture), true);
        tmpDecal.setPosition(x* 2f, 1f, y * 2f);
        tmpDecal.setWidth(2);
        tmpDecal.setHeight(2);
        tmpDecal.setRotationX(-70f);
        objects.add(tmpDecal);
      }
      
    }
    
    
    //treeDecal.lookAt(camera.position.cpy(), camera.up.cpy().nor());
    //Gdx.app.log("TESt", "Rotation x" + treeDecal.getRotation().x);
  }
  
  @Override
  public void dispose() {
    instances.clear();
    decalBatch.dispose();
  }
  
  @Override
  public void pause() {
    
  }
  
  @Override
  public void render() {
    //Gdx.app.log(TAG, "Rotation x"+ camera.);
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    camera.update();
    
    //treeDecal.lookAt(camera.position.cpy(), camera.up.cpy().nor());
    characterDecal.lookAt(camera.position.cpy(), camera.up.cpy().nor());
    
    decalBatch.add(characterDecal);
    //decalBatch.add(treeDecal);
    for (Decal decal : instances) {
      decalBatch.add(decal);
    }
   
    for (Decal decal : objects) {
      //decal.lookAt(camera.position.cpy(), camera.up.cpy().nor());
      
      decalBatch.add(decal);
      
      //decal.setRotationY(0f);
      //decal.setRotationZ(0f);
    }
    
    decalBatch.flush();
  }
  
  @Override
  public void resize(int arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    
  }
  
}
