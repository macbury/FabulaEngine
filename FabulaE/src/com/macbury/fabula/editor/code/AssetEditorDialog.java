package com.macbury.fabula.editor.code;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.badlogic.gdx.Gdx;

public class AssetEditorDialog extends JDialog {
  
  private final JPanel contentPanel = new JPanel();
  private RSyntaxTextArea textArea;

  public AssetEditorDialog() {
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Asset editor");
    setBounds(100, 100, 821, 544);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    
    this.textArea = new RSyntaxTextArea(20, 60);
    textArea.setWhitespaceVisible(true);
    textArea.setRoundedSelectionEdges(true);
    textArea.setPaintTabLines(true);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
    textArea.setCodeFoldingEnabled(true);
    textArea.setAntiAliasingEnabled(true);
    textArea.setTabSize(2);
    textArea.setAutoIndentEnabled(true);
    textArea.setTabsEmulated(true);
    
    textArea.setText(Gdx.files.internal("data/assets.game").readString());
    
    RTextScrollPane sp = new RTextScrollPane(textArea);
    contentPanel.add(sp, BorderLayout.CENTER);
    sp.setFoldIndicatorEnabled(true);
    
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    
    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);

  }
  
}
