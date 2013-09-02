package com.macbury.fabula.editor.tree;

import java.util.Enumeration;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.macbury.fabula.manager.G;


public class GameTreeModel implements TreeModel {
  private TreeModelListener treeModelListener;
  private GameFolderNode rootNode;

  public GameTreeModel() {
    this.rootNode = new GameFolderNode("Game");
    this.rootNode.add(new GamePlayerStartPositionNode());
    this.rootNode.add(new GameShadersFolderNode());
    this.rootNode.add(new GameObjectsFolderNode());
    this.rootNode.add(new GameMapsFolderNode());
  }
  
  @Override
  public void addTreeModelListener(TreeModelListener l) {
    this.treeModelListener = l;
  }

  @Override
  public Object getChild(Object parent, int index) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
    return node.getChildAt(index);
  }

  @Override
  public int getChildCount(Object parent) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
    return node.getChildCount();
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
    return node.getIndex((TreeNode) child);
  }

  @Override
  public Object getRoot() {
    return rootNode;
  }

  @Override
  public boolean isLeaf(Object parent) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
    return node.isLeaf();
  }

  @Override
  public void removeTreeModelListener(TreeModelListener l) {
    treeModelListener = null;
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    // TODO Auto-generated method stub
    
  }
  
  public class BaseGameFolderNode extends DefaultMutableTreeNode {
    private String name;
    public BaseGameFolderNode(String string) {
      super(string);
      this.name = string;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
  }
  
  public class GameFolderNode extends BaseGameFolderNode {
    public GameFolderNode(String string) {
      super(string);
    }
  }
  
  public class GameShadersFolderNode extends BaseGameFolderNode {
    public GameShadersFolderNode() {
      super("Shaders");
      for (String name : G.shaders.getAllShaderNames()) {
        add(new GameShaderNode(name));
      }
    }
  }
  
  public class GameMapsFolderNode extends BaseGameFolderNode {
    public GameMapsFolderNode() {
      super("Maps");
      
      String[] maps = G.db.getMapNames();
      
      if (maps != null) {
        for (int i = 0; i < maps.length; i++) {
          add(new GameMapNode(maps[i]));
        }
      }
    }
  }
  
  public class GameMapNode extends BaseGameFolderNode {
    public GameMapNode(String name) {
      super(name);
    }
  }
  
  public class GameShaderNode extends BaseGameFolderNode {
    public GameShaderNode(String name) {
      super(name);
    }
  }
  
  public class GamePlayerStartPositionNode extends BaseGameFolderNode {
    public GamePlayerStartPositionNode() {
      super("Player start position");
    }
  }
  
  public class GameObjectsFolderNode extends BaseGameFolderNode {
    public GameObjectsFolderNode() {
      super("Objects");
      add(new GameObjectNode("House"));
    }
  }
  
  public class GameObjectNode extends BaseGameFolderNode {
    public GameObjectNode(String name) {
      super(name);
    }
  }
}
