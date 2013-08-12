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
import com.macbury.fabula.manager.GameManager;

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

public class WorldEditorFrame extends JFrame implements ChangeListener {
  
  protected static final String TAG = "WorldEditorFrame";
  private JPanel contentPane;
  private LwjglCanvas gameCanvas;
  public JLabel statusBarLabel;
  private GameManager gameManager;
  private JTabbedPane tabbedInspectorPane;
  private JSpinner terrainBrushSizeSpinner;
  private JSpinner terrainBrushAmountSpinner;
  private JComboBox terrainChangeModeComboBox;
  
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
    setExtendedState(Frame.MAXIMIZED_BOTH); 
    
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);
    
    JMenuItem mntmNewMenuItem = new JMenuItem("New");
    mnFile.add(mntmNewMenuItem);
    
    JSeparator separator = new JSeparator();
    mnFile.add(separator);
    
    JMenuBar menuBar_1 = new JMenuBar();
    mnFile.add(menuBar_1);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    toolBar.setFloatable(false);
    contentPane.add(toolBar, BorderLayout.NORTH);
    
    JButton button = new JButton("");
    button.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/editor/document-new.png")));
    toolBar.add(button);
    
    JButton button_1 = new JButton("");
    button_1.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/editor/document-open.png")));
    toolBar.add(button_1);
    
    JButton btnNewButton = new JButton("");
    btnNewButton.setIcon(new ImageIcon(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/editor/play.png")));
    toolBar.add(btnNewButton);
    
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
    terrainChangeModeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Up", "Down", "Set"}));
    panel.add(terrainChangeModeComboBox, "6, 2, fill, default");
    
    JLabel lblNewLabel_1 = new JLabel("Power");
    panel.add(lblNewLabel_1, "2, 4");
    
    this.terrainBrushAmountSpinner = new JSpinner();
    terrainBrushAmountSpinner.addChangeListener(this);
    terrainBrushAmountSpinner.setModel(new SpinnerNumberModel(new Float(0.1f), new Float(0.1f), null, new Float(0.1f)));
    panel.add(terrainBrushAmountSpinner, "6, 4");
    
    JLabel lblNewLabel_2 = new JLabel("Size");
    panel.add(lblNewLabel_2, "2, 6");
    
    this.terrainBrushSizeSpinner = new JSpinner();
    terrainBrushSizeSpinner.addChangeListener(this);
    
    terrainBrushSizeSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(2)));
    panel.add(terrainBrushSizeSpinner, "6, 6");
    
    JPanel panel_1 = new JPanel();
    tabbedInspectorPane.addTab("Paint", null, panel_1, null);
    
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

  private void updateInfoForTerrainBrush() {
    terrainBrushSizeSpinner.setValue(this.gameManager.getWorldEditScreen().getTerrainBrush().getSize());
    terrainBrushAmountSpinner.setValue(this.gameManager.getWorldEditScreen().getTerrainBrush().getPower());
  }
}
