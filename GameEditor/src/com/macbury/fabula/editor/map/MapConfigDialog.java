package com.macbury.fabula.editor.map;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

public class MapConfigDialog extends JDialog {
  
  private final JPanel contentPanel = new JPanel();
  private JTextField textField;
  
  /**
   * Create the dialog.
   */
  public MapConfigDialog() {
    setResizable(false);
    setTitle("Map Configure");
    setBounds(100, 100, 310, 208);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Name");
    lblNewLabel.setBounds(20, 24, 46, 14);
    contentPanel.add(lblNewLabel);
    
    textField = new JTextField();
    textField.setBounds(76, 21, 199, 20);
    contentPanel.add(textField);
    textField.setColumns(10);
    
    JLabel lblSize = new JLabel("Size");
    lblSize.setBounds(20, 55, 46, 14);
    contentPanel.add(lblSize);
    
    JSpinner spinner = new JSpinner();
    spinner.setBounds(186, 52, 89, 20);
    contentPanel.add(spinner);
    
    JSpinner spinner_1 = new JSpinner();
    spinner_1.setBounds(76, 52, 89, 20);
    contentPanel.add(spinner_1);
    
    JLabel lblX = new JLabel("x");
    lblX.setHorizontalAlignment(SwingConstants.CENTER);
    lblX.setBounds(163, 52, 23, 17);
    contentPanel.add(lblX);
    
    JComboBox comboBox = new JComboBox();
    comboBox.setBounds(76, 83, 199, 20);
    contentPanel.add(comboBox);
    
    JLabel lblTileset = new JLabel("Tileset");
    lblTileset.setBounds(20, 86, 46, 14);
    contentPanel.add(lblTileset);
    
    JLabel lblNewLabel_1 = new JLabel("Type");
    lblNewLabel_1.setBounds(20, 114, 46, 14);
    contentPanel.add(lblNewLabel_1);
    
    JComboBox comboBox_1 = new JComboBox();
    comboBox_1.setBounds(76, 111, 199, 20);
    contentPanel.add(comboBox_1);
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
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }
}
