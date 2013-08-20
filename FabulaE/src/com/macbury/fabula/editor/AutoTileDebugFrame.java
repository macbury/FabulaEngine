package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Window.Type;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

import com.macbury.fabula.terrain.AutoTiles;
import javax.swing.JScrollPane;

public class AutoTileDebugFrame extends JFrame {
  private JPanel contentPane;
  public DefaultTableModel model;
  private JScrollPane scrollPane;
  private JTable table;
  
  /**
   * Create the frame.
   */
  public AutoTileDebugFrame() {
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
    table.setFillsViewportHeight(true);
    table.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setViewportView(table);
    
    JButton btnSave = new JButton("Save");
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
    
    for (String key : AutoTiles.getCornerMap().keySet()) {
      String value = AutoTiles.getCornerMap().get(key).toString();
      model.addRow(new Object[] { key, value });
    }  
    
    table.setModel(model);
  }
  
}
