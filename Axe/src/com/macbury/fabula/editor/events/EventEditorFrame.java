package com.macbury.fabula.editor.events;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;

public class EventEditorFrame extends JDialog {
  
  private final JPanel contentPanel = new JPanel();
  private JTextField textField;
  
  /**
   * Create the dialog.
   */
  public EventEditorFrame() {
    setTitle("Event Editor");
    setBounds(100, 100, 873, 617);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    contentPanel.add(tabbedPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    tabbedPane.addTab("Page 1", null, panel, null);
    panel.setLayout(new BorderLayout(0, 0));
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setContinuousLayout(true);
    splitPane.setResizeWeight(0.3);
    panel.add(splitPane);
    
    JPanel panel_1 = new JPanel();
    splitPane.setLeftComponent(panel_1);
    
    JPanel panel_2 = new JPanel();
    splitPane.setRightComponent(panel_2);
    
    JPanel panel_3 = new JPanel();
    panel_3.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.add(panel_3, BorderLayout.NORTH);
    panel_3.setLayout(new BorderLayout(0, 0));
    
    JLabel lblNewLabel = new JLabel("Event Id:  ");
    panel_3.add(lblNewLabel, BorderLayout.WEST);
    
    textField = new JTextField();
    lblNewLabel.setLabelFor(textField);
    panel_3.add(textField, BorderLayout.CENTER);
    textField.setColumns(10);
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
