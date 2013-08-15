package com.macbury.fabula.editor;

import java.awt.BorderLayout;
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
import com.macbury.fabula.editor.brushes.AutoTileBrush;
import com.macbury.fabula.editor.brushes.TerrainBrush;
import com.macbury.fabula.editor.brushes.TerrainBrush.TerrainBrushType;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.terrain.AutoTiles;

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

public class WorldEditorFrame extends JFrame implements ChangeListener, ItemListener {
  
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
  
  public WorldEditorFrame(GameManager game) {
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
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
    
    JSeparator separator = new JSeparator();
    mnFile.add(separator);
    
    JMenuItem mntmQuit = new JMenuItem("Quit");
    mnFile.add(mntmQuit);
    
    JMenuBar menuBar_1 = new JMenuBar();
    mnFile.add(menuBar_1);
    
    JMenu mnAssets = new JMenu("Assets");
    menuBar.add(mnAssets);
    
    JMenuItem mntmNewMenuItem_1 = new JMenuItem("Rebuild tilesets");
    mnAssets.add(mntmNewMenuItem_1);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    JSplitPane inspectorAndOpenGlContainerSplitPane = new JSplitPane();
    inspectorAndOpenGlContainerSplitPane.setContinuousLayout(true);
    inspectorAndOpenGlContainerSplitPane.setResizeWeight(0.1);
    contentPane.add(inspectorAndOpenGlContainerSplitPane, BorderLayout.CENTER);
    
    JSplitPane mapsTreeAndInspectorSplitPane = new JSplitPane();
    mapsTreeAndInspectorSplitPane.setContinuousLayout(true);
    mapsTreeAndInspectorSplitPane.setResizeWeight(0.35);
    mapsTreeAndInspectorSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    inspectorAndOpenGlContainerSplitPane.setLeftComponent(mapsTreeAndInspectorSplitPane);
    
    JTree mapTree = new JTree();
    mapsTreeAndInspectorSplitPane.setLeftComponent(mapTree);
    
    this.tabbedInspectorPane = new JTabbedPane(JTabbedPane.TOP);
    
    tabbedInspectorPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    mapsTreeAndInspectorSplitPane.setRightComponent(tabbedInspectorPane);
    
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
    panel_1.setLayout(new GridLayout(0, 1, 0, 0));
    
    this.autoTileList = new JList(new Object[] { });
    autoTileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    autoTileList.setVisibleRowCount(0);
    autoTileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    autoTileList.setFixedCellWidth(AutoTiles.TILE_SIZE);
    autoTileList.setFixedCellHeight(AutoTiles.TILE_SIZE);
    
    
    panel_1.add(autoTileList);
    
    JPanel panel_2 = new JPanel();
    tabbedInspectorPane.addTab("Objects", null, panel_2, null);
    
    JPanel panel_3 = new JPanel();
    tabbedInspectorPane.addTab("Events", null, panel_3, null);
    
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
      while (running) {
        try {
          if (!WorldEditorFrame.this.gameManager.loading()) {
            Thread.sleep(50);
            WorldEditorFrame.this.statusBarLabel.setText(WorldEditorFrame.this.gameManager.getWorldEditScreen().debugInfo);
          }
        } catch (InterruptedException e) {
          running = false;
        }
      }
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (e.getSource() == tabbedInspectorPane) {
      switch (tabbedInspectorPane.getSelectedIndex()) {
        case 0:
          updateInfoForTerrainBrush();
        break;
        
        case 1:
          updateInfoForAutotileBrush();
        break;
        
        default:
          break;
      }
    }
    
    if (e.getSource() == terrainBrushSizeSpinner) {
      this.gameManager.getWorldEditScreen().getTerrainBrush().setSize((int)terrainBrushSizeSpinner.getValue());
    }
    
    if (e.getSource() == terrainBrushAmountSpinner) {
      this.gameManager.getWorldEditScreen().getTerrainBrush().setPower((float)terrainBrushAmountSpinner.getValue());
    }
  }

  private void updateInfoForAutotileBrush() {
    AutoTileBrush atBrush     = this.gameManager.getWorldEditScreen().getAutoTileBrush();
    this.autoTileListRenderer = new IconListRenderer(atBrush.getAutoTileIcons());
    autoTileList.setCellRenderer(autoTileListRenderer);

    DefaultListModel listModel = new DefaultListModel();
    
    for (String key : atBrush.getOrderedTileNames()) {
      listModel.addElement(key);
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
  }
}
