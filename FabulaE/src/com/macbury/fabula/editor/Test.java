package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

public class Test extends JFrame {
  
  private JPanel contentPane;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Test frame = new Test();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  /**
   * Create the frame.
   */
  public Test() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1048, 757);
    
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    JToolBar toolBar = new JToolBar();
    contentPane.add(toolBar, BorderLayout.NORTH);
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setResizeWeight(0.3);
    contentPane.add(splitPane, BorderLayout.CENTER);
    
    JSplitPane splitPane_1 = new JSplitPane();
    splitPane_1.setResizeWeight(0.6);
    splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitPane.setLeftComponent(splitPane_1);
    
    JTree tree = new JTree();
    splitPane_1.setRightComponent(tree);
    
    JScrollPane scrollPane = new JScrollPane();
    splitPane_1.setLeftComponent(scrollPane);
    
    JPanel panel = new JPanel();
    splitPane.setRightComponent(panel);
    
    JScrollBar scrollBar = new JScrollBar();
    scrollBar.setOrientation(JScrollBar.HORIZONTAL);
    panel.add(scrollBar);
  }
  
}
