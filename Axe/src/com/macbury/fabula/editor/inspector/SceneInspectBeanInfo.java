package com.macbury.fabula.editor.inspector;

import java.util.ArrayList;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.l2fprod.common.beans.editor.DimensionPropertyEditor;
import com.l2fprod.common.beans.editor.FloatPropertyEditor;
import com.macbury.fabula.editor.brushes.AutoTileBrush.PaintMode;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.screens.WorldEditScreen;
import com.macbury.fabula.terrain.foliage.FoliageDescriptor;
import com.macbury.fabula.terrain.foliage.FoliageSet;
import com.macbury.fabula.terrain.tileset.Tileset;

import de.matthiasmann.twlthemeeditor.properties.EnumProperty;

public class SceneInspectBeanInfo extends BaseBeanInfo {
  private static final String CATEGORY_MAP       = "Map";
  private static final String CATEGORY_GEOMETRY  = "Geometry";
  private static final String CATEGORY_COLLISION = "Collision";
  private static final String CATEGORY_TEXTURE   = "Auto Tile";
  private static final String CATEGORY_VIEW      = "View";
  private static final String CATEGORY_LIQUID    = "Liquids";
  private static final String CATEGORY_FOLIAGES  = "Foliages";
  public SceneInspectBeanInfo() {
    super(SceneInspect.class);
    
    ExtendedPropertyDescriptor tilesetProperty = addProperty("tileset").setCategory(CATEGORY_TEXTURE);
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
    
    ExtendedPropertyDescriptor terrainHeightProperty = addProperty("terrainHeight").setCategory(CATEGORY_GEOMETRY);
    terrainHeightProperty.setDisplayName("Set height");
    terrainHeightProperty.setShortDescription("Set terrain height");
    terrainHeightProperty.setPropertyEditorClass(TerrainSpinnerEditor.class);
    
    ExtendedPropertyDescriptor terrainPassableHeightProperty = addProperty("terrainPassable").setCategory(CATEGORY_COLLISION);
    terrainPassableHeightProperty.setDisplayName("Passable");
    
    ExtendedPropertyDescriptor terrainAutoTileTypeProperty = addProperty("paintMode").setCategory(CATEGORY_TEXTURE);
    terrainAutoTileTypeProperty.setDisplayName("Mode");
    terrainAutoTileTypeProperty.setShortDescription("How you place blocks");
    terrainAutoTileTypeProperty.setPropertyEditorClass(AutoTileEditor.class);
    
    ExtendedPropertyDescriptor wireframeProperty = addProperty("showWireframe").setCategory(CATEGORY_VIEW);
    wireframeProperty.setDisplayName("Show wireframe");
    
    ExtendedPropertyDescriptor collidersProperty = addProperty("showColliders").setCategory(CATEGORY_VIEW);
    collidersProperty.setDisplayName("Show colliders");
    
    ExtendedPropertyDescriptor terrainLiquidHeightProperty = addProperty("liquidHeight").setCategory(CATEGORY_LIQUID);
    terrainLiquidHeightProperty.setDisplayName("Height");
    terrainLiquidHeightProperty.setShortDescription("Set terrain liquid height");
    terrainLiquidHeightProperty.setPropertyEditorClass(TerrainSpinnerEditor.class);
    
    ExtendedPropertyDescriptor applyLiquidProperty = addProperty("liquid").setCategory(CATEGORY_LIQUID);
    applyLiquidProperty.setDisplayName("Apply");
    applyLiquidProperty.setShortDescription("Should apply or remove liquid from tiles");
    
    ExtendedPropertyDescriptor liquidAmplitudeProperty = addProperty("liquidAmplitude").setCategory(CATEGORY_LIQUID);
    liquidAmplitudeProperty.setDisplayName("Amplitude");
    liquidAmplitudeProperty.setShortDescription("...");
    
    ExtendedPropertyDescriptor liquidSpeedProperty = addProperty("liquidSpeed").setCategory(CATEGORY_LIQUID);
    liquidSpeedProperty.setDisplayName("Liquid speed");
    liquidSpeedProperty.setShortDescription("...");
    
    ExtendedPropertyDescriptor liquidMaterialProperty = addProperty("liquidMaterial").setCategory(CATEGORY_LIQUID);
    liquidMaterialProperty.setDisplayName("Material");
    liquidMaterialProperty.setShortDescription("Graphics for liquid");
    liquidMaterialProperty.setPropertyEditorClass(LiquidMaterialEditor.class);
    
    ExtendedPropertyDescriptor liquidAnimationSpeed = addProperty("liquidAnimationSpeed").setCategory(CATEGORY_LIQUID);
    liquidAnimationSpeed.setDisplayName("Animation speed");
    liquidAnimationSpeed.setShortDescription("Tile animation speed");
    
    ExtendedPropertyDescriptor liquidAlpha = addProperty("liquidAlpha").setCategory(CATEGORY_LIQUID);
    liquidAlpha.setDisplayName("Alpha");
    liquidAlpha.setShortDescription("Liquid transparency");
    
    ExtendedPropertyDescriptor liquidMix = addProperty("liquidMix").setCategory(CATEGORY_LIQUID);
    liquidMix.setDisplayName("Mix");
    liquidMix.setShortDescription("Diffrence between water texture and skybox cubemap");
    
    ExtendedPropertyDescriptor foliagesNameProperty = addProperty("foliageSet").setCategory(CATEGORY_FOLIAGES);
    foliagesNameProperty.setDisplayName("Texture");
    foliagesNameProperty.setShortDescription("Foliage textures");
    foliagesNameProperty.setPropertyEditorClass(FoliagesEditor.class);
    
    ExtendedPropertyDescriptor foliageDescriptorNameProperty = addProperty("foliageDescriptor").setCategory(CATEGORY_FOLIAGES);
    foliageDescriptorNameProperty.setDisplayName("Type");
    foliageDescriptorNameProperty.setShortDescription("Foliage type");
    foliageDescriptorNameProperty.setPropertyEditorClass(FoliageDescriptorEditor.class);
    
    ExtendedPropertyDescriptor foliageAmplitude = addProperty("foliageAmplitude").setCategory(CATEGORY_FOLIAGES);
    foliageAmplitude.setDisplayName("Amplitude");
    foliageAmplitude.setShortDescription("Foliage amplitude");
    
    ExtendedPropertyDescriptor foliageSpeed = addProperty("foliageSpeed").setCategory(CATEGORY_FOLIAGES);
    foliageSpeed.setDisplayName("Speed");
    foliageSpeed.setShortDescription("Foliage speed");
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
  
  public static class FoliagesEditor extends ComboBoxPropertyEditor {
    public FoliagesEditor() {
      super();
      ArrayList<FoliageSet> foliages = G.db.getFoliages();
      String[] values = new String[foliages.size()];
      
      for (int i = 0; i < foliages.size(); i++) {
        values[i] = foliages.get(i).getName();
      }
      
      setAvailableValues(values);
    }
  }
  
  public static class FoliageDescriptorEditor extends ComboBoxPropertyEditor {
    public FoliageDescriptorEditor() {
      super();
      ArrayList<FoliageDescriptor> leaves  = WorldEditScreen.shared().getScene().getTerrain().getFoliageSet().getLeaves();
      String[] values = new String[leaves.size()+1];
      values[0] = " ";
      
      for (int i = 0; i < leaves.size(); i++) {
        values[i+1] = leaves.get(i).getRegionName();
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
  
  public static class LiquidMaterialEditor extends ComboBoxPropertyEditor {
    public LiquidMaterialEditor() {
      super();
      Array<String> uniqueName   = new Array<String>();
      
      for (AtlasRegion region: G.db.getLiquidAtlas().getRegions()) {
        if (uniqueName.indexOf(region.name, false) == -1) {
          uniqueName.add(region.name);
        }
      }
      
      String[] values = new String[uniqueName.size];
      
      for (int i = 0; i < uniqueName.size; i++) {
        values[i] = uniqueName.get(i);
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
