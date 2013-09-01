package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.utils.Array;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.macbury.fabula.editor.brushes.AutoTileBrush;
import com.macbury.fabula.editor.brushes.AutoTileBrush.PaintMode;
import com.macbury.fabula.editor.code.AssetEditorDialog;
import com.macbury.fabula.editor.gamerunner.RunningGameConsoleFrame;
import com.macbury.fabula.editor.shaders.ShaderEditorFrame;
import com.macbury.fabula.editor.tiles.AutoTileDebugFrame;
import com.macbury.fabula.editor.tiles.TilesetBuilderDialog;
import com.macbury.fabula.editor.tree.GameTransferableHandler;
import com.macbury.fabula.editor.tree.GameTreeCellRenderer;
import com.macbury.fabula.editor.tree.GameTreeModel;
import com.macbury.fabula.editor.tree.GameTreeModel.BaseGameFolderNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GameShaderNode;
import com.macbury.fabula.editor.undo_redo.ChangeManager;
import com.macbury.fabula.editor.undo_redo.ChangeManagerListener;
import com.macbury.fabula.manager.EditorGameManager;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.screens.WorldEditScreen;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Tileset;

public class WorldEditorFrame extends JFrame implements ChangeListener, ItemListener, ListSelectionListener, ActionListener, ChangeManagerListener, MouseListener, DropTargetListener {
  
  protected static final String TAG = "WorldEditorFrame";
  private JPanel contentPane;
  private LwjglCanvas gameCanvas;
  public JLabel statusBarLabel;
  private EditorGameManager gameManager;
  private JTabbedPane tabbedInspectorPane;
  private JSpinner terrainBrushAmountSpinner;
  private JList autoTileList;
  private IconListRenderer autoTileListRenderer;
  private JTextField txtAmbientColor;
  private JTextField txtDirectionalLightColor;
  private JSpinner lightPositionZSpinner;
  private JSpinner lightPositionYSpinner;
  private JSpinner lightPositionXSpinner;
  private JComboBox paintModeComboBox;
  private JMenuItem mntmBuildTileMap;
  private AutoTileDebugFrame autoTileDebugFrame;
  private ShaderEditorFrame  shaderEditorFrame;
  private JMenuItem mntmRun;
  private JMenuItem mntmRebuildTilesets;
  private JTextArea logArea;
  private JMenuItem mntmDebugFrameBuffer;
  private JMenuBar mainMenuBar;
  private JSplitPane mainSplitPane;
  private ChangeManager changeManager;
  private JMenuItem mntmUndo;
  private JMenuItem mntmRedo;
  private JTree gameTree;
  private JComboBox shadersComboBox;
  private JButton btnPickAmbientColor;
  private JButton btnPickDirectionalLightColor;
  private JTextField mapNameTextField;
  private JMenuItem mntmSaveGame;
  public WorldEditorFrame(EditorGameManager game) {
    PrintStream origOut = System.out;
    PrintStream interceptor = new LogInterceptor(origOut);
    System.setOut(interceptor);
    
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
    
    this.autoTileDebugFrame      = new AutoTileDebugFrame();
    
    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1331, 923);
    //setExtendedState(Frame.MAXIMIZED_BOTH); 
    
    this.mainMenuBar = new JMenuBar();
    setJMenuBar(mainMenuBar);
    
    JMenu mnGame = new JMenu("Game");
    mainMenuBar.add(mnGame);
    
    this.mntmRun = new JMenuItem("Run");
    mntmRun.addActionListener(this);
    mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
    mnGame.add(mntmRun);
    
    JMenuItem mntmUploadData = new JMenuItem("Upload data");
    mnGame.add(mntmUploadData);
    
    JMenuItem mntmBuildAssetBundle = new JMenuItem("Build Asset Bundle");
    mnGame.add(mntmBuildAssetBundle);
    
    JSeparator separator_1 = new JSeparator();
    mnGame.add(separator_1);
    
    this.mntmSaveGame = new JMenuItem("Save");
    mntmSaveGame.addActionListener(this);
    mntmSaveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    mnGame.add(mntmSaveGame);
    
    JMenu mnEdit = new JMenu("Edit");
    mainMenuBar.add(mnEdit);
    
    this.mntmUndo = new JMenuItem("Undo");
    mntmUndo.addActionListener(this);
    mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
    mnEdit.add(mntmUndo);
    
    this.mntmRedo = new JMenuItem("Redo");
    mntmRedo.addActionListener(this);
    mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
    mnEdit.add(mntmRedo);
    
    JSeparator separator = new JSeparator();
    mnEdit.add(separator);
    
    JMenuItem mntmNewMenuItem = new JMenuItem("Cut");
    mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
    mntmNewMenuItem.setEnabled(false);
    mnEdit.add(mntmNewMenuItem);
    
    JMenuItem mntmCopy = new JMenuItem("Copy");
    mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
    mntmCopy.setEnabled(false);
    mnEdit.add(mntmCopy);
    
    JMenuItem mntmPaste = new JMenuItem("Paste");
    mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
    mntmPaste.setEnabled(false);
    mnEdit.add(mntmPaste);
    
    JMenu mnDeveloper = new JMenu("Developer");
    mainMenuBar.add(mnDeveloper);
    
    this.mntmBuildTileMap = new JMenuItem("Auto Tile Hash Map");
    mntmBuildTileMap.addActionListener(this);
    mnDeveloper.add(mntmBuildTileMap);
    
    this.mntmRebuildTilesets = new JMenuItem("Rebuild tilesets");
    mntmRebuildTilesets.addActionListener(this);
    mnDeveloper.add(mntmRebuildTilesets);
    
    this.mntmDebugFrameBuffer = new JMenuItem("Debug frame buffer");
    mntmDebugFrameBuffer.addActionListener(this);
    mnDeveloper.add(mntmDebugFrameBuffer);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    this.gameManager = game;
    this.gameCanvas = new LwjglCanvas(game, true);
    
    addWindowListener(new ExitListener(gameCanvas));
    
    this.mainSplitPane = new JSplitPane();
    mainSplitPane.setBorder(BorderFactory.createEmptyBorder());
    mainSplitPane.setResizeWeight(0.7);
    mainSplitPane.setContinuousLayout(true);
    mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    contentPane.add(mainSplitPane, BorderLayout.CENTER);
    
    JPanel panel_8 = new JPanel();
    panel_8.setBorder(new EmptyBorder(0, 0, 0, 0));
    mainSplitPane.setRightComponent(panel_8);
    panel_8.setLayout(new BorderLayout(0, 0));
    
    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBorder(BorderFactory.createEmptyBorder());
    panel_8.add(scrollPane_1, BorderLayout.CENTER);
    
    this.logArea = new JTextArea();
    logArea.setForeground(Color.GREEN);
    logArea.setBackground(Color.BLACK);
    logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
    logArea.setEditable(false);
    logArea.setLineWrap(true);
    scrollPane_1.setViewportView(logArea);
    
    JPanel panel_5 = new JPanel();
    panel_8.add(panel_5, BorderLayout.SOUTH);
    panel_5.setBorder(new EmptyBorder(10, 5, 5, 5));
    panel_5.setLayout(new BorderLayout(0, 0));
    
    this.statusBarLabel = new JLabel("X: 0 Y:0 Z:0");
    panel_5.add(statusBarLabel, BorderLayout.WEST);
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    panel_5.add(progressBar, BorderLayout.EAST);
    
    JPanel panel_9 = new JPanel();
    panel_9.setBorder(new EmptyBorder(0, 0, 0, 0));
    mainSplitPane.setLeftComponent(panel_9);
    panel_9.setLayout(new BorderLayout(0, 0));
    
    JSplitPane inspectorAndOpenGlContainerSplitPane = new JSplitPane();
    panel_9.add(inspectorAndOpenGlContainerSplitPane, BorderLayout.CENTER);
    inspectorAndOpenGlContainerSplitPane.setContinuousLayout(true);
    inspectorAndOpenGlContainerSplitPane.setResizeWeight(0.06);
    
    JSplitPane mapsTreeAndInspectorSplitPane = new JSplitPane();
    mapsTreeAndInspectorSplitPane.setBorder(BorderFactory.createEmptyBorder());
    mapsTreeAndInspectorSplitPane.setContinuousLayout(true);
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
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,}));
    
    JLabel lblNewLabel_5 = new JLabel("Name");
    panel_6.add(lblNewLabel_5, "2, 2, right, default");
    
    mapNameTextField = new JTextField();
    panel_6.add(mapNameTextField, "4, 2, fill, default");
    mapNameTextField.setColumns(10);
    
    JLabel lblNewLabel_6 = new JLabel("Tileset");
    panel_6.add(lblNewLabel_6, "2, 4, right, default");
    
    JComboBox comboBox = new JComboBox();
    panel_6.add(comboBox, "4, 4, fill, default");
    
    JLabel lblNewLabel_3 = new JLabel("Ambient Color:");
    panel_6.add(lblNewLabel_3, "2, 6, right, default");
    
    txtAmbientColor = new JTextField();
    txtAmbientColor.setText("#ffffff");
    txtAmbientColor.setHorizontalAlignment(SwingConstants.LEFT);
    panel_6.add(txtAmbientColor, "4, 6, fill, default");
    txtAmbientColor.setColumns(10);
    
    this.btnPickAmbientColor = new JButton("...");
    btnPickAmbientColor.addActionListener(this);
    panel_6.add(btnPickAmbientColor, "6, 6, center, default");
    
    JLabel lblDirectionalLightColor = new JLabel("Directional light color");
    panel_6.add(lblDirectionalLightColor, "2, 8, right, default");
    
    txtDirectionalLightColor = new JTextField();
    txtDirectionalLightColor.setText("#ffffff");
    panel_6.add(txtDirectionalLightColor, "4, 8, fill, default");
    txtDirectionalLightColor.setColumns(10);
    
    this.btnPickDirectionalLightColor = new JButton("...");
    btnPickDirectionalLightColor.addActionListener(this);
    panel_6.add(btnPickDirectionalLightColor, "6, 8");
    
    JLabel lblLightPosition = new JLabel("Light position X:");
    panel_6.add(lblLightPosition, "2, 10, right, default");
    
    this.lightPositionXSpinner = new JSpinner();
    lightPositionXSpinner.addChangeListener(this);
    lightPositionXSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(-1000), new Float(1000), new Float(0.010f)));
    panel_6.add(lightPositionXSpinner, "4, 10");
    
    JLabel lblLightPositionY = new JLabel("Light position Y:");
    panel_6.add(lblLightPositionY, "2, 12, right, default");
    
    this.lightPositionYSpinner = new JSpinner();
    lightPositionYSpinner.addChangeListener(this);
    lightPositionYSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(-1000), new Float(1000), new Float(0.010f)));
    panel_6.add(lightPositionYSpinner, "4, 12");
    
    JLabel lblLightPositionZ = new JLabel("Light position Z:");
    panel_6.add(lblLightPositionZ, "2, 14, right, default");
    
    this.lightPositionZSpinner = new JSpinner();
    lightPositionZSpinner.addChangeListener(this);
    lightPositionZSpinner.setModel(new SpinnerNumberModel(new Float(0), new Float(-1000), new Float(1000), new Float(0.010f)));
    panel_6.add(lightPositionZSpinner, "4, 14");
    
    JLabel lblPostProcessing = new JLabel("Post processing");
    panel_6.add(lblPostProcessing, "2, 16, right, default");
    
    this.shadersComboBox = new JComboBox();
    shadersComboBox.addActionListener(this);
    panel_6.add(shadersComboBox, "4, 16, fill, default");
    
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
    
    JLabel lblNewLabel_1 = new JLabel("Power");
    panel.add(lblNewLabel_1, "2, 2");
    
    this.terrainBrushAmountSpinner = new JSpinner();
    terrainBrushAmountSpinner.addChangeListener(this);
    terrainBrushAmountSpinner.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.5f)));
    panel.add(terrainBrushAmountSpinner, "6, 2");
    
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
    
    JPanel panel_4 = new JPanel();
    tabbedInspectorPane.addTab("Liquid", null, panel_4, null);
    
    JPanel panel_2 = new JPanel();
    tabbedInspectorPane.addTab("Object", null, panel_2, null);
    panel_2.setLayout(new FormLayout(new ColumnSpec[] {
        ColumnSpec.decode("15px"),
        FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("max(19dlu;default)"),
        ColumnSpec.decode("default:grow"),
        ColumnSpec.decode("15px"),},
      new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC,}));
    
    JLabel lblNewLabel = new JLabel("X");
    panel_2.add(lblNewLabel, "3, 2, center, default");
    
    JSpinner spinner = new JSpinner();
    panel_2.add(spinner, "4, 2");
    
    JLabel lblNewLabel_2 = new JLabel("Y");
    panel_2.add(lblNewLabel_2, "3, 4, center, default");
    
    JSpinner spinner_1 = new JSpinner();
    panel_2.add(spinner_1, "4, 4");
    
    JLabel lblNewLabel_4 = new JLabel("Z");
    panel_2.add(lblNewLabel_4, "3, 6, center, default");
    
    JSpinner spinner_2 = new JSpinner();
    panel_2.add(spinner_2, "4, 6");
    
    JPanel panel_3 = new JPanel();
    tabbedInspectorPane.addTab("Events", null, panel_3, null);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
    mapsTreeAndInspectorSplitPane.setLeftComponent(scrollPane);
    
    this.gameTree = new JTree();
    
    gameTree.setDragEnabled(true);
    gameTree.setTransferHandler(new GameTransferableHandler());
    
    gameTree.setCellRenderer(new GameTreeCellRenderer());
    gameTree.addMouseListener(this);
    gameTree.setShowsRootHandles(true);
    gameTree.setSelectionRow(0);
    this.gameTree.setBorder(BorderFactory.createEmptyBorder());
    gameTree.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setViewportView(gameTree);
    
    JPanel openGLContainerPane = new JPanel();
    openGLContainerPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    inspectorAndOpenGlContainerSplitPane.setRightComponent(openGLContainerPane);
    openGLContainerPane.add(this.gameCanvas.getCanvas(), BorderLayout.CENTER);
    openGLContainerPane.setLayout(new BoxLayout(openGLContainerPane, BoxLayout.X_AXIS));
    
    DropTarget dt = new DropTarget(this.gameCanvas.getCanvas(), this);
    tabbedInspectorPane.addChangeListener(this);
    
    Thread statusbarThread = new Thread(new StatusBarInfoRunnable());
    statusbarThread.start();
    
    changeManager = new ChangeManager(this);
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
      
      gameTree.setModel(new GameTreeModel());
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
    
    if (e.getSource() == terrainBrushAmountSpinner) {
      this.gameManager.getWorldEditScreen().getTerrainBrush().setPower((float)terrainBrushAmountSpinner.getValue());
    }
  }

  private void updateInfoForMapSettings() {
    Scene scene = this.gameManager.getWorldEditScreen().getScene();
    DirectionalLight sun = scene.getSunLight();
    this.gameManager.getWorldEditScreen().setChangeManager(changeManager);
    this.lightPositionXSpinner.setValue(sun.direction.x);
    this.lightPositionYSpinner.setValue(sun.direction.y);
    this.lightPositionZSpinner.setValue(sun.direction.z);
    
    Array<String> shadersName = G.shaders.getAllShaderNames().toArray();
    shadersComboBox.setModel(new DefaultComboBoxModel(shadersName.toArray(String.class)));
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
    terrainBrushAmountSpinner.setValue(this.gameManager.getWorldEditScreen().getTerrainBrush().getPower());
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    System.gc();
    
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
    Scene scene            = screen.getScene();
    AutoTileBrush brush = screen.getAutoTileBrush();
    if (e.getSource() == mntmBuildTileMap) {
      autoTileDebugFrame.setBrush(brush);
      autoTileDebugFrame.updateRows();
      autoTileDebugFrame.setVisible(true);
    }
    
    if (e.getSource() == mntmRun) {
      RunningGameConsoleFrame runningGameConsoleFrame = new RunningGameConsoleFrame();
      runningGameConsoleFrame.runGame(this, gameManager);
    }
    
    if (e.getSource() == this.mntmRebuildTilesets) {
      TilesetBuilderDialog dialog = new TilesetBuilderDialog();
      dialog.setVisible(true);
    }
    
    if (e.getSource() == mntmSaveGame) {
      G.db.save();
      scene.save();
    }
    
    if (e.getSource() == mntmDebugFrameBuffer) {
      screen.getScene().debug();
    }
    
    if (e.getSource() == mntmUndo) {
      changeManager.undo();
    }
    
    if (e.getSource() == mntmRedo) {
      changeManager.redo();
    }
    
    if (e.getSource() == btnPickAmbientColor) {
      Color color = new Color(scene.getLights().ambientLight.r, scene.getLights().ambientLight.g, scene.getLights().ambientLight.b, scene.getLights().ambientLight.a);
      color = JColorChooser.showDialog(this, "Ambient Color", color);
      txtAmbientColor.setText("#"+Integer.toHexString(color.getRGB()).toUpperCase());
      scene.getLights().ambientLight.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    if (e.getSource() == btnPickDirectionalLightColor) {
      Color color = new Color(scene.getSunLight().color.r, scene.getSunLight().color.g, scene.getSunLight().color.b, scene.getSunLight().color.a);
      color = JColorChooser.showDialog(this, "Directional light Color", color);
      txtDirectionalLightColor.setText("#"+Integer.toHexString(color.getRGB()).toUpperCase());
      scene.getSunLight().color.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    if (e.getSource() == shadersComboBox) {
      screen.getScene().setFinalShader((String)shadersComboBox.getSelectedItem());
    }
  }

  private class LogInterceptor extends PrintStream {
    public LogInterceptor(OutputStream out) {
      super(out, true);
    }
    
    @Override
    public void print(String s) {
      JTextArea area = WorldEditorFrame.this.logArea;
      try {
        area.getDocument().insertString(area.getDocument().getEndPosition().getOffset(),s+"\n", null);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
      
      WorldEditorFrame.this.logArea.setCaretPosition(WorldEditorFrame.this.logArea.getDocument().getLength());
      super.print(s);
    }
  }

  @Override
  public void onChangeManagerChange(ChangeManager changeManager) {
    this.mntmUndo.setEnabled(changeManager.canUndo());
    this.mntmRedo.setEnabled(changeManager.canRedo());
  }


  @Override
  public void mouseClicked(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void mouseEntered(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void mouseExited(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getSource() == gameTree && e.getClickCount() == 2) {
      TreePath selPath = gameTree.getPathForLocation(e.getX(), e.getY());
      if (selPath != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
        if (GameShaderNode.class.isInstance(node)) {
          this.shaderEditorFrame       = new ShaderEditorFrame(node.getUserObject().toString());
          shaderEditorFrame.setVisible(true);
        }
        
        if (gameTree.getModel().getRoot() == node) {
          AssetEditorDialog editor = new AssetEditorDialog();
          editor.setVisible(true);
        }
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void dragEnter(DropTargetDragEvent dtde) {
    Gdx.app.log(TAG, "dragEnter<><><><><><><");
    //TODO: set for world screen drag element
  }

  @Override
  public void dragExit(DropTargetEvent dtde) {
    Gdx.app.log(TAG, "dragExit");
    //TODO: remove drag from world screen
  }

  @Override
  public void dragOver(DropTargetDragEvent arg0) {
    Gdx.app.log(TAG, "dragOver");
  }

  @Override
  public void drop(DropTargetDropEvent dtde) {
    //TODO: apply element
    Transferable tr = dtde.getTransferable();
    try {
      BaseGameFolderNode node = (BaseGameFolderNode)tr.getTransferData(GameTransferableHandler.GAME_TRANSFERABLE_FLAVOR);
      
      WorldEditScreen screen = this.gameManager.getWorldEditScreen();
      screen.onDrop(node);
      dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
      dtde.dropComplete(true);
    } catch (UnsupportedFlavorException | IOException e) {
      e.printStackTrace();
      dtde.dropComplete(false);
    }
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent arg0) {
    // TODO Auto-generated method stub
    
  }
}
