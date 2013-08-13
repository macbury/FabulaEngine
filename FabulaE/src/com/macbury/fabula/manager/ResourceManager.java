package com.macbury.fabula.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResourceManager {
  private AssetManager assetManager;
  private static final String TAG = "ResourceManager";
  private static ResourceManager _shared;
  
  private Map<String, TextureAtlas> atlasMap;
  private Map<String, Skin> skinMap;
  private Map<String, BitmapFont> fonts;
  private Map<String, String> textures;
  private Map<String, String> music;
  private Map<String, ShaderProgram> shaders;
  private boolean loadedXML = false;
  public static ResourceManager shared() {
    if (_shared == null) {
      _shared = new ResourceManager();
    }
    return _shared;
  }
  
  public ResourceManager() {
    this.assetManager = new AssetManager();
    this.textures     = new HashMap<String, String>();
    this.music        = new HashMap<String, String>();
    this.shaders      = new HashMap<String, ShaderProgram>();
    this.atlasMap     = new HashMap<String, TextureAtlas>();
  }
  
  public void loadSynch() throws Exception {
    load();
    assetManager.finishLoading();
  }
  
  public void load() throws Exception {
    File rawXml = Gdx.files.internal("data/assets.game").file();
    Gdx.app.log(TAG, "Loaded resources XML");
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder               = null;
    
    try {
      docBuilder = docBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
        throw new Exception("Could not load resources", e);
    }
    Document doc = null;
    try {
        doc = docBuilder.parse(rawXml);
    } catch (SAXException e) {
        throw new Exception("Could not load resources", e);
    } catch (IOException e) {
        throw new Exception("Could not load resources", e);
    }
    
    doc.getDocumentElement ().normalize ();
    
    NodeList listResources = doc.getElementsByTagName("resource");
    int totalResources = listResources.getLength();
    Gdx.app.log(TAG, "Resources to parse found: " + totalResources);
    
    for(int resourceIdx = 0; resourceIdx < totalResources; resourceIdx++){
      Node resourceNode = listResources.item(resourceIdx);
      if(resourceNode.getNodeType() == Node.ELEMENT_NODE){
        Element resourceElement = (Element)resourceNode;
        String type = resourceElement.getAttribute("type");
        if(type.equals("atlas")){
          addElementAsAtlas(resourceElement);
        } else if (type.equals("theme")) {
          addElementAsTheme(resourceElement);
        } else if (type.equals("font")) {
          addElementAsFont(resourceElement);
        } else if (type.equals("texture")) {
          addElementAsTexture(resourceElement);
        } else if (type.equals("music")) {
          addElementAsMusic(resourceElement);
        } else if (type.equals("shader")) {
          addElementAsShader(resourceElement);
        }
      }
    }
    
    loadedXML = true;
  }

  private void addElementAsShader(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "data/shaders/"+path;
    Gdx.app.log(TAG, "Found shader: " + id + " from " + path);
    
    String vertexShader   = Gdx.files.internal(path + ".vert").readString();
    String fragmentShader = Gdx.files.internal(path + ".frag").readString();
    ShaderProgram shader  = new ShaderProgram(vertexShader, fragmentShader);
    if (!shader.isCompiled())
      throw new IllegalStateException(shader.getLog());
    
    shaders.put(id, shader);
  }

  private void addElementAsMusic(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "data/music/"+path;
    Gdx.app.log(TAG, "Found music: " + id + " from " + path);
    music.put(id, path);
    assetManager.load(path, Music.class);
  }

  private void addElementAsTexture(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "data/textures/"+path;
    
    Gdx.app.log(TAG, "Found texture: " + id + " from " + path);
    //TextureParameter param = new TextureParameter();
    //param. 
    //TODO Load texture parameters from xml 
    textures.put(id, path);
    assetManager.load(path, Texture.class);
  }

  private void addElementAsFont(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    int size    = Integer.parseInt(resourceElement.getAttribute("size"));
    String path = resourceElement.getTextContent();
    path        = "assets/fonts/"+path;
    
    Gdx.app.log(TAG, "Loading font: " + id + " with size " + size + " from " + path);
    
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal( path ));
    BitmapFont font                 = generator.generateFont(size);
    fonts.put(id, font);
    generator.dispose();
  }

  private void addElementAsTheme(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "assets/theme/"+path + ".json";
    Gdx.app.log(TAG, "Loading UI: " + id + " from " + path);
    skinMap.put(id, new Skin( Gdx.files.internal( path ) ) );
  }

  private void addElementAsAtlas(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "data/textures/"+path + ".atlas";
    Gdx.app.log(TAG, "Loading: " + id + " from " + path);
    atlasMap.put(id, new TextureAtlas( Gdx.files.internal( path ) ) );
  }
  
  
  public TextureAtlas getAtlas(String id) {
    return this.atlasMap.get(id);
  }
  
  public Skin getSkin(String id) {
    return this.skinMap.get(id);
  }
  
  public BitmapFont getFont(String id) {
    return this.fonts.get(id);
  }
  
  public Skin getMainSkin() {
    return this.getSkin("UI_SKIN");
  }

  public Texture getTexture(String key) {
    return assetManager.get(this.textures.get(key), Texture.class);
  }
  
  public ShaderProgram getShaderProgram(String key) {
    return shaders.get(key);
  }
  
  public boolean isLoading() {
    return !this.assetManager.update();
  }

  public float getLoadingProgress() {
    return this.assetManager.getProgress();
  }
}