package com.macbury.fabula.editor;

import java.util.Enumeration;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class GameTreeModel implements TreeModel {
  private TreeModelListener treeModelListener;
  private GameFolderNode rootNode;

  public GameTreeModel() {
    this.rootNode = new GameFolderNode("Game");
    this.rootNode.add(new GameShadersFolderNode());
  }
  
  @Override
  public void addTreeModelListener(TreeModelListener l) {
    this.treeModelListener = l;
  }

  @Override
  public Object getChild(Object parent, int index) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getChildCount(Object parent) {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getRoot() {
    return rootNode;
  }

  @Override
  public boolean isLeaf(Object node) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void removeTreeModelListener(TreeModelListener l) {
    treeModelListener = null;
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    // TODO Auto-generated method stub
    
  }
  
  public class GameFolderNode extends DefaultMutableTreeNode {
    public GameFolderNode(String string) {
      super(string);
    }
  }
  
  public class GameShadersFolderNode extends DefaultMutableTreeNode {
    public GameShadersFolderNode() {
      super("Shaders");
      add(new DefaultMutableTreeNode("SHADER_BLOOM"));
    }
    
  }
}
