package com.macbury.fabula.editor.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.editor.tree.GameTreeModel.BaseGameFolderNode;

public class GameTreeCellRenderer implements TreeCellRenderer {
  private JLabel label;
  
  private static Icon OPENED_FOLDER = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/project.ico")));
  
  public GameTreeCellRenderer() {
    label = new JLabel();
  }
  
  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    BaseGameFolderNode node = (BaseGameFolderNode)value;
    label.setIcon(OPENED_FOLDER);
    label.setText(node.getName());
    
    if (selected) {
      label.setBackground(SystemColor.black);
    } else {
      label.setBackground(SystemColor.window);
    }
    
    return label;
  }

}
