package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.ListCellRenderer;
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
import com.badlogic.gdx.files.FileHandle;
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
import com.macbury.fabula.editor.inspector.DefaultBeanBinder;
import com.macbury.fabula.editor.inspector.SceneInspect;
import com.macbury.fabula.editor.inspector.SceneSheetPanel;
import com.macbury.fabula.editor.shaders.ShaderEditorFrame;
import com.macbury.fabula.editor.tiles.AutoTileDebugFrame;
import com.macbury.fabula.editor.tiles.FocusedTileListCellRenderer;
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
import javax.swing.JToolBar;
import javax.swing.JToggleButton;
import javax.swing.Box;
import javax.swing.JScrollBar;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import javax.swing.JInternalFrame;
import javax.swing.JCheckBoxMenuItem;

public class WorldEditorFrame extends JFrame implements ChangeListener, ItemListener, ListSelectionListener, ActionListener, ChangeManagerListener, WindowListener, PropertyChangeListener  {
  
  protected static final String TAG = "WorldEditorFrame";
  private JPanel contentPane;
  private LwjglCanvas gameCanvas;
  public JLabel statusBarLabel;
  private EditorGameManager gameManager;
  private JList autoTileList;
  private IconListRenderer autoTileListRenderer;
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
  private JMenuItem mntmSaveGame;
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
  private ButtonGroup toolbarGroup;
  private JToggleButton tglbtnTerrainEdit;
  private JToggleButton tglbtnAutoTileEdit;
  private JToggleButton tglbtnEventEditor;
  private SceneSheetPanel inspectorSheetPanel;
  private JMenuItem mntmCleanLogs;
  private JMenuItem mntmPlaceModel;
  private JToggleButton tglbtnFoliagebrushbutton;
  private JToggleButton tglbtnLiquidbrushbutton;
  private JMenuItem mntmResetCamera;
  private int shaderWatchID;
  private JMenu mnAssets;
  public JPopupMenu editEventPopupMenu;
  private JMenuItem mntmEditEvent;
  private JMenuItem mntmDeleteEvent;
  private JMenuItem mntmCancel_1;
  private JToggleButton tglbtnPassablebrush;
  private JMenuItem mntmGeneratePassableMap;
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
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
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
    
    mnMap.addSeparator();
    
    this.mntmReloadMap = new JMenuItem("Reload");
    mntmReloadMap.addActionListener(this);
    mnMap.add(mntmReloadMap);
    
    mntmResetCamera = new JMenuItem("Reset Camera");
    mntmResetCamera.addActionListener(this);
    mnMap.add(mntmResetCamera);
    
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
    
     mnEdit.addSeparator();
     
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
    
    this.mntmCleanLogs = new JMenuItem("Clear logs");
    mntmCleanLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.SHIFT_MASK));
    mntmCleanLogs.addActionListener(this);
    mnGame.add(mntmCleanLogs);
    
    mnGame.addSeparator();
    
    JMenuItem mntmBuildAssetBundle = new JMenuItem("Build Asset Bundle");
    mntmBuildAssetBundle.setEnabled(false);
    mnGame.add(mntmBuildAssetBundle);
    
    mnAssets = new JMenu("Assets");
    mainMenuBar.add(mnAssets);
    
    JMenu mnDeveloper = new JMenu("Tools");
    mainMenuBar.add(mnDeveloper);
    
    this.mntmBuildTileMap = new JMenuItem("Auto Tile Hash Map");
    mntmBuildTileMap.addActionListener(this);
    
    mntmGeneratePassableMap = new JMenuItem("Generate passable map");
    mnDeveloper.add(mntmGeneratePassableMap);
    mnDeveloper.addSeparator();
    
    this.mntmRebuildTilesets = new JMenuItem("Rebuild tilesets");
    mntmRebuildTilesets.addActionListener(this);
    mnDeveloper.add(mntmRebuildTilesets);
    mnDeveloper.add(mntmBuildTileMap);
    
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
    logArea.setText("Test");
    logArea.setRows(2);
    logArea.setForeground(Color.LIGHT_GRAY);
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
    inspectorAndOpenGlContainerSplitPane.setResizeWeight(0.001);
    panel_9.add(inspectorAndOpenGlContainerSplitPane, BorderLayout.CENTER);
    inspectorAndOpenGlContainerSplitPane.setContinuousLayout(true);
    inspectorAndOpenGlContainerSplitPane.setDividerLocation(260);
    JPopupMenu.setDefaultLightWeightPopupEnabled( false );
    
    JPanel openGLContainerPane = new JPanel();
    openGLContainerPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    inspectorAndOpenGlContainerSplitPane.setRightComponent(openGLContainerPane);
    canvas = this.gameCanvas.getCanvas();
    openGLContainerPane.add(canvas, BorderLayout.CENTER);
    
    this.eventPopupMenu = new JPopupMenu();
    addPopup(openGLContainerPane, eventPopupMenu);
    
    this.mntmNewEvent = new JMenuItem("New event");
    mntmNewEvent.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/event.png")));
    eventPopupMenu.add(mntmNewEvent);
    
    mntmPlaceModel = new JMenuItem("Place model");
    mntmPlaceModel.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/model.png")));
    eventPopupMenu.add(mntmPlaceModel);
    
    this.mntmSetStartPosition = new JMenuItem("Set start position");
    mntmSetStartPosition.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/icon/start_position.png")));
    mntmSetStartPosition.addActionListener(this);
    eventPopupMenu.add(mntmSetStartPosition);
    
    eventPopupMenu.addSeparator();
    
    JMenuItem mntmCancel = new JMenuItem("Cancel");
    mntmCancel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    eventPopupMenu.add(mntmCancel);
    
    openGLContainerPane.setLayout(new BoxLayout(openGLContainerPane, BoxLayout.X_AXIS));
    
    editEventPopupMenu = new JPopupMenu();
    openGLContainerPane.add(editEventPopupMenu);
    
    mntmEditEvent = new JMenuItem("Edit");
    editEventPopupMenu.add(mntmEditEvent);
    
    mntmDeleteEvent = new JMenuItem("Delete");
    editEventPopupMenu.add(mntmDeleteEvent);
    editEventPopupMenu.addSeparator();
    
    mntmCancel_1 = new JMenuItem("Cancel");
    mntmCancel_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    editEventPopupMenu.add(mntmCancel_1);
    
    
    JPanel panel_11 = new JPanel();
    inspectorAndOpenGlContainerSplitPane.setLeftComponent(panel_11);
    panel_11.setLayout(new BorderLayout(0, 0));
    
    
    this.toolbarGroup = new ButtonGroup();
    
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    panel_11.add(toolBar, BorderLayout.NORTH);
    
    this.tglbtnTerrainEdit = new JToggleButton("");
    tglbtnTerrainEdit.addActionListener(this);
    tglbtnTerrainEdit.setSelected(true);
    tglbtnTerrainEdit.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/terrain.png")));
    toolBar.add(tglbtnTerrainEdit);
    toolbarGroup.add(tglbtnTerrainEdit);
    
    this.tglbtnAutoTileEdit = new JToggleButton("");
    tglbtnAutoTileEdit.addActionListener(this);
    
    tglbtnPassablebrush = new JToggleButton("");
    tglbtnPassablebrush.addActionListener(this);
    tglbtnPassablebrush.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/passable.png")));
    toolBar.add(tglbtnPassablebrush);
    tglbtnAutoTileEdit.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/autotile.png")));
    toolBar.add(tglbtnAutoTileEdit);
    toolbarGroup.add(tglbtnPassablebrush);
    toolbarGroup.add(tglbtnAutoTileEdit);
    
    tglbtnLiquidbrushbutton = new JToggleButton("");
    tglbtnLiquidbrushbutton.addActionListener(this);
    tglbtnLiquidbrushbutton.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/injection.png")));
    toolBar.add(tglbtnLiquidbrushbutton);
    toolbarGroup.add(tglbtnLiquidbrushbutton);
    
    tglbtnFoliagebrushbutton = new JToggleButton("");
    tglbtnFoliagebrushbutton.addActionListener(this);
    tglbtnFoliagebrushbutton.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/grass.png")));
    toolBar.add(tglbtnFoliagebrushbutton);
    toolbarGroup.add(tglbtnFoliagebrushbutton);
    
    this.tglbtnEventEditor = new JToggleButton("");
    tglbtnEventEditor.addActionListener(this);
    tglbtnEventEditor.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/events.png")));
    toolBar.add(tglbtnEventEditor);
    toolbarGroup.add(tglbtnEventEditor);
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setDividerLocation(320);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitPane.setContinuousLayout(true);
    panel_11.add(splitPane, BorderLayout.CENTER);
    
    JPanel inspectorPanel = new JPanel();
    splitPane.setRightComponent(inspectorPanel);
    inspectorPanel.setLayout(new BorderLayout(0, 0));
    
    this.inspectorSheetPanel = new SceneSheetPanel();
    inspectorSheetPanel.setRestoreToggleStates(true);
    inspectorSheetPanel.setSorting(true);
    inspectorSheetPanel.setSortingProperties(true);
    inspectorSheetPanel.setSortingCategories(true);
    inspectorSheetPanel.setMode(PropertySheet.VIEW_AS_CATEGORIES);
    inspectorSheetPanel.setDescriptionVisible(true);
    inspectorSheetPanel.addPropertyChangeListener(this);
    inspectorSheetPanel.addPropertySheetChangeListener(this);
    inspectorPanel.add(inspectorSheetPanel);
    
    JPanel autoTilesPanel = new JPanel();
    autoTilesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
    splitPane.setLeftComponent(autoTilesPanel);
    autoTilesPanel.setLayout(new BorderLayout(0, 0));
    
    this.autoTileList = new JList(new Object[] { });
    autoTileList.setBackground(Color.BLACK);
    JScrollPane autoTileScrollPane = new JScrollPane(autoTileList);
    autoTileScrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
    autoTilesPanel.add(autoTileScrollPane, BorderLayout.CENTER);
    autoTileList.setValueIsAdjusting(true);
    autoTileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    autoTileList.setBorder(null);
    autoTileList.addListSelectionListener(this);
    autoTileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    autoTileList.setVisibleRowCount(0);
    autoTileList.setFixedCellWidth(AutoTiles.TILE_SIZE);
    autoTileList.setFixedCellHeight(AutoTiles.TILE_SIZE);

    Thread statusbarThread = new Thread(new StatusBarInfoRunnable());
    statusbarThread.start();
    
    changeManager = new ChangeManager(this);
    addWindowListener(this);
    
    refreshDevices();
    setJMenuBar(mainMenuBar);
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
      
      onGameFinishedStarting();

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
  
  private void onGameFinishedStarting() {
    updateInfoForInspector();
    
    for (final FileHandle fh : G.fs("").list()) {
      if (fh.isDirectory()) {
        JMenuItem openItemFolder = new JMenuItem(fh.nameWithoutExtension()+"/");
        openItemFolder.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            try {
              Desktop.getDesktop().open(fh.file());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
        mnAssets.add(openItemFolder);
      }
      
    }
    
    mnAssets.addSeparator();
    JMenuItem preprocessedItemFolder = new JMenuItem("preprocessed/");
    preprocessedItemFolder.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent arg0) {
        try {
          Desktop.getDesktop().open(new File("preprocessed/"));
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
    mnAssets.add(preprocessedItemFolder);
    
    try {
      String path = G.fs("shaders/").file().getAbsolutePath();
      Gdx.app.log(TAG, "Watching: " + path);
      this.shaderWatchID = JNotify.addWatch(path, JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED, true, new ShaderFileChangeListener());
    } catch (JNotifyException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void stateChanged(ChangeEvent e) {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    Scene scene = screen.getScene();
    DirectionalLight sun = scene.getSunLight();
  }

  private void updateInfoForMapSettings() {
    Scene scene = this.gameManager.getWorldEditScreen().getScene();
    
    this.gameManager.getWorldEditScreen().setContainerFrame(this);
    
    DirectionalLight sun = scene.getSunLight();
    this.gameManager.getWorldEditScreen().setChangeManager(changeManager);
    /*this.lightPositionXSpinner.setValue(sun.direction.x);
    this.lightPositionYSpinner.setValue(sun.direction.y);
    this.lightPositionZSpinner.setValue(sun.direction.z);*/
    
    Array<String> shadersName = G.shaders.getAllShaderNames().toArray();
    //shadersComboBox.setModel(new DefaultComboBoxModel(shadersName.toArray(String.class)));
    
    DefaultComboBoxModel<String> tilesetModel = new DefaultComboBoxModel<String>();

    ArrayList<Tileset> tilesets = G.db.getTilesets();

    autoTileList.setSelectedIndex(tilesets.indexOf(scene.getTerrain().getTileset()));
    
    if (scene.haveName()) {
      setTitle("Axe - ["+scene.getName()+"]");
    } else {
      setTitle("Axe - [ New map ]");
    }
    
  }
  
  public void updateInfoForInspector() {
    updateInspectorBean();
    updateInfoForAutotileBrush();
    updateSelectedBrush();
    updateInfoForMapSettings();
    this.gameManager.getWorldEditScreen().setContainerFrame(this);
  }
  
  public void updateInspectorBean() {
    DefaultBeanBinder binder = new DefaultBeanBinder(new SceneInspect(this.gameManager.getWorldEditScreen()), inspectorSheetPanel);
  }

  private void updateInfoForAutotileBrush() {
    if (!WorldEditorFrame.this.gameManager.loading()) {
      AutoTileBrush atBrush     = this.gameManager.getWorldEditScreen().getAutoTileBrush();
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
    
  }

  private void updateInfoForTerrainBrush() {
    //terrainBrushAmountSpinner.setValue(this.gameManager.getWorldEditScreen().getTerrainBrush().getPower());
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    System.gc();
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    Scene scene            = screen.getScene();
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
    
    if (e.getSource() == mntmCleanLogs) {
      this.logArea.setText("");
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
    
    if (e.getSource() == mntmReloadMap) {
      reloadMap();
    }
    
    if (e.getSource() == mntmOpen) {
      openMap();
    }
    
    if (e.getSource() == mntmDebugFrameBuffer) {
    }
    
    if (e.getSource() == mntmResetCamera) {
      resetCamera();
    }
    
    if (e.getSource() == mntmUndo) {
      changeManager.undo();
    }
    
    if (e.getSource() == mntmRedo) {
      changeManager.redo();
    }
    
    if (e.getSource() == mntmSetStartPosition) {
      screen.getEventBrush().placeStartPosition();
    }
    
    if (e.getSource() == mntmReloadMap) {
      
    }
    
    if (e.getSource() == mntmForceAppStop) {
      AdbManager.stopApplication(GameManager.ANDROID_APP_PACKAGE);
    }
    
    updateSelectedBrush();
  }

  private void resetCamera() {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    screen.resetCamera();
  }

  private void reloadMap() {
    resetEditor();
    
    this.gameManager.getWorldEditScreen().openMap(G.fs(this.gameManager.getWorldEditScreen().getScene().getPath()).file());
  }

  private void updateSelectedBrush() {
    WorldEditScreen screen = this.gameManager.getWorldEditScreen();
    Scene scene            = screen.getScene();
    AutoTileBrush brush    = screen.getAutoTileBrush();
    
    if (tglbtnPassablebrush.isSelected()) {
      screen.setCurrentBrush(screen.getPassableBrush());
    }
    
    if (tglbtnTerrainEdit.isSelected()) {
      screen.setCurrentBrush(screen.getTerrainBrush());
    } 
    
    if (tglbtnAutoTileEdit.isSelected()) {
      screen.setCurrentBrush(screen.getAutoTileBrush());
      autoTileList.setEnabled(true);
    } else {
      autoTileList.setEnabled(false);
    }
    
    if (tglbtnEventEditor.isSelected()) {
      screen.setCurrentBrush(screen.getEventBrush());
    }
    
    screen.getCurrentBrush().setWorldEditScreen(screen);
    screen.getCurrentBrush().setChangeManager(this.changeManager);
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
    updateInfoForInspector();
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
  public void windowActivated(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    try {
      saveMap();
      JNotify.removeWatch(shaderWatchID);
    } catch (Exception e) {
      e.printStackTrace();
    }
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

  @Override
  public void propertyChange(PropertyChangeEvent arg0) {
    EventQueue.invokeLater(new Runnable(){
      @Override
      public void run() {
        if (!WorldEditorFrame.this.gameManager.loading()) {
          updateInfoForAutotileBrush();
        }
        
      }
    });
    
  }

  public SceneSheetPanel getInspector() {
    return inspectorSheetPanel;
  }
}
