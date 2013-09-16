package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.utils.Array;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.macbury.fabula.editor.adb.AdbManager;
import com.macbury.fabula.editor.brushes.AutoTileBrush;
import com.macbury.fabula.editor.brushes.AutoTileBrush.PaintMode;
import com.macbury.fabula.editor.gamerunner.RunningGameConsoleFrame;
import com.macbury.fabula.editor.shaders.ShaderEditorFrame;
import com.macbury.fabula.editor.tiles.AutoTileDebugFrame;
import com.macbury.fabula.editor.tiles.TilesetBuilderDialog;
import com.macbury.fabula.editor.tree.GameTransferableHandler;
import com.macbury.fabula.editor.tree.GameTreeModel.BaseGameFolderNode;
import com.macbury.fabula.editor.undo_redo.ChangeManager;
import com.macbury.fabula.editor.undo_redo.ChangeManagerListener;
import com.macbury.fabula.manager.EditorGameManager;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.map.Scene;
import com.macbury.fabula.screens.WorldEditScreen;
import com.macbury.fabula.terrain.AutoTile;
import com.macbury.fabula.terrain.AutoTiles;
import com.macbury.fabula.terrain.Tileset;

public class WorldEditorFrame extends JFrame implements ChangeListener, ItemListener, ListSelectionListener, ActionListener, ChangeManagerListener, MouseListener, DropTargetListener, WindowListener  {
  
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
  private JComboBox shadersComboBox;
  private JButton btnPickAmbientColor;
  private JButton btnPickDirectionalLightColor;
  private JMenuItem mntmSaveGame;
  private JComboBox tilesetComboBox;
  private JMenuItem mntmOpen;
  private JMenuItem mntmNew;
  private JMenuItem mntmReloadMap;
  public Canvas canvas;
  private JPopupMenu gamePopupMenu;
  private JMenuItem mntmForceAppStop;
  public JPopupMenu eventPopupMenu;
  private JMenuItem mntmNewEvent;
  private JMenuItem mntmSetStartPosition;
  private JRadioButtonMenuItem rdbtnmntmDevice;
  private JRadioButtonMenuItem rdbtnmntmEmulator;
  public WorldEditorFrame(EditorGameManager game) {
    PrintStream origOut = System.out;
    PrintStream interceptor = new LogInterceptor(origOut);
    System.setOut(interceptor);
    
    setIconImage(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/gwn.ico")));
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
    
    JMenu mnMap = new JMenu("Map");
    mainMenuBar.add(mnMap);
    
    this.mntmNew = new JMenuItem("New");
    mntmNew.addActionListener(this);
    mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
    mnMap.add(mntmNew);
    
    this.mntmSaveGame = new JMenuItem("Save");
    mnMap.add(mntmSaveGame);
    mntmSaveGame.addActionListener(this);
    mntmSaveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    
    this.mntmOpen = new JMenuItem("Open");
    mntmOpen.addActionListener(this);
    
    JMenuItem mntmSaveAs = new JMenuItem("Save as");
    mntmSaveAs.setEnabled(false);
    mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
    mnMap.add(mntmSaveAs);
    mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
    mnMap.add(mntmOpen);
    
    JSeparator separator_1 = new JSeparator();
    mnMap.add(separator_1);
    
    this.mntmReloadMap = new JMenuItem("Reload");
    mntmReloadMap.setEnabled(false);
    mnMap.add(mntmReloadMap);
    
    JMenuItem mntmResize = new JMenuItem("Resize");
    mntmResize.setEnabled(false);
    mnMap.add(mntmResize);
    
    JMenu mnGame = new JMenu("Game");
    mainMenuBar.add(mnGame);
    
    this.mntmRun = new JMenuItem("Run");
    mntmRun.addActionListener(this);
    mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
    mnGame.add(mntmRun);
    
    this.mntmForceAppStop = new JMenuItem("Terminate");
    mntmForceAppStop.addActionListener(this);
  
    
    JMenu mnTarget = new JMenu("Target");
    mnGame.add(mnTarget);
    
    ButtonGroup group = new ButtonGroup();
    
    this.rdbtnmntmEmulator = new JRadioButtonMenuItem("Emulator");
    rdbtnmntmEmulator.setSelected(true);
    mnTarget.add(rdbtnmntmEmulator);
    group.add(rdbtnmntmEmulator);
    
    this.rdbtnmntmDevice = new JRadioButtonMenuItem("Device");
    mnTarget.add(rdbtnmntmDevice);
    group.add(rdbtnmntmDevice);
    
    mnGame.add(mntmForceAppStop);
    
    JSeparator separator_2 = new JSeparator();
    mnGame.add(separator_2);
    
    JMenuItem mntmBuildAssetBundle = new JMenuItem("Build Asset Bundle");
    mntmBuildAssetBundle.setEnabled(false);
    mnGame.add(mntmBuildAssetBundle);
    
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
    mainSplitPane.setResizeWeight(0.8);
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
    logArea.setRows(2);
    logArea.setForeground(Color.GREEN);
    logArea.setBackground(Color.BLACK);
    logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
    logArea.setEditable(false);
    logArea.setLineWrap(true);
    scrollPane_1.setViewportView(logArea);
    
    JPanel panel_5 = new JPanel();
    panel_8.add(panel_5, BorderLayout.SOUTH);
    panel_5.setBorder(new EmptyBorder(5, 5, 5, 5));
    panel_5.setLayout(new BorderLayout(0, 0));
    
    this.statusBarLabel = new JLabel("X: 0 Y:0 Z:0");
    panel_5.add(statusBarLabel, BorderLayout.WEST);
    
    JPanel panel_9 = new JPanel();
    panel_9.setBorder(new EmptyBorder(0, 0, 0, 0));
    mainSplitPane.setLeftComponent(panel_9);
    panel_9.setLayout(new BorderLayout(0, 0));
    
    JSplitPane inspectorAndOpenGlContainerSplitPane = new JSplitPane();
    panel_9.add(inspectorAndOpenGlContainerSplitPane, BorderLayout.CENTER);
    inspectorAndOpenGlContainerSplitPane.setContinuousLayout(true);
    
    JPopupMenu.setDefaultLightWeightPopupEnabled( false );
    
    JPanel openGLContainerPane = new JPanel();
    openGLContainerPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    inspectorAndOpenGlContainerSplitPane.setRightComponent(openGLContainerPane);
    canvas = this.gameCanvas.getCanvas();
    openGLContainerPane.add(canvas, BorderLayout.CENTER);
    
    this.eventPopupMenu = new JPopupMenu();
    addPopup(openGLContainerPane, eventPopupMenu);
    
    this.mntmNewEvent = new JMenuItem("New event");
    eventPopupMenu.add(mntmNewEvent);
    
    this.mntmSetStartPosition = new JMenuItem("Set start position");
    mntmSetStartPosition.addActionListener(this);
    eventPopupMenu.add(mntmSetStartPosition);
    
    JSeparator separator_3 = new JSeparator();
    eventPopupMenu.add(separator_3);
    
    JMenuItem mntmCancel = new JMenuItem("Cancel");
    eventPopupMenu.add(mntmCancel);
    
    openGLContainerPane.setLayout(new BoxLayout(openGLContainerPane, BoxLayout.X_AXIS));
    
    JPanel panel_11 = new JPanel();
    inspectorAndOpenGlContainerSplitPane.setLeftComponent(panel_11);
    panel_11.setLayout(new BorderLayout(0, 0));
    
    this.tabbedInspectorPane = new JTabbedPane(JTabbedPane.TOP);
    panel_11.add(tabbedInspectorPane);
    
    tabbedInspectorPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    
    JPanel panel_6 = new JPanel();
    panel_6.setBackground(Color.WHITE);
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/settings.png")), panel_6, null);
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
    
    JLabel lblPostProcessing = new JLabel("Post processing");
    panel_6.add(lblPostProcessing, "2, 2, right, default");
    
    this.shadersComboBox = new JComboBox();
    shadersComboBox.addActionListener(this);
    panel_6.add(shadersComboBox, "4, 2, fill, default");
    
    JLabel lblNewLabel_6 = new JLabel("Tileset");
    panel_6.add(lblNewLabel_6, "2, 4, right, default");
    
    this.tilesetComboBox = new JComboBox();
    tilesetComboBox.addItemListener(this);
    panel_6.add(tilesetComboBox, "4, 4, fill, default");
    
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
    
    JPanel panel = new JPanel();
    panel.setBackground(SystemColor.window);
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/map.png")), panel, null);
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
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/tiles.png")), panel_1, null);
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
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/grass.png")), panel_7, null);
    
    JPanel panel_4 = new JPanel();
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/liquid.png")), panel_4, null);
    
    JPanel panel_2 = new JPanel();
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/model.png")), panel_2, null);
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
    tabbedInspectorPane.addTab("", new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/shaders.png")), panel_3, null);
    
    JPanel panel_10 = new JPanel();
    panel_10.setBackground(Color.WHITE);
    tabbedInspectorPane.addTab("New tab", null, panel_10, null);
    tabbedInspectorPane.addChangeListener(this);
    panel_10.setLayout(new BorderLayout(0, 0));
    
    PropertySheetPanel sheet = new PropertySheetPanel();
    sheet.setRestoreToggleStates(true);
    sheet.setSorting(true);
    sheet.setSortingProperties(true);
    sheet.setSortingCategories(true);
    sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
    sheet.setDescriptionVisible(true);
    panel_10.add(sheet);
    
    DropTarget dt = new DropTarget(this.gameCanvas.getCanvas(), this);
    
    Thread statusbarThread = new Thread(new StatusBarInfoRunnable());
    statusbarThread.start();
    
    changeManager = new ChangeManager(this);
    addWindowListener(this);
    
    refreshDevices();
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
      G.db.save();
      switch (tabbedInspectorPane.getSelectedIndex()) {
        case 0:
          updateInfoForMapSettings();
          screen.setCurrentBrush(null);
        break;
        case 1:
          screen.setCurrentBrush(screen.getTerrainBrush());
          updateInfoForTerrainBrush();
        break;
        
        case 2:
          screen.setCurrentBrush(screen.getAutoTileBrush());
          updateInfoForAutotileBrush();
        break;
        
        case 6:
          screen.setCurrentBrush(screen.getEventBrush());
        break;
        
        default:
          screen.setCurrentBrush(null);
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
    
    this.gameManager.getWorldEditScreen().setContainerFrame(this);
    
    DirectionalLight sun = scene.getSunLight();
    this.gameManager.getWorldEditScreen().setChangeManager(changeManager);
    this.lightPositionXSpinner.setValue(sun.direction.x);
    this.lightPositionYSpinner.setValue(sun.direction.y);
    this.lightPositionZSpinner.setValue(sun.direction.z);
    
    Array<String> shadersName = G.shaders.getAllShaderNames().toArray();
    shadersComboBox.setModel(new DefaultComboBoxModel(shadersName.toArray(String.class)));
    
    DefaultComboBoxModel<String> tilesetModel = new DefaultComboBoxModel<String>();

    ArrayList<Tileset> tilesets = G.db.getTilesets();
    for (Tileset tileset : tilesets) {
      tilesetModel.addElement(tileset.getName());
    }
    tilesetComboBox.setModel(tilesetModel);

    autoTileList.setSelectedIndex(tilesets.indexOf(scene.getTerrain().getTileset()));
    
    if (scene.haveName()) {
      setTitle("Axe - ["+scene.getName()+"]");
    } else {
      setTitle("Axe - [ New map ]");
    }
    
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
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    Scene scene            = screen.getScene();
    if (e.getSource() == paintModeComboBox) {
      updateInfoForAutotileBrush();
    }
    
    if (e.getSource() == tilesetComboBox) {
      scene.getTerrain().setTileset((String)tilesetComboBox.getSelectedItem());
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
      if (rdbtnmntmEmulator.isSelected()) {
        runningGameConsoleFrame.setTarget(RunningGameConsoleFrame.TargetType.Emulator);
      } else {
        runningGameConsoleFrame.setTarget(RunningGameConsoleFrame.TargetType.Device);
      }
      
      runningGameConsoleFrame.runGame(this, gameManager);
    }
    
    if (e.getSource() == this.mntmRebuildTilesets) {
      TilesetBuilderDialog dialog = new TilesetBuilderDialog();
      dialog.setVisible(true);
    }
    
    if (e.getSource() == mntmNew) {
      newMap();
    }
    
    if (e.getSource() == mntmSaveGame) {
      saveMap();
    }
    
    if (e.getSource() == mntmOpen) {
      openMap();
    }
    
    if (e.getSource() == mntmDebugFrameBuffer) {
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
    
    if (e.getSource() == mntmSetStartPosition) {
      screen.getEventBrush().placeStartPosition();
    }
    
    if (e.getSource() == mntmReloadMap) {
      
    }
    
    if (e.getSource() == mntmForceAppStop) {
      AdbManager.stopApplication(GameManager.ANDROID_APP_PACKAGE);
    }
  }

  private void refreshDevices() {
    
  }

  private void openMap() {
    saveMap();
    JFileChooser fh = new JFileChooser(G.fs("maps/").file());
    
    fh.addChoosableFileFilter(new FileNameExtensionFilter("Map file", "red"));
    int returnVal = fh.showOpenDialog(this);
    
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fh.getSelectedFile();
      this.gameManager.getWorldEditScreen().openMap(file);
      resetEditor();
    }
    
  }

  private void resetEditor() {
    tabbedInspectorPane.setSelectedIndex(0);
    updateInfoForMapSettings();
    changeManager.clear();
  }

  private void newMap() {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    saveMap();
    screen.newMap(50, 50);
    resetEditor();
  }

  public boolean saveMap() {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    Scene scene            = screen.getScene();
    
    if (scene.haveName()) {
      scene.save();
      updateInfoForMapSettings();
      return true;
    } else {
      String output = JOptionPane.showInputDialog(this, "Save map name:");
      if (output != null && output.length() > 1) {
        scene.setName(output);
        scene.save();
        G.db.reloadMapData();
        updateInfoForMapSettings();
        return true;
      } else {
        return false;
      }
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
    /*if (e.getSource() == gameTree && e.getClickCount() == 2) {
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
    }*/
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

  @Override
  public void windowActivated(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    saveMap();
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }



  private static void addPopup(Component component, final JPopupMenu popup) {
    component.addMouseListener(new MouseAdapter() {
    	public void mousePressed(MouseEvent e) {
    		if (e.isPopupTrigger()) {
    			showMenu(e);
    		}
    	}
    	public void mouseReleased(MouseEvent e) {
    		if (e.isPopupTrigger()) {
    			showMenu(e);
    		}
    	}
    	private void showMenu(MouseEvent e) {
    		popup.show(e.getComponent(), e.getX(), e.getY());
    	}
    });
  }
}
