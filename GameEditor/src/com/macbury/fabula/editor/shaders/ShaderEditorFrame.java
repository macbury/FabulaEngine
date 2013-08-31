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

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.manager.G;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ShaderEditorFrame extends JDialog implements ActionListener {
  
  private final JPanel contentPanel = new JPanel();
  private RSyntaxTextArea fragmentTextArea;
  private JTabbedPane tabbedPane;
  private RSyntaxTextArea vertexTextArea;
  private JSplitPane splitPane;
  private JPanel panel;
  private JPanel panel_1;
  private String shaderKey;


  public ShaderEditorFrame(String shaderKey) {
    this.shaderKey = shaderKey;
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Edit shader - " + shaderKey);
    setType(Type.POPUP);
    setBounds(100, 100, 771, 657);
    getContentPane().setLayout(new BorderLayout(0, 0));
    
    JSplitPane splitPane_1 = new JSplitPane();
    splitPane_1.setResizeWeight(0.5);
    splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    getContentPane().add(splitPane_1, BorderLayout.CENTER);
    
    JPanel vertexPanel = new JPanel();
    splitPane_1.setLeftComponent(vertexPanel);
    vertexPanel.setLayout(new BorderLayout(0, 0));
    
    JLabel lblNewLabel = new JLabel("Vertex");
    lblNewLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
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
    this.vertexTextArea.setText(G.shaders.get(shaderKey).getVertexShaderSource());
    vertexPanel.add(new RTextScrollPane(this.vertexTextArea), BorderLayout.CENTER);
    
    JPanel fragmentPanel = new JPanel();
    splitPane_1.setRightComponent(fragmentPanel);
    fragmentPanel.setLayout(new BorderLayout(0, 0));
    
    JLabel lblNewLabel_1 = new JLabel("Fragment");
    lblNewLabel_1.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
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
    this.fragmentTextArea.setText(G.shaders.get(shaderKey).getFragmentShaderSource());
    fragmentPanel.add(new RTextScrollPane(this.fragmentTextArea), BorderLayout.CENTER);
    
    JPanel panel_2 = new JPanel();
    panel_2.setBorder(new EmptyBorder(5, 20, 5, 20));
    getContentPane().add(panel_2, BorderLayout.SOUTH);
    panel_2.setLayout(new BorderLayout(0, 0));
    
    JButton btnSave = new JButton("Save");
    btnSave.addActionListener(this);
    btnSave.setHorizontalAlignment(SwingConstants.RIGHT);
    panel_2.add(btnSave, BorderLayout.EAST);

  }


  @Override
  public void actionPerformed(ActionEvent e) {
    G.shaders.update(shaderKey, this.fragmentTextArea.getText(), this.vertexTextArea.getText());
  }
}
