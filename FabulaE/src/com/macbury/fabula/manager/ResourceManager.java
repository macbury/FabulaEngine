package com.macbury.fabula.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.macbury.fabula.map.SkyBox;
import com.macbury.fabula.terrain.Tileset;
import com.thesecretpie.shader.ShaderManager;

public class ResourceManager {
  private static final String TAG = "ResourceManager";
  private static ResourceManager _shared;
  
  private Map<String, TextureAtlas> atlasMap;
  private Map<String, Skin> skinMap;
  private Map<String, BitmapFont> fonts;
  private Map<String, Texture> textures;
  private Map<String, String> music;
  private Map<String, Tileset> tilesets;
  private Map<String, SkyBox> skyBoxes;
  private boolean loadedXML = false;
  
  public static ResourceManager shared() {
    if (_shared == null) {
      _shared = new ResourceManager();
    }
    return _shared;
  }
  
  public ResourceManager() {
    this.textures      = new HashMap<String, Texture>();
    this.music         = new HashMap<String, String>();
    this.atlasMap      = new HashMap<String, TextureAtlas>();
    this.tilesets      = new HashMap<String, Tileset>();
    this.skyBoxes      = new HashMap<String, SkyBox>();
  }
  
  public void load() throws Exception {
    File rawXml = Gdx.files.internal("assets/assets.game").file();
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
        } else if (type.equals("tileset")) {
          addElementAsTileset(resourceElement);
        } else if (type.equals("skybox")) {
          addElementAsSkyBox(resourceElement);
        }
      }
    }
    
    loadedXML = true;
  }

  private void addElementAsSkyBox(Element resourceElement) {
    String id    = resourceElement.getAttribute("id");
    String path  = resourceElement.getTextContent();
    path        = "assets/textures/skybox/"+path;
    
    Gdx.app.log(TAG, "Loading SkyBox: " + id + " from " + path);
    
    SkyBox skyBox = new SkyBox();
    skyBox.setName(id);
    
    skyBox.setTextureXNEG(getSkyBoxTexture(id, path, "_xneg"));
    skyBox.setTextureXPOS(getSkyBoxTexture(id, path, "_xpos"));
    skyBox.setTextureYNEG(getSkyBoxTexture(id, path, "_yneg"));
    skyBox.setTextureYPOS(getSkyBoxTexture(id, path, "_ypos"));
    skyBox.setTextureZNEG(getSkyBoxTexture(id, path, "_zneg"));
    skyBox.setTextureZPOS(getSkyBoxTexture(id, path, "_zpos"));
    this.skyBoxes.put(id, skyBox);
  }
  
  private Texture getSkyBoxTexture(String id, String path, String type) {
    Texture texture = new Texture(Gdx.files.internal(path+type+".png"));
    textures.put("TEXTURE_"+id+type.toUpperCase(), texture);
    return texture;
  }

  private void addElementAsTileset(Element resourceElement) {
    String id    = resourceElement.getAttribute("id");
    String atlas = resourceElement.getAttribute("atlas");
    String path  = "assets/textures/"+atlas + ".atlas";
    Gdx.app.log(TAG, "Loading tileset atlas: " + id + " from " + path);
    
    FileHandle file = Gdx.files.internal(path);
    TextureAtlas textureAtlas = null;
    if (file.file().exists()) {
      textureAtlas = new TextureAtlas(file);
    } else {
      textureAtlas = new TextureAtlas();
    }
    
    for (Texture text : textureAtlas.getTextures()) {
      text.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    }
    
    atlasMap.put(id, textureAtlas);
    
    Tileset tileset = new Tileset(textureAtlas, id);
    tileset.setAtlasName(atlas);
    NodeList autoTileResources = resourceElement.getElementsByTagName("autotile");
    
    for (int i = 0; i < autoTileResources.getLength(); i++) {
      Element autoTileResource = (Element) autoTileResources.item(i); 
      boolean slope            = autoTileResource.hasAttribute("slope");
      String name              = autoTileResource.getAttribute("name");
      tileset.buildAutotiles(name, slope);
    }
    
    tilesets.put(id, tileset);
  }

  private void addElementAsShader(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String name = resourceElement.getTextContent();
    String path = "assets/shaders/"+name;
    Gdx.app.log(TAG, "Found shader: " + id + " from " + path);
    G.game.getShaderManager().add(id, name+".vert", name+".frag");
  }

  private void addElementAsMusic(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "assets/music/"+path;
    Gdx.app.log(TAG, "Found music: " + id + " from " + path);
    music.put(id, path);
  }

  private void addElementAsTexture(Element resourceElement) {
    String id   = resourceElement.getAttribute("id");
    String path = resourceElement.getTextContent();
    path        = "assets/textures/"+path;
    
    Gdx.app.log(TAG, "Found texture: " + id + " from " + path);
    textures.put(id, new Texture(Gdx.files.internal(path)));
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
    path        = "assets/textures/"+path + ".atlas";
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
    return textures.get(key);
  }
  
  public ShaderProgram getShaderProgram(String key) {
    return null;
  }

  public Tileset getTileset(String key) {
    return tilesets.get(key);
  }

  public Collection<Tileset> allTilesets() {
    return tilesets.values();
  }

  public SkyBox getSkyBox(String string) {
    return skyBoxes.get(string);
  }

}