package com.macbury.fabula.editor.inspector;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.Tileset;

public class SceneInspectBeanInfo extends BaseBeanInfo {
  private static final String CATEGORY_MAP = "Map";

  public SceneInspectBeanInfo() {
    super(SceneInspect.class);
    
    ExtendedPropertyDescriptor tilesetProperty = addProperty("tileset").setCategory(CATEGORY_MAP);
    tilesetProperty.setDisplayName("Tileset");
    tilesetProperty.setShortDescription("Change map autotiles texture");
    tilesetProperty.setPropertyEditorClass(TilesetEditor.class);
    
    ExtendedPropertyDescriptor shaderProperty = addProperty("shader").setCategory(CATEGORY_MAP);
    shaderProperty.setDisplayName("Shader");
    shaderProperty.setShortDescription("Final shader effect");
    shaderProperty.setPropertyEditorClass(ShaderEditor.class);
  }
  
  public static class ShaderEditor extends ComboBoxPropertyEditor {
    public ShaderEditor() {
      super();
      Array<String> shadersName = G.shaders.getAllShaderNames().toArray();
      String[] values = new String[shadersName.size];
      
      for (int i = 0; i < shadersName.size; i++) {
        values[i] = shadersName.get(i);
      }
      
      setAvailableValues(values);
    }
  }
  
  public static class TilesetEditor extends ComboBoxPropertyEditor {
    public TilesetEditor() {
      super();
      ArrayList<Tileset> tilesets = G.db.getTilesets();
      String[] values = new String[tilesets.size()];
      
      for (int i = 0; i < tilesets.size(); i++) {
        values[i] = tilesets.get(i).getName();
      }
      
      setAvailableValues(values);
    }
  }
}
