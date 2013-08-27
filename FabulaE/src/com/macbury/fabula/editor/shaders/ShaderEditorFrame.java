package com.macbury.fabula.editor.shaders;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import java.awt.Window.Type;
import javax.swing.JSeparator;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JLabel;

public class ShaderEditorFrame extends JDialog {
  
  private final JPanel contentPanel = new JPanel();
  private RSyntaxTextArea fragmentTextArea;
  private JTabbedPane tabbedPane;
  private RSyntaxTextArea vertexTextArea;
  private JSplitPane splitPane;
  private JPanel panel;
  private JPanel panel_1;

  /**
   * RTextScrollPane sp = new RTextScrollPane(fragmentTextArea);
    tabbedPane.addTab("Fragment", null, sp, null);
    sp.setFoldIndicatorEnabled(true);
   */
  public ShaderEditorFrame() {
    setTitle("Shader Editor");
    setType(Type.UTILITY);
    setBounds(100, 100, 771, 624);
    getContentPane().setLayout(new BorderLayout(0, 0));
    
    JSplitPane splitPane_1 = new JSplitPane();
    splitPane_1.setResizeWeight(0.5);
    splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    getContentPane().add(splitPane_1);
    
    JPanel vertexPanel = new JPanel();
    splitPane_1.setLeftComponent(vertexPanel);
    vertexPanel.setLayout(new BorderLayout(0, 0));
    
    JLabel lblNewLabel = new JLabel("Vertex");
    vertexPanel.add(lblNewLabel, BorderLayout.NORTH);
    
    this.vertexTextArea = new RSyntaxTextArea(20, 60);
    this.vertexTextArea.setWhitespaceVisible(true);
    this.vertexTextArea.setRoundedSelectionEdges(true);
    this.vertexTextArea.setPaintTabLines(true);
    ((RSyntaxDocument) this.vertexTextArea.getDocument()).setSyntaxStyle(new GLSLTokenMaker());
    this.vertexTextArea.setCodeFoldingEnabled(true);
    this.vertexTextArea.setAntiAliasingEnabled(true);
    this.vertexTextArea.setTabSize(2);
    this.vertexTextArea.setAutoIndentEnabled(true);
    this.vertexTextArea.setTabsEmulated(true);
    
    vertexPanel.add(new RTextScrollPane(this.vertexTextArea), BorderLayout.CENTER);
    
    JPanel fragmentPanel = new JPanel();
    splitPane_1.setRightComponent(fragmentPanel);
    fragmentPanel.setLayout(new BorderLayout(0, 0));
    
    JLabel lblNewLabel_1 = new JLabel("Fragment");
    fragmentPanel.add(lblNewLabel_1, BorderLayout.NORTH);
    
    this.fragmentTextArea = new RSyntaxTextArea(20, 60);
    this.fragmentTextArea.setWhitespaceVisible(true);
    this.fragmentTextArea.setRoundedSelectionEdges(true);
    this.fragmentTextArea.setPaintTabLines(true);
    ((RSyntaxDocument) this.fragmentTextArea.getDocument()).setSyntaxStyle(new GLSLTokenMaker());
    this.fragmentTextArea.setCodeFoldingEnabled(true);
    this.fragmentTextArea.setAntiAliasingEnabled(true);
    this.fragmentTextArea.setTabSize(2);
    this.fragmentTextArea.setAutoIndentEnabled(true);
    this.fragmentTextArea.setTabsEmulated(true);
    fragmentPanel.add(new RTextScrollPane(this.fragmentTextArea), BorderLayout.CENTER);
    
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    getContentPane().add(toolBar, BorderLayout.NORTH);
    
    JButton btnApply = new JButton("Apply");
    toolBar.add(btnApply);
    
    JComboBox comboBox = new JComboBox();
    toolBar.add(comboBox);
  }
}
