package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;

public class TilesetManagerDialog extends JDialog {
  
  private final JPanel contentPanel = new JPanel();
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      TilesetManagerDialog dialog = new TilesetManagerDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Create the dialog.
   */
  public TilesetManagerDialog() {
    setTitle("Tileset manager");
    setAlwaysOnTop(true);
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(null);
    
    JEditorPane dtrpnAsdasdAsdad = new JEditorPane();
    dtrpnAsdasdAsdad.setEditable(false);
    dtrpnAsdasdAsdad.setBounds(10, 11, 414, 206);
    contentPanel.add(dtrpnAsdasdAsdad);
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
    }
  }
}
