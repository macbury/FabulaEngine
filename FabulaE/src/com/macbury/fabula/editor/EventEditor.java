package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.GridLayout;

public class EventEditor extends JFrame {
  
  private JPanel contentPane;
  /**
   * Create the frame.
   */
  public EventEditor() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 785, 579);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    tabbedPane.addTab("New tab", null, panel, null);
    panel.setLayout(new GridLayout(1, 0, 0, 0));
    
    RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
    textArea.setCodeFoldingEnabled(true);
    textArea.setAntiAliasingEnabled(true);
    textArea.setTabSize(2);
    textArea.setAutoIndentEnabled(true);
    textArea.setTabsEmulated(true);
    RTextScrollPane sp = new RTextScrollPane(textArea);
    sp.setFoldIndicatorEnabled(true);
    
    panel.add(sp);
  }
  
}
