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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.editor.tree.GameTreeModel.BaseGameFolderNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GameMapNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GameObjectNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GamePlayerStartPositionNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GameShaderNode;

public class GameTreeCellRenderer extends DefaultTreeCellRenderer {
  
  private ImageIcon codeIcon;
  private ImageIcon closedFolderIcon;
  private ImageIcon openedFolderIcon;
  private ImageIcon startPositionIcon;
  private ImageIcon mapIcon;
  private ImageIcon objectIcon; 
  
  public GameTreeCellRenderer() {
    this.startPositionIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/start.png")));
    this.codeIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/shaders.png")));
    this.mapIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/map.png")));
    this.objectIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/model.png")));
    this.closedFolderIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/folder_closed.png")));
    this.openedFolderIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WorldEditorFrame.class.getResource("/com/macbury/fabula/editor/icons/folder_opened.png")));
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){
    super.getTreeCellRendererComponent(tree, value,
    selected, expanded, leaf, row, hasFocus);
    JLabel label = (JLabel) this ;
    if (GameObjectNode.class.isInstance(value)) {
      label.setIcon(objectIcon);
    } else if (GameMapNode.class.isInstance(value)) {
      label.setIcon(mapIcon);
    } else if (GamePlayerStartPositionNode.class.isInstance(value)) {
      label.setIcon(startPositionIcon);
    } else if (GameShaderNode.class.isInstance(value)) {
      label.setIcon(codeIcon);
    } else {
      if (expanded) {
        label.setIcon(openedFolderIcon) ;
      } else {
        label.setIcon(closedFolderIcon) ;
      }
      
    }
    
    return this;
  }

}
