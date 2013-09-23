package com.macbury.fabula.editor.inspector;

import java.util.ArrayList;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.badlogic.gdx.utils.Array;
import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.l2fprod.common.beans.editor.DimensionPropertyEditor;
import com.l2fprod.common.beans.editor.FloatPropertyEditor;
import com.macbury.fabula.editor.brushes.AutoTileBrush.PaintMode;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.tileset.Tileset;

import de.matthiasmann.twlthemeeditor.properties.EnumProperty;

public class SceneInspectBeanInfo extends BaseBeanInfo {
  private static final String CATEGORY_MAP    = "Map";
  private static final String CATEGORY_BRUSH  = "Brush";
  private static final String CATEGORY_VIEW   = "View";
  private static final String CATEGORY_LIQUID = "Liquids";
  
  public SceneInspectBeanInfo() {
    super(SceneInspect.class);
    
    ExtendedPropertyDescriptor tilesetProperty = addProperty("tileset").setCategory(CATEGORY_MAP);
    tilesetProperty.setDisplayName("Tileset");
    tilesetProperty.setShortDescription("Change map autotiles texture");
    tilesetProperty.setPropertyEditorClass(TilesetEditor.class);
    
    ExtendedPropertyDescriptor skyboxProperty = addProperty("skyBox").setCategory(CATEGORY_MAP);
    skyboxProperty.setDisplayName("Sky Box");
    skyboxProperty.setShortDescription("Sky Box for map and water reflection");
    skyboxProperty.setPropertyEditorClass(SkyBoxEditor.class);
    
    ExtendedPropertyDescriptor sizeProperty = addProperty("mapSize").setCategory(CATEGORY_MAP);
    sizeProperty.setDisplayName("Size");
    sizeProperty.setShortDescription("Change map size");
    sizeProperty.setPropertyEditorClass(DimensionPropertyEditor.class);
    
    ExtendedPropertyDescriptor shaderProperty = addProperty("shader").setCategory(CATEGORY_MAP);
    shaderProperty.setDisplayName("Shader");
    shaderProperty.setShortDescription("Final shader effect");
    shaderProperty.setPropertyEditorClass(ShaderEditor.class);
    
    ExtendedPropertyDescriptor terrainHeightProperty = addProperty("terrainHeight").setCategory(CATEGORY_BRUSH);
    terrainHeightProperty.setDisplayName("Terrain height");
    terrainHeightProperty.setShortDescription("Set terrain height");
    terrainHeightProperty.setPropertyEditorClass(TerrainSpinnerEditor.class);
    
    ExtendedPropertyDescriptor terrainPassableHeightProperty = addProperty("terrainPassable").setCategory(CATEGORY_BRUSH);
    terrainPassableHeightProperty.setDisplayName("Passable");
    
    ExtendedPropertyDescriptor terrainAutoTileTypeProperty = addProperty("paintMode").setCategory(CATEGORY_BRUSH);
    terrainAutoTileTypeProperty.setDisplayName("Paint mode");
    terrainAutoTileTypeProperty.setShortDescription("How you place blocks");
    terrainAutoTileTypeProperty.setPropertyEditorClass(AutoTileEditor.class);
    
    ExtendedPropertyDescriptor wireframeProperty = addProperty("showWireframe").setCategory(CATEGORY_VIEW);
    wireframeProperty.setDisplayName("Show wireframe");
    
    ExtendedPropertyDescriptor collidersProperty = addProperty("showColliders").setCategory(CATEGORY_VIEW);
    collidersProperty.setDisplayName("Show colliders");
    
    ExtendedPropertyDescriptor terrainLiquidHeightProperty = addProperty("liquidHeight").setCategory(CATEGORY_LIQUID);
    terrainLiquidHeightProperty.setDisplayName("Liquid height");
    terrainLiquidHeightProperty.setShortDescription("Set terrain liquid height");
    terrainLiquidHeightProperty.setPropertyEditorClass(TerrainSpinnerEditor.class);
    
    ExtendedPropertyDescriptor applyLiquidProperty = addProperty("liquid").setCategory(CATEGORY_LIQUID);
    applyLiquidProperty.setDisplayName("Apply liquid");
    applyLiquidProperty.setShortDescription("Should apply or remove liquid from tiles");
    
    ExtendedPropertyDescriptor liquidAmplitudeProperty = addProperty("liquidAmplitude").setCategory(CATEGORY_LIQUID);
    liquidAmplitudeProperty.setDisplayName("Liquid amplitude");
    liquidAmplitudeProperty.setShortDescription("...");
    liquidAmplitudeProperty.setPropertyEditorClass(TerrainSpinnerEditor.class);
    
    ExtendedPropertyDescriptor liquidSpeedProperty = addProperty("liquidSpeed").setCategory(CATEGORY_LIQUID);
    liquidSpeedProperty.setDisplayName("Liquid speed");
    liquidSpeedProperty.setShortDescription("...");
    liquidSpeedProperty.setPropertyEditorClass(TerrainSpinnerEditor.class);
  }
  
  public static class AutoTileEditor extends ComboBoxPropertyEditor {
    public AutoTileEditor() {
      super();
      PaintMode[] modes = PaintMode.values();
      String[] values = new String[modes.length];
      
      for (int i = 0; i < modes.length; i++) {
        values[i] = modes[i].toString();
      }
      
      setAvailableValues(values);
    }
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
  
  public static class SkyBoxEditor extends ComboBoxPropertyEditor {
    public SkyBoxEditor() {
      super();
      String[] skyboxes = G.db.getSkyBoxes();
      String[] values   = new String[skyboxes.length + 1];
      values[0] = " ";
      
      for (int i = 0; i < skyboxes.length; i++) {
        values[i+1] = skyboxes[i];
      }
      
      setAvailableValues(values);
    }
  }
  
  public static class TerrainSpinnerEditor extends SpinnerPropertyEditor {

    public TerrainSpinnerEditor() {
      super(Float.class);
    }

    @Override
    public SpinnerModel getModel() {
      return new SpinnerNumberModel(new Float(0), null, null, new Float(0.5f));
    }
    
  }
}
