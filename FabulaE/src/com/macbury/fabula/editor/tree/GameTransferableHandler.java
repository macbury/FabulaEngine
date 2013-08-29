package com.macbury.fabula.editor.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import com.macbury.fabula.editor.tree.GameTreeModel.BaseGameFolderNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GamePlayerStartPositionNode;

public class GameTransferableHandler extends TransferHandler {

  
  @Override
  protected Transferable createTransferable(JComponent c) {
    JTree tree = (JTree) c;
    BaseGameFolderNode node = (BaseGameFolderNode) tree.getSelectionPath().getLastPathComponent();
    
    if (GamePlayerStartPositionNode.class.isInstance(node)) {
      return new GameTransferable(node);
    } else {
      return null;
    }
  }


 public class GameTransferable implements Transferable {
  private BaseGameFolderNode node;

  public GameTransferable(BaseGameFolderNode node) {
    this.node = node;
  }
   
  @Override
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    return this.node;
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    DataFlavor flavor;
    try {
      flavor = new DataFlavor(node.getClass().getClass().toString());
      DataFlavor[] output = new DataFlavor[] { flavor } ;
      return output ;
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    // TODO Auto-generated method stub
    return false;
  }
   
 }
}
