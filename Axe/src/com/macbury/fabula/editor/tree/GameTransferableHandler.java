package com.macbury.fabula.editor.tree;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;

import com.macbury.fabula.editor.tree.GameTreeModel.BaseGameFolderNode;
import com.macbury.fabula.editor.tree.GameTreeModel.GamePlayerStartPositionNode;

public class GameTransferableHandler extends TransferHandler {
  public final static DataFlavor GAME_TRANSFERABLE_FLAVOR = createConstant(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + GameTransferable.class.getName() + "\"");
  
  private static DataFlavor createConstant(String prn) {
    try {
        return new DataFlavor(prn);
    } catch (Exception e) {
        return null;
    }
  }
  
  @Override
  public int getSourceActions(JComponent c) {
    return COPY;
  }

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
  
  @Override
  public boolean canImport(TransferSupport support) {
    return true;
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
      return new DataFlavor[] { GAME_TRANSFERABLE_FLAVOR } ;
    }
  
    @Override
    public boolean isDataFlavorSupported(DataFlavor falvor) {
      return true;
    }
  }
  
}
