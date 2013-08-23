package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import javax.swing.JSeparator;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.macbury.fabula.editor.brushes.AutoTileBrush;
import com.macbury.fabula.editor.brushes.TerrainBrush;
import com.macbury.fabula.editor.brushes.TerrainBrush.TerrainBrushType;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.screens.WorldEditScreen;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Tileset;

import java.awt.Canvas;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.Panel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.table.DefaultTableModel;
import java.awt.SystemColor;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JSlider;
import com.macbury.fabula.editor.brushes.AutoTileBrush.PaintMode;
import com.macbury.fabula.editor.code.AssetEditorDialog;
import com.macbury.fabula.editor.tiles.AutoTileDebugFrame;
import com.macbury.fabula.editor.tiles.TilesetBuilderDialog;

import javax.swing.JScrollPane;
import java.awt.Toolkit;

public class WorldEditorFrame extends JFrame implements ChangeListener, ItemListener, ListSelectionListener, ActionListener {
  
  protected static final String TAG = "WorldEditorFrame";
  private JPanel contentPane;
  private LwjglCanvas gameCanvas;
  public JLabel statusBarLabel;
  private GameManager gameManager;
  private JTabbedPane tabbedInspectorPane;
  private JSpinner terrainBrushSizeSpinner;
  private JSpinner terrainBrushAmountSpinner;
  private JComboBox terrainChangeModeComboBox;
  private JList autoTileList;
  private IconListRenderer autoTileListRenderer;
  private JTextField txtffffff;
  private JTextField txtffffff_1;
  private JSpinner lightPositionZSpinner;
  private JSpinner lightPositionYSpinner;
  private JSpinner lightPositionXSpinner;
  private JComboBox paintModeComboBox;
  private JMenuItem mntmBuildTileMap;
  private AutoTileDebugFrame autoTileDebugFrame;
  private RunningGameConsoleFrame runningGameConsoleFrame;
  private JMenuItem mntmRun;
  private JMenuItem mntmRebuildTilesets;
  private JMenuItem mntmEditAssetsgame;
  
  public WorldEditorFrame(GameManager game) {
    setIconImage(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/gwn.ico")));
    setTitle("WorldEd - [No Name]");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run () {
        Runtime.getRuntime().halt(0); // Because fuck you, Swing shutdown hooks.
      }
    });
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    
    this.autoTileDebugFrame = new AutoTileDebugFrame();
    this.runningGameConsoleFrame = new RunningGameConsoleFrame();
    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1280, 760);
    //setExtendedState(Frame.MAXIMIZED_BOTH); 
    
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);
    
    JMenuItem mntmNewMenuItem = new JMenuItem("New");
    mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
    mnFile.add(mntmNewMenuItem);
    
    JMenuItem mntmNewMenuItem_2 = new JMenuItem("Open");
    mntmNewMenuItem_2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
    mnFile.add(mntmNewMenuItem_2);
    
    JMenuItem mntmSave = new JMenuItem("Save");
    mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    mnFile.add(mntmSave);
    
    JSeparator separator = new JSeparator();
    mnFile.add(separator);
    
    JMenuItem mntmQuit = new JMenuItem("Quit");
    mnFile.add(mntmQuit);
    
    JMenuBar menuBar_1 = new JMenuBar();
    mnFile.add(menuBar_1);
    
    JMenu mnGame = new JMenu("Game");
    menuBar.add(mnGame);
    
    this.mntmRun = new JMenuItem("Run");
    mntmRun.addActionListener(this);
    mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
    mnGame.add(mntmRun);
    
    JMenu mnDeveloper = new JMenu("Developer");
    menuBar.add(mnDeveloper);
    
    this.mntmBuildTileMap = new JMenuItem("Auto Tile Hash Map");
    mntmBuildTileMap.addActionListener(this);
    mnDeveloper.add(mntmBuildTileMap);
    
    this.mntmRebuildTilesets = new JMenuItem("Rebuild tilesets");
    mntmRebuildTilesets.addActionListener(this);
    mnDeveloper.add(mntmRebuildTilesets);
    
    this.mntmEditAssetsgame = new JMenuItem("Edit Assets.game");
    mntmEditAssetsgame.addActionListener(this);
    mnDeveloper.add(mntmEditAssetsgame);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    JSplitPane inspectorAndOpenGlContainerSplitPane = new JSplitPane();
    inspectorAndOpenGlContainerSplitPane.setContinuousLayout(true);
    inspectorAndOpenGlContainerSplitPane.setResizeWeight(0.03);
    contentPane.add(inspectorAndOpenGlContainerSplitPane, BorderLayout.CENTER);
    
    JSplitPane mapsTreeAndInspectorSplitPane = new JSplitPane();
    mapsTreeAndInspectorSplitPane.setContinuousLayout(true);
    mapsTreeAndInspectorSplitPane.setResizeWeight(0.1);
    mapsTreeAndInspectorSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    inspectorAndOpenGlContainerSplitPane.setLeftComponent(mapsTreeAndInspectorSplitPane);
    
    this.tabbedInspectorPane = new JTabbedPane(JTabbedPane.TOP);
    
    tabbedInspectorPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    mapsTreeAndInspectorSplitPane.setRightComponent(tabbedInspectorPane);
    
    JPanel panel_6 = new JPanel();
    panel_6.setBackground(Color.WHITE);
    tabbedInspectorPane.addTab("Settings", null, panel_6, null);
    panel_6.setLayout(new FormLayout(new ColumnSpec[] {
        ColumnSpec.decode("5px"),
        FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"),
        ColumnSpec.decode("5px"),
        FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC,},
      new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,}));
    
    JLabel lblNewLabel_3 = new JLabel("Ambient Color:");
    panel_6.add(lblNewLabel_3, "2, 2, right, default");
    
    txtffffff = new JTextField();
    txtffffff.setText("#ffffff");
    txtffffff.setHorizontalAlignment(SwingConstants.LEFT);
    panel_6.add(txtffffff, "4, 2, fill, default");
    txtffffff.setColumns(10);
    
    JButton btnNewButton = new JButton("Pick Color");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JColorChooser.showDialog(
            WorldEditorFrame.this,
            "Choose Background Color", Color.BLUE);
      }
    });
    panel_6.add(btnNewButton, "6, 2, center, default");
    
    JLabel lblDirectionalLightColor = new JLabel("Directional light color");
    panel_6.add(lblDirectionalLightColor, "2, 4, right, default");
    
    txtffffff_1 = new JTextField();
    txtffffff_1.setText("#ffffff");
    panel_6.add(txtffffff_1, "4, 4, fill, default");
    txtffffff_1.setColumns(10);
    
    JButton btnPickColor = new JButton("Pick Color");
    panel_6.add(btnPickColor, "6, 4");
    
    JLabel lblLightPosition = new JLabel("Light position X:");
    panel_6.add(lblLightPosition, "2, 6");
    
    this.lightPositionXSpinner = new JSpinner();
    lightPositionXSpinner.addChangeListener(this);
    lightPositionXSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(-1000), new Float(1000), new Float(0.010f)));
    panel_6.add(lightPositionXSpinner, "4, 6");
    
    JLabel lblLightPositionY = new JLabel("Light position Y:");
    panel_6.add(lblLightPositionY, "2, 8");
    
    this.lightPositionYSpinner = new JSpinner();
    lightPositionYSpinner.addChangeListener(this);
    lightPositionYSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(-1000), new Float(1000), new Float(0.010f)));
    panel_6.add(lightPositionYSpinner, "4, 8");
    
    JLabel lblLightPositionZ = new JLabel("Light position Z:");
    panel_6.add(lblLightPositionZ, "2, 10");
    
    this.lightPositionZSpinner = new JSpinner();
    lightPositionZSpinner.addChangeListener(this);
    lightPositionZSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(-1000), new Float(1000), new Float(0.010f)));
    panel_6.add(lightPositionZSpinner, "4, 10");
    
    JPanel panel = new JPanel();
    panel.setBackground(SystemColor.window);
    tabbedInspectorPane.addTab("Terrain", null, panel, null);
    panel.setLayout(new FormLayout(new ColumnSpec[] {
        ColumnSpec.decode("left:4dlu"),
        ColumnSpec.decode("left:default"),
        FormFactory.RELATED_GAP_COLSPEC,
        FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"),
        ColumnSpec.decode("right:4dlu"),},
      new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,}));
    
    JLabel lblNewLabel = new JLabel("Type");
    panel.add(lblNewLabel, "2, 2");
    
    this.terrainChangeModeComboBox = new JComboBox();
    terrainChangeModeComboBox.addItemListener(this);
    terrainChangeModeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Up", "Down", "Set"}));
    panel.add(terrainChangeModeComboBox, "6, 2, fill, default");
    
    JLabel lblNewLabel_1 = new JLabel("Power");
    panel.add(lblNewLabel_1, "2, 4");
    
    this.terrainBrushAmountSpinner = new JSpinner();
    terrainBrushAmountSpinner.addChangeListener(this);
    terrainBrushAmountSpinner.setModel(new SpinnerNumberModel(new Float(0.1f), new Float(0.0f), null, new Float(0.1f)));
    panel.add(terrainBrushAmountSpinner, "6, 4");
    
    JLabel lblNewLabel_2 = new JLabel("Size");
    panel.add(lblNewLabel_2, "2, 6");
    
    this.terrainBrushSizeSpinner = new JSpinner();
    terrainBrushSizeSpinner.addChangeListener(this);
    
    terrainBrushSizeSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
    panel.add(terrainBrushSizeSpinner, "6, 6");
    
    JPanel panel_1 = new JPanel();
    tabbedInspectorPane.addTab("Tiles", null, panel_1, null);
    panel_1.setLayout(new BorderLayout(0, 0));
    
    this.paintModeComboBox = new JComboBox();
    paintModeComboBox.addItemListener(this);
    
    this.autoTileList = new JList(new Object[] { });
    panel_1.add(new JScrollPane(autoTileList), BorderLayout.CENTER);
    autoTileList.setValueIsAdjusting(true);
    autoTileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    autoTileList.setBorder(new EmptyBorder(0, 0, 0, 0));
    autoTileList.addListSelectionListener(this);
    autoTileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    autoTileList.setVisibleRowCount(0);
    autoTileList.setFixedCellWidth(AutoTiles.TILE_SIZE);
    autoTileList.setFixedCellHeight(AutoTiles.TILE_SIZE);
    paintModeComboBox.setModel(new DefaultComboBoxModel(PaintMode.values()));
    panel_1.add(paintModeComboBox, BorderLayout.NORTH);
    
    JPanel panel_7 = new JPanel();
    tabbedInspectorPane.addTab("Grass", null, panel_7, null);
    
    JPanel panel_2 = new JPanel();
    tabbedInspectorPane.addTab("Objects", null, panel_2, null);
    
    JPanel panel_3 = new JPanel();
    tabbedInspectorPane.addTab("Events", null, panel_3, null);
    
    JScrollPane scrollPane = new JScrollPane();
    mapsTreeAndInspectorSplitPane.setLeftComponent(scrollPane);
    
    JTree tree = new JTree();
    tree.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setViewportView(tree);
    
    JPanel openGLContainerPane = new JPanel();
    openGLContainerPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    inspectorAndOpenGlContainerSplitPane.setRightComponent(openGLContainerPane);
    
    this.gameManager = game;
    this.gameCanvas = new LwjglCanvas(game, true);
    openGLContainerPane.add(this.gameCanvas.getCanvas(), BorderLayout.CENTER);
    openGLContainerPane.setLayout(new BoxLayout(openGLContainerPane, BoxLayout.X_AXIS));
    
    addWindowListener(new ExitListener(gameCanvas));
    
    JPanel panel_4 = new JPanel();
    panel_4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
    contentPane.add(panel_4, BorderLayout.SOUTH);
    panel_4.setLayout(new BorderLayout(0, 0));
    
    JPanel panel_5 = new JPanel();
    panel_5.setBorder(new EmptyBorder(5, 15, 5, 15));
    panel_4.add(panel_5, BorderLayout.NORTH);
    panel_5.setLayout(new BorderLayout(0, 0));
    
    this.statusBarLabel = new JLabel("X: 0 Y:0 Z:0");
    panel_5.add(statusBarLabel, BorderLayout.WEST);
    
    Thread statusbarThread = new Thread(new StatusBarInfoRunnable());
    statusbarThread.start();
    
    tabbedInspectorPane.addChangeListener(this);
    
  }
  
  private class StatusBarInfoRunnable implements Runnable {
    public void run() {
      boolean running = true;
      while(WorldEditorFrame.this.gameManager.loading()) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
      updateInfoForMapSettings();
      
      while (running) {
        try {
          Thread.sleep(50);
          WorldEditorFrame.this.statusBarLabel.setText(WorldEditorFrame.this.gameManager.getWorldEditScreen().debugInfo);
        } catch (InterruptedException e) {
          running = false;
        }
      }
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    Scene scene = screen.getScene();
    DirectionalLight sun = scene.getSunLight();
    
    if (e.getSource() == tabbedInspectorPane) {
      switch (tabbedInspectorPane.getSelectedIndex()) {
        case 0:
          updateInfoForMapSettings();
        break;
        case 1:
          screen.setCurrentBrush(screen.getTerrainBrush());
          updateInfoForTerrainBrush();
        break;
        
        case 2:
          screen.setCurrentBrush(screen.getAutoTileBrush());
          updateInfoForAutotileBrush();
        break;
        
        default:
          break;
      }
    }
    
    if (e.getSource() == lightPositionXSpinner) {
      sun.direction.x = (float) lightPositionXSpinner.getValue();
    }
    
    if (e.getSource() == lightPositionYSpinner) {
      sun.direction.y = (float) lightPositionYSpinner.getValue();
    }
    
    if (e.getSource() == lightPositionZSpinner) {
      sun.direction.z = (float) lightPositionZSpinner.getValue();
    }
    
    if (e.getSource() == terrainBrushSizeSpinner) {
      this.gameManager.getWorldEditScreen().getTerrainBrush().setSize((int)terrainBrushSizeSpinner.getValue());
    }
    
    if (e.getSource() == terrainBrushAmountSpinner) {
      this.gameManager.getWorldEditScreen().getTerrainBrush().setPower((float)terrainBrushAmountSpinner.getValue());
    }
  }

  private void updateInfoForMapSettings() {
    Scene scene = this.gameManager.getWorldEditScreen().getScene();
    DirectionalLight sun = scene.getSunLight();
    this.lightPositionXSpinner.setValue(sun.direction.x);
    this.lightPositionYSpinner.setValue(sun.direction.y);
    this.lightPositionZSpinner.setValue(sun.direction.z);
  }

  private void updateInfoForAutotileBrush() {
    AutoTileBrush atBrush     = this.gameManager.getWorldEditScreen().getAutoTileBrush();
    atBrush.setPaintMode((PaintMode) this.paintModeComboBox.getSelectedItem());
    atBrush.buildAllPreviewsUnlessBuilded();
    this.autoTileListRenderer = new IconListRenderer(atBrush.getAutoTileIcons());
    autoTileList.setCellRenderer(autoTileListRenderer);

    DefaultListModel listModel = new DefaultListModel();
    
    if (atBrush.getCurrentPaintMode() == PaintMode.AutoTile) {
      for (String key : atBrush.getOrderedTileNames()) {
        listModel.addElement(key);
      }
    } else {
      for (String key : atBrush.getAllOrderedTileNames()) {
        listModel.addElement(key);
      }
    }
    
    autoTileList.setModel(listModel);
  }

  private void updateInfoForTerrainBrush() {
    terrainBrushSizeSpinner.setValue(this.gameManager.getWorldEditScreen().getTerrainBrush().getSize());
    terrainBrushAmountSpinner.setValue(this.gameManager.getWorldEditScreen().getTerrainBrush().getPower());
    int terrainChangeModeIndex = 0;
    
    //if (this.gameManager.getWorldEditScreen().getTerrainBrush().getType() == TerrainBrushType.)
    
    switch (this.gameManager.getWorldEditScreen().getTerrainBrush().getType()) {
      case Up:
        terrainChangeModeIndex = 0;
      break;
      
      case Down:
        terrainChangeModeIndex = 1;
      break;
      
      case Set:
        terrainChangeModeIndex = 2;
      break;
      
      default:
        terrainChangeModeIndex = 0;
      break;
    }
      
    terrainChangeModeComboBox.setSelectedIndex(terrainChangeModeIndex);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    System.gc();
    if (e.getSource() == terrainChangeModeComboBox) {
      TerrainBrush terrainBrush = this.gameManager.getWorldEditScreen().getTerrainBrush();
      switch (terrainChangeModeComboBox.getSelectedIndex()) {
        case 0:
          terrainBrush.setType(TerrainBrushType.Up);
        break;
        
        case 1:
          terrainBrush.setType(TerrainBrushType.Down);
        break;
        
        case 2:
          terrainBrush.setType(TerrainBrushType.Set);
        break;
        
        default:
          terrainBrush.setType(TerrainBrushType.Up);
        break;
      }
    }
    
    if (e.getSource() == paintModeComboBox) {
      updateInfoForAutotileBrush();
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    if (e.getSource() == autoTileList) {
      Tileset tileset     = screen.getScene().getTerrain().getTileset();
      AutoTileBrush brush = screen.getAutoTileBrush();
      String key          = (String)autoTileList.getSelectedValue();
      AutoTile at         = tileset.getAutoTile(key);
      
      if (at != null) {
        if (brush.getCurrentPaintMode().equals(PaintMode.AutoTile)) {
          brush.setCurrentAutoTiles(at.getAutoTiles());
        } else {
          brush.setCurrentAutoTile(at);
        }
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    AutoTileBrush brush = screen.getAutoTileBrush();
    if (e.getSource() == mntmBuildTileMap) {
      autoTileDebugFrame.setBrush(brush);
      autoTileDebugFrame.updateRows();
      autoTileDebugFrame.setVisible(true);
    }

    if (e.getSource() == mntmRun) {
      runningGameConsoleFrame.runGame(this.gameManager);
    }
    
    if (e.getSource() == mntmEditAssetsgame) {
      AssetEditorDialog editor = new AssetEditorDialog();
      editor.setVisible(true);
    }
    
    if (e.getSource() == this.mntmRebuildTilesets) {
      TilesetBuilderDialog dialog = new TilesetBuilderDialog();
      dialog.setVisible(true);
    }
  }
}
