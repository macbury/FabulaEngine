package com.macbury.fabula.editor.tiles;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Window.Type;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

import com.macbury.fabula.editor.brushes.AutoTileBrush;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.terrain.tileset.AutoTiles;

import javax.swing.JScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AutoTileDebugFrame extends JFrame implements WindowListener, ActionListener {
  private JPanel contentPane;
  public DefaultTableModel model;
  private JScrollPane scrollPane;
  private JTable table;
  private AutoTileBrush brush;
  
  /**
   * Create the frame.
   */
  public AutoTileDebugFrame() {
    addWindowListener(this);
    setAlwaysOnTop(true);
    setTitle("Auto Tile Hash Table");
    setType(Type.UTILITY);
    setBounds(100, 100, 351, 546);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    model = new DefaultTableModel(
      new String[][] {
          {"a", "b"},
        },
        new String[] {
          "Hash", "Type"
        }
      ) {
      Class[] columnTypes = new Class[] {
        String.class, String.class
      };
      public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
      }
    };
    
    scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    
    table = new JTable();
    table.setShowGrid(false);
    table.setFillsViewportHeight(true);
    table.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setViewportView(table);
    
    JButton btnSave = new JButton("Save");
    btnSave.addActionListener(this);
    contentPane.add(btnSave, BorderLayout.SOUTH);
  }

  public void updateRows() {
    model = new DefaultTableModel(
        new String[][] {},
          new String[] {
            "Hash", "Type"
          }
        ) {
        Class[] columnTypes = new Class[] {
          String.class, String.class
        };
        public Class getColumnClass(int columnIndex) {
          return columnTypes[columnIndex];
        }
      };
    
    Set<String> keys = G.db.CORNER_MAP.keySet();
    
    for (String key : keys) {
      String value = G.db.CORNER_MAP.get(key).toString();
      model.addRow(new Object[] { key, value });
    }  
    
    setTitle("Combinations: "+keys.size());
    
    table.setModel(model);
  }

  public void setBrush(AutoTileBrush brush) {
    this.brush = brush;
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
    if (brush != null) {
      brush.rebuildCombinations();
      updateRows();
    }
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    G.db.save();
  }
  
}
