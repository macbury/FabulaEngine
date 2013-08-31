package com.macbury.fabula.editor;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class IconListRenderer extends DefaultListCellRenderer {
  private Map<String, ImageIcon> icons;

  public IconListRenderer(Map<String, ImageIcon> icons) {
    this.icons = icons;
  }
  
  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    Icon icon    = icons.get(value);
    label.setIcon(icon);
    //label.setText("");
    return label;
  }

  public void setIcons(Map<String, ImageIcon> autoTileIcons) {
    this.icons = autoTileIcons;
  }
  
}
